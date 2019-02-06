package io.ticofab.reactivekraken.websocket.v01.model

import io.ticofab.reactivekraken.websocket.v01.model.Subscription._
import io.ticofab.reactivekraken.websocket.v01.model.SubscriptionStatus._
import io.ticofab.reactivekraken.websocket.v01.model.Trade.{apply => _, unapply => _, _}
import spray.json._

/**
  * This trait can be mixed in for JSON operations on websocket messages.
  */
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
        case TopicTicker => "ticker"
        case TopicOHLC => "ohlc"
        case TopicTrade => "trade"
        case TopicBook => "book"
        case TopicSpread => "spread"
        case AllTopics => "*"
        case _ => serializationError(s"failure to serialize $obj")
      }
      JsString(string)
    }

    override def read(json: JsValue) = json match {
      case JsString(value) => value match {
        case "ticker" => TopicTicker
        case "ohlc" => TopicOHLC
        case "trade" => TopicTrade
        case "book" => TopicBook
        case "spread" => TopicSpread
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

  implicit val subscriptionStatusFormat = jsonFormat4(SubscriptionStatus.apply)

  implicit val priceAndVolumeFormat: RootJsonFormat[PriceAndVolume] = new RootJsonFormat[PriceAndVolume] {
    override def write(obj: PriceAndVolume) = serializationError("messages are not meant to be serialized")

    override def read(json: JsValue) = json match {
      case JsArray(Vector(JsString(price), JsNumber(wholeLotVolume), JsString(lotVolume))) => PriceAndVolume(price.toDouble, lotVolume.toDouble, Some(wholeLotVolume.toDouble))
      case JsArray(Vector(JsNumber(price), JsNumber(lotVolume))) => PriceAndVolume(price.toDouble, lotVolume.toDouble)
      case JsArray(Vector(JsString(price), JsString(lotVolume))) => PriceAndVolume(price.toDouble, lotVolume.toDouble)
      case _ => deserializationError(s"failure to deserialize $json")
    }
  }

  implicit val valueFormat: RootJsonFormat[Value] = new RootJsonFormat[Value] {
    override def write(obj: Value) = serializationError("messages are not meant to be serialized")

    override def read(json: JsValue) = json match {
      case JsArray(Vector(JsNumber(today), JsNumber(last24Hours))) => Value(today.toDouble, last24Hours.toDouble)
      case JsArray(Vector(JsString(today), JsString(last24Hours))) => Value(today.toDouble, last24Hours.toDouble)
      case _ => deserializationError(s"failure to deserialize $json")
    }
  }

  implicit val tickerFormat: RootJsonFormat[Ticker] = new RootJsonFormat[Ticker] {
    val defaultString         = JsString("-1.0")
    val defaultPriceAndVolume = JsArray(Vector(defaultString, defaultString, defaultString))
    val defaultValue          = JsArray(Vector(defaultString, defaultString))

    override def write(obj: Ticker) = serializationError("messages are not meant to be serialized")

    override def read(json: JsValue) = json match {
      case JsArray(Vector(JsNumber(cid), JsObject(map))) =>
        Ticker(cid.toInt,
          map.getOrElse("a", defaultPriceAndVolume).convertTo[PriceAndVolume],
          map.getOrElse("b", defaultPriceAndVolume).convertTo[PriceAndVolume],
          map.getOrElse("c", defaultPriceAndVolume).convertTo[PriceAndVolume],
          map.getOrElse("h", defaultValue).convertTo[Value],
          map.getOrElse("l", defaultValue).convertTo[Value],
          map.getOrElse("o", defaultValue).convertTo[Value],
          map.getOrElse("p", defaultValue).convertTo[Value],
          map.getOrElse("t", defaultValue).convertTo[Value],
          map.getOrElse("v", defaultValue).convertTo[Value])
      case _ => deserializationError(s"failure to deserialize $json")
    }
  }

  implicit val triggeringOrderSideFormat: RootJsonFormat[TriggeringOrderSide] = new RootJsonFormat[TriggeringOrderSide] {
    override def write(obj: TriggeringOrderSide) = serializationError("messages are not meant to be serialized")

    override def read(json: JsValue) = json match {
      case JsString(value) => value match {
        case "b" => Buy
        case "s" => Sell
        case _ => deserializationError(s"failure to deserialize $json")
      }
      case _ => deserializationError(s"failure to deserialize $json")
    }
  }

  implicit val triggeringOrderTypeFormat: RootJsonFormat[TriggeringOrderType] = new RootJsonFormat[TriggeringOrderType] {
    override def write(obj: TriggeringOrderType) = serializationError("messages are not meant to be serialized")

    override def read(json: JsValue) = json match {
      case JsString(value) => value match {
        case "m" => Market
        case "l" => Limit
        case _ => deserializationError(s"failure to deserialize $json")
      }
      case _ => deserializationError(s"failure to deserialize $json")
    }
  }

  implicit val tradeFormat: RootJsonFormat[Trade] = new RootJsonFormat[Trade] {
    override def write(obj: Trade) = serializationError("messages are not meant to be serialized")

    override def read(json: JsValue) = json match {
      case JsArray(Vector(JsString(price), JsString(volume), JsString(time), tos, tot, JsString(misc))) =>
        Trade(price.toDouble, volume.toDouble, time.toDouble.toLong, tos.convertTo[TriggeringOrderSide], tot.convertTo[TriggeringOrderType], misc)
    }
  }

  implicit val tradesFormat: RootJsonFormat[Trades] = new RootJsonFormat[Trades] {
    override def write(obj: Trades) = serializationError("messages are not meant to be serialized")

    override def read(json: JsValue) = json match {
      case JsArray(Vector(JsNumber(cid), trades)) => Trades(cid.toInt, trades.convertTo[List[Trade]])
      case _ => deserializationError(s"failure to deserialize $json")
    }

  }

  implicit val ohlcFormat: RootJsonFormat[OHLC] = new RootJsonFormat[OHLC] {
    override def write(obj: OHLC) = serializationError("messages are not meant to be serialized")

    override def read(json: JsValue) = json match {
      case JsArray(Vector(JsNumber(cid), JsArray(Vector(JsString(time), JsString(endtime), JsString(open),
      JsString(high), JsString(low), JsString(close), JsString(vwap), JsString(volume), JsNumber(count))))) =>
        OHLC(cid.toInt, time.toDouble.toLong, endtime.toDouble.toLong, open.toDouble, high.toDouble, low.toDouble, close.toDouble, vwap.toDouble, volume.toDouble, count.toInt)
      case _ => deserializationError(s"failure to deserialize $json")
    }

  }

  // format that discriminates based on an additional
  // field "type" that can either be "Cat" or "Dog"
  implicit val krakenWsMessageFormat = new RootJsonFormat[KrakenWsMessage] {
    def write(obj: KrakenWsMessage): JsValue = obj match {
      case ev: KrakenWsEvent =>
        JsObject((ev match {
          case c: Ping => c.toJson
          case p: Pong => p.toJson
          case s: SystemStatus => s.toJson
          case h: HeartBeat => h.toJson
          case s: Subscribe => s.toJson
          case s: SubscriptionStatus => s.toJson
          case _ => serializationError(s"failure to serialize $ev")
        }).asJsObject.fields + ("event" -> JsString(ev.event)))
      case _: KrakenWsMessage => serializationError("messages aren't meant to be serialized")
    }

    def read(json: JsValue): KrakenWsMessage = {
      println("received " + json)
      json match {
        case JsArray(Vector(JsNumber(cid), value)) => value match {
          case JsArray(Vector(_, _, _, _, _, _, _, _, _)) => json.convertTo[OHLC]
          case JsArray(Vector(a, b, c)) => println("got spread"); Ping()
          case JsArray(_) => json.convertTo[Trades]
          case JsObject(map) => map.toList match {
            case _ :: _ :: _ :: _ :: _ :: _ :: _ :: _ :: _ :: Nil => json.convertTo[Ticker]
            case ("as", _) :: ("bs", _) :: Nil => println("got book snapshot"); Ping()
            case ("a", _) :: Nil => println("got book update asks"); Ping()
            case ("b", _) :: Nil => println("got book update bids"); Ping()
            case _ => deserializationError(s"failure to deserialize $json")
          }
          case _ => deserializationError(s"failure to deserialize $json")
        }
        case JsArray(Vector(JsNumber(cid), JsObject(a), JsObject(b))) => println("got book update asks and bids"); Ping()
        case _ =>
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
  }
}
