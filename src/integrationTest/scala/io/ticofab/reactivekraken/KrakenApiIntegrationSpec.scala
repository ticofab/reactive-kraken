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

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import io.ticofab.reactivekraken.api.JsonSupport
import io.ticofab.reactivekraken.messages._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Properties

class KrakenApiIntegrationSpec extends TestKit(ActorSystem("KrakenApiIntegrationSpec"))
  with WordSpecLike with Matchers with BeforeAndAfterAll with JsonSupport {

  val timeout = 10.seconds

  def nonceGenerator = () => System.currentTimeMillis
  val apiKey = Properties.envOrNone("KRAKEN_API_KEY")
  val apiSecret = Properties.envOrNone("KRAKEN_API_SECRET")
  val apiActor = system.actorOf(KrakenApiActor(nonceGenerator, apiKey, apiSecret))

  "The KrakenAPIActor" should {

    "Return a correct Asset response" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentAssets)
      probe.expectMsgPF(timeout) {
        case ca: CurrentAssets => println(ca)
        case a: MessageResponse => fail("wrong message: " + a)
      }

    }

    "Return a correct AssetPair response" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentAssetPair("ETH", "EUR"))
      probe.expectMsgPF(timeout) {
        case cap: CurrentAssetPair => println(cap)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return a correct Ticker response" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentTicker("ETH", "EUR"))
      probe.expectMsgPF(timeout) {
        case ct: CurrentTicker => println(ct)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return the current account balance" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentAccountBalance)
      probe.expectMsgPF(timeout) {
        case cab: CurrentAccountBalance => println(cab)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return the current trade balance" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentTradeBalance())
      probe.expectMsgPF(timeout) {
        case ctb: CurrentTradeBalance => println(ctb)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return the current open orders" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentOpenOrders)
      probe.expectMsgPF(timeout) {
        case coo: CurrentOpenOrders => println(coo)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return the current closed orders" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentClosedOrders)
      probe.expectMsgPF(timeout) {
        case cco: CurrentClosedOrders => println(cco)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return an error if asked to access a protected API without authentication" in {
      val probe = TestProbe()
      val unApiActor = system.actorOf(KrakenApiActor(nonceGenerator))
      probe.send(unApiActor, GetCurrentAccountBalance)
      probe.expectMsgPF(timeout) {
        case kaae: KrakenApiActorError => println(kaae)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

  }
}
