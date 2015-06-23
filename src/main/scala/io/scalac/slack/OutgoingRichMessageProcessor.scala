package io.scalac.slack

import akka.actor.{Actor, ActorLogging, ActorRef}
import io.scalac.slack.common._

/**
 * Created on 08.02.15 23:00
 *
 */
class OutgoingRichMessageProcessor(apiActor: ActorRef, eventBus: MessageEventBus) extends Actor with ActorLogging {

  override def receive: Receive = {

    case msg: RichOutboundMessage =>
      if (msg.elements.nonEmpty)
        apiActor ! msg //trasport through WebAPI until RTM support begin

    case ignored => //nothing else

  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    eventBus.subscribe(self, Outgoing)
  }
}
