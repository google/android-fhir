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
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlMapper

@RunWith(AndroidJUnit4::class)
class A_JacksonMapperBenchmark {
  @get:Rule val benchmarkRule = BenchmarkRule()

  /**
   * The JSONMapper and the XMLMapper take 800ms to initialize the first time on Desktop. They seem
   * to take less time on mobile or there is something pre-loading this object. Either way, it's
   * important to keep an eye on it.
   */
  @Test
  fun loadJsonMapper() {
    benchmarkRule.measureRepeated { assertThat(JsonCqlMapper.getMapper()).isNotNull() }
  }
}
