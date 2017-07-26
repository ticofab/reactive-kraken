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
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import io.ticofab.reactivekraken.messages._

import scala.concurrent.duration._

object KrakenApiStream {
  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()

  def nonceGenerator = () => System.currentTimeMillis

  def assetPairStream(currency: String, respectToCurrencty: String) = {
    val apiActor = as.actorOf(KrakenApiActor(nonceGenerator))
    Source
      .tick(0.seconds, 2.second, GetCurrentAssetPair(currency, respectToCurrencty))
      .mapAsync(2) { gcap => (apiActor ? gcap) (2.second).mapTo[CurrentAssetPair] }
  }

  def tickerStream(currency: String, respectToCurrencty: String) = {
    val apiActor = as.actorOf(KrakenApiActor(nonceGenerator))
    Source
      .tick(0.seconds, 2.second, GetCurrentTicker(currency, respectToCurrencty))
      .mapAsync(2) { gct => (apiActor ? gct) (2.second).mapTo[CurrentTicker] }
  }


}
