package io.scalac.slack

/**
 * Created on 21.01.15 01:23
 */
sealed trait SlackError

object ApiTestError extends SlackError

//no authentication token provided
object NotAuthenticated extends SlackError

//invalid auth token
object InvalidAuth extends SlackError

//token is for deleted user or team
object AccountInactive extends SlackError

//team is being migrated between servers
object MigrationInProgress extends SlackError

case class UnspecifiedError(msg: String) extends SlackError


object SlackError {
  def apply(errorName: String) = {
    errorName match {
      case "not_authed" => NotAuthenticated
      case "invalid_auth" => InvalidAuth
      case "account_inactive" => AccountInactive
      case "migration_in_progress" => MigrationInProgress
      case err => new UnspecifiedError(err)
    }
  }
}