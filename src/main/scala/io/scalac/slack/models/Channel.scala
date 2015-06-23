package io.scalac.slack.models

import org.joda.time.DateTime

/**
 * Created on 28.01.15 23:04
 */
case class Channel(name: String, creator: String, isMember: Boolean, isChannel: Boolean, id: String, isGeneral: Boolean, isArchived: Boolean, created: DateTime, purpose: Option[ChannelInfo], topic: Option[ChannelInfo], unreadCount: Option[Int], lastRead :Option[DateTime],members: Option[List[String]])