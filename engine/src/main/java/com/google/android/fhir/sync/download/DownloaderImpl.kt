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

import com.google.android.fhir.sync.BundleRequest
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.DownloadState
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.Downloader
import com.google.android.fhir.sync.Request
import com.google.android.fhir.sync.ResourceSyncException
import com.google.android.fhir.sync.UrlRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

/**
 * Implementation of the [Downloader]. It orchestrates the pre & post processing of resources via
 * [DownloadWorkManager] and downloading of resources via [DataSource]. [Downloader] clients should
 * call download and listen to the various states emitted by [DownloadWorkManager] as
 * [DownloadState].
 */
internal class DownloaderImpl(
  private val dataSource: DataSource,
  private val downloadWorkManager: DownloadWorkManager
) : Downloader {
  private val resourceTypeList = ResourceType.values().map { it.name }

  override suspend fun download(): Flow<DownloadState> = flow {
    var resourceTypeToDownload: ResourceType = ResourceType.Bundle
    // download count summary of all resources for progress i.e. <type, total, completed>
    val totalResourcesToDownloadCount = getProgressSummary().values.sumOf { it ?: 0 }
    emit(DownloadState.Started(resourceTypeToDownload, totalResourcesToDownloadCount))
    var downloadedResourcesCount = 0
    var request = downloadWorkManager.getNextRequest()
    while (request != null) {
      try {
        resourceTypeToDownload = request.toResourceType()
        downloadWorkManager.processResponse(dataSource.download(request)).toList().let {
          downloadedResourcesCount += it.size
          emit(DownloadState.Success(it, totalResourcesToDownloadCount, downloadedResourcesCount))
        }
      } catch (exception: Exception) {
        Timber.e(exception)
        emit(DownloadState.Failure(ResourceSyncException(resourceTypeToDownload, exception)))
      }
      request = downloadWorkManager.getNextRequest()
    }
  }

  private fun Request.toResourceType() =
    when (this) {
      is UrlRequest ->
        ResourceType.fromCode(url.findAnyOf(resourceTypeList, ignoreCase = true)!!.second)
      is BundleRequest -> ResourceType.Bundle
    }

  private suspend fun getProgressSummary() =
    downloadWorkManager
      .getSummaryRequestUrls()
      .map { summary ->
        summary.key to
          runCatching { dataSource.download(Request.of(summary.value)) }
            .onFailure { Timber.e(it) }
            .getOrNull()
            .takeIf { it is Bundle }
            ?.let { (it as Bundle).total }
      }
      .also { Timber.i("Download summary " + it.joinToString()) }
      .toMap()
}
