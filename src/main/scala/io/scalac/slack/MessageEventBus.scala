package io.scalac.slack

import akka.event.{ActorEventBus, LookupClassification}
import io.scalac.slack.common._

/**
 * Created on 08.02.15 22:16
 */
class MessageEventBus extends ActorEventBus with LookupClassification {
  override type Event = MessageEvent

  override type Classifier = MessageEventType

  override protected def mapSize(): Int = 2

  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event
  }

  override protected def classify(event: Event): Classifier = {
    event match {
      case im: IncomingMessage => Incoming
      case om: OutgoingMessage => Outgoing
      case rich: RichOutboundMessage => Outgoing
    }
  }
}
