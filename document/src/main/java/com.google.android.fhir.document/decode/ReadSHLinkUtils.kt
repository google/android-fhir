/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.document.decode

import android.util.Base64
import com.nimbusds.jose.JWEDecrypter
import com.nimbusds.jose.JWEObject
import com.nimbusds.jose.crypto.DirectDecrypter
import java.io.ByteArrayOutputStream
import java.util.zip.DataFormatException
import java.util.zip.Inflater
import org.json.JSONObject

object ReadSHLinkUtils {

  /* Extracts the part of the link after the 'shlink:/' */
  fun extractUrl(scannedData: String): String {
    if (scannedData.contains("shlink:/")) {
      return scannedData.substringAfterLast("shlink:/")
    }
    throw IllegalArgumentException("Not a valid SHLink")
  }

  /* Decodes the extracted url from Base64Url to a byte array */
  fun decodeUrl(extractedUrl: String): ByteArray {
    if (extractedUrl.isEmpty()) {
      throw IllegalArgumentException("Not a valid Base64 encoded string")
    }
    try {
      return Base64.decode(extractedUrl.toByteArray(), Base64.URL_SAFE)
    } catch (err: IllegalArgumentException) {
      throw IllegalArgumentException("Not a valid Base64 encoded string")
    }
  }

  /* Returns a string of data found in the verifiableCredential field in the given JSON */
  fun extractVerifiableCredential(jsonString: String): String {
    val jsonObject = JSONObject(jsonString)
    if (jsonObject.has("verifiableCredential")) {
      val verifiableCredentialArray = jsonObject.getJSONArray("verifiableCredential")

      if (verifiableCredentialArray.length() > 0) {
        // Assuming you want the first item from the array
        return verifiableCredentialArray.getString(0)
      }
    }
    return ""
  }

  /* Decodes and decompresses the payload in a JWT token */
  fun decodeAndDecompressPayload(token: String): String {
    try {
      val tokenParts = token.split('.')
      if (tokenParts.size < 2) {
        throw Error("Invalid JWT token passed in")
      }
      val decoded = Base64.decode(tokenParts[1], Base64.URL_SAFE)
      val inflater = Inflater(true)
      inflater.setInput(decoded)
      val initialBufferSize = 100000
      val decompressedBytes = ByteArrayOutputStream(initialBufferSize)
      val buffer = ByteArray(8192)

      try {
        while (!inflater.finished()) {
          val length = inflater.inflate(buffer)
          decompressedBytes.write(buffer, 0, length)
        }
        decompressedBytes.close()
      } catch (e: DataFormatException) {
        throw Error("$e.printStackTrace()")
      }
      inflater.end()
      return decompressedBytes.toByteArray().decodeToString()
    } catch (err: Error) {
      throw Error("Invalid JWT token passed in: $err")
    }
  }

  /* Decodes and decompresses the embedded health data from a JWE token into a string */
  fun decodeShc(responseBody: String, key: String): String {
    try {
      if (responseBody.isEmpty() or key.isEmpty()) {
        throw IllegalArgumentException("The provided strings should not be empty")
      }
      val jweObject = JWEObject.parse(responseBody)
      val decodedKey: ByteArray = Base64.decode(key, Base64.URL_SAFE)
      val decrypter: JWEDecrypter = DirectDecrypter(decodedKey)
      jweObject.decrypt(decrypter)
      return jweObject.payload.toString()
    } catch (err: Exception) {
      throw Exception("JWE decryption failed: $err")
    }
  }
}
