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
import com.google.android.fhir.json.db.Database
import com.google.android.fhir.json.db.impl.dao.LocalChangeToken
import com.google.android.fhir.json.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.json.sync.ConflictResolver
import com.google.android.fhir.json.sync.Resolved
import java.time.Instant
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.json.JSONObject

/** Implementation of [JsonEngine]. */
internal class JsonEngineImpl(private val database: Database, private val context: Context) :
  JsonEngine {
  override suspend fun create(vararg resource: JSONObject): List<String> {
    return database.insert(*resource)
  }

  override suspend fun get(id: String): JSONObject {
    return database.select(id)
  }

  override suspend fun update(vararg resource: JSONObject) {
    database.update(*resource)
  }

  override suspend fun delete(id: String) {
    database.delete(id)
  }

  override suspend fun getLastSyncTimeStamp(): OffsetDateTime? {
    return DatastoreUtil(context).readLastSyncTimestamp()
  }

  override suspend fun syncDownload(
    conflictResolver: ConflictResolver,
    download: suspend () -> Flow<List<JSONObject>>
  ) {
    download().collect { resources ->
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

  private suspend fun saveResolvedResourcesToDatabase(resolved: List<JSONObject>?) {
    resolved?.let {
      database.deleteUpdates(it)
      database.update(*it.toTypedArray())
    }
  }

  private suspend fun saveRemoteResourcesToDatabase(resources: List<JSONObject>) {
    database.insertSyncedResources(resources)
  }

  private suspend fun resolveConflictingResources(
    resources: List<JSONObject>,
    conflictingResourceIds: Set<String>,
    conflictResolver: ConflictResolver
  ) =
    resources
      .filter { conflictingResourceIds.contains(it["id"].toString()) }
      .map { conflictResolver.resolve(database.select(it["id"].toString()), it) }
      .filterIsInstance<Resolved>()
      .map { it.resolved }
      .takeIf { it.isNotEmpty() }

  private suspend fun getConflictingResourceIds(resources: List<JSONObject>) =
    resources
      .map { it["id"].toString() }
      .toSet()
      .intersect(database.getAllLocalChanges().map { it.localChange.resourceId }.toSet())

  override suspend fun syncUpload(
    upload: suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, JSONObject>>
  ) {
    val localChanges = database.getAllLocalChanges()
    if (localChanges.isNotEmpty()) {
      upload(localChanges).collect {
        database.deleteUpdates(it.first)
        updateVersionIdAndLastUpdated(it.second)
      }
    }
  }

  private suspend fun updateVersionIdAndLastUpdated(resource: JSONObject) {
    database.updateVersionIdAndLastUpdated(
      resource["id"].toString(),
      Instant.parse(resource["lastUpdated"].toString())
    )
  }
}
