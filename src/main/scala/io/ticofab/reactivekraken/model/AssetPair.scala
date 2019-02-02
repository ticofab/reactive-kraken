package io.ticofab.reactivekraken.model

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

case class AssetPair(altName: String,
                     aClassBase: String,
                     base: String,
                     aClassQuote: String,
                     quote: String,
                     lot: String,
                     pairDecimals: Int,
                     lotDecimals: Int,
                     lotMultiplier: Int,
                     leverageBuy: List[Int],
                     leverageSell: List[Int],
                     fees: List[List[Double]],
                     feesMaker: List[List[Double]],
                     feeVolumeCurrency: String,
                     marginCall: Int,
                     marginStop: Int)

/*
{
   "error":[

   ],
   "result":{
      "XETHZEUR":{
         "altname":"ETHEUR",
         "aclass_base":"currency",
         "base":"XETH",
         "aclass_quote":"currency",
         "quote":"ZEUR",
         "lot":"unit",
         "pair_decimals":5,
         "lot_decimals":8,
         "lot_multiplier":1,
         "leverage_buy":[
            2,
            3,
            4,
            5
         ],
         "leverage_sell":[
            2,
            3,
            4,
            5
         ],
         "fees":[
            [
               0,
               0.26
            ],
            [
               50000,
               0.24
            ],
            [
               100000,
               0.22
            ],
            [
               250000,
               0.2
            ],
            [
               500000,
               0.18
            ],
            [
               1000000,
               0.16
            ],
            [
               2500000,
               0.14
            ],
            [
               5000000,
               0.12
            ],
            [
               10000000,
               0.1
            ]
         ],
         "fees_maker":[
            [
               0,
               0.16
            ],
            [
               50000,
               0.14
            ],
            [
               100000,
               0.12
            ],
            [
               250000,
               0.1
            ],
            [
               500000,
               0.08
            ],
            [
               1000000,
               0.06
            ],
            [
               2500000,
               0.04
            ],
            [
               5000000,
               0.02
            ],
            [
               10000000,
               0
            ]
         ],
         "fee_volume_currency":"ZUSD",
         "margin_call":80,
         "margin_stop":40
      }
   }
}
 */
