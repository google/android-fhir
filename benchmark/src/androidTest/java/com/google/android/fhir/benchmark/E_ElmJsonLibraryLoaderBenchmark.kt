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
import java.io.StringReader
import org.hl7.fhir.r4.model.Library
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader

@RunWith(AndroidJUnit4::class)
class E_ElmJsonLibraryLoaderBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  private fun open(assetName: String): InputStream? {
    return javaClass.getResourceAsStream(assetName)!!
  }

  @Test
  fun parseImmunityCheckCqlFromFhirLibrary() {
    benchmarkRule.measureRepeated {
      val immunityCheckLibrary = runWithTimingDisabled {
        val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
        val jsonParser = fhirContext.newJsonParser()
        jsonParser.parseResource(open("/immunity-check/ImmunityCheck.json")) as Library
      }

      val jsonLib = immunityCheckLibrary.content.first { it.contentType == "application/elm+json" }

      val immunityCheckCqlLibrary = JsonCqlLibraryReader().read(StringReader(String(jsonLib.data)))

      assertThat(immunityCheckCqlLibrary.identifier.id).isEqualTo("ImmunityCheck")
    }
  }

  @Test
  fun parseFhirHelpersCqlFromFhirLibrary() {
    benchmarkRule.measureRepeated {
      val fhirHelpersLibrary = runWithTimingDisabled {
        val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
        val jsonParser = fhirContext.newJsonParser()
        jsonParser.parseResource(open("/immunity-check/FhirHelpers.json")) as Library
      }

      val jsonLib = fhirHelpersLibrary.content.first { it.contentType == "application/elm+json" }

      val fhirHelpersCqlLibrary = JsonCqlLibraryReader().read(StringReader(String(jsonLib.data)))

      assertThat(fhirHelpersCqlLibrary.identifier.id).isEqualTo("FHIRHelpers")
    }
  }
}
