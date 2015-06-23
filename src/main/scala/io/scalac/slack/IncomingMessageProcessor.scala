package io.scalac.slack

import akka.actor.{Actor, ActorLogging}
import io.scalac.slack.common._
import spray.json._

/**
 * Created on 08.02.15 23:36
 * Incoming message processor should parse incoming string
 * and change into proper protocol
 */
class IncomingMessageProcessor(eventBus: MessageEventBus) extends Actor with ActorLogging {

  import io.scalac.slack.common.MessageJsonProtocol._

  override def receive: Receive = {

    case s: String =>
      try {
        val mType = s.parseJson.convertTo[MessageType]
        val incomingMessage: IncomingMessage = mType match {
          case MessageType("hello", _) => Hello
          case MessageType("pong", _) => Pong
          case MessageType("message", None) => s.parseJson.convertTo[BaseMessage]
          case _ =>
            UndefinedMessage(s)
        }
        eventBus.publish(incomingMessage)
      }
      catch {
        case e : Exception =>
        eventBus.publish(UndefinedMessage(s))
      }
    case ignored => //nothing special
  }
}
