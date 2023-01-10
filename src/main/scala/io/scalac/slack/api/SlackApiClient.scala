package io.scalac.slack.api

import akka.actor.ActorSystem
import akka.event.Logging
import io.scalac.slack.Config
import spray.client.pipelining._
import spray.http.HttpHeaders.Authorization
import spray.http._
import spray.json._

import scala.concurrent.Future
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
/**
 * Created on 29.01.15 22:43
 */
object SlackApiClient extends ApiClient{

  val log = Logging

  implicit val system = ActorSystem("SlackApiClient")

  import system.dispatcher

  //function from HttpRequest to HttpResponse
  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  def get[T <: ResponseObject](endpoint: String, params: Map[String, String] = Map.empty[String, String], token: Option[String] = None)(implicit reader: JsonReader[T]): Future[T] = request(HttpMethods.GET, endpoint, params, token)
  def post[T <: ResponseObject](endpoint: String, params: Map[String, String] = Map.empty[String, String], token: Option[String] = None)(implicit reader: JsonReader[T]): Future[T] = request(HttpMethods.POST, endpoint, params, token)

  def request[T <: ResponseObject](method: HttpMethod,
                                   endpoint: String,
                                   queryParams: Map[String, String] = Map.empty[String,String],
                                   token: Option[String] = None)(implicit reader: JsonReader[T]): Future[T] = {

    val url = Uri(apiUrl(endpoint)).withQuery(queryParams)

    var request = HttpRequest(method, url)

    if (token.isDefined) {
      request = request.withHeaders(Authorization(OAuth2BearerToken(token.get)))
    }

    val futureResponse = pipeline(request).map(_.entity.asString)
    (for {
      responseJson <- futureResponse
      response = JsonParser(responseJson).convertTo[T]
    } yield response) recover {
      case cause => throw new Exception("Something went wrong", cause)
    }

  }

  def apiUrl(endpoint: String) = Config.baseUrl(endpoint)
}
