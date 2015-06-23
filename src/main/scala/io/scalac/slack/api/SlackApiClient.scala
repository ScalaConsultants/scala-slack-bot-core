package io.scalac.slack.api

import akka.actor.ActorSystem
import akka.event.Logging
import io.scalac.slack.Config
import spray.client.pipelining._
import spray.http._
import spray.json._

import scala.concurrent.Future

/**
 * Created on 29.01.15 22:43
 */
object SlackApiClient extends ApiClient{

  val log = Logging

  implicit val system = ActorSystem("SlackApiClient")

  import system.dispatcher

  //function from HttpRequest to HttpResponse
  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  def get[T <: ResponseObject](endpoint: String, params: Map[String, String] = Map.empty[String, String])(implicit reader: JsonReader[T]): Future[T] = request(HttpMethods.GET, endpoint, params)
  def post[T <: ResponseObject](endpoint: String, params: Map[String, String] = Map.empty[String, String])(implicit reader: JsonReader[T]): Future[T] = request(HttpMethods.POST, endpoint, params)

  def request[T <: ResponseObject](method: HttpMethod, endpoint: String, params: Map[String, String] = Map.empty[String,String])(implicit reader: JsonReader[T]): Future[T] = {

    val url = Uri(apiUrl(endpoint)).withQuery(params)

    val futureResponse = pipeline(HttpRequest(method, url)).map(_.entity.asString)
    (for {
      responseJson <- futureResponse
      response = JsonParser(responseJson).convertTo[T]
    } yield response) recover {
      case cause => throw new Exception("Something went wrong", cause)
    }

  }

  def apiUrl(endpoint: String) = Config.baseUrl(endpoint)
}
