package io.scalac.slack.common

import io.scalac.slack.api.BotInfo

object BotInfoKeeper {
  var current: Option[BotInfo] = None
}
