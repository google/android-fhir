/*
 * Copyright 2023-2026 Google LLC
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

import co.touchlab.kermit.Logger
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.ResourceSyncException
import com.google.fhir.model.r4.Bundle
import com.google.fhir.model.r4.terminologies.ResourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
  private val resourceTypeList = ResourceType.entries.map { it.name }

  override suspend fun download(): Flow<DownloadState> = flow {
    var resourceTypeToDownload: ResourceType = ResourceType.Bundle
    // download count summary of all resources for progress i.e. <type, total, completed>
    val totalResourcesToDownloadCount = getProgressSummary().values.sumOf { it?.value ?: 0 }
    emit(DownloadState.Started(resourceTypeToDownload, totalResourcesToDownloadCount))
    var downloadedResourcesCount = 0
    var request = downloadWorkManager.getNextRequest()
    while (request != null) {
      val downloadState =
        try {
          resourceTypeToDownload = request.toResourceType()
          downloadWorkManager.processResponse(dataSource.download(request)).toList().let {
            downloadedResourcesCount += it.size
            DownloadState.Success(it, totalResourcesToDownloadCount, downloadedResourcesCount)
          }
        } catch (exception: Exception) {
          Logger.e(exception) { exception.message ?: "Error downloading resource" }
          DownloadState.Failure(
            ResourceSyncException(resourceTypeToDownload, exception.message ?: "Unknown Exception"),
          )
        }
      emit(downloadState)
      request = downloadWorkManager.getNextRequest()
    }
  }

  private fun DownloadRequest.toResourceType() =
    when (this) {
      is UrlDownloadRequest ->
        ResourceType.valueOf(url.findAnyOf(resourceTypeList, ignoreCase = true)!!.second)
      is BundleDownloadRequest -> ResourceType.Bundle
    }

  private suspend fun getProgressSummary() =
    downloadWorkManager
      .getSummaryRequestUrls()
      .map { summary ->
        summary.key to
          runCatching { dataSource.download(DownloadRequest.of(summary.value)) }
            .onFailure { exception ->
              Logger.e(exception) { exception.message ?: "Error downloading resource" }
            }
            .getOrNull()
            .takeIf { it is Bundle }
            ?.let { (it as Bundle).total }
      }
      .also { Logger.i("Download summary ${it.joinToString()}") }
      .toMap()
}
