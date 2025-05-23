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
import java.io.BufferedReader
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Resource

class ResourcesDataProvider(
  private val assetManager: AssetManager,
  private val fhirR4JsonParser: IParser,
) {
  suspend fun provideResources(onResourcesProvided: (List<Resource>) -> Unit) {
    try {
      readFileResources()
        .chunked(READ_CHUNK_SIZE)
        .map { it.map { resourceStr -> fhirR4JsonParser.parseResource(resourceStr) as Resource } }
        .forEach { onResourcesProvided(it) }
    } finally {
      closeReaders()
    }
  }

  private suspend fun readFileResources(): Sequence<String> =
    withContext(currentCoroutineContext()) {
      val ndjsonFiles =
        assetManager.list(BULK_DATA_DIR)?.filter { it.endsWith(".ndjson") } ?: emptyList()
      return@withContext ndjsonFiles
        .asSequence()
        .map { assetManager.open("$BULK_DATA_DIR/$it") }
        .flatMap {
          val reader = it.bufferedReader()
          openReaders += reader
          reader.lineSequence()
        }
    }

  private suspend fun closeReaders() =
    withContext(currentCoroutineContext()) {
      openReaders.forEach {
        try {
          it.close()
        } finally {
          openReaders -= it
        }
      }
    }

  private val openReaders = mutableListOf<BufferedReader>()

  companion object {
    private const val READ_CHUNK_SIZE = 250
    private const val BULK_DATA_DIR = "bulk_data"
  }
}
