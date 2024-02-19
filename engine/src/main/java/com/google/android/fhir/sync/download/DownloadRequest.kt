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

package com.google.android.fhir.sync.download

import org.hl7.fhir.r4.model.Bundle

/**
 * Structure represents a request that can be made to download resources from the FHIR server. The
 * request may contain http headers for conditional requests for getting precise results.
 *
 * Implementations of [DownloadRequest] are [UrlDownloadRequest] and [BundleDownloadRequest] and the
 * application developers may choose the appropriate [DownloadRequest.of] companion functions to
 * create request objects.
 *
 * **UrlRequest**
 *
 * The application developer may use a request like below to get an update on Patient/123 since it
 * was last downloaded.
 *
 * ```
 *  Request.of("/Patient/123", mapOf("If-Modified-Since" to "knownLastUpdatedOfPatient123"))
 * ```
 *
 * **BundleRequest**
 *
 * The application developer may use a request like below to download multiple resources in a single
 * shot.
 *
 * ```
 * Request.of(Bundle().apply {
 *  addEntry(Bundle.BundleEntryComponent().apply {
 *    request = Bundle.BundleEntryRequestComponent().apply {
 *       url = "Patient/123"
 *       method = Bundle.HTTPVerb.GET
 *    }
 *  })
 *  addEntry(Bundle.BundleEntryComponent().apply {
 *    request = Bundle.BundleEntryRequestComponent().apply {
 *       url = "Patient/124"
 *       method = Bundle.HTTPVerb.GET
 *    }
 *  })
 * })
 * ```
 */
sealed class DownloadRequest(open val headers: Map<String, String>) {
  companion object {
    /** @return [UrlDownloadRequest] for a FHIR search [url]. */
    fun of(url: String, headers: Map<String, String> = emptyMap()) =
      UrlDownloadRequest(url, headers)

    /** @return [BundleDownloadRequest] for a FHIR search [bundle]. */
    fun of(bundle: Bundle, headers: Map<String, String> = emptyMap()) =
      BundleDownloadRequest(bundle, headers)
  }
}

/**
 * A [url] based FHIR request to download resources from the server. e.g.
 * `Patient?given=valueGiven&family=valueFamily`
 */
data class UrlDownloadRequest
internal constructor(val url: String, override val headers: Map<String, String> = emptyMap()) :
  DownloadRequest(headers)

/**
 * A [bundle] based FHIR request to download resources from the server. For an example, see
 * [bundle-request-medsallergies.json](https://www.hl7.org/fhir/bundle-request-medsallergies.json.html)
 */
data class BundleDownloadRequest
internal constructor(val bundle: Bundle, override val headers: Map<String, String> = emptyMap()) :
  DownloadRequest(headers)
