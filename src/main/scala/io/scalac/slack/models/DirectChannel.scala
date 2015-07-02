package io.scalac.slack.models

/**
 * Direct channel object between bot and user
 * Object holds user's ID along with channel's ID
 */
case class DirectChannel(id: String, userId: String)
