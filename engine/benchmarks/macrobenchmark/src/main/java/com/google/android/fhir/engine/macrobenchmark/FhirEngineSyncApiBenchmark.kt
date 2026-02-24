/*
 * Copyright 2025-2026 Google LLC
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
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class FhirEngineSyncApiBenchmark {

  @get:Rule val benchmarkRule = MacrobenchmarkRule()

  @Test
  fun tracingSyncApi() {
    val metrics =
      listOf<Metric>(
        TraceSectionMetric(
          "DownloadFhirSyncWorkerSection",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "BundleUploadFhirSyncWorkerSection",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "PerResourceUploadFhirSyncWorkerSection",
          mode = TraceSectionMetric.Mode.Average,
        ),
      )

    benchmarkRule.measureRepeated(
      packageName = TARGET_PACKAGE,
      metrics = metrics,
      iterations = 1,
      startupMode = null,
      setupBlock = { startActivityAndWait() },
    ) {
      clickOnTestTag("syncBenchmarkSection")

      @Suppress("ControlFlowWithEmptyBody")
      // Loops indefinitely until done - todo: add some form of timeout
      while (!device.wait(Until.gone(By.textStartsWith("Waiting \u2026")), 1000)) {}
      @Suppress("ControlFlowWithEmptyBody")
      while (!device.wait(Until.gone(By.textStartsWith("Running \u2026")), 1000)) {}

      device.pressBack()
    }
  }
}
