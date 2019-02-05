package io.ticofab.reactivekraken.v0.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{FormData, HttpMethods, HttpRequest, Uri}
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.v0.signature.Signer
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait RequestHelper extends JsonSupport {

  protected implicit val as: ActorSystem
  protected implicit val ec: ExecutionContext
  protected implicit val am: ActorMaterializer

  /**
    *
    * @param path   The relative path of the request
    * @param params Optional request parameters
    * @return The correct Uri for this request
    */
  def getUri(path: String, params: Map[String, String] = Map()): Uri = Uri("https://api.kraken.com" + path).withQuery(Query(params))

  /**
    * Creates the signed HTTP request to fire
    *
    * @param path      The request path.
    * @param params    The request query params.
    * @param apiKey    The user's API key
    * @param apiSecret The user's API secret
    * @return The appropriate HTTP request to fire.
    */
  def getSignedRequest(path: String,
                       apiKey: String,
                       apiSecret: String,
                       nonce: Long,
                       params: Map[String, String] = Map()) = {
    val postData = "nonce=" + nonce.toString
    val signature = Signer.getSignature(path, nonce, postData, apiSecret)
    val headers = List(RawHeader("API-Key", apiKey), RawHeader("API-Sign", signature))
    val uri = getUri(path, params)
    HttpRequest(HttpMethods.POST, uri, headers, FormData(Map("nonce" -> nonce.toString)).toEntity)
  }

  /**
    * Fires an HTTP request and converts the result to the appropriate type
    *
    * @param request The HTTP request to fire
    * @tparam RESPONSE_CONTENT_TYPE The type of the content expected in case of successful response
    * @return A future of the typed Response
    */
  def handleRequest[RESPONSE_CONTENT_TYPE: JsonFormat](request: HttpRequest): Future[Response[RESPONSE_CONTENT_TYPE]] =
    Http()
      .singleRequest(request)
      .flatMap(_.entity.toStrict(2.second))
      .map(_.data)
      .map(_.utf8String)
      .map(_.parseJson.convertTo[Response[RESPONSE_CONTENT_TYPE]])
      .recover { case t: Throwable => Response[RESPONSE_CONTENT_TYPE](List(t.getMessage), None) }

  /**
    * Extracts the content of a parsed HTTP response and encapsulates it in the proper response message class.
    *
    * @param resp           The typed response from the HTTP request
    * @param messageFactory Function to create the message
    * @param contentFactory Function to create the message content
    * @tparam RESPONSE_CONTENT_TYPE The content of the response
    * @tparam MESSAGE_TYPE          Type of the message
    * @tparam MESSAGE_CONTENT_TYPE  Type of the message content
    * @return The message to send back to the sender
    */
  def extractMessage[RESPONSE_CONTENT_TYPE, MESSAGE_TYPE, MESSAGE_CONTENT_TYPE]
  (resp: Response[RESPONSE_CONTENT_TYPE],
   messageFactory: Either[List[String], MESSAGE_CONTENT_TYPE] => MESSAGE_TYPE,
   contentFactory: Response[RESPONSE_CONTENT_TYPE] => MESSAGE_CONTENT_TYPE): MESSAGE_TYPE = {
    if (resp.error.nonEmpty) messageFactory(Left(resp.error))
    else if (resp.result.isDefined) messageFactory(Right(contentFactory(resp)))
    else messageFactory(Left(List("Something went wrong: response has no content.")))
  }

}
