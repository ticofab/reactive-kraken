package io.ticofab.reactivekraken.websocket.v01.model

import io.ticofab.reactivekraken.websocket.v01.model.KrakenWsMessages._
import io.ticofab.reactivekraken.websocket.v01.model.Subscription.{Spread, _}
import spray.json._

trait KrakenWsMessagesJson extends DefaultJsonProtocol {
  implicit val pingFormat         = jsonFormat1(Ping)
  implicit val pongFormat         = jsonFormat1(Pong)
  implicit val systemStatusFormat = jsonFormat3(SystemStatus)
  implicit val heartbeatFormat    = jsonFormat0(HeartBeat)

  implicit val currencyPairFormat: RootJsonFormat[CurrencyPair] = new RootJsonFormat[CurrencyPair] {
    override def write(obj: CurrencyPair) = JsString(obj.first + "/" + obj.second)

    override def read(json: JsValue) = json match {
      case JsString(value) => value.split("/").toList match {
        case first :: second :: Nil => CurrencyPair(first, second)
        case _ => deserializationError(s"failure to deserialize $value")
      }
      case _ => deserializationError(s"failure to deserialize ${json.compactPrint}")
    }
  }

  implicit val intervalFormat: RootJsonFormat[Interval] = new RootJsonFormat[Interval] {
    override def write(obj: Interval) = {
      val number = obj match {
        case OneMinute => 1
        case FiveMinutes => 5
        case FifteenMinutes => 15
        case ThirtyMinutes => 30
        case SixtyMinutes => 60
        case TwoHunderdFortyMinutes => 240
        case OneThousandFourHandredFortyMinutes => 1440
        case TenThousandEightyMinutes => 10080
        case TwentyOneThousandSixHundredMinutes => 21600
        case _ => serializationError(s"failure to serialize $obj")
      }
      JsNumber(number)
    }

    override def read(json: JsValue) = json match {
      case JsNumber(value) => value.toInt match {
        case 1 => OneMinute
        case 5 => FiveMinutes
        case 15 => FifteenMinutes
        case 30 => ThirtyMinutes
        case 60 => SixtyMinutes
        case 240 => TwoHunderdFortyMinutes
        case 1440 => OneThousandFourHandredFortyMinutes
        case 10080 => TenThousandEightyMinutes
        case 21600 => TwentyOneThousandSixHundredMinutes
        case _ => deserializationError(s"failure to deserialize $json")
      }
      case _ => deserializationError(s"failure to deserialize $json")
    }
  }

  implicit val topicFormat: RootJsonFormat[SubscriptionTopic] = new RootJsonFormat[SubscriptionTopic] {
    override def write(obj: SubscriptionTopic) = {
      val string = obj match {
        case Ticker => "ticker"
        case OHLC => "ohlc"
        case Trade => "trade"
        case Book => "book"
        case Spread => "spread"
        case AllTopics => "*"
        case _ => serializationError(s"failure to serialize $obj")
      }
      JsString(string)
    }

    override def read(json: JsValue) = json match {
      case JsString(value) => value match {
        case "ticker" => Ticker
        case "ohlc" => OHLC
        case "trade" => Trade
        case "book" => Book
        case "spread" => Spread
        case "*" => AllTopics

      }
      case _ => deserializationError(s"failure to deserialize $json")
    }
  }

  implicit val depthFormat: RootJsonFormat[Depth] = new RootJsonFormat[Depth] {
    override def write(obj: Depth) = {
      val number = obj match {
        case Ten => 10
        case TwentyFive => 25
        case OneHundred => 100
        case FiveHundred => 500
        case OneThousand => 1000
        case _ => serializationError(s"failure to serialize $obj")
      }
      JsNumber(number)
    }

    override def read(json: JsValue) = json match {
      case JsNumber(value) => value.toInt match {
        case 10 => Ten
        case 25 => TwentyFive
        case 100 => OneHundred
        case 500 => FiveHundred
        case 1000 => OneThousand
        case _ => deserializationError(s"failure to deserialize $json")
      }
      case _ => deserializationError(s"failure to deserialize $json")
    }
  }

  implicit val subscriptionFormat = jsonFormat3(Subscription.apply)

  implicit val subscribeFormat = jsonFormat3(Subscribe)

  implicit val subscriptionEventFormat: RootJsonFormat[SubscriptionEvent] = new RootJsonFormat[SubscriptionEvent] {
    override def write(obj: SubscriptionEvent) = ???

    override def read(json: JsValue) = json match {
      case JsString(value) => value match {
        case "subscribed" => Subscribed
        case "unsubscribed" => Unsubscribed
        case "error" => Error
        case _ => deserializationError(s"failure to serialize $json")
      }
      case _ => deserializationError(s"failure to serialize $json")
    }
  }

  implicit val subscriptionStatusFormat = jsonFormat4(SubscriptionStatus)

  // format that discriminates based on an additional
  // field "type" that can either be "Cat" or "Dog"
  implicit val krakenWsMessageFormat = new RootJsonFormat[KrakenWsMessage] {
    def write(obj: KrakenWsMessage): JsValue =
      JsObject((obj match {
        case c: Ping => c.toJson
        case p: Pong => p.toJson
        case s: SystemStatus => s.toJson
        case h: HeartBeat => h.toJson
        case s: Subscribe => s.toJson
        case s: SubscriptionStatus => s.toJson
        case _ => serializationError(s"failure to serialize $obj")
      }).asJsObject.fields + ("event" -> JsString(obj.event)))

    def read(json: JsValue): KrakenWsMessage =
      json.asJsObject.getFields("event") match {
        case Seq(JsString("ping")) => json.convertTo[Ping]
        case Seq(JsString("pong")) => json.convertTo[Pong]
        case Seq(JsString("systemStatus")) => json.convertTo[SystemStatus]
        case Seq(JsString("heartbeat")) => json.convertTo[HeartBeat]
        case Seq(JsString("subscribe")) => json.convertTo[Subscribe]
        case Seq(JsString("subscriptionStatus")) => json.convertTo[SubscriptionStatus]
        case _ => deserializationError(s"failure to deserialize $json")
      }
  }
}
