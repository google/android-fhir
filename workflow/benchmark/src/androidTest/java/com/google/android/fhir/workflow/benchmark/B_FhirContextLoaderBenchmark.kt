/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.workflow.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Enumerations
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class B_FhirContextLoaderBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  /**
   * FhirContexts generally take 2 seconds to load completely. This test mimics that loading time.
   * getResourceDefinition forces the initialization of the Context.
   */
  @Test
  fun loadDstu2() {
    benchmarkRule.measureRepeated {
      assertThat(
          FhirContext.forDstu2().getResourceDefinition(Enumerations.ResourceType.ACCOUNT.toCode())
        )
        .isNotNull()
    }
  }

  @Test
  fun loadDstu3() {
    benchmarkRule.measureRepeated {
      assertThat(
          FhirContext.forDstu3().getResourceDefinition(Enumerations.ResourceType.ACCOUNT.toCode())
        )
        .isNotNull()
    }
  }

  @Test
  fun loadR4() {
    benchmarkRule.measureRepeated {
      assertThat(
          FhirContext.forR4().getResourceDefinition(Enumerations.ResourceType.ACCOUNT.toCode())
        )
        .isNotNull()
    }
  }

  @Test
  fun loadR5() {
    benchmarkRule.measureRepeated {
      assertThat(
          FhirContext.forR5().getResourceDefinition(Enumerations.ResourceType.ACCOUNT.toCode())
        )
        .isNotNull()
    }
  }
}
