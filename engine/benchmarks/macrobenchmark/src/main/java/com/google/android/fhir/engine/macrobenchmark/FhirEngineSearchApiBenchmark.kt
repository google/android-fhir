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
class FhirEngineSearchApiBenchmark {

  @get:Rule val benchmarkRule = MacrobenchmarkRule()

  @Test
  fun tracingSearchApi() {
    val metrics =
      listOf<Metric>(
        TraceSectionMetric(
          "searchWithTypeStringSearchParameter",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "searchWithTypeNumberSearchParameter",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "searchWithTypeDateSearchParameter",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "searchWithTypeQuantitySearchParameter",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "searchPatientIdWithTokenIdentifier",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric("searchPatientHasEncounter", mode = TraceSectionMetric.Mode.Average),
        TraceSectionMetric(
          "searchPatientSortedByBirthDate",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "searchPatientWithEitherGivenNameOrBirthDate",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric("searchPatientSortedByName", mode = TraceSectionMetric.Mode.Average),
        TraceSectionMetric(
          "searchPatientGivenWithDisjunctValues",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "searchEncounterLocalLastUpdated",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "searchPatientWithIncludeGeneralPractitioner",
          mode = TraceSectionMetric.Mode.Average,
        ),
        TraceSectionMetric(
          "searchPatientWithRevIncludeConditions",
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
      clickOnTestTag("searchBenchmarkSection")

      @Suppress("ControlFlowWithEmptyBody")
      // Loops indefinitely until done - todo: add some form of timeout
      while (!device.wait(Until.gone(By.textStartsWith("Loading \u2026")), 1000)) {}
      device.pressBack()
    }
  }
}
