package io.scalac.slack.websockets

import akka.actor.{Actor, Props}
import akka.io.IO
import io.scalac.slack._
import spray.can.Http
import spray.can.server.UHttp
import spray.can.websocket.WebSocketClientWorker
import spray.can.websocket.frame.{CloseFrame, StatusCode, TextFrame}
import spray.http.{HttpHeaders, HttpMethods, HttpRequest}
import spray.json._
/**
 * Created on 28.01.15 19:45
 */
class WSActor(eventBus: MessageEventBus) extends Actor with WebSocketClientWorker {

  override def receive = connect orElse handshaking orElse closeLogic

//  implicit val eventBus = SlackBot.eventBus
  val out = context.actorOf(Props(classOf[OutgoingMessageProcessor], self, eventBus))
  val in = context.actorOf(Props(classOf[IncomingMessageProcessor], eventBus))

  private def connect(): Receive = {
    case WebSocket.Connect(host, port, resource, ssl) =>
      val headers = List(
        HttpHeaders.Host(host, port),
        HttpHeaders.Connection("Upgrade"),
        HttpHeaders.RawHeader("Upgrade", "websocket"),
        HttpHeaders.RawHeader("Sec-WebSocket-Version", "13"),
        HttpHeaders.RawHeader("Sec-WebSocket-Key", Config.websocketKey))
      request = HttpRequest(HttpMethods.GET, resource, headers)
      IO(UHttp)(context.system) ! Http.Connect(host, port, ssl)
  }

  override def businessLogic = {
    case WebSocket.Release => close()
    case TextFrame(msg) => //message received

      // Each message without parsing is sent to eventprocessor
      // Because all messages from websockets should be read fast
      // If EventProcessor slow down with parsing
      // can be used dispatcher
      println(s"RECEIVED MESSAGE: ${msg.utf8String} ")
      in ! msg.utf8String

    case WebSocket.Send(message) => //message to send

      println(s"SENT MESSAGE: $message ")
      send(message)
    case ignoreThis => // ignore
  }

  def send(message: String) = connection ! TextFrame(message)

  def close() = connection ! CloseFrame(StatusCode.NormalClose)

  private var request: HttpRequest = null

  override def upgradeRequest = request

}

object WebSocket {

  sealed trait WebSocketMessage

  case class Connect(
                      host: String,
                      port: Int,
                      resource: String,
                      withSsl: Boolean = false) extends WebSocketMessage

  case class Send(msg: String) extends WebSocketMessage

  case object Release extends WebSocketMessage

}

