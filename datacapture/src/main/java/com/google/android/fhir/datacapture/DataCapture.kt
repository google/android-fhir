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

import android.app.Application
import android.content.Context

/**
 * The clients may use [DataCapture] to provide [DataCaptureConfig] to the library. The clients may
 * set the configuration by implementing [DataCaptureConfig.Provider] interface in the [Application]
 * class. The library would load the configuration by calling
 * [DataCaptureConfig.Provider.getDataCaptureConfiguration].
 */
internal object DataCapture {
  private lateinit var configuration: DataCaptureConfig

  /**
   * If client has set a configuration by implementing [DataCaptureConfig.Provider], then it returns
   * that. Otherwise, it returns a default [DataCaptureConfig].
   */
  fun getConfiguration(context: Context?): DataCaptureConfig {
    if (!::configuration.isInitialized) {
      configuration =
        if (context?.applicationContext is DataCaptureConfig.Provider) {
          (context.applicationContext as DataCaptureConfig.Provider).getDataCaptureConfiguration()
        } else {
          DataCaptureConfig()
        }
    }
    return configuration
  }
}
