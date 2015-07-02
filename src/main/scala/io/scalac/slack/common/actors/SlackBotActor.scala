package io.scalac.slack.common.actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import io.scalac.slack.api.{ApiActor, ApiTest, AuthData, AuthTest, BotInfo, Connected, RegisterModules, RtmData, RtmStart, RtmStartResponse, Start, Stop}
import io.scalac.slack.common.{BotInfoKeeper, RegisterDirectChannels, RegisterUsers, Shutdownable}
import io.scalac.slack.websockets.{WSActor, WebSocket}
import io.scalac.slack.{BotModules, Config, MessageEventBus, MigrationInProgress, OutgoingRichMessageProcessor, SlackError}

import scala.concurrent.duration._

class SlackBotActor(modules: BotModules, eventBus: MessageEventBus, master: Shutdownable, usersStorageOpt: Option[ActorRef] = None) extends Actor with ActorLogging {

  import context.{dispatcher, system}

  val api = context.actorOf(Props[ApiActor])
  val richProcessor = context.actorOf(Props(classOf[OutgoingRichMessageProcessor], api, eventBus))
  val websocketClient = system.actorOf(Props(classOf[WSActor], eventBus), "ws-actor")

  var errors = 0

  override def receive: Receive = {
    case Start =>
      //test connection
      log.info("trying to connect to Slack's server...")
      api ! ApiTest()
    case Stop =>
      master.shutdown()
    case Connected =>
      log.info("connected successfully...")
      log.info("trying to auth")
      api ! AuthTest(Config.apiKey)
    case ad: AuthData =>
      log.info("authenticated successfully")
      log.info("request for websocket connection...")
      api ! RtmStart(Config.apiKey)
    case RtmData(url) =>
      log.info("fetched WSS URL")
      log.info(url)
      log.info("trying to connect to websockets channel")
      val dropProtocol = url.drop(6)
      val host = dropProtocol.split('/')(0)
      val resource = dropProtocol.drop(host.length)

      implicit val timeout: Timeout = 5.seconds

      log.info(s"Connecting to host [$host] and resource [$resource]")

      websocketClient ! WebSocket.Connect(host, 443, resource, withSsl = true)

      context.system.scheduler.scheduleOnce(Duration.create(5, TimeUnit.SECONDS), self, RegisterModules)

    case bi@BotInfo(_, _) =>
      BotInfoKeeper.current = Some(bi)
    case RegisterModules =>
      modules.registerModules(context, websocketClient)
    case MigrationInProgress =>
      log.warning("MIGRATION IN PROGRESS, next try for 10 seconds")
      system.scheduler.scheduleOnce(10.seconds, self, Start)
    case se: SlackError if errors < 10 =>
      errors += 1
      log.error(s"connection error [$errors], repeat for 10 seconds")
      log.error(s"SlackError occured [${se.toString}]")
      system.scheduler.scheduleOnce(10.seconds, self, Start)
    case se: SlackError =>
      log.error(s"SlackError occured [${se.toString}]")
      master.shutdown()
    case res: RtmStartResponse =>
      if(usersStorageOpt.isDefined) {
        val userStorage = usersStorageOpt.get

        userStorage ! RegisterUsers(res.users: _*)
        userStorage ! RegisterDirectChannels(res.ims: _*)
      }

    case WebSocket.Release =>
      websocketClient ! WebSocket.Release

  }

}
