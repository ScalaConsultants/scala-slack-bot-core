package io.scalac.slack.models

/**
 * Created on 28.01.15 23:11
 */
case class SlackUser(id: String, name: String, deleted: Boolean, isAdmin: Option[Boolean], isOwner: Option[Boolean], isPrimaryOwner: Option[Boolean], isRestricted: Option[Boolean], isUltraRestricted: Option[Boolean], hasFiles: Option[Boolean], isBot: Option[Boolean], presence: Presence)

sealed trait Presence

object Away extends Presence
object Active extends Presence