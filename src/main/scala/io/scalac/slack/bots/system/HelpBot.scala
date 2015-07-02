package io.scalac.slack.bots.system

import io.scalac.slack.MessageEventBus
import io.scalac.slack.bots.AbstractBot
import io.scalac.slack.common.{Command, HelpRequest, OutboundMessage}

/**
 * Maintainer: Patryk
 */
class HelpBot(override val bus: MessageEventBus) extends AbstractBot {
  override def act: Receive = {
    case Command("help", options, raw) =>
      publish(HelpRequest(options.headOption, raw.channel))
  }

  override def help(channel: String): OutboundMessage = OutboundMessage(channel,
    s"*$name* is for helping. Duh \\n" +
      s"`help` - display help from all bots \\n " +
      s"`help {botName}` - display help for certain bot module")
}
