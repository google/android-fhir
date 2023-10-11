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

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.SHLData
import com.google.android.fhir.document.interfaces.RetrofitSHLService
import com.nimbusds.jose.EncryptionMethod
import com.nimbusds.jose.JWEAlgorithm
import com.nimbusds.jose.JWEHeader
import com.nimbusds.jose.JWEObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.DirectEncrypter
import java.lang.Exception
import java.security.SecureRandom
import java.text.SimpleDateFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

internal class GenerateShlUtils(
  private val qrGeneratorUtils: QRGeneratorUtils,
  private val apiService: RetrofitSHLService,
) {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  /* Encrypt a given string as a JWE token, using a given key */
  fun encrypt(data: String, key: String): String {
    val header = JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
    val jweObj = JWEObject(header, Payload(data))
    val decodedKey = Base64.decode(key, Base64.URL_SAFE)
    val encrypter = DirectEncrypter(decodedKey)
    jweObj.encrypt(encrypter)
    return jweObj.serialize()
  }

  /* POST the IPS document to the manifest URL */
  suspend fun postPayload(
    file: String,
    manifestToken: String,
    key: String,
    managementToken: String,
  ): JSONObject {
    try {
      val contentEncrypted = encrypt(file, key)

      // val retrofit = RetrofitSHLService.builder("$manifestUrl/", NetworkConfiguration()).build()
      val authorization = "Bearer $managementToken"
      val response = apiService.postPayload(manifestToken, contentEncrypted, authorization)

      return if (response.isSuccessful) {
        println(response.message())
        val responseBody = response.body()?.string()
        JSONObject(responseBody)
      } else {
        println("HTTP Error: ${response.code()}")
        JSONObject()
      }
    } catch (e: Exception) {
      println(e)
      Log.e(TAG, "Error while posting payload: ${e.message}", e)
      throw e
    }
  }

  /* Converts the inputted expiry date to epoch seconds */
  fun dateStringToEpochSeconds(dateString: String): Long {
    val format = SimpleDateFormat("yyyy-M-d")
    val date = format.parse(dateString)
    return date?.time?.div(1000) ?: 0L
  }

  /* Base64Url encodes a given string */
  private fun base64UrlEncode(data: String): String {
    return Base64.encodeToString(data.toByteArray(), Base64.URL_SAFE)
  }

  /* Generate a random SHL-specific key */
  fun generateRandomKey(): String {
    val random = SecureRandom()
    val keyBytes = ByteArray(32)
    random.nextBytes(keyBytes)
    return Base64.encodeToString(keyBytes, Base64.URL_SAFE)
  }

  /* Generate an SHL */
  fun generateAndPostPayload(
    passcode: String,
    shlData: SHLData,
    context: Context,
    qrView: ImageView,
    viewModelScope: CoroutineScope,
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val initialPostResponse = getManifestUrlAndToken(passcode)
      val shLink = generateShLink(initialPostResponse, shlData, passcode)
      generateAndSetQRCode(context, shLink, qrView)
    }
  }

  /* Generate and display the SHL QR code*/
  private fun generateAndSetQRCode(context: Context, shLink: String, qrView: ImageView) {
    val qrCodeBitmap = generateQRCode(context, shLink)
    updateImageViewOnMainThread(qrView, qrCodeBitmap)
  }

  /* POST the data to the SHL server and return the link itself */
  private suspend fun generateShLink(
    initialPostResponse: JSONObject,
    shlData: SHLData,
    passcode: String,
  ): String {
    val manifestToken = initialPostResponse.getString("id")
    val manifestUrl = "https://api.vaxx.link/api/shl/$manifestToken"
    val managementToken = initialPostResponse.getString("managementToken")
    val exp =
      if (shlData.expirationTime.isNotEmpty()) {
        dateStringToEpochSeconds(shlData.expirationTime).toString()
      } else {
        ""
      }
    val key = generateRandomKey()
    val shLinkPayload =
      constructSHLinkPayload(manifestUrl, shlData.label, getKeyFlags(passcode), key, exp)
    val encodedPayload = base64UrlEncode(shLinkPayload)
    val data: String = parser.encodeResourceToString(shlData.ipsDoc.document)
    postPayload(data, manifestToken, key, managementToken)
    return "https://demo.vaxx.link/viewer#shlink:/$encodedPayload"
  }

  /* Send a POST request to the SHL server to get a new manifest URL.
  Can optionally add a passcode to the SHL here */
  suspend fun getManifestUrlAndToken(passcode: String): JSONObject {
    val requestBody =
      if (passcode.isNotBlank()) {
        "{\"passcode\": \"$passcode\"}".toRequestBody("application/json".toMediaTypeOrNull())
      } else {
        "{}".toRequestBody("application/json".toMediaTypeOrNull())
      }
    val response = apiService.getManifestUrlAndToken("", requestBody)
    return if (response.isSuccessful) {
      val responseBody = response.body()?.string()
      JSONObject(responseBody)
    } else {
      println("HTTP Error: ${response.code()}")
      JSONObject()
    }
  }

  /* Set the image view to the QR code */
  private fun updateImageViewOnMainThread(qrView: ImageView, qrCodeBitmap: Bitmap) {
    val handler = Handler(Looper.getMainLooper())
    handler.post { qrView.setImageBitmap(qrCodeBitmap) }
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
