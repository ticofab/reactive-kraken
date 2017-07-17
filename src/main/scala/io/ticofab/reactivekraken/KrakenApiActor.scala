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

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.api.HttpRequestor
import io.ticofab.reactivekraken.model.{AssetResponse, JsonSupport}
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case object GetAssets

case class GetCurrentRate(currency: String, respectToCurrency: String)

case class CurrentRate(currency: String, respectToCurrency: String, value: Double)

class KrakenAPIActor extends Actor with JsonSupport with HttpRequestor {

  implicit val as = context.system
  implicit val am = ActorMaterializer()

  override def receive = {

    case GetAssets =>
      val originalSender = sender

      fireRequest(HttpRequest(uri = "https://api.kraken.com/0/public/Assets"))
        .map(_.parseJson.convertTo[AssetResponse])
        .recover { case t: Throwable => AssetResponse(List(t.getMessage), Map()) }
        .onComplete {
          case Success(response) => originalSender ! response
          case Failure(error) => originalSender ! AssetResponse(List(error.getMessage), Map())
        }

    case GetCurrentRate(currency, respectToCurrency) =>
      val originalSender = sender

    case _ =>
  }
}

object KrakenAPIActor {
  def apply() = Props(new KrakenAPIActor)
}
