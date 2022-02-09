/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.demo.screenshots

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import com.facebook.testing.screenshot.ScreenshotRunner

class ScreenShotTestRunner : AndroidJUnitRunner() {
  override fun onCreate(arguments: Bundle?) {
    ScreenshotRunner.onCreate(this, arguments)
    super.onCreate(arguments)
  }

  override fun finish(resultCode: Int, results: Bundle?) {
    ScreenshotRunner.onDestroy()
    super.finish(resultCode, results)
  }
}
