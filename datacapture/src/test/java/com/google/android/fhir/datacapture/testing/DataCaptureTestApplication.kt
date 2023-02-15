/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture.testing

import android.app.Application
import com.google.android.fhir.datacapture.DataCaptureConfig

/** Application class when you want to test the [DataCaptureConfig.Provider]. */
internal class DataCaptureTestApplication : Application(), DataCaptureConfig.Provider {
  var dataCaptureConfiguration: DataCaptureConfig? = null

  override fun getDataCaptureConfig(): DataCaptureConfig {
    if (dataCaptureConfiguration == null) {
      dataCaptureConfiguration = DataCaptureConfig()
    }

    return dataCaptureConfiguration!!
  }
}
