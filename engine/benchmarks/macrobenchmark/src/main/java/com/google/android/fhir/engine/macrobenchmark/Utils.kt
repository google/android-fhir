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

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.fail

const val TARGET_PACKAGE = "com.google.android.fhir.engine.benchmarks.app"
const val DEFAULT_ITERATIONS = 10

fun MacrobenchmarkScope.clickOnTestTag(testTag: String) {
  val selector = By.res(testTag)
  if (!device.wait(Until.hasObject(selector), 2_500)) {
    fail("Did not find object with testTag $testTag")
  }

  device.findObject(selector).click()
  // Chill to ensure we capture the end of the click span in the trace.
  Thread.sleep(100)
}
