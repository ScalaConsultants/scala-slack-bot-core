package io.scalac.slack

import akka.actor.{Actor, ActorLogging, ActorRef}
import io.scalac.slack.common._
import io.scalac.slack.websockets.WebSocket

/**
 * Created on 08.02.15 23:00
 * Outgoing message protocol should change received
 * protocol into string and send it to websocket
 */
class OutgoingMessageProcessor(wsActor: ActorRef, eventBus: MessageEventBus) extends Actor with ActorLogging {

  override def receive: Receive = {
    case Ping =>
      wsActor ! WebSocket.Send(Ping.toJson)

    case msg: OutboundMessage =>
      wsActor ! WebSocket.Send(msg.toJson)

    case ignored => //nothing else

  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    eventBus.subscribe(self, Outgoing)
  }
}
