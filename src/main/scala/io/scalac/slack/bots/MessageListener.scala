package io.scalac.slack.bots

import akka.actor.{Actor, ActorLogging}
import io.scalac.slack.MessageEventBus
import io.scalac.slack.common.{Incoming, MessageEvent, Outgoing, RichOutboundMessage, _}

/**
 * Created on 08.02.15 23:52
 */
trait MessagePublisher {
//  def bus = SlackBot.eventBus

  def bus: MessageEventBus

  def publish(event: MessageEvent) = {
    bus.publish(event)
  }
  def publish(event: RichOutboundMessage) = {
    bus.publish(event)
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
