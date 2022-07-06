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

package com.google.android.fhir.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.workflow.FhirOperator
import com.google.common.truth.Truth.assertThat
import java.io.InputStream
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Resource
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
class CQLEvaluatorBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  private val fhirEngine =
    FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)

  private val json = fhirContext.newJsonParser()

  private fun open(assetName: String): InputStream? {
    return javaClass.getResourceAsStream(assetName)
  }

  private fun load(assetName: String): IBaseResource {
    return json.parseResource(open(assetName))
  }

  @Test
  fun evaluatesFirstTimeTest() = runBlocking {
    val patientImmunizationHistory =
      json.parseResource(open("/immunity-check/ImmunizationHistory.json")) as Bundle
    for (entry in patientImmunizationHistory.entry) {
      fhirEngine.create(entry.resource)
    }

    benchmarkRule.measureRepeated {
      val fhirOperator = FhirOperator(fhirContext, fhirEngine)

      fhirOperator.loadLibs(load("/immunity-check/ImmunityCheck.json") as Bundle)

      runEvaluateLibrary(fhirOperator)
    }
  }

  @Test
  fun evaluatesRepeatTest() = runBlocking {
    val patientImmunizationHistory =
      json.parseResource(open("/immunity-check/ImmunizationHistory.json")) as Bundle
    for (entry in patientImmunizationHistory.entry) {
      fhirEngine.create(entry.resource)
    }

    val fhirOperator = FhirOperator(fhirContext, fhirEngine)

    fhirOperator.loadLibs(load("/immunity-check/ImmunityCheck.json") as Bundle)

    // Removes first run.
    runEvaluateLibrary(fhirOperator)

    // Start Test.
    benchmarkRule.measureRepeated { runEvaluateLibrary(fhirOperator) }
  }

  fun runEvaluateLibrary(fhirOperator: FhirOperator) {
    val results =
      fhirOperator.evaluateLibrary(
        "http://localhost/Library/ImmunityCheck|1.0.0",
        "d4d35004-24f8-40e4-8084-1ad75924514f",
        setOf("CompletedImmunization")
      ) as
        Parameters

    assertThat(results.getParameterBool("CompletedImmunization")).isTrue()
  }
}
