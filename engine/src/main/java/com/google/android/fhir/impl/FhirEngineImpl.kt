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

package com.google.android.fhir.impl

import android.content.Context
import com.google.android.fhir.DatastoreUtil
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.LocalChange
import com.google.android.fhir.ResourceForDatabaseToSave
import com.google.android.fhir.ResourceType
import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.toLocalChange
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.index.ResourceIndexerManager
import com.google.android.fhir.logicalId
import com.google.android.fhir.resourceType
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.count
import com.google.android.fhir.search.execute
import com.google.android.fhir.sync.ConflictResolver
import com.google.android.fhir.sync.Resolved
import com.google.android.fhir.toTimeZoneString
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.hl7.fhir.instance.model.api.IAnyResource

/** Implementation of [FhirEngine]. */
internal class FhirEngineImpl(private val database: Database, private val context: Context) :
  FhirEngine {
  override suspend fun create(vararg resource: IAnyResource, resourcedIndexerManager: ResourceIndexerManager): List<String> {
    return database.insert(*resource, resourcedIndexerManager  = resourcedIndexerManager )
  }

  override suspend fun get(type: ResourceType, id: String): IAnyResource {
    return database.select(type, id)
  }

  override suspend fun update(vararg resource: IAnyResource, resourcedIndexerManager: ResourceIndexerManager) {
    database.update(*resource, resourcedIndexerManager  = resourcedIndexerManager )
  }

  override suspend fun delete(type: ResourceType, id: String) {
    database.delete(type, id)
  }

  override suspend fun <R : IAnyResource> search(search: Search): List<R> {
    return search.execute(database)
  }

  override suspend fun count(search: Search): Long {
    return search.count(database)
  }

  override suspend fun getLastSyncTimeStamp(): OffsetDateTime? {
    return DatastoreUtil(context).readLastSyncTimestamp()
  }

  override suspend fun clearDatabase() {
    database.clearDatabase()
  }

  override suspend fun getLocalChange(type: ResourceType, id: String): LocalChange? {
    return database.getLocalChange(type, id)?.toLocalChange()
  }

  override suspend fun purge(type: ResourceType, id: String, forcePurge: Boolean) {
    database.purge(type, id, forcePurge)
  }

  override suspend fun syncDownload(
    conflictResolver: ConflictResolver,
    resourcedIndexerManager: ResourceIndexerManager,
    download: suspend (SyncDownloadContext) -> Flow<List<IAnyResource>>,
  ) {
    download(
        object : SyncDownloadContext {
          override suspend fun getLatestTimestampFor(type: ResourceType) = database.lastUpdate(type)
        }
      )
      .collect { resources ->
        database.withTransaction {
          val resolved =
            resolveConflictingResources(
              resources,
              getConflictingResourceIds(resources),
              conflictResolver
            )
          saveRemoteResourcesToDatabase(resources, resourcedIndexerManager )
          saveResolvedResourcesToDatabase(resolved, resourcedIndexerManager )
        }
      }
  }

  private suspend fun saveResolvedResourcesToDatabase(resolved: List<IAnyResource>?, resourcedIndexerManager: ResourceIndexerManager ) {
    resolved?.let {
      database.deleteUpdates(it)
      database.update(*it.toTypedArray(), resourcedIndexerManager  = resourcedIndexerManager )
    }
  }

  private suspend fun saveRemoteResourcesToDatabase(
    resources: List<IAnyResource>,
    resourcedIndexerManager: ResourceIndexerManager
  ) {
    val timeStamps =
      resources
        .groupBy { it.resourceType }
        .entries.map {
          SyncedResourceEntity(it.key, it.value.maxOf { it.meta.lastUpdated }.toTimeZoneString())
        }
    database.insertSyncedResources(timeStamps, resourcedIndexerManager , resources)
  }

  private suspend fun resolveConflictingResources(
    resources: List<IAnyResource>,
    conflictingResourceIds: Set<String>,
    conflictResolver: ConflictResolver
  ) =
    resources
      .filter { conflictingResourceIds.contains(it.logicalId) }
      .map { conflictResolver.resolve(database.select(it.resourceType, it.logicalId), it) }
      .filterIsInstance<Resolved>()
      .map { it.resolved }
      .takeIf { it.isNotEmpty() }

  private suspend fun getConflictingResourceIds(resources: List<IAnyResource>) =
    resources
      .map { it.logicalId }
      .toSet()
      .intersect(database.getAllLocalChanges().map { it.localChange.resourceId }.toSet())

  override suspend fun syncUpload(
    getResourceTypeToSave: (IAnyResource) -> ResourceForDatabaseToSave?,
    upload: suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, IAnyResource>>
  ) {
    val localChanges = database.getAllLocalChanges()
    if (localChanges.isNotEmpty()) {
      upload(localChanges.map { it.toLocalChange() }).collect {
        database.deleteUpdates(it.first)
        val omar = getResourceTypeToSave(it.second)
        if (omar != null) {
          database.updateVersionIdAndLastUpdated(omar.id, omar.resourceType, omar.versionId, omar.lastUpdated)
        }
      }
    }
  }

}
