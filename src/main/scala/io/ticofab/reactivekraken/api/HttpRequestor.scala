package io.ticofab.reactivekraken.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Fabio Tiriticco on 16/07/2017.
  */
trait HttpRequestor {

  implicit def as: ActorSystem
  implicit def am: ActorMaterializer

  def fireRequest(request: HttpRequest): Future[String] =
    Http().singleRequest(request)
    .flatMap(_.entity.toStrict(2.second))
    .map(_.data)
    .map(_.utf8String)
}
