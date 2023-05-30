/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.sync

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Manager that generates the FHIR requests and handles the FHIR responses of a download job.
 *
 * TODO(jingtang10): What happens after the end of a download job. Should a new download work
 * manager be created or should there be an API to restart a new download job.
 */
interface DownloadWorkManager {
  /**
   * Returns the URL for the next download request, or `null` if there is no more download request
   * to be issued.
   */
  suspend fun getNextRequest(): Request?

  /* TODO: Generalize the DownloadWorkManager API to not sequentially download resource by type (https://github.com/google/android-fhir/issues/1884) */
  /**
   * Returns the map of resourceType and URL for summary of total count for each download request
   */
  suspend fun getSummaryRequestUrls(): Map<ResourceType, String>

  /**
   * Processes the download response and returns the resources to be saved to the local database.
   */
  suspend fun processResponse(response: Resource): Collection<Resource>
}

/**
 * Structure represents a request that can be made to download resources from the FHIR server. The
 * request may contain http headers for conditional requests for getting precise results.
 *
 * Implementations of [Request] are [UrlRequest] and [BundleRequest] and the application developers
 * may choose the appropriate [Request.of] companion functions to create request objects.
 *
 * **UrlRequest**
 *
 * The application developer may use a request like below to get an update on Patient/123 since it
 * was last downloaded.
 * ```
 *  Request.of("/Patient/123", mapOf("If-Modified-Since" to "knownLastUpdatedOfPatient123"))
 * ```
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
sealed class Request(open val headers: Map<String, String>) {
  companion object {
    /** @return [UrlRequest] for a FHIR search [url]. */
    fun of(url: String, headers: Map<String, String> = emptyMap()) = UrlRequest(url, headers)

    /** @return [BundleRequest] for a FHIR search [bundle]. */
    fun of(bundle: Bundle, headers: Map<String, String> = emptyMap()) =
      BundleRequest(bundle, headers)
  }
}

/**
 * A [url] based FHIR request to download resources from the server. e.g.
 * `Patient?given=valueGiven&family=valueFamily`
 */
data class UrlRequest
internal constructor(val url: String, override val headers: Map<String, String> = emptyMap()) :
  Request(headers)

/**
 * A [bundle] based FHIR request to download resources from the server. For an example, see
 * [bundle-request-medsallergies.json](https://www.hl7.org/fhir/bundle-request-medsallergies.json.html)
 * .
 */
data class BundleRequest
internal constructor(val bundle: Bundle, override val headers: Map<String, String> = emptyMap()) :
  Request(headers)
