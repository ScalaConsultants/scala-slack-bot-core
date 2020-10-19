package io.scalac.slack.common

import akka.actor.{ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import io.scalac.slack.api.Ok
import io.scalac.slack.models.{Active, DirectChannel, SlackUser}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Maintainer: @marioosh
 */
class UsersStorageTest(_system: ActorSystem) extends TestKit(_system) with DefaultTimeout with ImplicitSender with Matchers with WordSpecLike with BeforeAndAfterAll {

  def this() = this(ActorSystem("UsersStorageTestActorSystem"))

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "UserStorage" must {

    "save incoming users" in {
      val mario = SlackUser("1234", "mario", deleted = false, Some(false), None, None, None, None, None, None, Active)
      val stefek = SlackUser("12345", "stefek", deleted = false, Some(false), None, None, None, None, None, None, Active)
      val us = system.actorOf(Props[UsersStorage])

      within(2 seconds) {
        us ! RegisterUsers(mario)
        expectMsg(Ok)
      }

    }

    "find user from storage" in {
      val mario = SlackUser("U1234", "mario", deleted = false, Some(false), None, None, None, None, None, None, Active)
      val stefek = SlackUser("U12345", "stefek", deleted = false, Some(false), None, None, None, None, None, None, Active)
      val us = system.actorOf(Props[UsersStorage])

      within(1 second) {
        us ! RegisterUsers(mario, stefek)
        expectMsg(Ok)
        us ! FindUser("mario")
        expectMsg(Some(UserInfo("U1234", "mario", Active)))
        us ! FindUser("U12345")
        expectMsg(Some(UserInfo("U12345", "stefek", Active)))
        us ! FindUser("gitara")
        expectMsg(None)

      }
    }
  }


  "Channel Catalog" must {
    "be able to add new channel and find it" in {
      val im1 = DirectChannel("D123", "U123")
      val im2 = DirectChannel("D124", "U124")
      val im3 = DirectChannel("D125", "U124")

      val us = system.actorOf(Props[UsersStorage])

      within(1 second) {
        us ! RegisterDirectChannels(im1, im2)
        expectMsg(Ok)
        us ! RegisterDirectChannels(im3)
        expectMsg(Ok)
        us ! FindChannel("D12234")
        expectMsg(None)
        us ! FindChannel("D123")
        expectMsg(Some("D123"))
        us ! FindChannel("D125")
        expectMsg(Some("D125"))
        us ! FindChannel("U124")
        expectMsg(Some("D125"))

      }
    }
    "be able to find channel by username" in {
      val im1 = DirectChannel("D123", "U123")
      val mario = SlackUser("U123", "mario", deleted = false, Some(false), None, None, None, None, None, None, Active)

      val us = system.actorOf(Props[UsersStorage])

      within(1 second) {
        us ! RegisterUsers(mario)
        expectMsg(Ok)
        us ! RegisterDirectChannels(im1)
        expectMsg(Ok)
        //find
        us ! FindChannel("D123")
        expectMsg(Some("D123"))
        us ! FindChannel("U123")
        expectMsg(Some("D123"))
        us ! FindChannel("mario")
        expectMsg(Some("D123"))

      }

    }
  }

}
