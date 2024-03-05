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

package com.google.android.fhir.sync

import com.google.android.fhir.sync.download.DownloadRequest
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Manages the process of downloading FHIR resources from a remote server.
 *
 * Implementations of this interface define how download requests are generated and how responses
 * are processed to update the local database.
 */

/* TODO(jingtang10): What happens after the end of a download job. Should a new download work
 *   manager be created or should there be an API to restart a new download job.
 */
interface DownloadWorkManager {
  /** Returns the next [DownloadRequest] to be executed, or `null` if there are no more requests. */
  suspend fun getNextRequest(): DownloadRequest?

  /* TODO: Generalize the DownloadWorkManager API to not sequentially download resource by type (https://github.com/google/android-fhir/issues/1884) */
  /**
   * Returns a map of [ResourceType] to URLs that can be used to retrieve the total count of
   * resources to be downloaded for each type. This information is used for displaying download
   * progress.
   */
  suspend fun getSummaryRequestUrls(): Map<ResourceType, String>

  /**
   * Processes the [response] received from the FHIR server.
   *
   * This method is responsible for:
   * * Extracting resources from the response.
   * * Identifying additional resource URLs to download, for example to handle pagination.
   * * Returning the resources to be saved to the local database.
   *
   * @param response The FHIR resource received from the server, often a
   *   [List][org.hl7.fhir.r4.model.ListResource] or [Bundle][org.hl7.fhir.r4.model.Bundle].
   * @return A collection of [Resource]s extracted from the response.
   */
  suspend fun processResponse(response: Resource): Collection<Resource>
}
