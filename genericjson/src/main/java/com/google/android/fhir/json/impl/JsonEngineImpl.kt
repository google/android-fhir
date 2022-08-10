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

package com.google.android.fhir.json.impl

import android.content.Context
import com.google.android.fhir.json.DatastoreUtil
import com.google.android.fhir.json.JsonEngine
import com.google.android.fhir.json.LocalChange
import com.google.android.fhir.json.SyncDownloadContext
import com.google.android.fhir.json.db.Database
import com.google.android.fhir.json.db.impl.dao.LocalChangeToken
import com.google.android.fhir.json.db.impl.dao.toLocalChange
import com.google.android.fhir.json.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.json.sync.ConflictResolver
import com.google.android.fhir.json.sync.JsonResource
import com.google.android.fhir.json.sync.Resolved
import com.google.android.fhir.json.toTimeZoneString
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/** Implementation of [JsonEngine]. */
internal class JsonEngineImpl(private val database: Database, private val context: Context) :
  JsonEngine {
  override suspend fun create(vararg resource: JsonResource): List<String> {
    return database.insert(*resource)
  }

  override suspend fun get(type: String, id: String): JsonResource {
    return database.select(type, id)
  }

  override suspend fun update(vararg resource: JsonResource) {
    database.update(*resource)
  }

  override suspend fun delete(type: String, id: String) {
    database.delete(type, id)
  }

  override suspend fun <R : JsonResource> search(): List<R> {
    return database.search()
  }

  override suspend fun count(): Long {
    return database.count()
  }

  override suspend fun getLastSyncTimeStamp(): OffsetDateTime? {
    return DatastoreUtil(context).readLastSyncTimestamp()
  }

  override suspend fun clearDatabase() {
    database.clearDatabase()
  }

  override suspend fun getLocalChange(type: String, id: String): LocalChange? {
    return database.getLocalChange(type, id)?.toLocalChange()
  }

  override suspend fun purge(type: String, id: String, forcePurge: Boolean) {
    database.purge(type, id, forcePurge)
  }

  override suspend fun syncDownload(
    conflictResolver: ConflictResolver,
    download: suspend (SyncDownloadContext) -> Flow<List<JsonResource>>
  ) {
    download(
      object : SyncDownloadContext {
        override suspend fun getLatestTimestampFor(type: String) = database.lastUpdate(type)
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
          saveRemoteResourcesToDatabase(resources)
          saveResolvedResourcesToDatabase(resolved)
        }
      }
  }

  private suspend fun saveResolvedResourcesToDatabase(resolved: List<JsonResource>?) {
    resolved?.let {
      database.deleteUpdates(it)
      database.update(*it.toTypedArray())
    }
  }

  private suspend fun saveRemoteResourcesToDatabase(resources: List<JsonResource>) {
    val timeStamps =
      resources.groupBy { it.resourceType }.entries.map {
        SyncedResourceEntity(it.key, it.value.maxOf { it.lastUpdated!! }.toTimeZoneString())
      }
    database.insertSyncedResources(timeStamps, resources)
  }

  private suspend fun resolveConflictingResources(
    resources: List<JsonResource>,
    conflictingResourceIds: Set<String>,
    conflictResolver: ConflictResolver
  ) =
    resources
      .filter { conflictingResourceIds.contains(it.id) }
      .map { conflictResolver.resolve(database.select(it.resourceType, it.id), it) }
      .filterIsInstance<Resolved>()
      .map { it.resolved }
      .takeIf { it.isNotEmpty() }

  private suspend fun getConflictingResourceIds(resources: List<JsonResource>) =
    resources
      .map { it.id }
      .toSet()
      .intersect(database.getAllLocalChanges().map { it.localChange.resourceId }.toSet())

  override suspend fun syncUpload(
    upload: suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, JsonResource>>
  ) {
    val localChanges = database.getAllLocalChanges()
    if (localChanges.isNotEmpty()) {
      upload(localChanges.map { it.toLocalChange() }).collect {
        database.deleteUpdates(it.first)
        updateVersionIdAndLastUpdated(it.second)
      }
    }
  }

  private suspend fun updateVersionIdAndLastUpdated(resource: JsonResource) {
    if (resource.versionId != null && resource.lastUpdated != null) {
      database.updateVersionIdAndLastUpdated(
        resource.id,
        resource.resourceType,
        resource.versionId!!,
        resource.lastUpdated!!.toInstant()
      )
    }
  }
}
