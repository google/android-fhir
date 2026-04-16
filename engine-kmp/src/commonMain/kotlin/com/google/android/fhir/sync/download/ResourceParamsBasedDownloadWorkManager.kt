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

import com.google.android.fhir.lastUpdated
import com.google.android.fhir.resourceType
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.GREATER_THAN_PREFIX
import com.google.android.fhir.sync.ParamMap
import com.google.android.fhir.sync.SyncDataParams
import com.google.android.fhir.sync.concatParams
import com.google.android.fhir.toTimeZoneString
import com.google.fhir.model.r4.Bundle
import com.google.fhir.model.r4.OperationOutcome
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.terminologies.ResourceType

typealias ResourceSearchParams = Map<ResourceType, ParamMap>

/**
 * [DownloadWorkManager] implementation based on the provided [ResourceSearchParams] to generate
 * [Resource] search queries and parse [Bundle.BundleType.Searchset] type [Bundle]. This
 * implementation takes a DFS approach and downloads all available resources for a particular
 * [ResourceType] before moving on to the next [ResourceType].
 */
class ResourceParamsBasedDownloadWorkManager(
  syncParams: ResourceSearchParams,
  val context: TimestampContext,
) : DownloadWorkManager {
  private val resourcesToDownloadWithSearchParams = ArrayDeque(syncParams.entries)
  private val urlOfTheNextPagesToDownloadForAResource = ArrayDeque<String>()

  override suspend fun getNextRequest(): DownloadRequest? {
    if (urlOfTheNextPagesToDownloadForAResource.isNotEmpty()) {
      return urlOfTheNextPagesToDownloadForAResource.removeFirstOrNull()?.let {
        DownloadRequest.of(it)
      }
    }

    return resourcesToDownloadWithSearchParams.removeFirstOrNull()?.let { (resourceType, params) ->
      val newParams =
        params.toMutableMap().apply { putAll(getLastUpdatedParam(resourceType, params, context)) }

      DownloadRequest.of("${resourceType.name}?${newParams.concatParams()}")
    }
  }

  /**
   * Returns the map of resourceType and URL for summary of total count for each download request
   */
  override suspend fun getSummaryRequestUrls(): Map<ResourceType, String> {
    return resourcesToDownloadWithSearchParams.associate { (resourceType, params) ->
      val newParams =
        params.toMutableMap().apply {
          putAll(getLastUpdatedParam(resourceType, params, context))
          putAll(getSummaryParam(params))
        }

      resourceType to "${resourceType.name}?${newParams.concatParams()}"
    }
  }

  private suspend fun getLastUpdatedParam(
    resourceType: ResourceType,
    params: ParamMap,
    context: TimestampContext,
  ): MutableMap<String, String> {
    val newParams = mutableMapOf<String, String>()
    if (!params.containsKey(SyncDataParams.SORT_KEY)) {
      newParams[SyncDataParams.SORT_KEY] = SyncDataParams.LAST_UPDATED_KEY
    }
    if (!params.containsKey(SyncDataParams.LAST_UPDATED_KEY)) {
      val lastUpdate = context.getLasUpdateTimestamp(resourceType)
      if (!lastUpdate.isNullOrEmpty()) {
        newParams[SyncDataParams.LAST_UPDATED_KEY] = "$GREATER_THAN_PREFIX$lastUpdate"
      }
    }
    return newParams
  }

  private fun getSummaryParam(params: ParamMap): MutableMap<String, String> {
    val newParams = mutableMapOf<String, String>()
    if (!params.containsKey(SyncDataParams.SUMMARY_KEY)) {
      newParams[SyncDataParams.SUMMARY_KEY] = SyncDataParams.SUMMARY_COUNT_VALUE
    }
    return newParams
  }

  override suspend fun processResponse(response: Resource): Collection<Resource> {
    if (response is OperationOutcome) {
      throw Exception(response.issue.firstOrNull()?.diagnostics?.value)
    }

    if ((response !is Bundle || response.type.value != Bundle.BundleType.Searchset)) {
      return emptyList()
    }

    response.link
      .firstOrNull { component -> component.relation.value == "next" }
      ?.url
      ?.let { next -> next.value?.let { urlOfTheNextPagesToDownloadForAResource.add(it) } }

    return response.entry
      .mapNotNull { it.resource }
      .also { resources ->
        resources
          .groupBy { ResourceType.valueOf(it.resourceType) }
          .entries
          .forEach { map ->
            map.value
              .mapNotNull { it.lastUpdated }
              .let { lastUpdatedList ->
                if (lastUpdatedList.isNotEmpty()) {
                  context.saveLastUpdatedTimestamp(
                    map.key,
                    lastUpdatedList.maxOrNull()?.toTimeZoneString(),
                  )
                }
              }
          }
      } as Collection<Resource>
  }

  interface TimestampContext {
    suspend fun saveLastUpdatedTimestamp(resourceType: ResourceType, timestamp: String?)

    suspend fun getLasUpdateTimestamp(resourceType: ResourceType): String?
  }
}
