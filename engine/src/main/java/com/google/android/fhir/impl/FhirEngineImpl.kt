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

package com.google.android.fhir.impl

import android.content.Context
import com.google.android.fhir.DatastoreUtil
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
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

/** Implementation of [FhirEngine]. */
internal class FhirEngineImpl(private val database: Database, private val context: Context) :
  FhirEngine {
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

  override suspend fun getLastSyncTimeStamp(): OffsetDateTime? {
    return DatastoreUtil(context).readLastSyncTimestamp()
  }

  override suspend fun syncDownload(
    download: suspend (SyncDownloadContext) -> Flow<List<Resource>>
  ) {
    download(
      object : SyncDownloadContext {
        override suspend fun getLatestTimestampFor(type: ResourceType) = database.lastUpdate(type)
      }
    )
      .collect { resources ->
        val timeStamps =
          resources.groupBy { it.resourceType }.entries.map {
            SyncedResourceEntity(it.key, it.value.maxOf { it.meta.lastUpdated }.toTimeZoneString())
          }
        database.insertSyncedResources(timeStamps, resources)
      }
  }

  override suspend fun syncUpload(
    upload: suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  ) {
    val localChanges = database.getAllLocalChanges()
    if (localChanges.isNotEmpty()) {
      upload(localChanges).collect {
        database.deleteUpdates(it.first)
        when (it.second) {
          is Bundle -> updateVersionIdAndLastUpdated(it.second as Bundle)
          else -> updateVersionIdAndLastUpdated(it.second)
        }
      }
    }
  }

  private suspend fun updateVersionIdAndLastUpdated(bundle: Bundle) {
    when (bundle.type) {
      Bundle.BundleType.TRANSACTIONRESPONSE -> {
        bundle.entry.forEach {
          when {
            it.hasResource() -> updateVersionIdAndLastUpdated(it.resource)
            it.hasResponse() -> updateVersionIdAndLastUpdated(it.response)
          }
        }
      }
      else -> {
        // Leave it for now.
        Timber.i("Received request to update meta values for ${bundle.type}")
      }
    }
  }

  private suspend fun updateVersionIdAndLastUpdated(response: Bundle.BundleEntryResponseComponent) {
    if (response.hasEtag() && response.hasLastModified() && response.hasLocation()) {
      response.location.split("/").let { location ->
        database.updateVersionIdAndLastUpdated(
          location[1],
          ResourceType.fromCode(location[0]),
          response.etag,
          response.lastModified.toInstant()
        )
      }
    }
  }

  private suspend fun updateVersionIdAndLastUpdated(resource: Resource) {
    if (resource.hasMeta() && resource.meta.hasVersionId() && resource.meta.hasLastUpdated()) {
      database.updateVersionIdAndLastUpdated(
        resource.id,
        resource.resourceType,
        resource.meta.versionId,
        resource.meta.lastUpdated.toInstant()
      )
    }
  }
}
