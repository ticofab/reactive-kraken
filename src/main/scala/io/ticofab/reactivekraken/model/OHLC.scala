package io.ticofab.reactivekraken.model


case class OHLCRow(time: Long, open: String, high: String, low: String, close: String, vwap: String, volume: String, count: Int)

case class OHLC(ohlcRows: List[OHLCRow], last: Long)


/*

{
  "error": [],
  "result": {
    "XETHZEUR": [
      [
        1549077540,
        "92.75",
        "92.75",
        "92.75",
        "92.75",
        "0.00",
        "0.00000000",
        0
      ],
      [
        1549077600,
        "92.75",
        "92.75",
        "92.75",
        "92.75",
        "0.00",
        "0.00000000",
        0
      ],
      [
        1549109340,
        "93.51",
        "93.51",
        "93.40",
        "93.40",
        "93.40",
        "96.93735225",
        2
      ],
      [
        1549109400,
        "93.40",
        "93.40",
        "93.39",
        "93.39",
        "93.39",
        "0.82500000",
        1
      ],

        ...... it keeps going like this ......


      [
        1549112640,
        "93.33",
        "93.33",
        "93.33",
        "93.33",
        "0.00",
        "0.00000000",
        0
      ],
      [
        1549120680,
        "93.38",
        "93.38",
        "93.36",
        "93.36",
        "93.36",
        "1.00080000",
        1
      ]
    ],
    "last": 1549120620
  }
}

 */
