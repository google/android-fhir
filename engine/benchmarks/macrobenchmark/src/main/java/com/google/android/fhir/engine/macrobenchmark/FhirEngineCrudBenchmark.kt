/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.engine.macrobenchmark

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMetricApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class FhirEngineCrudBenchmark {

  @get:Rule val benchmarkRule = MacrobenchmarkRule()

  @Test
  fun tracingCrud() {
    benchmarkRule.measureRepeated(
      packageName = TARGET_PACKAGE,
      metrics =
        listOf(
          TraceSectionMetric("Create API", mode = TraceSectionMetric.Mode.Sum),
          TraceSectionMetric("Get API", mode = TraceSectionMetric.Mode.Average),
          TraceSectionMetric("Update API", mode = TraceSectionMetric.Mode.Average),
          TraceSectionMetric("Delete API", mode = TraceSectionMetric.Mode.Average),
        ),
      iterations = 1,
      startupMode = null,
      setupBlock = { startActivityAndWait() },
    ) {
      clickOnTestTag("crudBenchmarkSection")

      @Suppress("ControlFlowWithEmptyBody")
      // Loops indefinitely until done - todo: add some form of time
      while (!device.wait(Until.gone(By.textStartsWith("Waiting for results")), 700)){}
    }
  }
}
