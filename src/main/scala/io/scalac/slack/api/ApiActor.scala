package io.scalac.slack.api

import akka.actor.{Actor, ActorLogging}
import io.scalac.slack.api.ResponseObject._
import io.scalac.slack.common.JsonProtocols._
import io.scalac.slack.common.{Attachment, RichOutboundMessage}
import io.scalac.slack.{ApiTestError, Config, SlackError}
import spray.json._

import scala.util.{Failure, Success}

/**
 * Created on 21.01.15 20:32
 */
class ApiActor extends Actor with ActorLogging {

  import context.dispatcher
  import io.scalac.slack.api.Unmarshallers._

  override def receive = {

    case ApiTest(param, error) =>
      log.debug("api.test requested")
      val send = sender()
      val params = Map("param" -> param, "error" -> error).collect { case (key, Some(value)) => key -> value}

      SlackApiClient.get[ApiTestResponse]("api.test", params) onComplete {
        case Success(res) =>
          if (res.ok) {
            send ! Ok(res.args)
          }
          else {
            send ! ApiTestError
          }
        case Failure(ex) =>
          send ! ex

      }

    case AuthTest(token) =>
      log.debug("auth.test requested")
      val send = sender()

      SlackApiClient.get[AuthTestResponse]("auth.test", Map("token" -> token.key)) onComplete {
        case Success(res) =>

          if (res.ok)
            send ! AuthData(res)
          else
            send ! SlackError(res.error.get)
        case Failure(ex) =>
          send ! ex
      }

    case RtmStart(token) =>
      log.debug("rtm.start requested")
      val send = sender()

      SlackApiClient.get[RtmStartResponse]("rtm.start", Map("token" -> token.key)) onComplete {

        case Success(res) =>
          if (res.ok)
            send ! RtmData(res.url)
          send ! res.self
        case Failure(ex) =>
          send ! ex
      }
    case msg: RichOutboundMessage =>
      log.debug("chat.postMessage requested")

      val attachments = msg.elements.filter(_.isValid).map(_.toJson).mkString("[", ",", "]")
      val params = Map("token" -> Config.apiKey.key, "channel" -> msg.channel, "as_user" -> "true", "attachments" -> attachments)

      SlackApiClient.post[ChatPostMessageResponse]("chat.postMessage", params) onComplete {
        case Success(res) =>
          if (res.ok) {
            log.info("[chat.postMessage]: message delivered: " + res.toString)
          }
        case Failure(ex) =>
          log.error("[chat.postMessage] Error encountered - " + ex.getMessage)
      }

  }
}
