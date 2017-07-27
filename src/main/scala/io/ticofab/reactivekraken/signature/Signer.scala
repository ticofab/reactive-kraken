package io.ticofab.reactivekraken.signature

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Base64

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

object Signer {

  /**
    * Gets the signature to use in your request to the Kraken API
    *
    * @param path      The path of your request, eg. '/0/private/Balance'
    * @param nonce     An always increasing unsigned 64 bit integer
    * @param postData  The post content of your request
    * @param apiSecret Your own API secret.
    * @return A string to add to your request, in the API-Sign header.
    */
  def getSignature(path: String, nonce: Long, postData: String, apiSecret: String) = {
    // Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
    val md = MessageDigest.getInstance("SHA-256")
    md.update((nonce + postData).getBytes)
    val mac = Mac.getInstance("HmacSHA512")
    mac.init(new SecretKeySpec(Base64.decodeBase64(apiSecret), "HmacSHA512"))
    mac.update(path.getBytes)
    new String(Base64.encodeBase64(mac.doFinal(md.digest())))
  }

}
