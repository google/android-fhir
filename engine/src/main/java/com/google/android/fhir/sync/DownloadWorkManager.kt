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

package com.google.android.fhir.sync

import com.google.android.fhir.sync.download.DownloadRequest
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Manager that generates the FHIR requests and handles the FHIR responses of a download job.
 *
 * TODO(jingtang10): What happens after the end of a download job. Should a new download work
 *   manager be created or should there be an API to restart a new download job.
 */
interface DownloadWorkManager {
  /**
   * Returns the URL for the next download request, or `null` if there is no more download request
   * to be issued.
   */
  suspend fun getNextRequest(): DownloadRequest?

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
