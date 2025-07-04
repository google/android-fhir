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

package com.google.android.fhir.engine.benchmarks.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.tracing.Trace
import com.google.android.fhir.DatabaseErrorStrategy.RECREATE_AT_OPEN
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.engine.benchmarks.app.data.patientIndexNumberCustomSearchParameter
import com.google.android.fhir.sync.remote.HttpLogger

class MainApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    Trace.forceEnableAppTracing()

    FhirEngineProvider.init(
      FhirEngineConfiguration(
        enableEncryptionIfSupported = true,
        RECREATE_AT_OPEN,
        ServerConfiguration(
          "https://hapi.fhir.org/baseR4/",
          httpLogger =
            HttpLogger(
              HttpLogger.Configuration(
                HttpLogger.Level.BODY,
              ),
            ) {
              Log.i(TAG, "App-HttpLog")
              Log.i(TAG, it)
            },
          networkConfiguration = NetworkConfiguration(uploadWithGzip = false),
        ),
        customSearchParameters = listOf(patientIndexNumberCustomSearchParameter),
      ),
    )
  }

  private fun constructFhirEngine(): FhirEngine {
    return FhirEngineProvider.getInstance(this)
  }

  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

  companion object {
    private val TAG = MainApplication::class.java.simpleName

    fun fhirEngine(context: Context) = (context.applicationContext as MainApplication).fhirEngine
  }
}
