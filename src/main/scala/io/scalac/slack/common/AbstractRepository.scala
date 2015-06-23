package io.scalac.slack.common

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta.MTable

abstract class AbstractRepository {
  val bucket: String
  protected val db = SlackbotDatabase.db

  def migrationNeeded()(implicit s: Session) = {
    MTable.getTables.list.exists(table => {
      table.name.name.contains(bucket)
    }) == false
  }

}
