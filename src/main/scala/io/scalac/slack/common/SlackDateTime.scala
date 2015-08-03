package io.scalac.slack.common

import org.joda.time.{DateTimeZone, DateTime}

/**
 * Created on 08.02.15 01:07
 * Slack use two formats of timestamp
 * 1. "1234567890" - time in seconds
 * 2. "1234567890.000012" - time in seconds and unique ID
 */
object SlackDateTime {

  /**
   *
   * @return 13-digist long timestamp (miliseconds)
   */
  def timeStamp(dt: DateTime = DateTime.now(DateTimeZone.UTC)): String = {
    dt.getMillis.toString //timestamp in milis
  }

  /**
   *
   * @return 10-digits long timestamp (seconds)
   */
  def seconds(dt: DateTime = DateTime.now(DateTimeZone.UTC)): String = {
    (dt.getMillis / 1000).toString
  }

  /**
   *
   * @return 10-digits long timestamp with unique connection ID
   */
  def uniqueTimeStamp(dt: DateTime = DateTime.now(DateTimeZone.UTC)): String = {
    seconds(dt) + "." + f"${MessageCounter.next}%06d"
  }

  def parseTimeStamp(ts: String): DateTime = {
    try {
      new DateTime(ts.toLong, DateTimeZone.UTC)
    } catch {
      case e: NumberFormatException =>
        DateTime.now(DateTimeZone.UTC)
    }
  }

  def parseSeconds(seconds: String): DateTime = {
    try {
      val tsl = seconds.toLong * 1000
      new DateTime(tsl,DateTimeZone.UTC)
    } catch {
      case e: NumberFormatException =>
        DateTime.now(DateTimeZone.UTC)
    }
  }

  def parseUniqueTimestamp(uniqueTimeStamp: String): DateTime = {
    parseSeconds(uniqueTimeStamp.split('.').head)
  }
}
