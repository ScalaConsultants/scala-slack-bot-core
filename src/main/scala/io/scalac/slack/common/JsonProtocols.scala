package io.scalac.slack.common

import spray.json._

/**
 * Created on 09.03.15 11:59
 */
object JsonProtocols extends DefaultJsonProtocol {

  implicit object AttachmentFormatWriter extends RootJsonWriter[Attachment] {
    val attachmentFormat = jsonFormat7(Attachment.apply)

    override def write(a: Attachment): JsValue = {
      JsObject(JsObject("fallback" -> JsString("wrong formatted message")).fields ++ a.toJson(attachmentFormat).asJsObject.fields)
    }
  }

  implicit val fieldFormat = jsonFormat3(Field)
}
