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

/** Module for downloading changes from a [DataSource]. */
interface Downloader {

  /**
   * Implement this method to set the initial resource to fetch. Can be a search query, such as
   * "Patient?address-city=NAIROBI or a resource Id, such as "List/123"
   */
  fun getInitialUrl(): String

  /**
   * Implement this method to affix the last update timestamp to the URL.
   * @preProcessUrl is the entire URL
   * @param lastUpdate retrieved from implementation of
   * [com.google.android.fhir.SyncDownloadContext.getLatestTimestampFor]
   */
  fun createDownloadUrl(preProcessUrl: String, lastUpdate: String?): String

  /**
   * Implement this method to take the FHIR resource returned by the server, and return the
   * resources that need to be saved into the local database.
   */
  fun extractResourcesFromResponse(resourceResponse: Resource): Collection<Resource>

  /**
   * Implement this method to parse the FHIR resource returned by the server to extract the next
   * URLs to download.
   */
  fun extractNextUrlsFromResource(resourceResponse: Resource): Collection<String>
}
