/*
 * Copyright 2023-2024 Google LLC
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

package com.google.android.fhir.document.scan

import com.google.android.fhir.document.IPSDocument
import com.google.android.fhir.document.decode.ReadSHLinkUtils
import java.nio.charset.StandardCharsets
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

/**
 * Represents a SHL data structure, which stores information related to SHL content required for the
 * scanning process.
 *
 * SHLs, or Smart Health Links, are a standardized format for securely sharing health-related
 * information. For official documentation and specifications, see
 * [SHL Documentation](https://docs.smarthealthit.org/smart-health-links/).
 *
 * @property fullLink The full Smart Health Link (could include an optional SHL viewer).
 * @property encodedShlPayload The Base64Url-encoded SHL payload.
 * @property manifestUrl The URL to the SHL manifest.
 * @property key The key for decoding the data.
 * @property label A label describing the SHL data.
 * @property flags Flags indicating specific conditions or requirements (e.g., "P" for passcode).
 * @property expirationTime The expiration time of the SHL data.
 * @property versionNumber The version number of the SHL data.
 * @property ipsDoc The IPS document linked to by the SHL.
 */
data class SHLinkScanData(
  val fullLink: String,
  val encodedShlPayload: String,
  val manifestUrl: String,
  val key: String,
  val label: String,
  val flag: String,
  val expirationTime: String,
  val versionNumber: String,
  val ipsDoc: IPSDocument?,
) {
  fun create(fullLink: String): SHLinkScanData {
    val extractedJson = ReadSHLinkUtils.extractUrl(fullLink)
    val decodedJson = ReadSHLinkUtils.decodeUrl(extractedJson)

    try {
      val jsonObject = JSONObject(String(decodedJson, StandardCharsets.UTF_8))
      return SHLinkScanData(
        fullLink,
        extractedJson,
        jsonObject.optString("url", ""),
        jsonObject.optString("key", ""),
        jsonObject.optString("label", ""),
        jsonObject.optString("flag", ""),
        jsonObject.optString("expirationTime", ""),
        jsonObject.optString("versionNumber", ""),
        null,
      )
    } catch (exception: JSONException) {
      Timber.e(exception, "Error creating JSONObject from decodedJson: $decodedJson")
      throw exception
    }
  }

  constructor() : this("", "", "", "", "", "", "", "", null)

  constructor(scannedValue: String) : this(scannedValue, "", "", "", "", "", "", "", null)

  constructor(
    fullLink: String,
    extractedJson: String,
    manifestUrl: String,
    key: String,
    flag: String,
  ) : this(fullLink, extractedJson, manifestUrl, key, "", flag, "", "", null)
}
