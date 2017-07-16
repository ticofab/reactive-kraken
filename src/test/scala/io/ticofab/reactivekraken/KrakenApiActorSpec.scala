package io.ticofab.reactivekraken

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.HttpRequest
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import io.ticofab.reactivekraken.api.HttpRequestor
import io.ticofab.reactivekraken.model.AssetResponse
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by Fabio Tiriticco on 15/07/2017.
  */
class KrakenApiActorSpec extends TestKit(ActorSystem("KrakenApiActorSpec")) with ImplicitSender
  with WordSpecLike with Matchers with MockitoSugar {

  "A KrakenApiActor " should {

    "Fire a request when asked to get assets" in {

      // mocking the http requestor
      trait MockHttpRequestor extends HttpRequestor {
        override def fireRequest(request: HttpRequest) = Future("mock")
      }

      val apiActor = TestActorRef(Props(new KrakenAPIActor with MockHttpRequestor))
      val probe = TestProbe()
      probe.send(apiActor, GetAssets)
      probe.expectMsgType[AssetResponse](3.seconds)

      //verify(apiActor.underlyingActor, times(1))
      //.fireRequest // TODO ask ale

    }

    "Don't respond upon receiving a message it doesn't understand" ignore {
      val testActor = TestActorRef[KrakenAPIActor]
      val probe = TestProbe()
      probe.send(testActor, "hello")
      probe.expectNoMsg(3.second)

    }

    "Return the current rate when asked to" ignore {
      val testActor = TestActorRef[KrakenAPIActor]
      val probe = TestProbe()
      probe.send(testActor, GetAssets)
      probe.expectNoMsg(2.seconds)
    }
  }

}
