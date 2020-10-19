package io.scalac.slack.common

import akka.actor.{Actor, ActorLogging}
import io.scalac.slack.api.Ok
import io.scalac.slack.models.{DirectChannel, Presence, SlackUser}

import scala.language.{implicitConversions, postfixOps}

/**
 * Maintainer: @marioosh
 */
class UsersStorage extends Actor with ActorLogging {

  var userCatalog = List.empty[UserInfo]
  var channelCatalog = List.empty[DirectChannel]

  implicit def convertUsers(su: SlackUser): UserInfo = UserInfo(su.id.trim, su.name.trim, su.presence)

  override def receive: Receive = {
    case RegisterUsers(users@_*) =>
      users.filterNot(u => u.deleted).foreach(addUser(_))
      sender ! Ok

    case FindUser(key) => sender ! userCatalog.find { user =>
      val matcher = key.trim.toLowerCase
      matcher == user.id.toLowerCase || matcher == user.name.toLowerCase
    }

    case RegisterDirectChannels(channels@_*) =>
      channels foreach addDirectChannel
      sender ! Ok

    case FindChannel(key) =>

      val id = userCatalog.find(u => u.name == key.trim.toLowerCase) match {
        case Some(user) => user.id
        case None => key
      }
      sender ! channelCatalog.find(c => c.id == id || c.userId == id).map(_.id)

  }

  def addDirectChannel(channel: DirectChannel): Unit = {
    channelCatalog = channel :: channelCatalog.filterNot(_.userId == channel.userId)
  }

  private def addUser(user: UserInfo): Unit = {
    userCatalog = user :: userCatalog.filterNot(_.id == user.id)
  }

}

case class UserInfo(id: String, name: String, presence: Presence) {
  def userLink() = s"""<@$id|name>"""
}

case class RegisterUsers(slackUsers: SlackUser*)

case class RegisterDirectChannels(ims: DirectChannel*)

case class FindUser(key: String)

case class FindChannel(key: String)