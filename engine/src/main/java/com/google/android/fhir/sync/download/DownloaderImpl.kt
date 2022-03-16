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

package com.google.android.fhir.sync.download

import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.DownloadManager
import com.google.android.fhir.sync.DownloadResult
import com.google.android.fhir.sync.Downloader
import com.google.android.fhir.sync.ResourceSyncException
import java.util.LinkedList
import java.util.Queue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.ResourceType

/**
 * [DownloadManager] based implementation of the [Downloader]. It orchestrates the pre & post
 * processing of resources via [DownloadManager] and downloading of resources via [DataSource].
 * [Downloader] clients should call download and listen to the various states emitted by
 * [Downloader] as [DownloadResult].
 */
internal class DownloaderImpl(val dataSource: DataSource, val downloadManager: DownloadManager) :
  Downloader {
  private val resourceTypeList = ResourceType.values().map { it.name }

  override suspend fun download(context: SyncDownloadContext): Flow<DownloadResult> = flow {
    var resourceTypeToDownload: ResourceType = ResourceType.Bundle
    emit(DownloadResult.Started(resourceTypeToDownload))
    val listOfUrls: Queue<String> = LinkedList()
    listOfUrls.add(downloadManager.getInitialUrl())
    while (listOfUrls.isNotEmpty()) {
      try {
        val preprocessedUrl = listOfUrls.remove()
        resourceTypeToDownload =
          ResourceType.fromCode(
            preprocessedUrl.findAnyOf(resourceTypeList, ignoreCase = true)!!.second
          )

        val lastUpdate = context.getLatestTimestampFor(resourceTypeToDownload)
        val downloadUrl = downloadManager.createDownloadUrl(preprocessedUrl, lastUpdate)
        with(dataSource.download(downloadUrl)) {
          downloadManager.extractNextUrlsFromResource(this).let { listOfUrls.addAll(it) }
          emit(DownloadResult.Success(downloadManager.extractResourcesFromResponse(this).toList()))
        }
      } catch (exception: Exception) {
        emit(DownloadResult.Failure(ResourceSyncException(resourceTypeToDownload, exception)))
      }
    }
  }
}
