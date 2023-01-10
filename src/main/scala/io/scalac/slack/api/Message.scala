package io.scalac.slack.api

/**
 * Messages sended between actors
 */
sealed trait Message

case object Start extends Message
case object Stop extends Message
case object RegisterModules extends Message

//API CALLS
case class ApiTest(param: Option[String] = None, error: Option[String] = None) extends Message
case class AuthTest(token: APIKey) extends Message

@deprecated("Use RtmConnect instead")
case class RtmStart(token: APIKey) extends Message

case class RtmConnect(token: APIKey) extends Message
//API RESPONSES
case object Ok extends Message
case object Connected extends Message

case class AuthData(url: String, team: String, user: String, teamId: String, userId: String) extends Message
case class RtmData(url: String)

object AuthData {
  def apply(atr: AuthData) = atr
}
