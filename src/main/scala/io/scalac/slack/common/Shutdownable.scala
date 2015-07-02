package io.scalac.slack.common

trait Shutdownable {

  def shutdown(): Unit
}
