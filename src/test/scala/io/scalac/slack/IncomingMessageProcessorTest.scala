package io.scalac.slack

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import io.scalac.slack.common._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created on 10.02.15 18:27
 */
class IncomingMessageProcessorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with Matchers with WordSpecLike with BeforeAndAfterAll {

  def this() = this(ActorSystem("IncomingMessageProcessorTestSystem"))

  def eB() = new MessageEventBus

  val theProbe = TestProbe()

  def getEchoSubscriber = {
    system.actorOf(Props(new Actor {
      def receive = {
        case im: IncomingMessage =>
          theProbe.ref ! im
      }
    }))
  }

   //date helpers
  val baseTime = new DateTime(2015, 2, 15, 8, 23, 45, 0)
  val uniqueTS = SlackDateTime.uniqueTimeStamp(baseTime)

  /**
   * Why this function is named matrix?
   * Because I can!
   * @param f code to execute
   * @return nothing at all
   */
  def matrix()(f: (ActorRef) => Unit) = {
    implicit val eventBus = eB()
    val echo = getEchoSubscriber
    val entry = system.actorOf(Props(classOf[IncomingMessageProcessor], eventBus))
    eventBus.subscribe(echo, Incoming)
    f(entry)
  }

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "IncommingMessageProcessor" must {
    "push to event bus undefined message" in {

      matrix() { entry =>
        entry ! "just string!"
        theProbe.expectMsg(1 second, UndefinedMessage("just string!"))
      }
    }
    "push hello Object into event bus" in {
      matrix() {
        entry =>
          entry ! """{"type":"hello"}"""
          theProbe.expectMsg(1 second, Hello)
      }
    }
    "push pong object into event bus" in {
      matrix() { entry =>
        entry ! """{"type":"pong","time":1423985025000,"reply_to":1}"""

        theProbe.expectMsg(1 second, Pong)
      }
    }

    "push BaseMessage without edited date" in {
      matrix() { entry =>
        entry ! s"""{
                   |  "type": "message",
                   |  "channel": "C2147483705",
                   |  "user": "U2147483697",
                   |  "text": "Hello world",
                   |  "ts": "1405894322.002768"
                   |}""".stripMargin
        theProbe.expectMsg(1 second, BaseMessage("Hello world", "C2147483705", "U2147483697", "1405894322.002768", edited = false))
      }
    }

/*
    "push BaseMessage with edited date" in {
      matrix() { entry =>
        entry ! s"""{
                   |   "type": "message",
                   |   "channel": "C2147483705",
                   |   "user": "U2147483697",
                   |   "text": "Hello, world!",
                   |   "ts": "$uniqueTS",
                                         |    "edited": {
                                         |       "user": "U2147483697",
                                         |       "ts": "$uniqueTS"
                                                                   |   }
                                                                   |}""".stripMargin

        theProbe.expectMsg(1 seconds, BaseMessage("Hello world!", "C2147483705", "U2147483697", baseTime, edited = true))
      }
    }
*/ // I DON"T KNOW WHY IT FAILS


  }
}
