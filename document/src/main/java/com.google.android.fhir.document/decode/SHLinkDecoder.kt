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

import com.google.android.fhir.document.IPSDocument

/**
 * The [SHLinkDecoder] interface defines a contract for decoding Smart Health Links (SHLs) into
 * [IPSDocument] objects. Implementations of this interface are responsible for decoding and
 * decompressing SHLs, fetching associated health data from external sources, and creating
 * IPSDocument instances.
 *
 * The process of decoding SHLs is outlined in its documentation
 * [SHL Documentation](https://docs.smarthealthit.org/smart-health-links/).
 *
 * ## Example Decoding Process:
 * A SHL is formatted as `[optionalViewer]shlink:/[Base64-Encoded Payload]` (e.g.,
 * `shlink:/eyJsYWJ...`). First, extract the portion of the link after 'shlink:/' and decode this to
 * give a SHL Payload. SHL Payloads are structured as:
 * ```
 * {
 *   "url": manifest url,
 *   "key": SHL-specific key,
 *   "label": "2023-07-12",
 *   "flag": "LPU",
 *   "exp": expiration time,
 *   "v": SHL Protocol Version
 * }
 * ```
 *
 * The label, flag, exp, and v properties are optional.
 *
 * Send a POST request to the manifest URL with a header of "Content-Type":"application/json" and a
 * body with a "Recipient", a "Passcode" if the "P" flag is present and optionally
 * "embeddedLengthMax":INT. Example request body:
 *
 *  ```
 *  {
 *    "recipient" : "example_name",
 *    "passcode" : "example_passcode"
 *  }
 *  ```
 * ```
 *
 * If the POST request is successful, a list of files is returned.
 * Example response:
 *
 * ```
 *
 * { "files" :
 * [ { "contentType": "application/smart-health-card", "location":"https://bucket.cloud.example..." }, { "contentType": "application/smart-health-card", "embedded":"eyJhb..." } ]
 * }
 *
 * ```
 *
 * A file can be one of two types:
 * - Location: If the resource is stored in a location, a single GET request can be made to retrieve the data.
 * - Embedded: If the file type is embedded, the data is a JWE token which can be decoded with the SHL-specific key.
 */
interface SHLinkDecoder {

  /**
   * Decodes and decompresses a Smart Health Link (SHL) into an [IPSDocument] object.
   *
   * @param shLink The full Smart Health Link.
   * @param recipient The recipient for the manifest request.
   * @param passcode The passcode for the manifest request (optional, will be null if the P flag is
   *   not present in the SHL payload).
   * @return An [IPSDocument] object if decoding is successful, otherwise null.
   */
  suspend fun decodeSHLinkToDocument(
    shLink: String,
    recipient: String,
    passcode: String?,
  ): IPSDocument?
}
