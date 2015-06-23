package io.scalac.slack.common

import java.util.concurrent.atomic.AtomicInteger

object MessageCounter {
  private val cc = new AtomicInteger(0)

  def next = cc.incrementAndGet()

  def reset() = cc.set(0)

}
