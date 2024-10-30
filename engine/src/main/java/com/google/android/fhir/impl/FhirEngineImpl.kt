/*
 * Copyright 2023-2024 Google LLC
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
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.LocalChange
import com.google.android.fhir.SearchResult
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.LocalChangeResourceReference
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.count
import com.google.android.fhir.search.execute
import com.google.android.fhir.sync.ConflictResolver
import com.google.android.fhir.sync.Resolved
import com.google.android.fhir.sync.upload.LocalChangeFetcherFactory
import com.google.android.fhir.sync.upload.ResourceConsolidatorFactory
import com.google.android.fhir.sync.upload.SyncUploadProgress
import com.google.android.fhir.sync.upload.UploadRequestResult
import com.google.android.fhir.sync.upload.UploadStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** Implementation of [FhirEngine]. */
internal class FhirEngineImpl(private val database: Database, private val context: Context) :
  FhirEngine {
  override suspend fun create(vararg resource: Resource) =
    withContext(Dispatchers.IO) { database.insert(*resource) }

  override suspend fun get(type: ResourceType, id: String) =
    withContext(Dispatchers.IO) { database.select(type, id) }

  override suspend fun update(vararg resource: Resource) =
    withContext(Dispatchers.IO) { database.update(*resource) }

  override suspend fun delete(type: ResourceType, id: String) =
    withContext(Dispatchers.IO) { database.delete(type, id) }

  override suspend fun <R : Resource> search(search: Search): List<SearchResult<R>> =
    withContext(Dispatchers.IO) { search.execute(database) }

  override suspend fun count(search: Search) =
    withContext(Dispatchers.IO) { search.count(database) }

  override suspend fun getLastSyncTimeStamp() =
    withContext(Dispatchers.IO) {
      FhirEngineProvider.getFhirDataStore(context).readLastSyncTimestamp()
    }

  override suspend fun clearDatabase() = withContext(Dispatchers.IO) { database.clearDatabase() }

  override suspend fun getLocalChanges(type: ResourceType, id: String) =
    withContext(Dispatchers.IO) { database.getLocalChanges(type, id) }

  override suspend fun purge(type: ResourceType, id: String, forcePurge: Boolean) =
    withContext(Dispatchers.IO) { database.purge(type, setOf(id), forcePurge) }

  override suspend fun purge(type: ResourceType, ids: Set<String>, forcePurge: Boolean) =
    withContext(Dispatchers.IO) { database.purge(type, ids, forcePurge) }

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

  override suspend fun withTransaction(block: suspend FhirEngine.() -> Unit) {
    database.withTransaction { this.block() }
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
    uploadStrategy: UploadStrategy,
    upload:
      (suspend (List<LocalChange>, List<LocalChangeResourceReference>) -> Flow<
          UploadRequestResult,
        >),
  ): Flow<SyncUploadProgress> = flow {
    val resourceConsolidator =
      ResourceConsolidatorFactory.byHttpVerb(uploadStrategy.requestGeneratorMode, database)
    val localChangeFetcher =
      LocalChangeFetcherFactory.byMode(uploadStrategy.localChangesFetchMode, database)

    emit(
      SyncUploadProgress(
        remaining = localChangeFetcher.total,
        initialTotal = localChangeFetcher.total,
      ),
    )

    while (localChangeFetcher.hasNext()) {
      val localChanges = localChangeFetcher.next()
      val localChangeReferences =
        database.getLocalChangeResourceReferences(localChanges.flatMap { it.token.ids })
      val uploadRequestResult =
        upload(localChanges, localChangeReferences)
          .onEach { result ->
            resourceConsolidator.consolidate(result)
            val newProgress =
              when (result) {
                is UploadRequestResult.Success -> localChangeFetcher.getProgress()
                is UploadRequestResult.Failure ->
                  localChangeFetcher.getProgress().copy(uploadError = result.uploadError)
              }
            emit(newProgress)
          }
          .firstOrNull { it is UploadRequestResult.Failure }

      if (uploadRequestResult is UploadRequestResult.Failure) {
        break
      }
    }
  }
}
