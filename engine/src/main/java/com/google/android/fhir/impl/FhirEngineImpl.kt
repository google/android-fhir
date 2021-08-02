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

package com.google.android.fhir.impl

import android.content.Context
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.count
import com.google.android.fhir.search.execute
import com.google.android.fhir.toTimeZoneString
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** Implementation of [FhirEngine]. */
internal class FhirEngineImpl
constructor(private val database: Database, private val context: Context) : FhirEngine {
  override suspend fun <R : Resource> save(vararg resource: R) {
    database.insert(*resource)
  }

  override suspend fun <R : Resource> update(resource: R) {
    database.update(resource)
  }

  override suspend fun <R : Resource> load(clazz: Class<R>, id: String): R {
    return database.select(clazz, id)
  }

  override suspend fun <R : Resource> remove(clazz: Class<R>, id: String) {
    database.delete(clazz, id)
  }

  override suspend fun <R : Resource> search(search: Search): List<R> {
    return search.execute(database)
  }

  override suspend fun count(search: Search): Long {
    return search.count(database)
  }

  override suspend fun syncDownload(download: suspend (SyncDownloadContext) -> List<Resource>) {
    val resources =
      download(
        object : SyncDownloadContext {
          override suspend fun getLatestTimestampFor(type: ResourceType) = database.lastUpdate(type)
        }
      )

    val timeStamps =
      resources.groupBy { it.resourceType }.entries.map {
        SyncedResourceEntity(it.key, it.value.maxOf { it.meta.lastUpdated }.toTimeZoneString())
      }
    database.insertSyncedResources(timeStamps, resources)
  }

  override suspend fun syncUpload(
    upload: (suspend (List<SquashedLocalChange>) -> List<LocalChangeToken>)
  ) {
    val localChanges = database.getAllLocalChanges()
    if (localChanges.isNotEmpty()) {
      upload(localChanges).forEach { database.deleteUpdates(it) }
    }
  }
}
