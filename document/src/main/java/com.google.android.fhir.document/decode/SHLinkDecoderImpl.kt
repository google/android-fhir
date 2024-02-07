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
import kotlinx.coroutines.coroutineScope
import org.hl7.fhir.r4.model.Bundle
import org.json.JSONArray
import org.json.JSONObject

class SHLinkDecoderImpl(
  private val readSHLinkUtils: ReadSHLinkUtils,
  private val apiService: RetrofitSHLService,
) : SHLinkDecoder {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  /* */
  override suspend fun decodeSHLinkToDocument(fullLink: String, jsonData: String): IPSDocument? {
    val shLinkScanData = SHLinkScanData().create(fullLink)
    val bundle = postToServer(jsonData, shLinkScanData)
    return if (bundle != null) {
      IPSDocument.create(bundle)
    } else {
      null
    }
  }

  /* */
  private suspend fun postToServer(jsonData: String, shLinkScanData: SHLinkScanData): Bundle? =
    coroutineScope {
      try {
        val response =
          apiService.getFilesFromManifest(
            shLinkScanData.manifestUrl.substringAfterLast("shl/"),
            JSONObject(jsonData),
          )
        if (response.isSuccessful) {
          val responseBody = response.body()?.string()
          responseBody?.let {
            return@coroutineScope decodeResponseBody(it, shLinkScanData)
          }
        } else {
          throw (Error("HTTP Error: ${response.code()}"))
        }
      } catch (err: Throwable) {
        throw (Error("Error posting to the manifest: $err"))
      }
    }

  /* */
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
                val responseFromLocation =
                  apiService.getFromLocation("file/${it.substringAfterLast("/")}")
                val responseBodyFromLocation = responseFromLocation.body()?.string()
                if (!responseBodyFromLocation.isNullOrBlank()) {
                  responseBodyFromLocation
                } else {
                  throw (IllegalArgumentException("No data found at the given location"))
                }
              }
            }
          }
          .toTypedArray()
      }
    return decodeEmbeddedArray(embeddedArray, shLinkScanData)
  }

  /* */
  private fun decodeEmbeddedArray(
    embeddedArray: Array<String>,
    shLinkScanData: SHLinkScanData,
  ): Bundle {
    var healthData = ""
    for (element in embeddedArray) {
      val decodedShc = shLinkScanData.key.let { readSHLinkUtils.decodeShc(element, it) }
      if (decodedShc != "") {
        val toDecode = readSHLinkUtils.extractVerifiableCredential(decodedShc)
        if (toDecode.isEmpty()) {
          healthData = decodedShc
          break
        }
        val document =
          JSONObject(readSHLinkUtils.decodeAndDecompressPayload(toDecode))
            .getJSONObject("vc")
            .getJSONObject("credentialSubject")
            .getJSONObject("fhirBundle")
        return parser.parseResource(document.toString()) as Bundle
      }
    }
    return parser.parseResource(healthData) as Bundle
  }
}
