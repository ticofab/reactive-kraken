package io.ticofab.reactivekraken.websocket.v01.model

import io.ticofab.reactivekraken.websocket.v01.model.Book.{Snapshot, Update}
import io.ticofab.reactivekraken.websocket.v01.model.Subscription._
import org.scalatest.WordSpec
import spray.json._

class KrakenWsMessagesJsonSpec extends WordSpec with KrakenWsMessagesJson {

  "A KrakenWsMessage" should {

    "Convert SystemStatus messages properly" in {
      val jsonStr =
        """
          |{"connectionID":17865194737501073182,"event":"systemStatus","status":"online","version":"0.1.1"}
        """.stripMargin

      val systemStatus = jsonStr.parseJson.convertTo[SystemStatus]
      assert(systemStatus.connectionID == BigInt("17865194737501073182"))
    }

    "Convert correctly a HearBeat event" in {
      val jsonStr =
        """
          |{"event":"heartbeat"}
        """.stripMargin
      val hb = jsonStr.parseJson.convertTo[HeartBeat]
      assert(hb.event == "heartbeat")
    }

    "Convert correctly a currency pair" in {
      val cp = CurrencyPair("EUR", "ETH")
      cp.toJson match {
        case JsString(value) => assert(value == "EUR/ETH")
        case _ => fail("wrong serialization")
      }

      val jsonStr = JsString("AAA/BBB")
      val cpP = jsonStr.convertTo[CurrencyPair]
      assert(cpP.first == "AAA" && cpP.second == "BBB")

      val cpList = List(CurrencyPair("AAA", "BBB"), CurrencyPair("DDD", "EEE"))
      cpList.toJson match {
        case JsArray(Vector(p1, _)) =>
          p1 match {
            case JsString("AAA/BBB") => succeed
            case _ => fail
          }
        case _ => fail("wrong number of pairs")
      }
    }

    "Convert correctly a Subscribe message" in {
      val json = Subscription(AllTopics, Some(TenThousandEightyMinutes), Some(OneHundred)).toJson
      val fields = json.asJsObject.fields
      assert(fields("name") == JsString("*"))
      assert(fields("interval") == JsNumber(10080))
      assert(fields("depth") == JsNumber(100))

      val json2 = Subscription(TopicTicker).toJson
      val fields2 = json2.asJsObject.fields
      assert(!fields2.contains("interval"))
      assert(fields2("name") == JsString("ticker"))
      assert(!fields2.contains("depth"))

      val jsonStr =
        """
          | {
          |  "event": "subscribe",
          |  "pair": [
          |    "XBT/EUR"
          |  ],
          |  "subscription": {
          |    "name": "ohlc",
          |    "interval": 5
          |  }
          | }
        """.stripMargin
      val sub = jsonStr.parseJson.convertTo[Subscribe]
      assert(sub.pair.size == 1)
      assert(sub.pair.head == CurrencyPair("XBT", "EUR"))
      assert(sub.reqId.isEmpty)
      assert(sub.subscription.name == TopicOHLC)
      assert(sub.subscription.interval.contains(FiveMinutes))
      assert(sub.subscription.depth.isEmpty)

      val jsonStr2 =
        """
          |{
          |  "event": "subscribe",
          |  "pair": [
          |    "XBT/USD","XBT/EUR"
          |  ],
          |  "subscription": {
          |    "name": "ticker"
          |  }
          | }
        """.stripMargin
      val sub2 = jsonStr2.parseJson.convertTo[Subscribe]
      assert(sub2.pair.size == 2)
      assert(sub2.pair.head == CurrencyPair("XBT", "USD"))
      assert(sub2.reqId.isEmpty)
      assert(sub2.subscription.name == TopicTicker)
      assert(sub2.subscription.interval.isEmpty)
      assert(sub2.subscription.depth.isEmpty)

    }

    "Convert a Ticker correctly" in {
      val jsonStr =
        """
          |[106,{"a":["90.75000",2,"2.84179191"],"b":["90.72000",4,"4.00000000"],"c":["90.75000","0.25743408"],"h":["93.50000","93.50000"],"l":["88.00000","88.00000"],"o":["93.10000","92.48000"],"p":["89.83517","90.09955"],"t":[7606,9443],"v":["79663.52063690","87512.86337865"]}]
        """.stripMargin
      val ticker = jsonStr.parseJson.convertTo[Ticker]
      assert(ticker.ask.wholeLotVolume.isDefined)
    }

    "Convert a Trades object correctly" in {
      val jsonStr =
        """
          |[105,[["90.93000","0.37800000","1549470888.352063","s","m",""]]]
        """.stripMargin
      val trades = jsonStr.parseJson.convertTo[Trades]
      assert(trades.channelId == 105)
      assert(trades.trades.size == 1)
    }

    "Convert OHLC objects correctly" in {
      val jsonStr =
        """
          |[108,["1549472678.449328","1549472700.000000","90.51000","90.52000","90.51000","90.52000","90.51901","4.00003466",2]]
        """.stripMargin
      val ohlc = jsonStr.parseJson.convertTo[OHLC]
      assert(ohlc.channelId == 108)
      assert(ohlc.endtime == 1549472700L)
    }

    "Convert Spread correctly" in {
      val jsonStr =
        """
          |[107,["90.28000","90.38000","1549473455.552417"]]
        """.stripMargin
      val spread = jsonStr.parseJson.convertTo[Spread]
      assert(spread.channelId == 107)
      assert(spread.timestamp == 1549473455L)
    }

    "Convert Book correctly" in {
      val jsonBookSnapshotStr =
        """
          |[
          |  0,
          |  {
          |    "as": [
          |      [
          |          "5541.30000",
          |          "2.50700000",
          |          "1534614248.123678"
          |      ],
          |      [
          |          "5541.80000",
          |          "0.33000000",
          |          "1534614098.345543"
          |      ],
          |      [
          |          "5542.70000",
          |          "0.64700000",
          |          "1534614244.654432"
          |      ]
          |    ],
          |    "bs": [
          |      [
          |          "5541.20000",
          |          "1.52900000",
          |          "1534614248.765567"
          |      ],
          |      [
          |          "5539.90000",
          |          "0.30000000",
          |          "1534614241.769870"
          |      ],
          |      [
          |          "5539.50000",
          |          "5.00000000",
          |          "1534613831.243486"
          |      ]
          |    ]
          |  }
          | ]
        """.stripMargin
      val book = jsonBookSnapshotStr.parseJson.convertTo[Book]
      assert(book.channelId == 0)
      assert(book.bookMessageType == Snapshot)
      assert(book.bids.size == 3)
      assert(book.bids.last.timestamp == 1534613831L)

      val jsonBookUpdateStr1 =
        """
          |[
          |  1234,
          |  {"a": [
          |    [
          |      "5541.30000",
          |      "2.50700000",
          |      "1534614248.456738"
          |    ],
          |    [
          |      "5542.50000",
          |      "0.40100000",
          |      "1534614248.456738"
          |    ]
          |  ]}
          | ]
        """.stripMargin
      val bookU1 = jsonBookUpdateStr1.parseJson.convertTo[Book]
      assert(bookU1.channelId == 1234)
      assert(bookU1.bookMessageType == Update)
      assert(bookU1.bids.isEmpty)
      assert(bookU1.asks.size == 2)

      val jsonBookUpdateStr2 =
        """
          |[
          |  1234,
          |  {"a": [
          |    [
          |      "5541.30000",
          |      "2.50700000",
          |      "1534614248.456738"
          |    ],
          |    [
          |      "5542.50000",
          |      "0.40100000",
          |      "1534614248.456738"
          |    ]
          |  ]
          |  },
          |  {"b": [
          |    [
          |      "5541.30000",
          |      "0.00000000",
          |      "1534614335.345903"
          |    ]
          |  ]
          |  }
          | ]
        """.stripMargin
      val bookU2 = jsonBookUpdateStr2.parseJson.convertTo[Book]
      assert(bookU2.channelId == 1234)
      assert(bookU2.asks.size == 2)
      assert(bookU2.bids.size == 1)
    }
  }
}
