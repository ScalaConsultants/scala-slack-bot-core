package io.scalac.slack.common

import org.scalatest.{FunSuite, Matchers}
import spray.json._


/**
 * Created on 10.02.15 17:37
 */
class MessageJsonProtocolTest extends FunSuite with Matchers {
  import io.scalac.slack.common.MessageJsonProtocol._

  test("MessageType message") {

    val mes = /*language=json*/ """{ "type" : "hello"}"""
    val hello = mes.parseJson.convertTo[MessageType]

    hello.messageType should equal ("hello")
    hello.subType should be(None)
  }

}
