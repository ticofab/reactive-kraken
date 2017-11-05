package io.ticofab.reactivekraken

/**
  * Copyright 2017 Fabio Tiriticco, Fabway
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.HttpRequest
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import io.ticofab.reactivekraken.KrakenPublicApiActor._
import io.ticofab.reactivekraken.api.HttpRequestor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class KrakenPublicApiActorSpec extends TestKit(ActorSystem("KrakenApiActorSpec")) with ImplicitSender
  with WordSpecLike with Matchers with MockitoSugar {

  def nonceGenerator = () => System.currentTimeMillis

  "A KrakenApiActor " should {

    "Fire a request when asked to get assets" in {

      // mocking the http requestor
      trait MockHttpRequestor extends HttpRequestor {
        override def fireRequest(request: HttpRequest) = Future("mock")
      }

      val apiActor: TestActorRef[KrakenPublicApiActor] = TestActorRef(Props(spy(new KrakenPublicApiActor(nonceGenerator) with MockHttpRequestor)))
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentAssets)
      probe.expectMsgType[CurrentAssets](3.seconds)

      verify(apiActor.underlyingActor, times(1))
        .fireRequest(ArgumentMatchers.any[HttpRequest])
    }

    "Not respond upon receiving a message it doesn't understand" in {
      val testActor = TestActorRef[KrakenPublicApiActor]
      val probe = TestProbe()
      probe.send(testActor, "hello")
      probe.expectNoMsg(1.second)
    }

    "Can used from multiple context" in {
      trait MockHttpRequestor extends HttpRequestor {
        override def fireRequest(request: HttpRequest) = Future({Thread.sleep(1000); "mock"})
      }

      val apiActor: TestActorRef[KrakenPublicApiActor] = TestActorRef(Props(spy(new KrakenPublicApiActor(nonceGenerator) with MockHttpRequestor)))
      val probe = TestProbe()
      val probe2 = TestProbe()
      probe.send(apiActor, GetCurrentAssets)
      probe2.send(testActor, "hello")
      probe.expectMsgType[CurrentAssets](3.seconds)

      verify(apiActor.underlyingActor, times(1))
        .fireRequest(ArgumentMatchers.any[HttpRequest])

      probe2.expectNoMsg(5.second)
    }

  }

}
