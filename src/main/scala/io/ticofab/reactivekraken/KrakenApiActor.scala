package io.ticofab.reactivekraken

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.api.HttpRequestor
import io.ticofab.reactivekraken.model.{AssetResponse, JsonSupport}
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by Fabio Tiriticco on 15/07/2017.
  */

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
        .recover {
          case t: Throwable =>
            println("recover: " + t)
            AssetResponse(List(t.getMessage), Map())
        }
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
