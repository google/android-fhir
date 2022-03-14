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
import com.google.android.fhir.sync.DownloadResult
import com.google.android.fhir.sync.Downloader
import com.google.android.fhir.sync.ParamMap
import com.google.android.fhir.sync.ResourceSyncException
import com.google.android.fhir.sync.ResourceSyncParams
import com.google.android.fhir.sync.SyncDataParams
import com.google.android.fhir.sync.concatParams
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * [Downloader] implementation to work with the [ResourceSyncParams] provided by the developer
 * application.
 */
internal class ResourceSyncParamBasedDownloader(
  private val resourceSyncParams: ResourceSyncParams,
  private val dataSource: DataSource,
) : Downloader {

  override suspend fun download(context: SyncDownloadContext) = flow {
    resourceSyncParams.forEach { (resourceType, params) ->
      try {
        emit(DownloadResult.Started(resourceType))
        var nextUrl: String? =
          getInitialUrl(resourceType, params, context.getLatestTimestampFor(resourceType))
        while (nextUrl != null) {
          val response = dataSource.download(nextUrl)
          val (result, url) = getDownloadResultAndNextUrl(resourceType, response)
          nextUrl = url
          emit(result)
        }
      } catch (exception: Exception) {
        emit(DownloadResult.Failure(ResourceSyncException(resourceType, exception)))
      }
    }
  }

  private fun getDownloadResultAndNextUrl(
    resourceType: ResourceType,
    response: Resource
  ): Pair<DownloadResult, String?> {
    return when {
      response is Bundle && response.type == Bundle.BundleType.SEARCHSET -> {
        DownloadResult.Success(response.entry.map { it.resource }) to
          response.link.firstOrNull { component -> component.relation == "next" }?.url
      }
      response is OperationOutcome && response.issue.isNotEmpty() -> {
        DownloadResult.Failure(
          ResourceSyncException(resourceType, FHIRException(response.issueFirstRep.diagnostics))
        ) to null
      }
      else -> {
        DownloadResult.Failure(
          ResourceSyncException(
            resourceType,
            FHIRException("Failed to extract download response from ${response.fhirType()}")
          )
        ) to null
      }
    }
  }

  private fun getInitialUrl(
    resourceType: ResourceType,
    params: ParamMap,
    lastUpdate: String?
  ): String {
    val newParams = params.toMutableMap()
    if (!params.containsKey(SyncDataParams.SORT_KEY)) {
      newParams[SyncDataParams.SORT_KEY] = SyncDataParams.LAST_UPDATED_ASC_VALUE
    }
    if (lastUpdate != null) {
      newParams[SyncDataParams.LAST_UPDATED_KEY] = "gt$lastUpdate"
    }
    return "${resourceType.name}?${newParams.concatParams()}"
  }
}
