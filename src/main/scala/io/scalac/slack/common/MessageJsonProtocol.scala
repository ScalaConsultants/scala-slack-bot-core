package io.scalac.slack.common

import spray.json._

/**
 * Created on 10.02.15 17:33
 */
object MessageJsonProtocol extends DefaultJsonProtocol {

  implicit object BaseMessageJsonReader extends RootJsonReader[BaseMessage] {

    def read(value: JsValue) = {

      value.asJsObject.getFields("text", "channel", "user", "ts", "edited") match {

        case Seq(JsString(text), JsString(channel), JsString(user), JsString(ts)) =>
          BaseMessage(text, channel, user, ts, edited = false)

        case Seq(JsString(text), JsString(channel), JsString(user), JsString(ts), JsObject(edited)) =>

          BaseMessage(text, channel, user, ts, edited = true)

        case _ =>
          throw new DeserializationException("BaseMessage expected")
      }
    }
  }


  implicit val messageTypeFormat = jsonFormat(MessageType, "type", "subtype")


}
