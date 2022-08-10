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

package com.google.android.fhir.json.sync.download

import com.google.android.fhir.json.SyncDownloadContext
import com.google.android.fhir.json.sync.DataSource
import com.google.android.fhir.json.sync.DownloadState
import com.google.android.fhir.json.sync.Downloader
import com.google.android.fhir.json.sync.JsonDownloadWorkManager
import com.google.android.fhir.json.sync.ResourceSyncException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

/**
 * Implementation of the [Downloader]. It orchestrates the pre & post processing of resources via
 * [JsonDownloadWorkManager] and downloading of resources via [DataSource]. [Downloader] clients
 * should call download and listen to the various states emitted by [JsonDownloadWorkManager] as
 * [DownloadState].
 */
internal class DownloaderImpl(
  private val dataSource: DataSource,
  private val jsonDownloadWorkManager: JsonDownloadWorkManager
) : Downloader {

  override suspend fun download(context: SyncDownloadContext): Flow<DownloadState> = flow {
    emit(DownloadState.Started(type = "JSON"))
    var url = jsonDownloadWorkManager.getNextRequestUrl(context)
    while (url != null) {
      try {
        val extraBody =
          JSONObject()
            .put("LAST_UPDATE", "")
            .put("USER_ID", "USER_ID")
            .put("OFFSET", "0")
        val response = dataSource.download(url, extraBody.toString())
        val resourcesToSave = jsonDownloadWorkManager.processResponse(response).toList()
        emit(DownloadState.Success(resourcesToSave))
      } catch (exception: Exception) {
        emit(DownloadState.Failure(ResourceSyncException(exception)))
      }
      url = jsonDownloadWorkManager.getNextRequestUrl(context)
    }
  }
}
