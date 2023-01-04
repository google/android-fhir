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

package com.google.android.fhir.sync.download

import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.DownloadState
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.Downloader
import com.google.android.fhir.sync.ResourceSyncException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.ResourceType

/**
 * Implementation of the [Downloader]. It orchestrates the pre & post processing of resources via
 * [DownloadWorkManager] and downloading of resources via [DataSource]. [Downloader] clients should
 * call download and listen to the various states emitted by [DownloadWorkManager] as
 * [DownloadState].
 */
internal class DownloaderImpl(
  private val dataSource: DataSource,
  private val downloadWorkManager: DownloadWorkManager,
) : Downloader {

  private val resourceTypeList = ResourceType.values().map { it.name }

  override suspend fun download(context: SyncDownloadContext): Flow<DownloadState> = flow {
    var resourceTypeToDownload: ResourceType = ResourceType.Bundle
    emit(DownloadState.Started(resourceTypeToDownload))
    var url = downloadWorkManager.getNextRequestUrl(context)
    while (url != null) {
      try {
        resourceTypeToDownload =
          ResourceType.fromCode(url.findAnyOf(resourceTypeList, ignoreCase = true)!!.second)

        val response = dataSource.download(url)
        emit(DownloadState.Success(downloadWorkManager.processResponse(response).toList()))
      } catch (exception: Exception) {
        emit(DownloadState.Failure(ResourceSyncException(resourceTypeToDownload, exception)))
      }

      url = downloadWorkManager.getNextRequestUrl(context)
    }
  }
}
