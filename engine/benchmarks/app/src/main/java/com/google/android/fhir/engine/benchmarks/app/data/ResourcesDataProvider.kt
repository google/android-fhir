/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.engine.benchmarks.app.data

import android.content.res.AssetManager
import ca.uhn.fhir.parser.IParser
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

private typealias AssetPath = String

private typealias AssetJsonString = String

class ResourcesDataProvider(
  private val assetManager: AssetManager,
  private val fhirR4JsonParser: IParser,
) {
  suspend fun collectResources(block: suspend (List<Resource>) -> Unit) {
    assetFilePaths()
      .map { assetManager.open(it).bufferedReader() }
      .forEach { it.useLines { line -> line.consume(block) } }
  }

  suspend fun collectPatientResources(block: (List<Resource>) -> Unit) {
    assetFilePaths({ it.contains(ResourceType.Patient.name, ignoreCase = true) })
      .map { assetManager.open(it).bufferedReader() }
      .forEach { it.useLines { line -> line.consume(block) } }
  }

  private suspend fun Sequence<AssetJsonString>.consume(block: suspend (List<Resource>) -> Unit) =
    withContext(currentCoroutineContext()) {
      chunked(READ_CHUNK_SIZE)
        .map { chunk -> chunk.map { res -> fhirR4JsonParser.parseResource(res) as Resource } }
        .forEach { block(it) }
    }

  private suspend fun assetFilePaths(filter: (String) -> Boolean = { true }): Sequence<AssetPath> =
    withContext(currentCoroutineContext()) {
      return@withContext assetManager
        .list(BULK_DATA_DIR)
        ?.filter { it.endsWith(".ndjson") && filter(it) }
        ?.map { "$BULK_DATA_DIR/$it" }
        ?.asSequence()
        ?: emptySequence()
    }

  companion object {
    private const val READ_CHUNK_SIZE = 250
    private const val BULK_DATA_DIR = "bulk_data"
  }
}
