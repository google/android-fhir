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
import com.google.android.fhir.ResourceType
import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.toLocalChange
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.hasLastUpdated
import com.google.android.fhir.hasMeta
import com.google.android.fhir.hasVersionId
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
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.r4.model.Bundle
import timber.log.Timber

/** Implementation of [FhirEngine]. */
internal class FhirEngineImpl(private val database: Database, private val context: Context) :
  FhirEngine {
  override suspend fun create(vararg resource: IAnyResource): List<String> {
    return database.insert(*resource)
  }

  override suspend fun get(type: ResourceType, id: String): IAnyResource {
    return database.select(type, id)
  }

  override suspend fun update(vararg resource: IAnyResource) {
    database.update(*resource)
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
    download: suspend (SyncDownloadContext) -> Flow<List<IAnyResource>>
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
          saveRemoteResourcesToDatabase(resources)
          saveResolvedResourcesToDatabase(resolved)
        }
      }
  }

  private suspend fun saveResolvedResourcesToDatabase(resolved: List<IAnyResource>?) {
    resolved?.let {
      database.deleteUpdates(it)
      database.update(*it.toTypedArray())
    }
  }

  private suspend fun saveRemoteResourcesToDatabase(resources: List<IAnyResource>) {
    val timeStamps =
      resources
        .groupBy { it.resourceType }
        .entries.map {
          SyncedResourceEntity(it.key, it.value.maxOf { it.meta.lastUpdated }.toTimeZoneString())
        }
    database.insertSyncedResources(timeStamps, resources)
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
    upload: suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, IAnyResource>>
  ) {
    val localChanges = database.getAllLocalChanges()
    if (localChanges.isNotEmpty()) {
      upload(localChanges.map { it.toLocalChange() }).collect {
        database.deleteUpdates(it.first)
        when (it.second) {
          is Bundle -> updateVersionIdAndLastUpdatedForBundle(it.second as Bundle)
          is org.hl7.fhir.r5.model.Bundle ->
            updateVersionIdAndLastUpdatedForBundle(it.second as org.hl7.fhir.r5.model.Bundle)
          else -> updateVersionIdAndLastUpdated(it.second)
        }
      }
    }
  }

  private suspend fun updateVersionIdAndLastUpdatedForBundle(bundle: IBaseBundle) {
    when (bundle) {
      is Bundle ->
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
      is org.hl7.fhir.r5.model.Bundle ->
        when (bundle.type) {
          org.hl7.fhir.r5.model.Bundle.BundleType.TRANSACTIONRESPONSE -> {
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
  }

  private suspend fun updateVersionIdAndLastUpdated(response: Bundle.BundleEntryResponseComponent) {
    if (response.hasEtag() && response.hasLastModified() && response.hasLocation()) {
      response.resourceIdAndType?.let { (id, type) ->
        database.updateVersionIdAndLastUpdated(
          id,
          type,
          response.etag,
          response.lastModified.toInstant()
        )
      }
    }
  }

  private suspend fun updateVersionIdAndLastUpdated(
    response: org.hl7.fhir.r5.model.Bundle.BundleEntryResponseComponent
  ) {
    if (response.hasEtag() && response.hasLastModified() && response.hasLocation()) {
      response.resourceIdAndType?.let { (id, type) ->
        database.updateVersionIdAndLastUpdated(
          id,
          type,
          response.etag,
          response.lastModified.toInstant()
        )
      }
    }
  }
  private suspend fun updateVersionIdAndLastUpdated(resource: IAnyResource) {
    if (resource.hasMeta() && resource.meta.hasVersionId() && resource.meta.hasLastUpdated()) {
      database.updateVersionIdAndLastUpdated(
        resource.id,
        resource.resourceType,
        resource.meta.versionId,
        resource.meta.lastUpdated.toInstant()
      )
    }
  }

  /**
   * May return a Pair of versionId and resource type extracted from the
   * [Bundle.BundleEntryResponseComponent.location].
   *
   * [Bundle.BundleEntryResponseComponent.location] may be:
   *
   * 1. absolute path: `<server-path>/<resource-type>/<resource-id>/_history/<version>`
   *
   * 2. relative path: `<resource-type>/<resource-id>/_history/<version>`
   */
  private val Bundle.BundleEntryResponseComponent.resourceIdAndType: Pair<String, ResourceType>?
    get() =
      location
        ?.split("/")
        ?.takeIf { it.size > 3 }
        ?.let { it[it.size - 3] to ResourceType.fromCode(it[it.size - 4]) }

  private val org.hl7.fhir.r5.model.Bundle.BundleEntryResponseComponent.resourceIdAndType:
    Pair<String, ResourceType>?
    get() =
      location
        ?.split("/")
        ?.takeIf { it.size > 3 }
        ?.let { it[it.size - 3] to ResourceType.fromCode(it[it.size - 4]) }
}
