package io.scalac.slack

import akka.actor.{ActorContext, ActorRef}

trait BotModules {
  def registerModules(context: ActorContext, websocketClient: ActorRef): Unit
}
