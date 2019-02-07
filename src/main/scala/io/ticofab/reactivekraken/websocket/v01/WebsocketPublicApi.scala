package io.ticofab.reactivekraken.websocket.v01

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import io.ticofab.reactivekraken.websocket.v01.model.{KrakenWsMessage, KrakenWsMessagesJson}
import spray.json._

import scala.concurrent.Future

object WebsocketPublicApi extends KrakenWsMessagesJson {

  val wsRequest = WebSocketRequest("wss://ws.kraken.com")

  def openConnection[Mat](source: Source[KrakenWsMessage, Mat],
                          sink: Sink[KrakenWsMessage, Future[Done]],
                          actorSystem: ActorSystem = ActorSystem("reactive-kraken")) = {

    implicit val as = actorSystem
    implicit val ec = as.dispatcher
    implicit val am = ActorMaterializer()

    val messageSource: Source[Message, Mat] = source.map(krakenWsMessage => TextMessage(krakenWsMessage.toJson.compactPrint))
    val messageSink: Sink[Message, Future[Done]] = sink.contramap[Message](tm => tm.asTextMessage.getStrictText.parseJson.convertTo[KrakenWsMessage])

    val flow = Flow.fromSinkAndSourceMat(messageSink, messageSource)(Keep.left)
    val (upgradeResponse, futureClosed) = Http().singleWebSocketRequest(wsRequest, flow)

    val futureConnected: Future[Done] = upgradeResponse.map { upgrade =>
      // just like a regular http request we can access response status which is available via upgrade.response.status
      // status code 101 (Switching Protocols) indicates that server support WebSockets
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        Done
      } else {
        throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
      }
    }

    (futureConnected, futureClosed)

  }
}
