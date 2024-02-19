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

import android.content.ContentValues.TAG
import android.util.Base64
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.RetrofitSHLService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber

internal class SHLinkGeneratorImpl(
  private val apiService: RetrofitSHLService,
  private val encryptionUtility: EncryptionUtils,
) : SHLinkGenerator {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  /* Generate an SHL */
  override suspend fun generateSHLink(
    shLinkGenerationData: SHLinkGenerationData,
    passcode: String,
    serverBaseUrl: String,
    optionalViewer: String,
  ): String {
    val initialPostResponse = getManifestUrlAndToken(passcode)
    return generateAndPostPayload(
      initialPostResponse,
      shLinkGenerationData,
      passcode,
      serverBaseUrl,
      "$optionalViewer#",
    )
  }

  /* Send a POST request to the SHL server to get a new manifest URL.
  Can optionally add a passcode to the SHL here */
  private suspend fun getManifestUrlAndToken(passcode: String): JSONObject {
    val requestBody =
      if (passcode.isNotBlank()) {
        "{\"passcode\": \"$passcode\"}".toRequestBody("application/json".toMediaTypeOrNull())
      } else {
        "{}".toRequestBody("application/json".toMediaTypeOrNull())
      }
    val response = apiService.getManifestUrlAndToken("", requestBody)
    return if (response.isSuccessful) {
      val responseBody = response.body()?.string()
      if (!responseBody.isNullOrBlank()) {
        JSONObject(responseBody)
      } else {
        Timber.e("Empty response body")
        JSONObject()
      }
    } else {
      Timber.e("HTTP Error: ${response.code()}")
      JSONObject()
    }
  }

  /* POST the data to the SHL server and return the link itself */
  private suspend fun generateAndPostPayload(
    initialPostResponse: JSONObject,
    shLinkGenerationData: SHLinkGenerationData,
    passcode: String,
    serverBaseUrl: String,
    optionalViewer: String,
  ): String {
    val manifestToken = initialPostResponse.getString("id")
    val manifestUrl = "$serverBaseUrl/api/shl/$manifestToken"
    val managementToken = initialPostResponse.getString("managementToken")
    val exp = shLinkGenerationData.expirationTime?.epochSecond?.toString() ?: ""
    val key = encryptionUtility.generateRandomKey()
    val shLinkPayload =
      constructSHLinkPayload(
        manifestUrl,
        shLinkGenerationData.label,
        getKeyFlags(passcode),
        key,
        exp,
      )
    val data: String = parser.encodeResourceToString(shLinkGenerationData.ipsDoc.document)
    postPayload(data, manifestToken, key, managementToken)
    val encodedPayload = base64UrlEncode(shLinkPayload)
    return "${optionalViewer}shlink:/$encodedPayload"
  }

  /* Constructs the SHL payload */
  private fun constructSHLinkPayload(
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
          exp?.takeIf { it.isNotEmpty() }?.let { put("exp", it) }
        }
        .toString()
    return payloadObject
  }

  /* Base64Url encodes a given string */
  private fun base64UrlEncode(data: String): String {
    return Base64.encodeToString(data.toByteArray(), Base64.URL_SAFE)
  }

  /* Sets the P flag if a passcode has been set */
  private fun getKeyFlags(passcode: String): String {
    return if (passcode.isNotEmpty()) "P" else ""
  }

  /* POST the IPS document to the manifest URL */
  private suspend fun postPayload(
    file: String,
    manifestToken: String,
    key: String,
    managementToken: String,
  ) {
    try {
      val contentEncrypted = encryptionUtility.encrypt(file, key)
      val authorization = "Bearer $managementToken"
      val response = apiService.postPayload(manifestToken, contentEncrypted, authorization)
      if (!response.isSuccessful) {
        Timber.e("HTTP Error: ${response.code()}")
      }
    } catch (e: Exception) {
      Timber.e(TAG, "Error while posting payload: ${e.message}", e)
      throw e
    }
  }
}
