package io.scalac.slack.api

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt


class ApiActorTest extends TestKit(ActorSystem("api-actor")) with WordSpecLike with Matchers with ImplicitSender with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "ApiActor" must {
    "connect after testing test api endpoint" in {
      implicit val timeout: Timeout = 5.second
      val actor = system.actorOf(Props[ApiActor])
      val responseFuture = actor.ask(ApiTest())
      val response = Await.result(responseFuture, timeout.duration)

      response shouldBe Connected
    }
  }
}
