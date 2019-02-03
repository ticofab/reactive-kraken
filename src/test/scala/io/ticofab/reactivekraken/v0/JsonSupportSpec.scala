package io.ticofab.reactivekraken.v0

import io.ticofab.reactivekraken.v0.api.JsonSupport
import io.ticofab.reactivekraken.v0.model._
import org.scalatest.WordSpec

/**
  * Copyright 2017-2019 Fabio Tiriticco, Fabway
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

import spray.json._

class JsonSupportSpec extends WordSpec with JsonSupport {

  "The JsonSupport logic" should {
    "Parse OHLC correctly" in {

      val jsonStr =
        """
          | {
          |    "XETHZEUR": [
          |      [
          |        1549077540,
          |        "92.75",
          |        "92.75",
          |        "92.75",
          |        "92.75",
          |        "0.00",
          |        "0.00000000",
          |        0
          |      ],
          |      [
          |        1549077600,
          |        "92.75",
          |        "92.75",
          |        "92.75",
          |        "92.75",
          |        "0.00",
          |        "0.00000000",
          |        0
          |      ],
          |      [
          |        1549109340,
          |        "93.51",
          |        "93.51",
          |        "93.40",
          |        "93.40",
          |        "93.40",
          |        "96.93735225",
          |        2
          |      ],
          |      [
          |        1549109400,
          |        "93.40",
          |        "93.40",
          |        "93.39",
          |        "93.39",
          |        "93.39",
          |        "0.82500000",
          |        1
          |      ],
          |      [
          |        1549112640,
          |        "93.33",
          |        "93.33",
          |        "93.33",
          |        "93.33",
          |        "0.00",
          |        "0.00000000",
          |        0
          |      ],
          |      [
          |        1549120680,
          |        "93.38",
          |        "93.38",
          |        "93.36",
          |        "93.36",
          |        "93.36",
          |        "1.00080000",
          |        1
          |      ]
          |    ],
          |    "last": 1549120620
          | }
        """.stripMargin

      val parsed = jsonStr.parseJson.convertTo[OHLC]
      assert(parsed.ohlcRows.size == 6)
    }

    "Parse OrderBook correctly" in {
      val jsonStr =
        """
          |
          |{
          |    "XETHZEUR": {
          |      "asks": [
          |        [
          |          "93.01000",
          |          "49.210",
          |          1549132309
          |        ],
          |        [
          |          "89.90000",
          |          "1.100",
          |          1549114562
          |        ],
          |        [
          |          "89.88000",
          |          "0.557",
          |          1549114500
          |        ]
          |      ],
          |      "bids": [
          |        [
          |          "89.90000",
          |          "1.100",
          |          1549114562
          |        ],
          |        [
          |          "89.88000",
          |          "0.557",
          |          1549114500
          |        ]
          |      ]
          |    }
          | }
          |
          |
          |
        """.stripMargin

      val parsed = jsonStr.parseJson
      val book = parsed.convertTo[OrderBook]
      assert(book.asks.size == 3)
      assert(book.bids.size == 2)
    }

    "Parse RecentTraes correctly" in {
      val jsonStr =
        """
          | {
          |    "XETHZEUR": [
          |      [
          |        "93.50000",
          |        "2.00000000",
          |        1549108576.2065,
          |        "b",
          |        "l",
          |        ""
          |      ],
          |      [
          |        "93.50000",
          |        "33.18559893",
          |        1549108576.3651,
          |        "s",
          |        "l",
          |        ""
          |      ],
          |      [
          |        "93.18000",
          |        "0.03530351",
          |        1549134324.3117,
          |        "s",
          |        "m",
          |        ""
          |      ],
          |      [
          |        "93.18000",
          |        "0.00009125",
          |        1549134324.314,
          |        "s",
          |        "m",
          |        ""
          |      ]
          |    ],
          |    "last": "1549134324313951917"
          |  }
        """.stripMargin

      val parsed = jsonStr.parseJson
      val trades = parsed.convertTo[RecentTrades]
      assert(trades.trades.size == 4)
    }

    "Parse RecentSpreads correctly" in {
      val jsonStr =
        """
          |{
          |    "XETHZEUR": [
          |      [
          |        1549133893,
          |        "93.14000",
          |        "93.22000"
          |      ],
          |      [
          |        1549133895,
          |        "93.14000",
          |        "93.20000"
          |      ],
          |      [
          |        1549133899,
          |        "93.14000",
          |        "93.23000"
          |      ],
          |      [
          |        1549135335,
          |        "93.23000",
          |        "93.25000"
          |      ]
          |    ],
          |    "last": 1549135335
          |  }
          |
        """.stripMargin
      val spreads = jsonStr.parseJson.convertTo[RecentSpreads]
      assert(spreads.spreads.size == 4)
    }

    "Parse a CLosedOrder correctly" in {
      val jsonStr =
        """
          | {
          |    "closed": {
          |      "OE5A2O-GS6V6-3XMSBM": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1547234427.4382,
          |        "closetm": 1547247575.0205,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "stop-loss",
          |          "price": "108.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 5.00000000 ETHEUR @ stop loss 108.11",
          |          "close": ""
          |        },
          |        "vol": "5.00000000",
          |        "vol_exec": "5.00000000",
          |        "cost": "539.20",
          |        "fee": "1.40",
          |        "price": "107.84",
          |        "stopprice": "108.09",
          |        "limitprice": "0.00000",
          |        "misc": "stopped",
          |        "oflags": "fciq"
          |      },
          |      "OOISRZ-ET3OT-7KSHMA": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1547220086.1378,
          |        "closetm": 1547234414.4717,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "stop-loss",
          |          "price": "109.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 5.00000000 ETHEUR @ stop loss 109.11",
          |          "close": ""
          |        },
          |        "vol": "5.00000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OUXA3P-Y6VV3-OWV3NK": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1547216462.024,
          |        "closetm": 1547220074.6302,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "stop-loss",
          |          "price": "108.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 5.00000000 ETHEUR @ stop loss 108.11",
          |          "close": ""
          |        },
          |        "vol": "5.00000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "ODO3U5-2W4YW-72RPXZ": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1547215922.0313,
          |        "closetm": 1547216442.7745,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "stop-loss",
          |          "price": "106.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 5.00000000 ETHEUR @ stop loss 106.11",
          |          "close": ""
          |        },
          |        "vol": "5.00000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OUBW3Z-4RFXZ-5HPBDM": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1547110380.8606,
          |        "closetm": 1547136626.3109,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "110.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 1.00000000 ETHEUR @ limit 110.11",
          |          "close": ""
          |        },
          |        "vol": "1.00000000",
          |        "vol_exec": "1.00000000",
          |        "cost": "110.11",
          |        "fee": "0.17",
          |        "price": "110.11",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O3TH6Q-JEXVE-W44BTA": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1544363247.1189,
          |        "closetm": 1544544635.5213,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "75.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 1.00000000 ETHEUR @ limit 75.11",
          |          "close": ""
          |        },
          |        "vol": "1.00000000",
          |        "vol_exec": "1.00000000",
          |        "cost": "75.11",
          |        "fee": "0.12",
          |        "price": "75.11",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OPXSQ4-2UR2G-H2U7OT": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1543629459.2655,
          |        "closetm": 1544026687.8192,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "91.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 2.00000000 ETHEUR @ limit 91.11",
          |          "close": ""
          |        },
          |        "vol": "2.00000000",
          |        "vol_exec": "2.00000000",
          |        "cost": "182.22",
          |        "fee": "0.29",
          |        "price": "91.11",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OHL6RR-BRWZR-V37RSX": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1543082793.4867,
          |        "closetm": 1543082793.4996,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "105.50",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 2.00000000 ETHEUR @ limit 105.50",
          |          "close": ""
          |        },
          |        "vol": "2.00000000",
          |        "vol_exec": "2.00000000",
          |        "cost": "211.00",
          |        "fee": "0.54",
          |        "price": "105.50",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OS6ZVN-GPY4Y-425ILG": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1542782981.5234,
          |        "closetm": 1542891821.6494,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "111.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 5.00000000 ETHEUR @ limit 111.11",
          |          "close": ""
          |        },
          |        "vol": "5.00000000",
          |        "vol_exec": "5.00000000",
          |        "cost": "555.55",
          |        "fee": "0.88",
          |        "price": "111.11",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O7HLQ3-UI4WQ-X2ONFI": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1542806143.5671,
          |        "closetm": 1542806231.2043,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "118.78",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 5.00000000 ETHEUR @ limit 118.78",
          |          "close": ""
          |        },
          |        "vol": "5.00000000",
          |        "vol_exec": "5.00000000",
          |        "cost": "593.90",
          |        "fee": "0.95",
          |        "price": "118.78",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OP7FGH-AGRVT-F5LRRG": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1542729265.7169,
          |        "closetm": 1542729265.7209,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "124.51",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 5.00000000 ETHEUR @ limit 124.51",
          |          "close": ""
          |        },
          |        "vol": "5.00000000",
          |        "vol_exec": "5.00000000",
          |        "cost": "622.30",
          |        "fee": "1.61",
          |        "price": "124.46",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OZFIIC-SSV5C-TSQNHX": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1542624534.8773,
          |        "closetm": 1542625493.0173,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "136.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 10.00000000 ETHEUR @ limit 136.11",
          |          "close": ""
          |        },
          |        "vol": "10.00000000",
          |        "vol_exec": "10.00000000",
          |        "cost": "1361.10",
          |        "fee": "2.17",
          |        "price": "136.11",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OVDVDX-YPB7Y-ZRL6NJ": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1520150520.4909,
          |        "closetm": 1520362889.784,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "158.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 1.00000000 LTCEUR @ limit 158.21",
          |          "close": ""
          |        },
          |        "vol": "1.00000000",
          |        "vol_exec": "1.00000000",
          |        "cost": "158.21",
          |        "fee": "0.25",
          |        "price": "158.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "ORCQ3Q-7EI3S-PP6QNV": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1519722996.6737,
          |        "closetm": 1519723003.0544,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "723.10",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.20000000 ETHEUR @ limit 723.10",
          |          "close": ""
          |        },
          |        "vol": "0.20000000",
          |        "vol_exec": "0.20000000",
          |        "cost": "144.62",
          |        "fee": "0.23",
          |        "price": "723.10",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OIG55L-L5GBZ-7NX3JE": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1519478648.3419,
          |        "closetm": 1519478648.346,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "677.88",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.20000000 ETHEUR @ limit 677.88",
          |          "close": ""
          |        },
          |        "vol": "0.20000000",
          |        "vol_exec": "0.20000000",
          |        "cost": "135.46",
          |        "fee": "0.35",
          |        "price": "677.30",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O4AASM-IJHYG-O32ERT": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1519380954.3001,
          |        "closetm": 1519386358.4322,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "710.27",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.20000000 ETHEUR @ limit 710.27",
          |          "close": ""
          |        },
          |        "vol": "0.20000000",
          |        "vol_exec": "0.20000000",
          |        "cost": "142.05",
          |        "fee": "0.22",
          |        "price": "710.27",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "ONATS5-FRLNP-M3EBPD": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1519231220.6727,
          |        "closetm": 1519231256.4181,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "674.00",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.05000000 ETHEUR @ limit 674.00",
          |          "close": ""
          |        },
          |        "vol": "0.05000000",
          |        "vol_exec": "0.05000000",
          |        "cost": "33.70",
          |        "fee": "0.05",
          |        "price": "674.00",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OQTZRI-SBYME-2KBN5L": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1519227462.7982,
          |        "closetm": 1519227464.3242,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "684.63",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.05000000 ETHEUR @ limit 684.63",
          |          "close": ""
          |        },
          |        "vol": "0.05000000",
          |        "vol_exec": "0.05000000",
          |        "cost": "34.23",
          |        "fee": "0.05",
          |        "price": "684.63",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "ONIWNU-SMPZY-JVVYJL": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1519166988.699,
          |        "closetm": 1519172131.444,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "706.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.10000000 ETHEUR @ limit 706.21",
          |          "close": ""
          |        },
          |        "vol": "0.10000000",
          |        "vol_exec": "0.10000000",
          |        "cost": "70.62",
          |        "fee": "0.11",
          |        "price": "706.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OTSSIN-Q3RMD-4ZIP3I": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1519166970.9061,
          |        "closetm": 1519167746.9586,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "718.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.10000000 ETHEUR @ limit 718.21",
          |          "close": ""
          |        },
          |        "vol": "0.10000000",
          |        "vol_exec": "0.10000000",
          |        "cost": "71.82",
          |        "fee": "0.11",
          |        "price": "718.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OSESEE-RYXBP-WAISPS": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1519166943.8906,
          |        "closetm": 1519167222.4051,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "726.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.10000000 ETHEUR @ limit 726.21",
          |          "close": ""
          |        },
          |        "vol": "0.10000000",
          |        "vol_exec": "0.10000000",
          |        "cost": "72.62",
          |        "fee": "0.11",
          |        "price": "726.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OVPTPT-NRC56-7TAK7F": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518936287.0186,
          |        "closetm": 1518936287.0358,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "759.98",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.20000000 ETHEUR @ limit 759.98",
          |          "close": ""
          |        },
          |        "vol": "0.20000000",
          |        "vol_exec": "0.20000000",
          |        "cost": "151.99",
          |        "fee": "0.39",
          |        "price": "759.98",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OYKO3A-DZOAT-RUB22B": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518680641.5471,
          |        "closetm": 1518680641.5505,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "754.29",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.05000000 ETHEUR @ limit 754.29",
          |          "close": ""
          |        },
          |        "vol": "0.05000000",
          |        "vol_exec": "0.05000000",
          |        "cost": "37.71",
          |        "fee": "0.09",
          |        "price": "754.29",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O6OGLZ-ORG3V-6CHHU2": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518675974.5416,
          |        "closetm": 1518675974.547,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "187.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.14740097 LTCEUR @ limit 187.21",
          |          "close": ""
          |        },
          |        "vol": "0.14740097",
          |        "vol_exec": "0.14740097",
          |        "cost": "27.59",
          |        "fee": "0.07",
          |        "price": "187.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OO2RKV-VP7IP-ANNJVD": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518652712.2185,
          |        "closetm": 1518653913.1539,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "740.77",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.04518847 ETHEUR @ limit 740.77",
          |          "close": ""
          |        },
          |        "vol": "0.04518847",
          |        "vol_exec": "0.04518847",
          |        "cost": "33.47",
          |        "fee": "0.05",
          |        "price": "740.76",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OIGV76-BOZDW-3QOOFP": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518616924.0197,
          |        "closetm": 1518618835.5222,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "BCHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "1050.2",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.03000000 BCHEUR @ limit 1050.2",
          |          "close": ""
          |        },
          |        "vol": "0.03000000",
          |        "vol_exec": "0.03000000",
          |        "cost": "31.5",
          |        "fee": "0",
          |        "price": "1050.2",
          |        "stopprice": "0.000000",
          |        "limitprice": "0.000000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OZ2IHN-RNLQS-IBX2FF": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518616476.7889,
          |        "closetm": 1518616654.3512,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "XBTEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "7515.0",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.00992200 XBTEUR @ limit 7515.0",
          |          "close": ""
          |        },
          |        "vol": "0.00992200",
          |        "vol_exec": "0.00992200",
          |        "cost": "74.5",
          |        "fee": "0.1",
          |        "price": "7515.0",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OG4BWV-VHQWT-XXJUDJ": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518616088.1262,
          |        "closetm": 1518616099.3582,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "174.85",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.25000000 LTCEUR @ limit 174.85",
          |          "close": ""
          |        },
          |        "vol": "0.25000000",
          |        "vol_exec": "0.25000000",
          |        "cost": "43.71",
          |        "fee": "0.06",
          |        "price": "174.85",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OZJ2OL-ZHBUF-HDHRXY": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518605386.4577,
          |        "closetm": 1518606718.6119,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "159.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.50000000 LTCEUR @ limit 159.21",
          |          "close": ""
          |        },
          |        "vol": "0.50000000",
          |        "vol_exec": "0.50000000",
          |        "cost": "79.60",
          |        "fee": "0.12",
          |        "price": "159.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OYNFML-F4LGY-WOJCCE": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518504973.6586,
          |        "closetm": 1518571151.5842,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "133.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 1.00000000 LTCEUR @ limit 133.21",
          |          "close": ""
          |        },
          |        "vol": "1.00000000",
          |        "vol_exec": "1.00000000",
          |        "cost": "133.21",
          |        "fee": "0.21",
          |        "price": "133.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OJJROE-QUYSR-75P32J": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1517575609.275,
          |        "closetm": 1518562723.4912,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "EOSETH",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "0.009000",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 5.20000000 EOSETH @ limit 0.009000",
          |          "close": ""
          |        },
          |        "vol": "5.20000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000000",
          |        "fee": "0.00000000",
          |        "price": "0.00000000",
          |        "stopprice": "0.00000000",
          |        "limitprice": "0.00000000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O6XYZK-JIW2E-YA4GZ3": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518560064.0664,
          |        "closetm": 1518560064.0757,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "680.00",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.30000000 ETHEUR @ limit 680.00",
          |          "close": ""
          |        },
          |        "vol": "0.30000000",
          |        "vol_exec": "0.30000000",
          |        "cost": "203.97",
          |        "fee": "0.53",
          |        "price": "679.92",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OSCOBO-HWCNU-5WBMUN": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1518559972.3417,
          |        "closetm": 1518560048.2058,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "679.60",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.30000000 ETHEUR @ limit 679.60",
          |          "close": ""
          |        },
          |        "vol": "0.30000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O5FSPM-WB5SE-JA6XOJ": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518559933.7039,
          |        "closetm": 1518559933.7105,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "128.00",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 1.00000000 LTCEUR @ limit 128.00",
          |          "close": ""
          |        },
          |        "vol": "1.00000000",
          |        "vol_exec": "1.00000000",
          |        "cost": "128.01",
          |        "fee": "0.33",
          |        "price": "128.01",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fcib"
          |      },
          |      "OZYYKY-W453G-AK3VMC": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1518202298.8875,
          |        "closetm": 1518504989.8184,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "140.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 1.00000000 LTCEUR @ limit 140.21",
          |          "close": ""
          |        },
          |        "vol": "1.00000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OUNUXI-VCSG3-XKHLTX": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518169251.8964,
          |        "closetm": 1518169296.5655,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "678.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.10000000 ETHEUR @ limit 678.21",
          |          "close": ""
          |        },
          |        "vol": "0.10000000",
          |        "vol_exec": "0.10000000",
          |        "cost": "67.82",
          |        "fee": "0.10",
          |        "price": "678.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OQTGIF-KG7U4-7TAIH4": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518164287.7315,
          |        "closetm": 1518164287.7394,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "XLMXBT",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "0.00004310",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 696.05568000 XLMXBT @ limit 0.00004310",
          |          "close": ""
          |        },
          |        "vol": "696.05568000",
          |        "vol_exec": "696.05568000",
          |        "cost": "0.03000000",
          |        "fee": "0.00007800",
          |        "price": "0.00004310",
          |        "stopprice": "0.000000000",
          |        "limitprice": "0.000000000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OR7G66-KMFMV-JDLXBK": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1518164223.0996,
          |        "closetm": 1518164223.1028,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "XBTEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "6500.1",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.03000000 XBTEUR @ limit 6500.1",
          |          "close": ""
          |        },
          |        "vol": "0.03000000",
          |        "vol_exec": "0.03000000",
          |        "cost": "194.6",
          |        "fee": "0.5",
          |        "price": "6489.2",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OSRKFW-3AYXS-DXAGE6": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1517924733.2389,
          |        "closetm": 1517924736.4679,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "558.89",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.10000000 ETHEUR @ limit 558.89",
          |          "close": ""
          |        },
          |        "vol": "0.10000000",
          |        "vol_exec": "0.10000000",
          |        "cost": "55.88",
          |        "fee": "0.08",
          |        "price": "558.89",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OYSWQR-6Y4E6-ZI7BEE": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1517906342.1408,
          |        "closetm": 1517906342.1464,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "458.88",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.10000000 ETHEUR @ limit 458.88",
          |          "close": ""
          |        },
          |        "vol": "0.10000000",
          |        "vol_exec": "0.10000000",
          |        "cost": "45.66",
          |        "fee": "0.11",
          |        "price": "456.60",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OSAVA7-URYKA-QIE3ZJ": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1517870384.6284,
          |        "closetm": 1517870384.6364,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "573.24",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.20000000 ETHEUR @ limit 573.24",
          |          "close": ""
          |        },
          |        "vol": "0.20000000",
          |        "vol_exec": "0.20000000",
          |        "cost": "114.44",
          |        "fee": "0.29",
          |        "price": "572.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OOAXF6-AETNM-253P6S": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1517870117.6144,
          |        "closetm": 1517870354.248,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "XBTEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "5646.8",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.01000000 XBTEUR @ limit 5646.8",
          |          "close": ""
          |        },
          |        "vol": "0.01000000",
          |        "vol_exec": "0.01000000",
          |        "cost": "56.4",
          |        "fee": "0",
          |        "price": "5646.8",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OPVXCS-CNSSL-F3XHBP": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1517740659.1358,
          |        "closetm": 1517740755.825,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "722.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.10000000 ETHEUR @ limit 722.21",
          |          "close": ""
          |        },
          |        "vol": "0.10000000",
          |        "vol_exec": "0.10000000",
          |        "cost": "72.22",
          |        "fee": "0.11",
          |        "price": "722.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O2V7UH-KHCJG-ITC2IT": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1517726536.36,
          |        "closetm": 1517736978.9644,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "140.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 1.00000000 LTCEUR @ limit 140.21",
          |          "close": ""
          |        },
          |        "vol": "1.00000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OGT4YA-ENXYY-YBDLDI": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1516956688.1774,
          |        "closetm": 1517479960.1059,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "120.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 1.00000000 LTCEUR @ limit 120.21",
          |          "close": ""
          |        },
          |        "vol": "1.00000000",
          |        "vol_exec": "1.00000000",
          |        "cost": "120.21",
          |        "fee": "0.19",
          |        "price": "120.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O63GFQ-HOYAN-BDGNCC": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1516658157.9523,
          |        "closetm": 1517478284.1058,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "LTCEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "122.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 1.00000000 LTCEUR @ limit 122.21",
          |          "close": ""
          |        },
          |        "vol": "1.00000000",
          |        "vol_exec": "1.00000000",
          |        "cost": "122.21",
          |        "fee": "0.19",
          |        "price": "122.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O2DCB3-6XDSE-56QRR5": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1516781114.4755,
          |        "closetm": 1517134886.3957,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "buy",
          |          "ordertype": "limit",
          |          "price": "792.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "buy 0.30000000 ETHEUR @ limit 792.21",
          |          "close": ""
          |        },
          |        "vol": "0.30000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OKIBE7-HJ6XE-3FNRNW": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1516804483.7436,
          |        "closetm": 1516847794.5001,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "872.21",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.10000000 ETHEUR @ limit 872.21",
          |          "close": ""
          |        },
          |        "vol": "0.10000000",
          |        "vol_exec": "0.10000000",
          |        "cost": "87.22",
          |        "fee": "0.00000",
          |        "price": "872.21",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "O6RFG4-J3ASU-JKLMHV": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1516804454.0347,
          |        "closetm": 1516843811.7166,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "869.33",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.10000000 ETHEUR @ limit 869.33",
          |          "close": ""
          |        },
          |        "vol": "0.10000000",
          |        "vol_exec": "0.10000000",
          |        "cost": "86.93",
          |        "fee": "0.00000",
          |        "price": "869.33",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OVMPQ5-CRFP6-O2M35C": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1516666621.3743,
          |        "closetm": 1516804465.4403,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "869.33",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.20000000 ETHEUR @ limit 869.33",
          |          "close": ""
          |        },
          |        "vol": "0.20000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      }
          |    },
          |    "count": 363
          |  }
        """.stripMargin

      val parsed = jsonStr.parseJson.convertTo[ClosedOrder]
      assert(parsed.closed.getOrElse(Map()).size == 50)
    }
  }

}
