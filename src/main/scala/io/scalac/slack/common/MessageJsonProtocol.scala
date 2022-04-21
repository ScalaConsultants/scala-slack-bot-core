package io.scalac.slack.common

import spray.json._

/**
 * Created on 10.02.15 17:33
 */
object MessageJsonProtocol extends DefaultJsonProtocol {

  implicit object BaseMessageJsonReader extends RootJsonReader[BaseMessage] {

    def read(value: JsValue) = {

      value.asJsObject.getFields("text", "channel", "user", "ts", "edited", "thread_ts") match {

        case Seq(JsString(text), JsString(channel), JsString(user), JsString(ts)) =>
          BaseMessage(text, channel, user, ts, edited = false)

        case Seq(JsString(text), JsString(channel), JsString(user), JsString(ts), JsObject(edited)) =>
          BaseMessage(text, channel, user, ts, edited = true)

        case Seq(JsString(text), JsString(channel), JsString(user), JsString(ts), JsString(thread_ts)) =>
          BaseMessage(text, channel, user, ts, Some(thread_ts))

        case Seq(JsString(text), JsString(channel), JsString(user), JsString(ts), JsString(thread_ts), JsObject(edited)) =>
          BaseMessage(text, channel, user, ts, Some(thread_ts), edited = true)

        case _ =>
          throw new DeserializationException("BaseMessage expected")
      }
    }
  }

  implicit object MessageThreadJsonReader extends RootJsonReader[MessageThread] {
    def read(value: JsValue) = {
      value.asJsObject.getFields("message","hidden","channel", "event_ts", "ts") match {
        case Vector(message,JsTrue, JsString(channel), JsString(event_ts), JsString(ts)) =>
          MessageThread(message.convertTo[Message], true, channel,event_ts,ts)

        case _ =>
          throw new DeserializationException("MessageThread expected")
      }
    }
  }

  implicit object MessageJsonReader extends RootJsonReader[Message] {

    def read(value: JsValue) = {
      value.asJsObject.getFields("user", "text", "thread_ts", "reply_count", "replies", "unread_count", "ts") match {
        case Vector(JsString(user), JsString(text), JsString(thread_ts), JsNumber(reply_count), replies, JsNumber(unread_count),JsString(ts)) =>
          Message(user, text, thread_ts, reply_count, replies.convertTo[Replies], unread_count, ts)

        case _ =>
          throw new DeserializationException("Message expected")
      }
    }
  }

  implicit object RepliesJsonReader extends RootJsonFormat[Replies] {

    def read(value: JsValue) = {
      println(value.getClass)
      value match {
        case JsArray(replies) =>
          val r = replies.map{ x =>
            x.convertTo[Reply]
          }
          Replies(r)

        case _ =>
          throw new DeserializationException("Reply expected")
      }
    }

    override def write(obj: Replies): JsValue = serializationError("not supported")
  }

  implicit object ReplyJsonReader extends RootJsonFormat[Reply] {

    def read(value: JsValue) = {
      value.asJsObject.getFields("user", "ts") match {
        case Seq(JsString(user), JsString(ts)) =>
          Reply(user, ts)

        case _ =>
          throw new DeserializationException("Reply expected")
      }
    }

    override def write(obj: Reply): JsValue = serializationError("not supported")
  }

  implicit val messageTypeFormat = jsonFormat(MessageType, "type", "subtype")
}
