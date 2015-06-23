package io.scalac.slack.common

import org.scalatest.{Matchers, FunSuite}
import spray.json._
import JsonProtocols._
/**
 * Created on 09.03.15 10:35
 */
class AttachmentTest extends FunSuite with Matchers {

  test("parse attachment with color only") {
    val att1 = Attachment(Color.good)
    val att = Attachment(color = Some("good"))
    att should equal (att1)
    att shouldNot be ('valid)
  }

  test("parse attachment with title ") {
    val att1 = Attachment(Title("fine title"))
    val att = Attachment(title = Some("fine title"))
    att should equal (att1)
    att should be ('valid)
  }

  test("parse attachment with titleURL only ") {
    val att1 = Attachment(Title("", Some("title url")))
    val att = Attachment()
    att should equal (att1)
    att shouldNot be ('valid)
  }

  test("parse attachment with title and titleURL  ") {
    val att1 = Attachment(Title("title", Some("title url")))
    val att = Attachment(title = Some("title"), title_link = Some("title url"))
    att should equal (att1)
    att should be ('valid)
  }
  test("parse attachment with title ,titleURL and Color") {
    val att1 = Attachment(Title("title", Some("title url")), Color.warning)
    val att = Attachment(title = Some("title"), title_link = Some("title url"), color = Some("warning"))
    att should equal (att1)
    att should be ('valid)
  }

  test("parse fields and pretext"){
    val att1 = Attachment(PreText("pretext"), Field("title 1", "content 1", short = false))
    val att = Attachment(pretext = Some("pretext"), fields = Some(List(Field("title 1", "content 1", short = false))))
    att should equal (att1)
    att should be ('valid)
  }

  test("parse some fields and text"){
    val att1 = Attachment(Text("sometext"), Field("title 1", "content 1", short = false), Field("title 2", "content 2", short = true))
    val att = Attachment(text = Some("sometext"), fields = Some(List(Field("title 1", "content 1", short = false), Field("title 2", "content 2", short = true))))
    att should equal (att1)
    att should be ('valid)
  }

  test("attachment to JSON"){
    val att1 = Attachment(Text("sometext"), Field("title 1", "content 1", short = false), Field("title 2", "content 2", short = true), Color.danger)
    //language=JSON
    val json = """{"fallback":"wrong formatted message","text":"sometext","fields":[{"title":"title 1","value":"content 1","short":false},{"title":"title 2","value":"content 2","short":true}],"color":"danger"}"""

    json should equal (att1.toJson.toString())

  }

  test("field serializer to JSON"){

    import io.scalac.slack.common.JsonProtocols._
    val field = Field("field title", "field value", short = false)

    val fieldJson = field.toJson.toString()
    //language=JSON
    val json = """{"title":"field title","value":"field value","short":false}"""
    json should equal (fieldJson)
  }

}
