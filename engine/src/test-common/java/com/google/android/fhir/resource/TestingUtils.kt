/*
 * Copyright 2020 Google LLC
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

import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.search.Search
import com.google.android.fhir.sync.DataSource
import com.google.common.truth.Truth
import java.time.OffsetDateTime
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.json.JSONArray
import org.json.JSONObject

/** Utilities for testing. */
class TestingUtils constructor(private val iParser: IParser) {

  /** Asserts that the `expected` and the `actual` FHIR resources are equal. */
  fun assertResourceEquals(expected: Resource?, actual: Resource?) {
    Truth.assertThat(iParser.encodeResourceToString(actual))
      .isEqualTo(iParser.encodeResourceToString(expected))
  }

  fun assertJsonArrayEqualsIgnoringOrder(actual: JSONArray, expected: JSONArray) {
    Truth.assertThat(actual.length()).isEqualTo(expected.length())
    val actuals = mutableListOf<String>()
    val expecteds = mutableListOf<String>()
    for (i in 0 until actual.length()) {
      actuals.add(actual.get(i).toString())
      expecteds.add(expected.get(i).toString())
    }
    actuals.sorted()
    expecteds.sorted()
    Truth.assertThat(actuals).containsExactlyElementsIn(expecteds)
  }

  /** Reads a [Resource] from given file in the `sampledata` dir */
  fun <R : Resource> readFromFile(clazz: Class<R>, filename: String): R {
    val resourceJson = readJsonFromFile(filename)
    return iParser.parseResource(clazz, resourceJson.toString()) as R
  }

  /** Reads a [JSONObject] from given file in the `sampledata` dir */
  fun readJsonFromFile(filename: String): JSONObject {
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
  }

  object TestFhirEngineImpl : FhirEngine {
    override suspend fun <R : Resource> save(vararg resource: R) {}

    override suspend fun <R : Resource> update(resource: R) {}

    override suspend fun <R : Resource> load(clazz: Class<R>, id: String): R {
      return clazz.newInstance()
    }

    override suspend fun <R : Resource> remove(clazz: Class<R>, id: String) {}

    override suspend fun <R : Resource> search(search: Search): List<R> {
      return emptyList()
    }

    override suspend fun syncUpload(
      upload: suspend (List<SquashedLocalChange>) -> List<LocalChangeToken>
    ) {
      upload(listOf())
    }

    override suspend fun syncDownload(download: suspend (SyncDownloadContext) -> List<Resource>) {
      download(
        object : SyncDownloadContext {
          override suspend fun getLatestTimestampFor(type: ResourceType): String {
            return "123456788"
          }
        }
      )
    }
    override suspend fun count(search: Search): Long {
      return 0
    }

    override suspend fun getLastSyncTimeStamp(): OffsetDateTime? {
      return OffsetDateTime.now()
    }
  }

  object TestCorruptDatasource : DataSource {
    override suspend fun loadData(path: String): Bundle {
      throw Exception("Loading failed...")
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
  }
}
