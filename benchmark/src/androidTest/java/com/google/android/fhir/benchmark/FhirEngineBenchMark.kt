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
import com.google.android.fhir.FhirEngineProvider
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Patient
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FhirEngineBenchMark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  @Test
  fun test_index_patient() {
    val patientFile =
      this::class.java.getResourceAsStream("/patient.json")!!.use {
        it.readBytes().decodeToString()
      }
    val fhirEngine = FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
    benchmarkRule.measureRepeated {
      val indexPatient = runWithTimingDisabled {
        FhirContext.forR4().newJsonParser().parseResource(Patient::class.java, patientFile)
      }
      runBlocking { fhirEngine.save(indexPatient) }
    }
  }
}
