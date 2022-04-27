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

package com.google.android.fhir.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngineProvider
import com.google.common.truth.Truth.assertThat
import java.io.InputStream
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Composition
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will output the
 * result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class EngineBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  private val fhirEngine = FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)

  private val json = fhirContext.newJsonParser()

  private fun open(assetName: String): InputStream? {
    return javaClass.getResourceAsStream(assetName)
  }

  @Test
  fun createCOVIDImmunization() = runBlocking {
    val parsedJSon =
      json.parseResource(open("/covid-check/COVIDImmunizationHistory.json")) as Composition

    benchmarkRule.measureRepeated {
      runBlocking {
        //assertThat(fhirEngine.get(ResourceType.Patient, "#1")).isNull()
        // assertThat(fhirEngine.get(ResourceType.Immunization, "#2")).isNull()
        // assertThat(fhirEngine.get(ResourceType.Organization, "#3")).isNull()

        val ids = fhirEngine.create(parsedJSon)
        println(ids[0])
        println(((fhirEngine.get(ResourceType.Composition, ids[0]) as Composition).subject.resource as Patient).id)
        assertThat(fhirEngine.get(ResourceType.Composition, ids[0])).isNotNull()
        assertThat(fhirEngine.get(ResourceType.Patient, "#1")).isNotNull()
        // assertThat(fhirEngine.get(ResourceType.Immunization, "#2")).isNotNull()
        // assertThat(fhirEngine.get(ResourceType.Organization, "#3")).isNotNull()
      }
    }
  }
}
