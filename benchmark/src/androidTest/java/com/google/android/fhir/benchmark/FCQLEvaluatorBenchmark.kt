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
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Parameters
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FCQLEvaluatorBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  private fun open(assetName: String): InputStream? {
    return javaClass.getResourceAsStream(assetName)
  }

  @Test
  fun evaluatesLibrary() = runBlocking {
    benchmarkRule.measureRepeated {
      val fhirContext = runWithTimingDisabled {
        FhirContext.forCached(FhirVersionEnum.R4)
      }

      val jsonParser = runWithTimingDisabled {
        fhirContext.newJsonParser()
      }

      val patientImmunizationHistory = runWithTimingDisabled {
        jsonParser.parseResource(open("/immunity-check/ImmunizationHistory.json")) as Bundle
      }

      val fhirEngine = runWithTimingDisabled { runBlocking {
        val fhirEngine = FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
        for (entry in patientImmunizationHistory.entry) {
          fhirEngine.create(entry.resource)
        }
        fhirEngine
      }}

      val lib = runWithTimingDisabled {
        jsonParser.parseResource(open("/immunity-check/ImmunityCheck.json")) as Bundle
      }

      val fhirOperator = runWithTimingDisabled {
        val fhirOperator = FhirOperator(fhirContext, fhirEngine)
        fhirOperator.loadLibs(lib)
        fhirOperator
      }

      runEvaluateLibrary(fhirOperator)
    }
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
