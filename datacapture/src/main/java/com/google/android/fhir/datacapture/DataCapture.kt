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

package com.google.android.fhir.datacapture

import android.content.Context

/** Stores config and global state of the Structured Data Capture Library. */
internal object DataCapture {
  private lateinit var configuration: DataCaptureConfig

  /**
   * If client has set a configuration by implementing [DataCaptureConfig.Provider], then it returns
   * that. Otherwise, it returns a default [DataCaptureConfig].
   */
  fun getConfiguration(context: Context): DataCaptureConfig {
    if (!::configuration.isInitialized) {
      configuration =
        if (context.applicationContext is DataCaptureConfig.Provider) {
          (context.applicationContext as DataCaptureConfig.Provider).getDataCaptureConfig()
        } else {
          DataCaptureConfig()
        }
    }
    return configuration
  }
}
