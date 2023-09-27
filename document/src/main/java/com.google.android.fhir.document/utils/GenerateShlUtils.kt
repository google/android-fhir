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

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.annotation.RequiresApi
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.SHLData
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.CloseableHttpClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClients
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils
import com.nimbusds.jose.EncryptionMethod
import com.nimbusds.jose.JWEAlgorithm
import com.nimbusds.jose.JWEHeader
import com.nimbusds.jose.JWEObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.DirectEncrypter
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Base64
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class GenerateShlUtils {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val qrGeneratorUtils = QRGeneratorUtils()

  /* Encrypt a given string as a JWE token, using a given key */
  @RequiresApi(Build.VERSION_CODES.O)
  fun encrypt(data: String, key: String): String {
    val header = JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
    val jweObj = JWEObject(header, Payload(data))
    val decodedKey = Base64.getUrlDecoder().decode(key)
    val encrypter = DirectEncrypter(decodedKey)
    jweObj.encrypt(encrypter)
    return jweObj.serialize()
  }

  /* POST the IPS document to the manifest URL */
  @RequiresApi(Build.VERSION_CODES.O)
  fun postPayload(file: String, manifestUrl: String, key: String, managementToken: String) {
    val contentEncrypted = encrypt(file, key)
    val httpClient: CloseableHttpClient = HttpClients.createDefault()
    val httpPost = HttpPost("$manifestUrl/file")
    httpPost.addHeader("Content-Type", "application/fhir+json")
    httpPost.addHeader("Authorization", "Bearer $managementToken")
    val entity = StringEntity(contentEncrypted)
    httpPost.entity = entity
    httpClient.execute(httpPost)
    httpClient.close()
  }

  /* Converts the inputted expiry date to epoch seconds */
  @RequiresApi(Build.VERSION_CODES.O)
  fun dateStringToEpochSeconds(dateString: String): Long {
    val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
    val localDate = LocalDate.parse(dateString, formatter)
    val zonedDateTime = localDate.atStartOfDay(ZoneOffset.UTC)
    return zonedDateTime.toEpochSecond()
  }

  /* Base64Url encodes a given string */
  @RequiresApi(Build.VERSION_CODES.O)
  fun base64UrlEncode(data: String): String {
    val bytes = data.toByteArray(StandardCharsets.UTF_8)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
  }

  /* Generate a random SHL-specific key */
  @RequiresApi(Build.VERSION_CODES.O)
  fun generateRandomKey(): String {
    val random = SecureRandom()
    val keyBytes = ByteArray(32)
    random.nextBytes(keyBytes)
    return Base64.getUrlEncoder().encodeToString(keyBytes)
  }

  /* Generate an SHL */
  @RequiresApi(Build.VERSION_CODES.O)
  @OptIn(DelicateCoroutinesApi::class)
  fun generateAndPostPayload(
    passcode: String,
    shlData: SHLData,
    context: Context,
    qrView: ImageView,
  ) {
    val expirationDate = shlData.expirationTime
    val labelData = shlData.label
    val bundle = shlData.ipsDoc.document
    var qrCodeBitmap: Bitmap?

    GlobalScope.launch(Dispatchers.IO) {
      val initialPostResponse = getManifestUrlAndToken(passcode)
      val manifestUrl = "https://api.vaxx.link/api/shl/${initialPostResponse.getString("id")}"
      val managementToken = initialPostResponse.getString("managementToken")
      val exp =
        if (expirationDate.isNotEmpty()) dateStringToEpochSeconds(expirationDate).toString() else ""
      val key = generateRandomKey()
      val shLinkPayload =
        constructSHLinkPayload(manifestUrl, labelData, getKeyFlags(passcode), key, exp)
      val encodedPayload = base64UrlEncode(shLinkPayload)
      val shLink = "https://demo.vaxx.link/viewer#shlink:/$encodedPayload"

      qrCodeBitmap = generateQRCode(context, shLink)
      updateImageViewOnMainThread(qrView, qrCodeBitmap!!)

      val data: String = parser.encodeResourceToString(bundle)
      postPayload(data, manifestUrl, key, managementToken)
    }
  }

  /* Send a POST request to the SHL server with the optional passcode */
  fun getManifestUrlAndToken(passcode: String): JSONObject {
    val httpClient: CloseableHttpClient = HttpClients.createDefault()
    return doPostRequest(httpClient, passcode)
  }

  /* Set the image view to the QR code */
  private fun updateImageViewOnMainThread(qrView: ImageView, qrCodeBitmap: Bitmap) {
    val handler = Handler(Looper.getMainLooper())
    handler.post { qrView.setImageBitmap(qrCodeBitmap) }
  }

  /* Send a POST request to the SHL server to get a new manifest URL.
     Can optionally add a passcode to the SHL here */
  private fun doPostRequest(httpClient: CloseableHttpClient, passcode: String): JSONObject {
    val httpPost = HttpPost("https://api.vaxx.link/api/shl")
    httpPost.addHeader("Content-Type", "application/json")
    val jsonData: String = if (passcode.isNotEmpty()) "{\"passcode\" : \"$passcode\"}" else "{}"
    val entity = StringEntity(jsonData)
    httpPost.entity = entity
    val response = httpClient.execute(httpPost)
    val responseBody = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
    httpClient.close()
    return JSONObject(responseBody)
  }

  /* Sets the P flag if a passcode has been set */
  fun getKeyFlags(passcode: String): String {
    return if (passcode.isNotEmpty()) "P" else ""
  }

  /* Generates the SHL QR code for the given payload */
  private fun generateQRCode(context: Context, content: String): Bitmap {
    val qrCodeBitmap = qrGeneratorUtils.createQRCodeBitmap(content)
    val logoBitmap = qrGeneratorUtils.createLogoBitmap(context, qrCodeBitmap)
    return qrGeneratorUtils.overlayLogoOnQRCode(qrCodeBitmap, logoBitmap)
  }

  /* Constructs the SHL payload */
  @RequiresApi(Build.VERSION_CODES.O)
  fun constructSHLinkPayload(
    manifestUrl: String,
    label: String?,
    flags: String?,
    key: String,
    exp: String?,
  ): String {
    val payloadObject =
      JSONObject()
        .apply {
          put("url", manifestUrl)
          put("key", key)
          flags?.let { put("flag", it) }
          label?.takeIf { it.isNotEmpty() }?.let { put("label", it) }
          exp?.takeIf { it.isNotEmpty() }?.let { put("exp", dateStringToEpochSeconds(it)) }
        }
        .toString()
    return payloadObject
  }
}
