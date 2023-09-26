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

package com.google.android.fhir.document.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.CloseableHttpClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClients
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils
import com.nimbusds.jose.JWEDecrypter
import com.nimbusds.jose.JWEObject
import com.nimbusds.jose.crypto.DirectDecrypter
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.zip.DataFormatException
import java.util.zip.Inflater
import org.json.JSONObject

class ReadShlUtils {

  // Extracts the part of the link after the 'shlink:/'
  fun extractUrl(scannedData: String): String {
    return scannedData.substringAfterLast("shlink:/")
  }

  // Decodes the extracted url from Base64Url to a byte array
  @RequiresApi(Build.VERSION_CODES.O)
  fun decodeUrl(extractedUrl: String): ByteArray {
    return Base64.getUrlDecoder().decode(extractedUrl.toByteArray())
  }

  // returns a string of the data in the verifiableCredential field in the returned JSON
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

  // Decodes and decompresses the payload in the JWE token
  @RequiresApi(Build.VERSION_CODES.O)
  fun decodeAndDecompressPayload(token: String): String {
    val tokenParts = token.split('.')
    val decoded = Base64.getUrlDecoder().decode(tokenParts[1])
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
      e.printStackTrace()
    }
    inflater.end()
    return decompressedBytes.toByteArray().decodeToString()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun decodeShc(responseBody: String, key: String): String {
    val jweObject = JWEObject.parse(responseBody)
    val decodedKey: ByteArray = Base64.getUrlDecoder().decode(key)
    val decrypter: JWEDecrypter = DirectDecrypter(decodedKey)
    jweObject.decrypt(decrypter)
    println(jweObject.payload.toString().trim())
    return jweObject.payload.toString()
  }

  fun getRequest(url: String): String? {
    val httpClient: CloseableHttpClient = HttpClients.createDefault()
    val httpGet = HttpGet(url)
    val response = httpClient.execute(httpGet)
    val responseBody = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
    httpClient.close()
    return responseBody
  }
}
