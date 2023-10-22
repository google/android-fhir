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
import com.google.android.fhir.configurablecare.care.CarePlanManager
import com.google.android.fhir.configurablecare.care.RequestManager
import com.google.android.fhir.configurablecare.care.TaskManager
import com.google.android.fhir.configurablecare.care.TestRequestHandler
import com.google.android.fhir.configurablecare.data.FhirSyncWorker
import com.google.android.fhir.configurablecare.external.ValueSetResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.hl7.fhir.utilities.npm.NpmPackage
import timber.log.Timber

class FhirApplication : Application(), DataCaptureConfig.Provider {
  private val BASE_URL = "https://192.168.1.100:8080/fhir/" // "http://127.0.0.1:8080/fhir"  // "http://10.0.2.2:8080/fhir/"
  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }
  private val taskManager: TaskManager by lazy { constructTaskManager() }
  private val carePlanManager: CarePlanManager by lazy { constructCarePlanManager() }
  private val requestManager: RequestManager by lazy { constructRequestManager() }
  private var dataCaptureConfig: DataCaptureConfig? = null

  private val dataStore by lazy { DemoDataStore(this) }

  private var contextR4 : ComplexWorkerContext? = null

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
    // Sync.oneTimeSync<FhirSyncWorker>(this)
    constructR4Context()
    Sync.oneTimeSync<FhirSyncWorker>(this)

    dataCaptureConfig =
      DataCaptureConfig().apply {
        urlResolver = ReferenceUrlResolver(this@FhirApplication as Context)
        valueSetResolverExternal = object : ValueSetResolver() {}
        xFhirQueryResolver = XFhirQueryResolver { it -> fhirEngine.search(it).map { it.resource } }
      }
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

  private fun constructR4Context() = CoroutineScope(Dispatchers.IO).launch {
    println("**** creating contextR4")

    val measlesIg =
      async {
      NpmPackage.fromPackage(
        assets.open("smart-imm/ig/package.r4.tgz")
      )
    }

    val baseIg = async {
      NpmPackage.fromPackage(
        assets.open("package.tgz")
      )
    }

    val packages = arrayListOf<NpmPackage>(
      measlesIg.await(),
      baseIg.await()
    )

    println("**** read assets contextR4")
    contextR4 = ComplexWorkerContext()
    contextR4?.apply {
      loadFromMultiplePackages(packages, true)
      println("**** created contextR4")
      ValueSetResolver.init(this@FhirApplication, this)
    }
  }

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as FhirApplication).fhirEngine

    fun dataStore(context: Context) = (context.applicationContext as FhirApplication).dataStore

    fun carePlanManager(context: Context) =
      (context.applicationContext as FhirApplication).carePlanManager

    fun requestManager(context: Context) =
      (context.applicationContext as FhirApplication).requestManager

    fun taskManager(context: Context) = (context.applicationContext as FhirApplication).taskManager

    fun contextR4(context: Context) = (context.applicationContext as FhirApplication).contextR4
  }

  override fun getDataCaptureConfig(): DataCaptureConfig = dataCaptureConfig ?: DataCaptureConfig()
}
