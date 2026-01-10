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

package com.google.android.fhir.datacapture

import android.content.Context

actual object DataCapture {

  private lateinit var configuration: DataCaptureConfig

  fun initialize(context: Context) {
    if (!::configuration.isInitialized) {
      configuration =
        if (context.applicationContext is DataCaptureConfig.Provider) {
          (context.applicationContext as DataCaptureConfig.Provider).getDataCaptureConfig()
        } else {
          DataCaptureConfig(localDateTimeFormatter = AndroidLocalDateFormatter(context))
        }
    }
  }

  actual fun getConfiguration(): DataCaptureConfig {
    if (this::configuration.isInitialized) {
      return configuration
    } else {
      throw Exception(
        "DataCapture not initialized. Initialize the library with DataCapture.initialize(context) ",
      )
    }
  }
}
