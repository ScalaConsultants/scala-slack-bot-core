package io.scalac.slack.common

import scala.slick.driver.H2Driver.simple._

object SlackbotDatabase {
  lazy val db = Database.forConfig("h2")
}
