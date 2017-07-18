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
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.api.JsonSupport.responseFormat
import io.ticofab.reactivekraken.api.{HttpRequestor, JsonSupport, Response}
import io.ticofab.reactivekraken.model.{Asset, AssetPair, Ticker}
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case object GetCurrentAssets

case class CurrentAssets(assets: Either[List[String], Map[String, Asset]])

case class GetCurrentAssetPair(currency: String, respectToCurrency: String)

case class CurrentAssetPair(assetPair: Either[List[String], Map[String, AssetPair]])

case class GetCurrentTicker(currency: String, respectToCurrency: String)

case class CurrentTicker(ticker: Either[List[String], Map[String, Ticker]])

class KrakenApiActor extends Actor with JsonSupport with HttpRequestor {

  implicit val as = context.system
  implicit val am = ActorMaterializer()

  private def handle[T: JsonFormat](request: HttpRequest): Future[Response[T]] =
    fireRequest(request)
      .map(_.parseJson.convertTo[Response[T]])
      .recover { case t: Throwable => Response[T](List(t.getMessage), Map()) }

  private def composeReturnMessage[T](pair: String, response: Response[T]): Either[List[String], T] =
    if (response.error.nonEmpty) Left(response.error)
    else response.result.get(pair) match {
      case None => Left(List("No answer"))
      case Some(responseValue) => Right(responseValue)
    }


  override def receive = {

    case GetCurrentAssets =>
      val request = HttpRequest(uri = "https://api.kraken.com/0/public/Assets")
      handle[Asset](request)
        .map { response =>
          if (response.error.nonEmpty) Left(response.error)
          else Right(response.result)
        }.map(CurrentAssets)
        .pipeTo(sender)

    case GetCurrentAssetPair(currency, respectToCurrency) =>
      val params = Map("pair" -> (currency + respectToCurrency))
      val request = HttpRequest(uri = Uri("https://api.kraken.com/0/public/AssetPairs").withQuery(Query(params)))
      handle[AssetPair](request)
        .map { response =>
          if (response.error.nonEmpty) Left(response.error)
          else Right(response.result)
        }.map(CurrentAssetPair)
        .pipeTo(sender)

    case GetCurrentTicker(currency, respectToCurrency) =>
      val params = Map("pair" -> (currency + respectToCurrency))
      val request = HttpRequest(uri = Uri("https://api.kraken.com/0/public/Ticker").withQuery(Query(params)))
      handle[Ticker](request)
        .map { response =>
          if (response.error.nonEmpty) Left(response.error)
          else Right(response.result)
        }.map(CurrentTicker)
        .pipeTo(sender)

  }
}

object KrakenApiActor {
  def apply() = Props(new KrakenApiActor)
}
