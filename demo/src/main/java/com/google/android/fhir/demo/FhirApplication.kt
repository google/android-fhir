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
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.fhir.DatabaseErrorStrategy.RECREATE_AT_OPEN
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.demo.data.FhirSyncWorker
import com.google.android.fhir.demo.security.SecurityRequirementViolationReceiver
import com.google.android.fhir.security.FhirSecurityConfiguration
import com.google.android.fhir.security.LockScreenComplexity
import com.google.android.fhir.security.LockScreenRequirement
import com.google.android.fhir.security.RequirementViolationAction
import com.google.android.fhir.sync.Sync
import com.google.android.fhir.sync.remote.HttpLogger
import java.util.EnumSet
import timber.log.Timber

class FhirApplication : Application(), DataCaptureConfig.Provider {
  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

  private var dataCaptureConfig: DataCaptureConfig? = null

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
    FhirEngineProvider.init(
      FhirEngineConfiguration(
        enableEncryptionIfSupported = true,
        databaseErrorStrategy = RECREATE_AT_OPEN,
        securityConfiguration =
          FhirSecurityConfiguration(
            LockScreenRequirement(
              complexity = LockScreenComplexity.HIGH,
              EnumSet.noneOf(RequirementViolationAction::class.java)
            ),
            PendingIntent.getBroadcast(
              applicationContext,
              /* requestCode= */ 0,
              Intent(applicationContext, SecurityRequirementViolationReceiver::class.java),
              /* flags= */ PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
          ),
        serverConfiguration =
          ServerConfiguration(
            "https://hapi.fhir.org/baseR4/",
            httpLogger =
              HttpLogger(
                HttpLogger.Configuration(
                  if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.BASIC
                )
              ) { Timber.tag("App-HttpLog").d(it) },
          )
      )
    )
    Sync.oneTimeSync<FhirSyncWorker>(this)

    dataCaptureConfig =
      DataCaptureConfig().apply {
        urlResolver = ReferenceUrlResolver(this@FhirApplication as Context)
      }
  }

  private fun constructFhirEngine(): FhirEngine {
    return FhirEngineProvider.getInstance(this)
  }

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as FhirApplication).fhirEngine
  }

  override fun getDataCaptureConfig(): DataCaptureConfig = dataCaptureConfig ?: DataCaptureConfig()
}
