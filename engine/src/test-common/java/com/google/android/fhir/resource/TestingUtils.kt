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

package com.google.android.fhir.resource

import androidx.work.Data
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.search.Search
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.DownloadManager
import com.google.common.truth.Truth.assertThat
import java.time.OffsetDateTime
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.json.JSONArray
import org.json.JSONObject

/** Utilities for testing. */
class TestingUtils constructor(private val iParser: IParser) {

  /** Asserts that the `expected` and the `actual` FHIR resources are equal. */
  fun assertResourceEquals(expected: Resource?, actual: Resource?) {
    assertThat(iParser.encodeResourceToString(actual))
      .isEqualTo(iParser.encodeResourceToString(expected))
  }

  fun assertJsonArrayEqualsIgnoringOrder(actual: JSONArray, expected: JSONArray) {
    assertThat(actual.length()).isEqualTo(expected.length())
    val actuals = mutableListOf<String>()
    val expecteds = mutableListOf<String>()
    for (i in 0 until actual.length()) {
      actuals.add(actual.get(i).toString())
      expecteds.add(expected.get(i).toString())
    }
    actuals.sorted()
    expecteds.sorted()
    assertThat(actuals).containsExactlyElementsIn(expecteds)
  }

  /** Reads a [Resource] from given file in the `sampledata` dir */
  fun <R : Resource> readFromFile(clazz: Class<R>, filename: String): R {
    val resourceJson = readJsonFromFile(filename)
    return iParser.parseResource(clazz, resourceJson.toString()) as R
  }

  /** Reads a [JSONObject] from given file in the `sampledata` dir */
  private fun readJsonFromFile(filename: String): JSONObject {
    val inputStream = javaClass.getResourceAsStream(filename)
    val content = inputStream!!.bufferedReader(Charsets.UTF_8).readText()
    return JSONObject(content)
  }

  /** Reads a [JSONArray] from given file in the `sampledata` dir */
  fun readJsonArrayFromFile(filename: String): JSONArray {
    val inputStream = javaClass.getResourceAsStream(filename)
    val content = inputStream!!.bufferedReader(Charsets.UTF_8).readText()
    return JSONArray(content)
  }

  object TestDataSourceImpl : DataSource {

    override suspend fun loadData(path: String): Bundle {
      return Bundle()
    }

    override suspend fun insert(
      resourceType: String,
      resourceId: String,
      payload: String
    ): Resource {
      return Observation()
    }

    override suspend fun update(
      resourceType: String,
      resourceId: String,
      payload: String
    ): OperationOutcome {
      return OperationOutcome()
    }

    override suspend fun delete(resourceType: String, resourceId: String): OperationOutcome {
      return OperationOutcome()
    }

    override suspend fun postBundle(payload: String): Resource {
      return Bundle()
    }
  }

  object TestDownloadManagerImpl : DownloadManager {

    override fun getInitialUrl(): String {
      return "Patient?address-city=NAIROBI"
    }

    override fun createDownloadUrl(preProcessUrl: String, lastUpdate: String?): String {
      return preProcessUrl
    }

    override fun extractResourcesFromResponse(resourceResponse: Resource): Collection<Resource> {
      val patient = Patient().setMeta(Meta().setLastUpdated(Date()))
      return listOf(patient)
    }

    override fun extractNextUrlsFromResource(resourceResponse: Resource): Collection<String> {
      return mutableListOf()
    }
  }

  class TestDownloadManagerImplWithQueue : DownloadManager {
    private val queueWork = mutableListOf("Patient/bob", "Encounter/doc")

    override fun getInitialUrl(): String = TestDownloadManagerImpl.getInitialUrl()

    override fun createDownloadUrl(preProcessUrl: String, lastUpdate: String?): String =
      TestDownloadManagerImpl.createDownloadUrl(preProcessUrl, lastUpdate)

    override fun extractResourcesFromResponse(resourceResponse: Resource): Collection<Resource> =
      TestDownloadManagerImpl.extractResourcesFromResponse(resourceResponse)

    override fun extractNextUrlsFromResource(resourceResponse: Resource): Collection<String> {
      val returnQueueWork = ArrayList(queueWork)
      queueWork.clear()
      return returnQueueWork
    }
  }

  object TestFhirEngineImpl : FhirEngine {
    override suspend fun <R : Resource> create(vararg resource: R) {}

    override suspend fun <R : Resource> update(resource: R) {}

    override suspend fun <R : Resource> load(clazz: Class<R>, id: String): R {
      return clazz.newInstance()
    }

    override suspend fun <R : Resource> remove(clazz: Class<R>, id: String) {}

    override suspend fun <R : Resource> search(search: Search): List<R> {
      return emptyList()
    }

    override suspend fun syncUpload(
      upload: suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
    ) {
      upload(listOf())
    }

    override suspend fun syncDownload(
      download: suspend (SyncDownloadContext) -> Flow<List<Resource>>
    ) {
      download(
        object : SyncDownloadContext {
          override suspend fun getLatestTimestampFor(type: ResourceType): String {
            return "123456788"
          }
        }
      )
        .collect {}
    }
    override suspend fun count(search: Search): Long {
      return 0
    }

    override suspend fun getLastSyncTimeStamp(): OffsetDateTime? {
      return OffsetDateTime.now()
    }
  }

  object TestFailingDatasource : DataSource {
    override suspend fun loadData(path: String): Bundle {
      val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
      // data size exceeding the bytes acceptable by WorkManager serializer
      val dataSize = Data.MAX_DATA_BYTES + 1
      val hugeStackTraceMessage = (1..dataSize).map { allowedChars.random() }.joinToString("")
      throw Exception(hugeStackTraceMessage)
    }

    override suspend fun insert(
      resourceType: String,
      resourceId: String,
      payload: String
    ): Resource {
      throw Exception("Insertion failed...")
    }

    override suspend fun update(
      resourceType: String,
      resourceId: String,
      payload: String
    ): OperationOutcome {
      throw Exception("Updating failed...")
    }

    override suspend fun delete(resourceType: String, resourceId: String): OperationOutcome {
      throw Exception("Deleting failed...")
    }

    override suspend fun postBundle(payload: String): Resource {
      throw Exception("Posting Bundle failed...")
    }
  }

  class BundleDataSource(val onPostBundle: suspend (String) -> Resource) : DataSource {
    override suspend fun loadData(path: String): Bundle {
      TODO("Not yet implemented")
    }

    override suspend fun insert(
      resourceType: String,
      resourceId: String,
      payload: String
    ): Resource {
      TODO("Not yet implemented")
    }

    override suspend fun update(
      resourceType: String,
      resourceId: String,
      payload: String
    ): OperationOutcome {
      TODO("Not yet implemented")
    }

    override suspend fun delete(resourceType: String, resourceId: String): OperationOutcome {
      TODO("Not yet implemented")
    }

    override suspend fun postBundle(payload: String) = onPostBundle(payload)
  }
}
