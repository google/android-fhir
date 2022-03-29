/*
 * Copyright 2021 Google LLC
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

import org.hl7.fhir.r4.model.Resource

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
  fun getNextRequestUrl(): String?

  /**
   * Implement this method to take the FHIR resource returned by the server, and return the
   * resources that need to be saved into the local database.
   */
  fun processResponse(response: Resource): Collection<Resource>
}

/**
 * Affixes the last updated timestamp to the request URL.
 *
 * If the request URL includes the `$everything` parameter, the last updated timestamp will be
 * attached using the `_since` parameter. Otherwise, the last updated timestamp will be attached
 * using the `_lastUpdated` parameter.
 */
fun affixLastUpdatedTimestamp(url: String, lastUpdated: String): String {
  var downloadUrl = url

  // Affix lastUpdate to a $everything query using _since as per:
  // https://hl7.org/fhir/operation-patient-everything.html
  if (downloadUrl.contains("\$everything")) {
    downloadUrl = "$downloadUrl?_since=$lastUpdated"
  }

  // Affix lastUpdate to non-$everything queries as per:
  // https://hl7.org/fhir/operation-patient-everything.html
  if (!downloadUrl.contains("\$everything")) {
    downloadUrl = "$downloadUrl&_lastUpdated=gt$lastUpdated"
  }

  // Do not modify any URL set by a server that specifies the token of the page to return.
  if (downloadUrl.contains("&page_token")) {
    downloadUrl = url
  }

  return downloadUrl
}
