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
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.isUploadSuccess
import com.google.android.fhir.logicalId
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

sealed class Result {
  object Success : Result()
  data class Error(val exceptions: List<ResourceSyncException>) : Result()
}

data class ResourceSyncException(val resourceType: ResourceType, val exception: Exception)

/** Class that helps synchronize the data source and save it in the local database */
internal class FhirSynchronizer(
  private val fhirEngine: FhirEngine,
  private val dataSource: DataSource,
  private val resourceSyncParams: ResourceSyncParams
) {
  suspend fun synchronize(): Result {
    return listOf(upload(), download())
      .filterIsInstance<Result.Error>()
      .flatMap { it.exceptions }
      .let {
        if (it.isEmpty()) {
          Result.Success
        } else {
          Result.Error(it)
        }
      }
  }

  private suspend fun download(): Result {
    val exceptions = mutableListOf<ResourceSyncException>()
    resourceSyncParams.forEach {
      try {
        downloadResourceType(it.key, it.value)
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

  private suspend fun downloadResourceType(resourceType: ResourceType, params: ParamMap) {
    fhirEngine.syncDownload { it ->
      var nextUrl = getInitialUrl(resourceType, params, it.getLatestTimestampFor(resourceType))
      val result = mutableListOf<Resource>()
      while (nextUrl != null) {
        val bundle = dataSource.loadData(nextUrl)
        nextUrl = bundle.link.firstOrNull { component -> component.relation == "next" }?.url
        if (bundle.type == Bundle.BundleType.SEARCHSET) {
          result.addAll(bundle.entry.map { it.resource })
        }
      }
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

  private suspend fun upload(): Result {
    val exceptions = mutableListOf<ResourceSyncException>()

    fhirEngine.syncUpload { list ->
      val tokens = mutableListOf<LocalChangeToken>()
      list.forEach {
        try {
          val response: Resource = doUpload(it.localChange)
          if (response.logicalId == it.localChange.resourceId || response.isUploadSuccess()) {
            tokens.add(it.token)
          } else {
            // TODO improve exception message
            exceptions.add(
              ResourceSyncException(
                ResourceType.valueOf(it.localChange.resourceType),
                FHIRException(
                  "Could not infer response \"${response.resourceType}/${response.logicalId}\" as success."
                )
              )
            )
          }
        } catch (exception: Exception) {
          exceptions.add(
            ResourceSyncException(ResourceType.valueOf(it.localChange.resourceType), exception)
          )
        }
      }
      return@syncUpload tokens
    }

    return if (exceptions.isEmpty()) {
      Result.Success
    } else {
      Result.Error(exceptions)
    }
  }

  private suspend fun doUpload(localChange: LocalChangeEntity): Resource =
    when (localChange.type) {
      LocalChangeEntity.Type.INSERT ->
        dataSource.insert(localChange.resourceType, localChange.resourceId, localChange.payload)
      LocalChangeEntity.Type.UPDATE ->
        dataSource.update(localChange.resourceType, localChange.resourceId, localChange.payload)
      LocalChangeEntity.Type.DELETE ->
        dataSource.delete(localChange.resourceType, localChange.resourceId)
    }
}
