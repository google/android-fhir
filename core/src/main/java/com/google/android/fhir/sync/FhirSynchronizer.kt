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

import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.logicalId
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

sealed class Result {
  object Success : Result()
  data class Error(val exceptions: List<ResourceSyncException>) : Result()
}

data class ResourceSyncException(val resourceType: ResourceType, val exception: Exception)

/** Class that helps synchronize the data source and save it in the local database */
class FhirSynchronizer(
  private val syncConfiguration: SyncConfiguration,
  private val dataSource: FhirDataSource,
  private val database: Database
) {
  suspend fun sync(): Result {
    val exceptions = mutableListOf<ResourceSyncException>()
    syncConfiguration.syncData.forEach { syncData ->
      val resourceSynchroniser =
        ResourceSynchronizer(syncData, dataSource, database, syncConfiguration.retry)
      try {
        resourceSynchroniser.sync()
      } catch (exception: Exception) {
        exceptions.add(ResourceSyncException(syncData.resourceType, exception))
      }
    }
    if (exceptions.isEmpty()) {
      return Result.Success
    } else {
      return Result.Error(exceptions)
    }
  }

  suspend fun upload(): Result {
    val exceptions = mutableListOf<ResourceSyncException>()
    database.getAllLocalChanges().forEach {
      try {
        val response: Resource = doUpload(it.localChange, dataSource)
        if (response.logicalId.equals(it.localChange.resourceId) || response.isSuccess()) {
          database.deleteUpdates(it.token)
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

    if (exceptions.isEmpty()) {
      return Result.Success
    } else {
      return Result.Error(exceptions)
    }
  }

  private suspend fun doUpload(
    localChange: LocalChangeEntity,
    datasource: FhirDataSource
  ): Resource =
    when (localChange.type) {
      LocalChangeEntity.Type.INSERT ->
        datasource.insert(localChange.resourceType, localChange.resourceId, localChange.payload)
      LocalChangeEntity.Type.UPDATE ->
        datasource.update(localChange.resourceType, localChange.resourceId, localChange.payload)
      LocalChangeEntity.Type.DELETE ->
        datasource.delete(localChange.resourceType, localChange.resourceId)
    }

  /**
   * Determines if the upload operation was successful or not.
   *
   * Current HAPI FHIR implementation does not give any signal other than 'severity' level for
   * operation success/failure. TODO: pass along the HTTP result (or any other signal) to determine
   * the outcome of an instance level RESTful operation.
   */
  private fun Resource.isSuccess(): Boolean {
    if (!this.resourceType.equals(ResourceType.OperationOutcome)) return false
    val outcome: OperationOutcome = this as OperationOutcome
    return outcome.hasIssue() &&
      issue.all { it.severity.equals(OperationOutcome.IssueSeverity.INFORMATION) }
  }
}
