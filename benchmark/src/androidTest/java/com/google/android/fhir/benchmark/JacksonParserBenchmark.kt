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
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.common.truth.Truth.assertThat
import java.io.InputStream
import org.hl7.fhir.instance.model.api.IBaseResource
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
class JacksonParserBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val json = fhirContext.newJsonParser()

  private fun open(assetName: String): InputStream? {
    return javaClass.getResourceAsStream(assetName)!!
  }

  private fun load(assetName: String): IBaseResource {
    return json.parseResource(open(assetName))
  }

  /** JSON Parsers */
  @Test
  fun parseCOVIDImmunizationHistory() {
    benchmarkRule.measureRepeated {
      assertThat(load("/immunity-check/ImmunizationHistory.json")).isNotNull()
    }
  }

  @Test
  fun parseCOVIDCheckFHIRBundle() {
    benchmarkRule.measureRepeated {
      assertThat(load("/immunity-check/ImmunityCheck.json")).isNotNull()
    }
  }
}
