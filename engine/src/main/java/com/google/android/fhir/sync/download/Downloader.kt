/*
 * Copyright 2022-2023 Google LLC
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

import com.google.android.fhir.sync.ResourceSyncException
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** Module for downloading the resources from the server. */
internal interface Downloader {
  /**
   * @return Flow of the [DownloadState] which keeps emitting [Resource]s or Error based on the
   *   response of each page download request. It also updates progress if [ProgressCallback] exists
   */
  suspend fun download(): Flow<DownloadState>
}

/* TODO: Generalize the Downloader API to not sequentially download resource by type (https://github.com/google/android-fhir/issues/1884) */
internal sealed class DownloadState {

  data class Started(val type: ResourceType, val total: Int) : DownloadState()

  data class Success(val resources: List<Resource>, val total: Int, val completed: Int) :
    DownloadState()

  data class Failure(val syncError: ResourceSyncException) : DownloadState()
}
