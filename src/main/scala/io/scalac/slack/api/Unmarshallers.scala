package io.scalac.slack.api

import io.scalac.slack.models._
import org.joda.time.DateTime
import spray.json._

/**
 * Created on 28.01.15 23:06
 */
object Unmarshallers extends DefaultJsonProtocol {

  implicit object DateTimeJsonFormat extends JsonFormat[DateTime] {
    def write(dt: DateTime) = JsNumber(dt.getMillis)

    def read(value: JsValue) = {
      value match {
        case JsNumber(number) => new DateTime(number.toLong * 1000)
        case JsString(num) =>
          val seconds = num.toString.split('.')(0)
          val nanos = num.split('.')(1).take(3)
          new DateTime((seconds + nanos).toLong)

        case _ => throw new DeserializationException("Timestamp expected")
      }
    }
  }

  implicit object PresenceJsonFormat extends JsonFormat[Presence] {
    def write(dt: Presence) = JsString(dt.getClass.toString.toLowerCase)

    def read(value: JsValue) = {

      value match {

        case JsString(presence) =>
          presence match {
            case "away" => Away
            case "active" => Active

          }

        case _ => throw new DeserializationException("Presence expected")
      }
    }
  }

  implicit val apiTestResponseFormat = jsonFormat3(ApiTestResponse)
  implicit val authTestResponseFormat = jsonFormat7(AuthTestResponse)
  implicit val channelInfoFormat = jsonFormat3(ChannelInfo)
  implicit val channelFormat = jsonFormat(Channel, "name", "creator", "is_member", "is_channel", "id", "is_general", "is_archived", "created", "purpose", "topic", "unread_count", "last_read", "members")
  implicit val userFormat = jsonFormat(SlackUser, "id", "name", "deleted", "is_admin", "is_owner", "is_primary_owner", "is_restricted", "is_ultra_restricted", "has_files", "is_bot", "presence")
  implicit val botInfoFormat = jsonFormat(BotInfo, "id", "name")
  implicit val rtmStartResponseFormat = jsonFormat(RtmStartResponse, "ok", "url", "users", "channels", "self")
  implicit val chatPostMessageResponse = jsonFormat(ChatPostMessageResponse, "ok", "channel", "error")
}
