package io.scalac.slack.bots

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.Timeout
import io.scalac.slack.MessageEventBus
import io.scalac.slack.common.{Incoming, MessageEvent, Outgoing, _}

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

trait MessagePublisher {

  import akka.pattern._

  def bus: MessageEventBus


  implicit def publish(event: MessageEvent): Unit = {
    bus.publish(event)
  }

  def publish(directMessage: DirectMessage)(implicit context: ExecutionContext, userStorage: ActorRef, timeout: Timeout): Unit = {
    userStorage ? FindChannel(directMessage.key) onSuccess {
      case Some(channel: String) =>
        val eventToSend = directMessage.event match {
          case message: OutboundMessage => message.copy(channel = channel)
          case message: RichOutboundMessage => message.copy(channel = channel)
          case other => other
        }
        publish(eventToSend)
    }
  }
}

abstract class MessageListener extends Actor with ActorLogging with MessagePublisher

/**
 * A raw messaging interface used to create internal system level bots.
 * For user facing bots use AbstractBot
 */
abstract class IncomingMessageListener extends MessageListener {
  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = bus.subscribe(self, Incoming)
}

abstract class OutgoingMessageListener extends MessageListener {
  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = bus.subscribe(self, Outgoing)
}

/**
 * The class to extend when creating a bot.
 */
abstract class AbstractBot extends IncomingMessageListener {
  log.debug(s"Starting ${self.path.name} on $bus")

  override val bus: MessageEventBus

  def name: String = self.path.name

  def help(channel: String): OutboundMessage

  def act: Actor.Receive

  def handleSystemCommands: Actor.Receive = {
    case HelpRequest(t, ch) if t.map(_ == name).getOrElse(true) => publish(help(ch))
  }

  override final def receive: Actor.Receive = act.orElse(handleSystemCommands)
}
