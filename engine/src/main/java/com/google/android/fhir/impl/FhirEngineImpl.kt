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
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.DatastoreUtil
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.SyncStrategyTypes
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.count
import com.google.android.fhir.search.execute
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.toTimeZoneString
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

/** Implementation of [FhirEngine]. */
internal class FhirEngineImpl(
  private val database: Database,
  private val context: Context,
  private val dataSource: DataSource?
) : FhirEngine {
  private var syncUploadStrategyContext = SyncUploadContext()

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

  private suspend fun collectAndEmitLocalChanges(
    squashedChanges: List<SquashedLocalChange>,
    upload: suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  ) {
    var resourceSuccessfullyUploaded = true
    Timber.i("COLLECTED FROM REARRANGE ${squashedChanges.size}")
    upload(squashedChanges).collect {
      val idsToDeleteFromUpdate = it.first.ids as MutableList

      // Check server for resource before delete.
      try {
        Timber.i(
          "Bundle From server" +
            FhirContext.forCached(FhirVersionEnum.R4)
              .newJsonParser()
              .encodeResourceToString(it.second)
        )
        (it.second as Bundle).entry.forEachIndexed { index, bundleEntry ->
          val response = bundleEntry.response
          val url = response.location.toString() + "?_elements=identifier"
          val downloadBundle = dataSource?.download(url)
          if (downloadBundle == null) {
            Timber.i("Resource failed in upload" + downloadBundle?.id.toString())
            idsToDeleteFromUpdate.remove(it.first.ids[index])
          } else {
            Timber.i("Resource Found after upload" + downloadBundle.id.toString())
          }
        }
        database.deleteUpdates(LocalChangeToken(idsToDeleteFromUpdate))
        when (it.second) {
          is Bundle -> updateVersionIdAndLastUpdated(it.second as Bundle)
          else -> updateVersionIdAndLastUpdated(it.second)
        }
      } catch (exception: Exception) {
        Timber.i(exception)
      }
    }
  }

  override suspend fun syncUpload(
    upload: suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  ) {
    val localChanges = database.getAllLocalChanges()
    if (localChanges.isNotEmpty()) {
      syncUploadStrategyContext.rearrangeSyncList(
        localChanges,
        database,
        ::collectAndEmitLocalChanges,
        upload
      )
    }
  }

  override fun setSyncUploadStrategy(syncUploadStrategy: SyncStrategyTypes) {
    syncUploadStrategyContext.setSyncStrategy(syncUploadStrategy)
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
      location?.split("/")?.takeIf { it.size > 3 }?.let {
        it[it.size - 3] to ResourceType.fromCode(it[it.size - 4])
      }
}
