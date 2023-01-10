package io.scalac.slack.websockets

import akka.{Done, NotUsed}
import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage, WebSocketRequest}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import io.scalac.slack._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * Created on 28.01.15 19:45
 */
class WSActor(eventBus: MessageEventBus) extends Actor with ActorLogging {

  private implicit val system = context.system
  private implicit val mat = ActorMaterializer()
  private implicit val ec: ExecutionContext = context.dispatcher
  override def receive = connect()

  val out = context.actorOf(Props(classOf[OutgoingMessageProcessor], self, eventBus))
  val in = context.actorOf(Props(classOf[IncomingMessageProcessor], eventBus))

  private val (sourceQueue, source) = Source.queue[Message](100, OverflowStrategy.fail).preMaterialize()
  private def messageSink: Sink[Message, Future[Done]] = Sink.foreach({
    case message: TextMessage.Strict =>
      log.debug(s"Received $message from websocket")
      in ! message.text

    case message: TextMessage.Streamed =>
      log.debug(s"Received stream message from socket")
      val futureString = message.textStream.runWith(Sink.fold("")(_ + _))
      futureString.onComplete({
        case Failure(exception) =>
          log.error("Error consuming streamed buffer", exception)
          throw exception
        case Success(value) =>
          in ! value
      })

    case message: BinaryMessage =>
      log.warning("Received binary message, ignoring")
      message.dataStream.runWith(Sink.ignore) // prevent memory leak by consuming buffer into ether
      () // ignore binary streamed messages, slack will use only json
  })

  private def messageSource: Source[Message, NotUsed] = source
  private def flow: Flow[Message, Message, Future[Done]] =
    Flow.fromSinkAndSourceMat(messageSink, messageSource)(Keep.left)
  private def connect(): Receive = {
    case WebSocket.Connect(url) =>
      val (upgradeResponse, closed) = Http()
        .singleWebSocketRequest(WebSocketRequest(url), flow)

      closed.onComplete(x => log.info(s"websocket closed ${x}"))
      upgradeResponse.map { upgrade =>
        if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
          Done
        } else {
          throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
        }
      }(context.dispatcher)

    case WebSocket.Send(msg) =>
      log.debug(s"send : $msg")
      sourceQueue.offer(TextMessage(msg))

  }

}

object WebSocket {

  sealed trait WebSocketMessage

  case class Connect(url: String) extends WebSocketMessage

  case class Send(msg: String) extends WebSocketMessage

  case object Release extends WebSocketMessage

}

