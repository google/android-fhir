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

package com.google.android.fhir.resource

import androidx.work.Data
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.LocalChange
import com.google.android.fhir.ResourceForDatabaseToSave
import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.search.Search
import com.google.android.fhir.sync.ConflictResolver
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.ResourceBundleAndAssociatedLocalChangeTokens
import com.google.android.fhir.sync.UploadWorkManager
import com.google.common.truth.Truth.assertThat
import java.time.OffsetDateTime
import java.util.Date
import java.util.LinkedList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Patient
import org.json.JSONArray
import org.json.JSONObject

/** Utilities for testing. */
class TestingUtils constructor(private val iParser: IParser) {

  /** Asserts that the `expected` and the `actual` FHIR resources are equal. */
  fun assertResourceEquals(expected: IAnyResource?, actual: IAnyResource?) {
    assertThat(iParser.encodeResourceToString(actual))
      .isEqualTo(iParser.encodeResourceToString(expected))
  }

  /** Asserts that the `expected` and the `actual` FHIR resources are not equal. */
  fun assertResourceNotEquals(expected: IAnyResource?, actual: IAnyResource?) {
    assertThat(iParser.encodeResourceToString(actual))
      .isNotEqualTo(iParser.encodeResourceToString(expected))
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
  fun <R : IAnyResource> readFromFile(clazz: Class<R>, filename: String): R {
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

    override suspend fun download(path: String): IAnyResource {
      return Bundle().apply { type = Bundle.BundleType.SEARCHSET }
    }

    override suspend fun upload(bundle: IBaseBundle): IAnyResource {
      return Bundle().apply { type = Bundle.BundleType.TRANSACTIONRESPONSE }
    }
  }

  open class TestDownloadManagerImpl(
    queries: List<String> = listOf("Patient?address-city=NAIROBI"),
  ) : DownloadWorkManager {
    private val urls = LinkedList(queries)
    override suspend fun getResourceTypeList(): Collection<String> = listOf("Patient", "Encounter")

    override suspend fun getNextRequestUrl(context: SyncDownloadContext): String? = urls.poll()

    override suspend fun processResponse(response: IAnyResource): Collection<IAnyResource> {
      val patient = Patient().setMeta(Meta().setLastUpdated(Date()))
      return listOf(patient)
    }
  }

  object TestFhirEngineImpl : FhirEngine {
    override suspend fun create(vararg resource: IAnyResource) = emptyList<String>()

    override suspend fun update(vararg resource: IAnyResource) {}

    override suspend fun get(resourceType: String, id: String): IAnyResource {
      return Patient()
    }

    override suspend fun delete(resourceType: String, id: String) {}

    override suspend fun <R : IAnyResource> search(search: Search): List<R> {
      return emptyList()
    }

    override suspend fun syncUpload(
      upload:
        suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, List<ResourceForDatabaseToSave>>>
    ) {
      upload(listOf())
    }

    override suspend fun syncDownload(
      conflictResolver: ConflictResolver,
      download: suspend (SyncDownloadContext) -> Flow<List<IAnyResource>>
    ) {
      download(
          object : SyncDownloadContext {
            override suspend fun getLatestTimestampFor(resourceType: String): String {
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

    override suspend fun clearDatabase() {}

    override suspend fun getLocalChange(resourceType: String, id: String): LocalChange? {
      TODO("Not yet implemented")
    }

    override suspend fun purge(resourceType: String, id: String, forcePurge: Boolean) {}
  }

  object TestFailingDatasource : DataSource {

    override suspend fun download(path: String): IAnyResource {
      val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
      // data size exceeding the bytes acceptable by WorkManager serializer
      val dataSize = Data.MAX_DATA_BYTES + 1
      val hugeStackTraceMessage = (1..dataSize).map { allowedChars.random() }.joinToString("")
      throw Exception(hugeStackTraceMessage)
    }

    override suspend fun upload(bundle: IBaseBundle): IAnyResource {
      throw Exception("Posting Bundle failed...")
    }
  }

  class BundleDataSource(val onPostBundle: suspend (IBaseBundle) -> IAnyResource) : DataSource {

    override suspend fun download(path: String): IAnyResource {
      TODO("Not yet implemented")
    }

    override suspend fun upload(bundle: IBaseBundle) = onPostBundle(bundle)
  }

  class TestUploadWorkManagerImpl : UploadWorkManager {
    override fun generate(
      localChanges: List<List<LocalChange>>
    ): List<ResourceBundleAndAssociatedLocalChangeTokens> {
      return listOf()
    }

    override fun getUploadResult(
      response: IAnyResource,
      localChangeTokens: List<LocalChangeToken>,
    ): Pair<LocalChangeToken, List<ResourceForDatabaseToSave>> {
      return localChangeTokens[0] to listOf()
    }
  }
}
