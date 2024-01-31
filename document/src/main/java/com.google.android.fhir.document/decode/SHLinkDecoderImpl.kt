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

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.IPSDocument
import com.google.android.fhir.document.RetrofitSHLService
import com.google.android.fhir.document.scan.SHLinkScanData
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.coroutineScope
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Patient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class SHLinkDecoderImpl(
  private val shLinkScanDataInput: SHLinkScanData?,
  private val readSHLinkUtils: ReadSHLinkUtils,
  private val apiService: RetrofitSHLService,
) : SHLinkDecoder {

  // private lateinit var shLinkScanData: SHLinkScanData
  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  override suspend fun decodeSHLinkToDocument(jsonData: String): IPSDocument? {
    val shLinkScanData = constructShlObj()
    println("EHUFHEHFEHK")
    println(shLinkScanData)
    val bundle = shLinkScanData?.let { postToServer(jsonData, it) }
    println(bundle)
    return if (bundle != null) {
      IPSDocument(bundle, ArrayList(), Patient()) // change this construction
    } else {
      null
    }
  }

  private suspend fun postToServer(jsonData: String, shLinkScanData: SHLinkScanData): Bundle? =
    coroutineScope {
      try {
        val response = apiService.getFilesFromManifest(shLinkScanData.manifestUrl, jsonData)
        response.handleApiResponse()?.let { responseBody ->
          return@coroutineScope decodeResponseBody(responseBody, shLinkScanData)
        }
      } catch (err: Error) {
        Timber.e("Error posting to the manifest: $err")
        null
      }
    }

  private suspend fun decodeResponseBody(
    responseBody: String,
    shLinkScanData: SHLinkScanData,
  ): Bundle {
    val jsonObject = JSONObject(responseBody)
    val embeddedArray =
      jsonObject.getJSONArray("files").let { jsonArray: JSONArray ->
        (0 until jsonArray.length())
          .mapNotNull { i ->
            val fileObject = jsonArray.getJSONObject(i)
            if (fileObject.has("embedded")) {
              fileObject.getString("embedded")
            } else {
              fileObject.getString("location").let {
                val responseFromLocation = apiService.getFromLocation(it)
                val responseBodyFromLocation = responseFromLocation.body()?.string()
                if (!responseBodyFromLocation.isNullOrBlank()) {
                  responseBodyFromLocation
                } else {
                  null
                }
              }
            }
          }
          .toTypedArray()
      }
    return decodeEmbeddedArray(embeddedArray, shLinkScanData)
  }

  private fun decodeEmbeddedArray(
    embeddedArray: Array<String>,
    shLinkScanData: SHLinkScanData,
  ): Bundle {
    var healthData = ""
    for (elem in embeddedArray) {
      val decodedShc = shLinkScanData.key.let { readSHLinkUtils.decodeShc(elem, it) }
      if (decodedShc != "") {
        val toDecode = readSHLinkUtils.extractVerifiableCredential(decodedShc)
        if (toDecode.isEmpty()) {
          healthData = decodedShc
          break
        }
        val obj = JSONObject(readSHLinkUtils.decodeAndDecompressPayload(toDecode))
        val doc =
          obj.getJSONObject("vc").getJSONObject("credentialSubject").getJSONObject("fhirBundle")
        return parser.parseResource(doc.toString()) as Bundle
      }
    }
    return parser.parseResource(healthData) as Bundle
  }

  private fun constructShlObj(): SHLinkScanData? {
    shLinkScanDataInput?.fullLink?.let { fullLink ->
      val extractedJson = readSHLinkUtils.extractUrl(fullLink)
      println(fullLink)
      println(readSHLinkUtils.extractUrl(fullLink))
      val decodedJson = readSHLinkUtils.decodeUrl(extractedJson)
      try {
        if (decodedJson != null) {
          print("Decoded JSON: $decodedJson")
          val jsonObject = JSONObject(String(decodedJson, StandardCharsets.UTF_8))

          return SHLinkScanData(
            shLinkScanDataInput.fullLink,
            extractedJson,
            jsonObject.optString("url", ""),
            key = jsonObject.optString("key", ""),
            flag = jsonObject.optString("flag", ""),
          )
        }
      } catch (e: JSONException) {
        Timber.e(e, "Error creating JSONObject from decodedJson: $decodedJson")
        return null
      }
    }
    return null
  }
}

private fun <T> Response<T>.handleApiResponse(): String? {
  return this.let {
    if (it.isSuccessful) {
      it.toString()
    } else {
      Timber.e("HTTP Error: ${it.code()}")
      null
    }
  }
}
