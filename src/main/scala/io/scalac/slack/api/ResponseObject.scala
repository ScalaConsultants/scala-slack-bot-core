package io.scalac.slack.api

import io.scalac.slack.models.{Channel, SlackUser}

/**
 * Created on 21.01.15 22:28
 */
sealed trait ResponseObject

case class ApiTestResponse(ok: Boolean, error: Option[String], args: Option[Map[String, String]]) extends ResponseObject

case class AuthTestResponse(ok: Boolean, error: Option[String], url: Option[String], team: Option[String], user: Option[String], team_id: Option[String], user_id: Option[String]) extends ResponseObject

case class RtmStartResponse(ok: Boolean, url: String, users: List[SlackUser], channels: List[Channel], self: BotInfo) extends ResponseObject

object ResponseObject {
  implicit def authTestResponseToAuthData(atr: AuthTestResponse): AuthData =
    AuthData(atr.url.getOrElse("url"), atr.team.getOrElse("team"), atr.user.getOrElse("user"), atr.team_id.getOrElse("teamID"), atr.user_id.getOrElse("userID"))
}

case class ChatPostMessageResponse(ok: Boolean, channel: String, error: Option[String]) extends ResponseObject

case class BotInfo(id: String, name: String)