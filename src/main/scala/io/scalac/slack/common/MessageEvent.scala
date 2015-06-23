package io.scalac.slack.common

import org.joda.time.DateTime

import scala.annotation.tailrec

/**
 * Created on 08.02.15 22:04
 */
sealed trait MessageEvent

/**
 * Incoming message types
 */
trait IncomingMessage extends MessageEvent

case object Pong extends IncomingMessage

case object Hello extends IncomingMessage

/**
 * BaseMessage is on the top of messages hierarchy, there is 21 subtypes of BaseMessage and each of them
 * should has its own model
 * @param text message text, written by user
 * @param channel ID of channel
 * @param user ID of message author
 * @param ts unique timestamp
 */
case class BaseMessage(text: String, channel: String, user: String, ts: String, edited: Boolean = false) extends IncomingMessage

//user issued command to bot
case class Command(command: String, params: List[String], underlying: BaseMessage) extends IncomingMessage

//last in the incoming messages hierarchy
case class UndefinedMessage(body: String) extends IncomingMessage

/**
 * User requested help for given target
 * @param target Some(x) bot named x should display it's help, None any bot receiving command should send help
 */
case class HelpRequest(target: Option[String], channel: String) extends IncomingMessage

/**
 * Outgoing message types
 */
trait OutgoingMessage extends MessageEvent {
  def toJson: String
}

case object Ping extends OutgoingMessage {
  override def toJson = s"""{"id": ${MessageCounter.next}, "type": "ping","time": ${SlackDateTime.timeStamp()}}"""
}

case class OutboundMessage(channel: String, text: String) extends OutgoingMessage {
  override def toJson =
    s"""{
      |"id": ${MessageCounter.next},
      |"type": "message",
      |"channel": "$channel",
      |"text": "$text"
      |}""".stripMargin
}

sealed trait RichMessageElement

case class Text(value: String) extends RichMessageElement

case class PreText(value: String) extends RichMessageElement

case class Field(title: String, value: String, short: Boolean = false) extends RichMessageElement

case class Title(value: String, url: Option[String] = None) extends RichMessageElement

case class Color(value: String) extends RichMessageElement

object Color {
  val good = Color("good")
  val warning = Color("warning")
  val danger = Color("danger")
}

case class ImageUrl(url: String) extends RichMessageElement

case class RichOutboundMessage(channel: String, elements: List[Attachment]) extends MessageEvent

case class Attachment(text: Option[String] = None, pretext: Option[String] = None, fields: Option[List[Field]] = None, title: Option[String] = None, title_link: Option[String] = None, color: Option[String] = None, image_url: Option[String] = None) {
  def isValid = text.isDefined || pretext.isDefined || title.isDefined || (fields.isDefined && fields.get.nonEmpty)

  def addElement(element: RichMessageElement): Attachment = {
    element match {
      case Color(value) if value.nonEmpty =>
        copy(color = Some(value))
      case Title(value, url) if value.nonEmpty =>
        copy(title = Some(value), title_link = url)
      case PreText(value) if value.nonEmpty =>
        copy(pretext = Some(value))
      case Text(value) if value.nonEmpty =>
        copy(text = Some(value))
      case ImageUrl(url) if url.nonEmpty => copy(image_url = Some(url))
      case f: Field => copy(fields = Some(this.fields.getOrElse(List.empty[Field]) :+ f))
      case _ => this
    }
  }
}

object Attachment {


  def apply(elements: RichMessageElement*): Attachment = {

    @tailrec
    def loopBuild(elems: List[RichMessageElement], acc: Attachment): Attachment = {
      elems match {
        case Nil => acc
        case head :: tail => loopBuild(tail, acc.addElement(head))
      }
    }
    loopBuild(elements.toList, new Attachment())
  }

}


/**
 * Classifier for message event
 */
sealed trait MessageEventType

object Incoming extends MessageEventType

object Outgoing extends MessageEventType

/**
 * Message Type is unmarshalling helper
 * that show what kind of type is incomming message
 * it's needed because of their similiarity
 */
case class MessageType(messageType: String, subType: Option[String])
