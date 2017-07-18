package io.ticofab.reactivekraken.model

/**
  * Copyright 2017 Fabio Tiriticco, Fabway
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

case class Asset(aClass: String,
                 altName: String,
                 decimals: Int,
                 displayDecimals: Int)

/*
{
   "error":[

   ],
   "result":{
      "DASH":{
         "aclass":"currency",
         "altname":"DASH",
         "decimals":10,
         "display_decimals":5
      },
      "EOS":{
         "aclass":"currency",
         "altname":"EOS",
         "decimals":10,
         "display_decimals":5
      },
      "GNO":{
         "aclass":"currency",
         "altname":"GNO",
         "decimals":10,
         "display_decimals":5
      },
      "KFEE":{
         "aclass":"currency",
         "altname":"FEE",
         "decimals":2,
         "display_decimals":2
      },
      "USDT":{
         "aclass":"currency",
         "altname":"USDT",
         "decimals":8,
         "display_decimals":4
      },
      "XDAO":{
         "aclass":"currency",
         "altname":"DAO",
         "decimals":10,
         "display_decimals":3
      },
      "XETC":{
         "aclass":"currency",
         "altname":"ETC",
         "decimals":10,
         "display_decimals":5
      },
      "XETH":{
         "aclass":"currency",
         "altname":"ETH",
         "decimals":10,
         "display_decimals":5
      },
      "XICN":{
         "aclass":"currency",
         "altname":"ICN",
         "decimals":10,
         "display_decimals":5
      },
      "XLTC":{
         "aclass":"currency",
         "altname":"LTC",
         "decimals":10,
         "display_decimals":5
      },
      "XMLN":{
         "aclass":"currency",
         "altname":"MLN",
         "decimals":10,
         "display_decimals":5
      },
      "XNMC":{
         "aclass":"currency",
         "altname":"NMC",
         "decimals":10,
         "display_decimals":5
      },
      "XREP":{
         "aclass":"currency",
         "altname":"REP",
         "decimals":10,
         "display_decimals":5
      },
      "XXBT":{
         "aclass":"currency",
         "altname":"XBT",
         "decimals":10,
         "display_decimals":5
      },
      "XXDG":{
         "aclass":"currency",
         "altname":"XDG",
         "decimals":8,
         "display_decimals":2
      },
      "XXLM":{
         "aclass":"currency",
         "altname":"XLM",
         "decimals":8,
         "display_decimals":5
      },
      "XXMR":{
         "aclass":"currency",
         "altname":"XMR",
         "decimals":10,
         "display_decimals":5
      },
      "XXRP":{
         "aclass":"currency",
         "altname":"XRP",
         "decimals":8,
         "display_decimals":5
      },
      "XXVN":{
         "aclass":"currency",
         "altname":"XVN",
         "decimals":4,
         "display_decimals":2
      },
      "XZEC":{
         "aclass":"currency",
         "altname":"ZEC",
         "decimals":10,
         "display_decimals":5
      },
      "ZCAD":{
         "aclass":"currency",
         "altname":"CAD",
         "decimals":4,
         "display_decimals":2
      },
      "ZEUR":{
         "aclass":"currency",
         "altname":"EUR",
         "decimals":4,
         "display_decimals":2
      },
      "ZGBP":{
         "aclass":"currency",
         "altname":"GBP",
         "decimals":4,
         "display_decimals":2
      },
      "ZJPY":{
         "aclass":"currency",
         "altname":"JPY",
         "decimals":2,
         "display_decimals":0
      },
      "ZKRW":{
         "aclass":"currency",
         "altname":"KRW",
         "decimals":2,
         "display_decimals":0
      },
      "ZUSD":{
         "aclass":"currency",
         "altname":"USD",
         "decimals":4,
         "display_decimals":2
      }
   }
}
 */
