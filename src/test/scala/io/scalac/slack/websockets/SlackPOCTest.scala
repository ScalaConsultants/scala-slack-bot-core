package io.scalac.slack.websockets

import akka.actor.{ActorContext, ActorRef, ActorSystem, Props}
import io.scalac.slack.api.Start
import io.scalac.slack.common.Shutdownable
import io.scalac.slack.{BotModules, MessageEventBus}
import io.scalac.slack.common.actors.SlackBotActor
import org.scalatest.{FunSuite, Matchers}

object SlackPOCTest extends App with Shutdownable {
  val system = ActorSystem("test")

  val bus = new MessageEventBus

  val bot = system.actorOf(Props[SlackBotActor](new SlackBotActor(new BotModules {
    override def registerModules(context: ActorContext, websocketClient: ActorRef): Unit = ()
  }, bus, this)))

  bot ! Start

  override def shutdown(): Unit = system.terminate()
}