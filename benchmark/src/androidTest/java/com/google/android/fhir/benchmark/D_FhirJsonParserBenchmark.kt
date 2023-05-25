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
import java.time.ZonedDateTime
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Library
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class D_FhirJsonParserBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  private fun open(assetName: String): InputStream? {
    return javaClass.getResourceAsStream(assetName)!!
  }

  @Test
  fun parseLightFhirBundle() {
    benchmarkRule.measureRepeated {
      val jsonParser = runWithTimingDisabled {
        val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
        fhirContext.newJsonParser()
      }

      val bundle = runWithTimingDisabled { open("/immunity-check/ImmunizationHistory.json") }

      System.out.println(ZonedDateTime.now())
      assertThat((jsonParser.parseResource(bundle) as Bundle).entryFirstRep.resource.id)
        .isEqualTo("Patient/d4d35004-24f8-40e4-8084-1ad75924514f")
      System.out.println(ZonedDateTime.now())
    }
  }

  @Test
  fun parseLightFhirLibrary() {
    benchmarkRule.measureRepeated {
      val jsonParser = runWithTimingDisabled {
        val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
        fhirContext.newJsonParser()
      }

      val immunityCheckJson = runWithTimingDisabled { open("/immunity-check/ImmunityCheck.json") }
      val immunityCheckLibrary = jsonParser.parseResource(immunityCheckJson) as Library
      val fhirHelpersJson = runWithTimingDisabled { open("/immunity-check/FhirHelpers.json") }
      val fhirHelpersLibrary = jsonParser.parseResource(fhirHelpersJson) as Library

      assertThat(immunityCheckLibrary.id).isEqualTo("Library/ImmunityCheck-1.0.0")
      assertThat(immunityCheckLibrary.content[0].data.size).isEqualTo(575)
      assertThat(fhirHelpersLibrary.id).isEqualTo("Library/FHIRHelpers-4.0.1")
      assertThat(fhirHelpersLibrary.content[0].data.size).isEqualTo(17845)
    }
  }
}
