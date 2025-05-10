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

package com.google.android.fhir.microbenchmark

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.search.count
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class DemoFhirEngineBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()
  private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
  private val assetManager = applicationContext.assets
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)

  @Test
  fun create() {
    val bulkFiles =
      assetManager.list(BULK_DATA_DIR)?.filter { it.endsWith(".ndjson") } ?: emptyList()
    val resources =
      bulkFiles
        .asSequence()
        .map { assetManager.open("$BULK_DATA_DIR/$it") }
        .flatMap { inputStream -> inputStream.bufferedReader().readLines() }
        .map { fhirContext.newJsonParser().parseResource(it) as Resource }
        .toList()

    val fhirEngine = FhirEngineProvider.getInstance(applicationContext)

    benchmarkRule.measureRepeated { runBlocking { fhirEngine.create(*resources.toTypedArray()) } }
    assertThat(runBlocking { fhirEngine.count<Patient> {} }).isGreaterThan(1L)
  }

  companion object {
    private const val BULK_DATA_DIR = "bulk_data"

    @JvmStatic
    @BeforeClass
    fun oneTimeSetup() {
      FhirEngineProvider.init(FhirEngineConfiguration(testMode = true))
    }

    @JvmStatic
    @AfterClass
    fun oneTimeTearDown() {
      FhirEngineProvider.cleanup()
    }
  }
}
