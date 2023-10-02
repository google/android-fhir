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
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.cql.engine.fhir.model.Dstu2FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver

@RunWith(AndroidJUnit4::class)
@Suppress("ktlint:standard:class-naming")
class C_CqlEngineFhirContextLoaderBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  /**
   * The CQL engine FhirModelResolvers need a complete FhirContext loaded with added classes. The
   * loading of the FhirContext has already happen on B, thus this is just for the added classes
   * from the Engine.
   */
  @Test
  fun loadDstu2FhirModelResolver() {
    benchmarkRule.measureRepeated { assertThat(Dstu2FhirModelResolver()).isNotNull() }
  }

  @Test
  fun loadDstu3FhirModelResolver() {
    benchmarkRule.measureRepeated { assertThat(Dstu3FhirModelResolver()).isNotNull() }
  }

  @Test
  fun loadR4FhirModelResolver() {
    benchmarkRule.measureRepeated { assertThat(R4FhirModelResolver()).isNotNull() }
  }
}
