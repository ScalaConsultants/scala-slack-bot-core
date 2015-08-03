package io.scalac.slack.common

import org.scalatest.{OneInstancePerTest, Matchers, FunSuite}

/**
 * Created on 08.02.15 00:57
 */
class MessageCounterTest extends FunSuite with Matchers with OneInstancePerTest {

  test("message counter should still increment and reset "){
    MessageCounter.reset()
    MessageCounter.next should equal (1)
    MessageCounter.next should equal (2)
    MessageCounter.next should equal (3)

    MessageCounter.next should equal (4)
    MessageCounter.reset()
    MessageCounter.next should equal (1)
  }
}
