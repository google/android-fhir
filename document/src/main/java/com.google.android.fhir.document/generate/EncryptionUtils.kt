/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.document.generate

import android.util.Base64
import com.nimbusds.jose.EncryptionMethod
import com.nimbusds.jose.JWEAlgorithm
import com.nimbusds.jose.JWEHeader
import com.nimbusds.jose.JWEObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.DirectEncrypter
import java.security.SecureRandom

object EncryptionUtils {

  /* Encrypt a given string as a JWE token, using a given key */
  fun encrypt(data: String, key: String): String {
    val header = JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
    val jweObj = JWEObject(header, Payload(data))
    val decodedKey = Base64.decode(key, Base64.URL_SAFE)
    val encrypter = DirectEncrypter(decodedKey)
    jweObj.encrypt(encrypter)
    return jweObj.serialize()
  }

  /* Generate a random SHL-specific key */
  fun generateRandomKey(): String {
    val random = SecureRandom()
    val shlKeySize = 32
    val keyBytes = ByteArray(shlKeySize)
    random.nextBytes(keyBytes)
    return Base64.encodeToString(keyBytes, Base64.URL_SAFE)
  }
}
