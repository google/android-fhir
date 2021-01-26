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
import com.google.fhirengine.db.impl.entities.SyncedResourceEntity
import com.google.fhirengine.sync.SyncData.Companion.LAST_UPDATED_ASC_VALUE
import com.google.fhirengine.sync.SyncData.Companion.LAST_UPDATED_KEY
import com.google.fhirengine.sync.SyncData.Companion.SORT_KEY
import com.google.fhirengine.toTimeZoneString
import java.io.IOException
import org.hl7.fhir.r4.model.Bundle

/**
 * Class that synchronises only one resource.
 */
class ResourceSynchronizer(
    private val syncData: SyncData,
    private val dataSource: FhirDataSource,
    private val database: Database,
    retry: Boolean
) {
    private var retrySync = retry

    suspend fun sync() {
        var nextUrl: String? = getInitialUrl()
        try {
            while (nextUrl != null) {
                val bundle = dataSource.loadData(nextUrl)
                nextUrl = bundle.link.firstOrNull { component -> component.relation == "next" }?.url
                if (bundle.type == Bundle.BundleType.SEARCHSET) {
                    saveSyncedResource(bundle)
                }
            }
        } catch (exception: IOException) {
            if (retrySync) {
                retrySync = false
                sync()
            } else {
                // propagate the exception upstream
                throw exception
            }
        }
    }

    private suspend fun getInitialUrl(): String? {
        val updatedSyncData = syncData
            .addSortParam()
            .addLastUpdateDate()
        return "${updatedSyncData.resourceType.name}?${updatedSyncData.concatParams()}"
    }

    private fun SyncData.addSortParam(): SyncData {
        if (params.containsKey(SORT_KEY)) {
            return this
        }
        val newParams = params.toMutableMap()
        newParams[SORT_KEY] = LAST_UPDATED_ASC_VALUE
        return SyncData(resourceType, newParams)
    }

    private suspend fun SyncData.addLastUpdateDate(): SyncData {
        val lastUpdate = database.lastUpdate(resourceType)
        if (lastUpdate == null) {
            return this
        }
        val newParams = params.toMutableMap()
        newParams[LAST_UPDATED_KEY] = "gt$lastUpdate"
        return SyncData(resourceType, newParams)
    }

    private suspend fun saveSyncedResource(bundle: Bundle) {
        val resources = bundle.entry.map { it.resource }
        if (resources.isNotEmpty()) {
            val mostRecentResource = resources[resources.lastIndex]
            database.insertSyncedResources(SyncedResourceEntity(
                syncData.resourceType,
                mostRecentResource.meta.lastUpdated.toTimeZoneString()),
                resources
            )
        }
    }
}
