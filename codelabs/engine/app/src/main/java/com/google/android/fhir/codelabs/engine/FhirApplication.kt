/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.codelabs.engine

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.fhir.DatabaseErrorStrategy.RECREATE_AT_OPEN
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.sync.remote.HttpLogger

class FhirApplication : Application() {
  private val fhirEngine: FhirEngine by lazy { FhirEngineProvider.getInstance(this) }

  override fun onCreate() {
    super.onCreate()

    FhirEngineProvider.init(
      FhirEngineConfiguration(
        enableEncryptionIfSupported = true,
        RECREATE_AT_OPEN,
        ServerConfiguration(
          baseUrl = "http://10.0.2.2:8080/fhir/",
          httpLogger =
            HttpLogger(
              HttpLogger.Configuration(
                if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.BASIC
              )
            ) { Log.d("App-HttpLog", it) },
        ),
      )
    )
  }

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as FhirApplication).fhirEngine
  }
}
