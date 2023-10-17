/*
 * Copyright 2022-2023 Google LLC
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
package com.google.android.fhir.configurablecare

import android.app.Application
import android.content.Context
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.DatabaseErrorStrategy.RECREATE_AT_OPEN
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.search.search
import com.google.android.fhir.sync.Sync
import com.google.android.fhir.sync.remote.HttpLogger
import com.google.android.fhir.configurablecare.ReferenceUrlResolver
import com.google.android.fhir.configurablecare.care.CarePlanManager
import com.google.android.fhir.configurablecare.care.RequestManager
import com.google.android.fhir.configurablecare.care.TaskManager
import com.google.android.fhir.configurablecare.care.TestRequestHandler
import com.google.android.fhir.configurablecare.data.FhirSyncWorker
import com.google.android.fhir.configurablecare.external.ValueSetResolver
import timber.log.Timber

class FhirApplication : Application(), DataCaptureConfig.Provider {
  private val BASE_URL = "http://10.0.2.2:8080/fhir/"
  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }
  private val taskManager: TaskManager by lazy { constructTaskManager() }
  private val carePlanManager: CarePlanManager by lazy { constructCarePlanManager() }
  private val requestManager: RequestManager by lazy { constructRequestManager() }
  private var dataCaptureConfig: DataCaptureConfig? = null

  private val dataStore by lazy { DemoDataStore(this) }

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
    FhirEngineProvider.init(
      FhirEngineConfiguration(
        enableEncryptionIfSupported = false,
        RECREATE_AT_OPEN,
        ServerConfiguration(
          BASE_URL,
          httpLogger =
            HttpLogger(
              HttpLogger.Configuration(
                if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.BASIC
              )
            ) { Timber.tag("App-HttpLog").d(it) }
        )
      )
    )
    Sync.oneTimeSync<FhirSyncWorker>(this)

    dataCaptureConfig =
      DataCaptureConfig().apply {
        urlResolver = ReferenceUrlResolver(this@FhirApplication as Context)
        valueSetResolverExternal = object : ValueSetResolver() {}
        xFhirQueryResolver = XFhirQueryResolver { it -> fhirEngine.search(it).map { it.resource } }
      }
    ValueSetResolver.init(this@FhirApplication)
  }

  private fun constructFhirEngine(): FhirEngine {
    return FhirEngineProvider.getInstance(this)
  }

  private fun constructCarePlanManager(): CarePlanManager {
    return CarePlanManager(fhirEngine, FhirContext.forR4(), this)
  }

  private fun constructRequestManager(): RequestManager {
    return RequestManager(fhirEngine, FhirContext.forR4(), TestRequestHandler())
  }

  private fun constructTaskManager(): TaskManager {
    return TaskManager(fhirEngine)
  }

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as FhirApplication).fhirEngine

    fun dataStore(context: Context) = (context.applicationContext as FhirApplication).dataStore

    fun carePlanManager(context: Context) =
      (context.applicationContext as FhirApplication).carePlanManager

    fun requestManager(context: Context) =
      (context.applicationContext as FhirApplication).requestManager

    fun taskManager(context: Context) = (context.applicationContext as FhirApplication).taskManager
  }

  override fun getDataCaptureConfig(): DataCaptureConfig = dataCaptureConfig ?: DataCaptureConfig()
}
