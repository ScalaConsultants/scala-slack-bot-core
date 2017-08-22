package io.scalac.slack.common

import org.scalatest.{FunSuite, Matchers}
import spray.json._

import scala.collection.Seq


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

  test("Reply") {

    val replyJson = /*language=json*/ """{
                                        |  "user": "U04PFEX3D",
                                        |  "ts": "1501038656.314433"
                                        |}""".stripMargin
    val reply = replyJson.parseJson.convertTo[Reply]

    reply.user should equal ("U04PFEX3D")
    reply.ts should equal ("1501038656.314433")
  }

  test("Replies") {

    val repliesJson = /*language=json*/ """[{
                                          |    "user": "U04PFEX3D",
                                          |    "ts": "1501038656.314433"
                                          |  }, {
                                          |    "user": "U04PFEX3D",
                                          |    "ts": "1501038707.323684"
                                          |  }, {
                                          |    "user": "U04PFEX3D",
                                          |    "ts": "1501038963.368899"
                                          |  }, {
                                          |    "user": "U04PFEX3D",
                                          |    "ts": "1501038994.374530"
                                          |  }, {
                                          |    "user": "U04PFEX3D",
                                          |    "ts": "1501040179.578238"
                                          |  }, {
                                          |    "user": "U04PFEX3D",
                                          |    "ts": "1501130090.866379"
                                          |  }, {
                                          |    "user": "U04PFEX3D",
                                          |    "ts": "1501130196.883555"
                                          |}]""".stripMargin
    val replies = repliesJson.parseJson.convertTo[Replies]

    replies.replies.size should equal (7)
    replies.replies.head.user should equal ("U04PFEX3D")
    replies.replies.head.ts should equal ("1501038656.314433")
    replies.replies(3).user should equal ("U04PFEX3D")
    replies.replies(3).ts should equal ("1501038994.374530")

    assert(replies.isInstanceOf[Replies])
  }

  test("Message object") {
    /*language=JSON*/
    val messageString = """{
                          |    "type": "message",
                          |    "user": "U04PFEX3D",
                          |    "text": "can\u2019t",
                          |    "thread_ts": "1500905121.099692",
                          |    "reply_count": 7,
                          |    "replies": [{
                          |        "user": "U04PFEX3D",
                          |        "ts": "1501038656.314433"
                          |    }, {
                          |        "user": "U04PFEX3D",
                          |        "ts": "1501038707.323684"
                          |    }, {
                          |        "user": "U04PFEX3D",
                          |        "ts": "1501038963.368899"
                          |    }, {
                          |        "user": "U04PFEX3D",
                          |        "ts": "1501038994.374530"
                          |    }, {
                          |        "user": "U04PFEX3D",
                          |        "ts": "1501040179.578238"
                          |    }, {
                          |        "user": "U04PFEX3D",
                          |        "ts": "1501130090.866379"
                          |    }, {
                          |        "user": "U04PFEX3D",
                          |        "ts": "1501130196.883555"
                          |    }],
                          |    "unread_count": 7,
                          |    "ts": "1500905121.099692"
                          |}""".stripMargin

    val message = messageString.parseJson.convertTo[Message]
    message.user should equal("U04PFEX3D")
    message.text should equal("can\u2019t")
    message.thread_ts should equal("1500905121.099692")
    message.reply_count should equal(7)
    message.unread_count should equal(7)
    message.ts should equal("1500905121.099692")
    message.replies.replies.size should equal(7)

    assert(message.replies.isInstanceOf[Replies])
    assert(message.replies.replies.head.isInstanceOf[Reply])
  }

}
