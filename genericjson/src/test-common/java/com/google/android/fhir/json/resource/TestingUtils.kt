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

package com.google.android.fhir.json.resource

import com.google.common.truth.Truth.assertThat
import org.json.JSONArray
import org.json.JSONObject

/** Utilities for testing. */
object TestingUtils {

  /** Asserts that the `expected` and the `actual` FHIR resources are equal. */
  fun assertResourceEquals(expected: JSONObject?, actual: JSONObject?) {
    assertThat(actual.toString()).isEqualTo(expected.toString())
  }

  /** Asserts that the `expected` and the `actual` FHIR resources are not equal. */
  fun assertResourceNotEquals(expected: JSONObject?, actual: JSONObject?) {
    assertThat(actual.toString()).isNotEqualTo(expected.toString())
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

  // object TestDataSourceImpl : DataSource {
  //
  //   override suspend fun download(path: String): JSONObject {
  //     return Bundle().apply { type = Bundle.BundleType.SEARCHSET }
  //   }
  //
  //   override suspend fun upload(bundle: Bundle): JSONObject {
  //     return Bundle().apply { type = Bundle.BundleType.TRANSACTIONRESPONSE }
  //   }
  // }
  //
  // open class TestDownloadManagerImpl(
  //   queries: List<String> = listOf("Patient?address-city=NAIROBI")
  // ) : DownloadWorkManager {
  //   private val urls = LinkedList(queries)
  //
  //   override suspend fun getNextRequestUrl(): String? = urls.poll()
  //
  //   override suspend fun processResponse(response: JSONObject): Collection<JSONObject> {
  //     val patient = Patient().setMeta(Meta().setLastUpdated(Date()))
  //     return listOf(patient)
  //   }
  // }
  //
  // class TestDownloadManagerImplWithQueue(
  //   queries: List<String> = listOf("Patient/bob", "Encounter/doc")
  // ) : TestDownloadManagerImpl(queries)
  //
  // object TestFhirEngineImpl : JsonEngine {
  //   override suspend fun create(vararg resource: JSONObject) = emptyList<String>()
  //
  //   override suspend fun update(vararg resource: JSONObject) {}
  //
  //   override suspend fun get(id: String): JSONObject {
  //     return JSONObject()
  //   }
  //
  //   override suspend fun delete(id: String) {}
  //
  //   override suspend fun syncUpload(
  //     upload: suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, JSONObject>>
  //   ) {
  //     upload(listOf())
  //   }
  //
  //   override suspend fun syncDownload(
  //     conflictResolver: ConflictResolver,
  //     download: suspend () -> Flow<List<JSONObject>>
  //   ) {
  //     download()
  //       .collect {}
  //   }
  //
  // }
  //
  // object TestFailingDatasource : DataSource {
  //
  //   override suspend fun download(path: String): JSONObject {
  //     val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
  //     // data size exceeding the bytes acceptable by WorkManager serializer
  //     val dataSize = Data.MAX_DATA_BYTES + 1
  //     val hugeStackTraceMessage = (1..dataSize).map { allowedChars.random() }.joinToString("")
  //     throw Exception(hugeStackTraceMessage)
  //   }
  //
  //   override suspend fun upload(bundle: JSONObject): JSONObject {
  //     throw Exception("Posting Bundle failed...")
  //   }
  // }
  //
  // class BundleDataSource(val onPostBundle: suspend (JSONObject) -> JSONObject) : DataSource {
  //
  //   override suspend fun download(path: String): JSONObject {
  //     TODO("Not yet implemented")
  //   }
  //
  //   override suspend fun upload(bundle: JSONObject) = onPostBundle(bundle)
  // }
}
