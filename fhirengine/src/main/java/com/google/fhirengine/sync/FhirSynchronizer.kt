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

package com.google.fhirengine.sync

import com.google.fhirengine.db.Database
import org.hl7.fhir.r4.model.ResourceType

sealed class Result {
    object Success : Result()
    data class Error(val exceptions: List<ResourceSyncException>) : Result()
}

data class ResourceSyncException(val resourceType: ResourceType, val exception: Exception)

/**
 * Class that helps synchronize the data source and save it in the local database
 */
class FhirSynchronizer(
    private val syncConfiguration: SyncConfiguration,
    private val dataSource: FhirDataSource,
    private val database: Database
) {
    suspend fun sync(): Result {
        val exceptions = mutableListOf<ResourceSyncException>()
        syncConfiguration.syncData.forEach { syncData ->
            val resourceSynchroniser = ResourceSynchronizer(
                syncData,
                dataSource,
                database,
                syncConfiguration.retry
            )
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
}
