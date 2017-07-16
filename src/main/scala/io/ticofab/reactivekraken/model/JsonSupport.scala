package io.ticofab.reactivekraken.model

import spray.json.DefaultJsonProtocol

/**
  * Created by Fabio Tiriticco on 16/07/2017.
  */
trait JsonSupport extends DefaultJsonProtocol {
  implicit val assetFormat = jsonFormat(Asset, "aclass", "altname", "decimals", "display_decimals")
  implicit val assetResponseFormat = jsonFormat(AssetResponse, "error", "result")
}
