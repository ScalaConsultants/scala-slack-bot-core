package io.scalac.slack.bots.system

import io.scalac.slack.MessageEventBus
import io.scalac.slack.bots.IncomingMessageListener
import io.scalac.slack.common.{BaseMessage, BotInfoKeeper, Command}

class CommandsRecognizerBot(override val bus: MessageEventBus) extends IncomingMessageListener {

  val commandChar = '$'

  def receive: Receive = {

    case bm@BaseMessage(text, channel, user, dateTime, Some(thread_ts),edited) =>
      //COMMAND links list with bot's nam jack can be called:
      // jack link list
      // jack: link list
      // @jack link list
      // @jack: link list
      // $link list
      def changeIntoCommand(pattern: String): Boolean = {
        if (text.trim.startsWith(pattern)) {
          val tokenized = text.trim.drop(pattern.length).trim.split("\\s")
          publish(Command(tokenized.head, tokenized.tail.toList.filter(_.nonEmpty), bm))
          true
        }
        false
      }

      //call by commad character
      if (!changeIntoCommand(commandChar.toString))
        BotInfoKeeper.current match {
          case Some(bi) =>
            //call by name
            changeIntoCommand(bi.name + ":") ||
              changeIntoCommand(bi.name) ||
              //call by ID
              changeIntoCommand(s"<@${bi.id}>:") ||
              changeIntoCommand(s"<@${bi.id}>")

          case None => //nothing to do!
        }
  }
}
