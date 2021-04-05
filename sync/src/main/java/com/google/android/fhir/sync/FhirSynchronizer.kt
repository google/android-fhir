/*
 * Copyright 2020 Google LLC
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

import com.google.android.fhir.FhirEngine
import java.io.IOException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

sealed class Result {
  object Success : Result()
  data class Error(val exceptions: List<ResourceSyncException>) : Result()
}

data class ResourceSyncException(val resourceType: ResourceType, val exception: Exception)

/** Class that helps synchronize the data source and save it in the local database */
class FhirSynchronizer(
  private val fhirEngine: FhirEngine,
  private val dataSource: DataSource,
  private val syncData: SyncData
) {
  suspend fun sync(): Result {
    val exceptions = mutableListOf<ResourceSyncException>()
    syncData.forEach {
      try {
        sync(it.key, it.value)
      } catch (exception: Exception) {
        exceptions.add(ResourceSyncException(it.key, exception))
      }
    }
    return if (exceptions.isEmpty()) {
      Result.Success
    } else {
      Result.Error(exceptions)
    }
  }

  private suspend fun sync(resourceType: ResourceType, params: ParamMap) {
    fhirEngine.syncDownload {
      var nextUrl = getInitialUrl(resourceType, params, it(resourceType))
      val result = mutableListOf<Resource>()
      try {
        while (nextUrl != null) {
          val bundle = dataSource.loadData(nextUrl)
          nextUrl = bundle.link.firstOrNull { component -> component.relation == "next" }?.url
          if (bundle.type == Bundle.BundleType.SEARCHSET) {
            result.addAll(bundle.entry.map { it.resource })
          }
        }
      } catch (e: IOException) {}

      return@syncDownload result
    }
  }

  private fun getInitialUrl(
    resourceType: ResourceType,
    params: ParamMap,
    lastUpdate: String?
  ): String? {
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
