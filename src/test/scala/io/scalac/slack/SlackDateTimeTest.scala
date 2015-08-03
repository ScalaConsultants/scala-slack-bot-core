package io.scalac.slack

import io.scalac.slack.common.{MessageCounter, SlackDateTime}
import org.joda.time.{DateTimeZone, DateTime}
import org.scalatest.{Matchers, WordSpecLike}

/**
 * Created on 13.02.15 11:42
 */
class SlackDateTimeTest extends Matchers with WordSpecLike {

  val baseDate = new DateTime(2015, 2, 15, 8, 23, 45, 0, DateTimeZone.UTC)

  "SlackDateTime" must {
    "properly change DateTime into timestamp" in {
      SlackDateTime.timeStamp(baseDate) should equal("1423988625000")
    }

    "properly change DateTime into seconds" in {
      SlackDateTime.seconds(baseDate) should equal("1423988625")
    }

    "properly change DateTime into unique timestamp" in {
      MessageCounter.reset()
      SlackDateTime.uniqueTimeStamp(baseDate) should equal("1423988625.000001")
    }

    "properly parse timestamp" in {
      SlackDateTime.parseTimeStamp("1423988625000") should equal(baseDate)
    }

    "properly parse seconds" in {
      SlackDateTime.parseSeconds("1423988625") should equal(baseDate)
    }

    "properly parse unique timestamp" in {
      SlackDateTime.parseUniqueTimestamp("1423988625.000001") should equal(baseDate)
    }

  }


}
