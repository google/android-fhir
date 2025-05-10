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

import android.content.Context
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>`
 *    tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces for
 * investigating your app's performance.
 */
@OptIn(ExperimentalMetricApi::class)
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {

  @get:Rule val benchmarkRule = MacrobenchmarkRule()

  private val applicationContext = ApplicationProvider.getApplicationContext<Context>()

  @Test
  fun tracingCreate() {
    benchmarkRule.measureRepeated(
      packageName = TARGET_PACKAGE,
      metrics = listOf(TraceSectionMetric("Create API")),
      iterations = DEFAULT_ITERATIONS,
      startupMode = null,
      setupBlock = { startActivityAndWait() },
    ) {
      clickOnId("create")
    }
  }

  private fun MacrobenchmarkScope.clickOnId(resourceId: String) {
    val selector = By.res(TARGET_PACKAGE, resourceId)
    if (!device.wait(Until.hasObject(selector), 2_500)) {
      fail("Did not find object with id $resourceId")
    }

    device.findObject(selector).click()
    // Chill to ensure we capture the end of the click span in the trace.
    Thread.sleep(100)
  }
}
