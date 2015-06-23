package io.scalac.slack.api

import spray.http.{HttpMethod, HttpRequest}
import spray.json.JsonReader

import scala.concurrent.Future

/**
 * Created on 25.01.15 22:22
 */
trait ApiClient {

  def request[T <: ResponseObject](method: HttpMethod, endpoint: String, params: Map[String, String] = Map.empty[String, String])(implicit reader: JsonReader[T]): Future[T]

}