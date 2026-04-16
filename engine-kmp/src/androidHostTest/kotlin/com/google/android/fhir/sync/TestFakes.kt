/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.sync

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.SearchResult
import com.google.android.fhir.db.LocalChangeResourceReference
import com.google.android.fhir.search.Search
import com.google.android.fhir.sync.download.DownloadRequest
import com.google.android.fhir.sync.download.UrlDownloadRequest
import com.google.android.fhir.sync.upload.SyncUploadProgress
import com.google.android.fhir.sync.upload.UploadRequestResult
import com.google.android.fhir.sync.upload.UploadStrategy
import com.google.fhir.model.r4.Bundle
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Meta
import com.google.fhir.model.r4.Patient
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.terminologies.ResourceType
import com.google.fhir.model.r4.FhirDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

internal object TestDataSourceImpl : DataSource {

  override suspend fun download(downloadRequest: DownloadRequest): Resource =
    when (downloadRequest) {
      is UrlDownloadRequest ->
        Bundle(type = Enumeration(value = Bundle.BundleType.Searchset))
      else ->
        Bundle(type = Enumeration(value = Bundle.BundleType.Batch_Response))
    }

  override suspend fun upload(request: com.google.android.fhir.sync.upload.request.UploadRequest): Resource {
    return Bundle(
      type = Enumeration(value = Bundle.BundleType.Transaction_Response),
      entry = listOf(Bundle.Entry(resource = Patient(id = "123"))),
    )
  }
}

internal open class TestDownloadManagerImpl(
  private val queries: List<String> = listOf("Patient?address-city=NAIROBI"),
) : DownloadWorkManager {
  private val urls = ArrayDeque(queries)

  override suspend fun getNextRequest(): DownloadRequest? =
    urls.removeFirstOrNull()?.let { DownloadRequest.of(it) }

  override suspend fun getSummaryRequestUrls(): Map<ResourceType, String> =
    queries
      .map { ResourceType.valueOf(it.substringBefore("?")) to it.plus("?_summary=count") }
      .toMap()

  override suspend fun processResponse(response: Resource): Collection<Resource> {
    val patient = Patient(
      id = "test-patient",
      meta = Meta(lastUpdated = com.google.fhir.model.r4.Instant(value = FhirDateTime.fromString(Clock.System.now().toString()))),
    )
    return listOf(patient)
  }
}

internal object TestFhirEngineImpl : FhirEngine {
  override suspend fun create(vararg resource: Resource) = emptyList<String>()

  override suspend fun update(vararg resource: Resource) {}

  override suspend fun get(type: ResourceType, id: String): Resource {
    return Patient()
  }

  override suspend fun delete(type: ResourceType, id: String) {}

  override suspend fun <R : Resource> search(search: Search): List<SearchResult<R>> {
    return emptyList()
  }

  override suspend fun syncUpload(
    uploadStrategy: UploadStrategy,
    upload:
      (suspend (List<LocalChange>, List<LocalChangeResourceReference>) -> Flow<UploadRequestResult>),
  ): Flow<SyncUploadProgress> = flow {
    emit(SyncUploadProgress(1, 1))
    upload(getLocalChanges(ResourceType.Patient, "123"), emptyList()).collect {
      when (it) {
        is UploadRequestResult.Success -> emit(SyncUploadProgress(0, 1))
        is UploadRequestResult.Failure -> emit(SyncUploadProgress(1, 1, it.uploadError))
      }
    }
  }

  override suspend fun syncDownload(
    conflictResolver: ConflictResolver,
    download: suspend () -> Flow<List<Resource>>,
  ) {
    download().collect()
  }

  override suspend fun withTransaction(block: suspend FhirEngine.() -> Unit) {}

  override suspend fun count(search: Search): Long {
    return 0
  }

  override suspend fun getLastSyncTimeStamp(): Instant? {
    return Clock.System.now()
  }

  override suspend fun clearDatabase() {}

  override suspend fun getLocalChanges(type: ResourceType, id: String): List<LocalChange> {
    return listOf(
      LocalChange(
        resourceType = type.name,
        resourceId = id,
        payload = """{ "resourceType" : "$type", "id" : "$id" }""",
        token = LocalChangeToken(listOf(1)),
        type = LocalChange.Type.INSERT,
        timestamp = Clock.System.now(),
      ),
    )
  }

  override suspend fun purge(type: ResourceType, id: String, forcePurge: Boolean) {}

  override suspend fun purge(type: ResourceType, ids: Set<String>, forcePurge: Boolean) {}
}

internal object TestFailingDatasource : DataSource {

  override suspend fun download(downloadRequest: DownloadRequest): Resource =
    when (downloadRequest) {
      is UrlDownloadRequest -> {
        throw Exception("Download failed with a large error message for testing purposes")
      }
      else -> throw Exception("Posting Download Bundle failed...")
    }

  override suspend fun upload(request: com.google.android.fhir.sync.upload.request.UploadRequest): Resource {
    throw Exception("Posting Upload Bundle failed...")
  }
}
