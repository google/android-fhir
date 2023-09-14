/*
 * Copyright 2023 Google LLC
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
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.SearchResult
import com.google.android.fhir.db.Database
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.count
import com.google.android.fhir.search.execute
import com.google.android.fhir.sync.ConflictResolver
import com.google.android.fhir.sync.Resolved
import com.google.android.fhir.sync.upload.DefaultResourceConsolidator
import com.google.android.fhir.sync.upload.LocalChangeFetcherFactory
import com.google.android.fhir.sync.upload.LocalChangesFetchMode
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** Implementation of [FhirEngine]. */
internal class FhirEngineImpl(private val database: Database, private val context: Context) :
  FhirEngine {
  override suspend fun create(vararg resource: Resource): List<String> {
    return database.insert(*resource)
  }

  override suspend fun get(type: ResourceType, id: String): Resource {
    return database.select(type, id)
  }

  override suspend fun update(vararg resource: Resource) {
    database.update(*resource)
  }

  override suspend fun delete(type: ResourceType, id: String) {
    database.delete(type, id)
  }

  override suspend fun <R : Resource> search(search: Search): List<SearchResult<R>> {
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

  override suspend fun getLocalChanges(type: ResourceType, id: String): List<LocalChange> {
    return database.getLocalChanges(type, id)
  }

  override suspend fun purge(type: ResourceType, id: String, forcePurge: Boolean) {
    database.purge(type, id, forcePurge)
  }

  override suspend fun syncDownload(
    conflictResolver: ConflictResolver,
    download: suspend () -> Flow<List<Resource>>,
  ) {
    download().collect { resources ->
      database.withTransaction {
        val resolved =
          resolveConflictingResources(
            resources,
            getConflictingResourceIds(resources),
            conflictResolver,
          )
        database.insertSyncedResources(resources)
        saveResolvedResourcesToDatabase(resolved)
      }
    }
  }

  private suspend fun saveResolvedResourcesToDatabase(resolved: List<Resource>?) {
    resolved?.let {
      database.deleteUpdates(it)
      database.update(*it.toTypedArray())
    }
  }

  private suspend fun resolveConflictingResources(
    resources: List<Resource>,
    conflictingResourceIds: Set<String>,
    conflictResolver: ConflictResolver,
  ) =
    resources
      .filter { conflictingResourceIds.contains(it.logicalId) }
      .map { conflictResolver.resolve(database.select(it.resourceType, it.logicalId), it) }
      .filterIsInstance<Resolved>()
      .map { it.resolved }
      .takeIf { it.isNotEmpty() }

  private suspend fun getConflictingResourceIds(resources: List<Resource>) =
    resources
      .map { it.logicalId }
      .toSet()
      .intersect(database.getAllLocalChanges().map { it.resourceId }.toSet())

  override suspend fun syncUpload(
    localChangesFetchMode: LocalChangesFetchMode,
    upload: suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>,
  ) {
    val resourceConsolidator = DefaultResourceConsolidator(database)
    val localChangeFetcher = LocalChangeFetcherFactory.byMode(localChangesFetchMode, database)
    while (localChangeFetcher.hasNext()) {
      upload(localChangeFetcher.next()).collect {
        resourceConsolidator.consolidate(it.first, it.second)
      }
    }
  }
}
