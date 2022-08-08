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

package com.google.android.fhir.demo

import android.app.Application
import android.content.Context
import com.google.android.fhir.demo.data.JsonPeriodicSyncWorker
import com.google.android.fhir.json.DatabaseErrorStrategy.RECREATE_AT_OPEN
import com.google.android.fhir.json.JsonEngine
import com.google.android.fhir.json.JsonEngineConfiguration
import com.google.android.fhir.json.JsonEngineProvider
import com.google.android.fhir.json.ServerConfiguration
import com.google.android.fhir.json.sync.Sync
import timber.log.Timber

class JsonApplication : Application() {
  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val jsonEngine: JsonEngine by lazy { constructJsonEngine() }

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
    JsonEngineProvider.init(
      JsonEngineConfiguration(
        enableEncryptionIfSupported = false,
        RECREATE_AT_OPEN,
        ServerConfiguration("https://hapi.fhir.org/baseR4/")
      )
    )
    Sync.oneTimeSync<JsonPeriodicSyncWorker>(this)
  }

  private fun constructJsonEngine(): JsonEngine {
    return JsonEngineProvider.getInstance(this)
  }

  companion object {
    fun jsonEngine(context: Context) = (context.applicationContext as JsonApplication).jsonEngine
  }
}
