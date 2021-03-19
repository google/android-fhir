/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.reference

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineBuilder
import com.google.android.fhir.FhirPeriodicSyncWorker
import com.google.android.fhir.HapiFhirResourceDataSource
import com.google.android.fhir.api.HapiFhirService.Companion.create
import com.google.android.fhir.sync.FhirDataSource
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.RepeatInterval
import com.google.android.fhir.sync.SyncConfiguration
import com.google.android.fhir.sync.SyncData
import java.util.concurrent.TimeUnit
import org.hl7.fhir.r4.model.ResourceType

class FhirApplication : Application() {

  // only initiate the FhirEngine when used for the first time, not when the app is created
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

  private fun constructFhirEngine(): FhirEngine {
    val parser = FhirContext.forR4().newJsonParser()
    val service = create(parser)
    val params = mutableMapOf("address-city" to "NAIROBI")
    val syncData: MutableList<SyncData> = ArrayList()
    syncData.add(SyncData(ResourceType.Patient, params))
    val configuration = SyncConfiguration(syncData, false)
    val periodicSyncConfiguration =
      PeriodicSyncConfiguration(
        syncConfiguration = configuration,
        syncConstraints = Constraints.Builder().build(),
        periodicSyncWorker = FhirPeriodicSyncWorker::class.java,
        repeat = RepeatInterval(interval = 1, timeUnit = TimeUnit.HOURS)
      )
    val dataSource: FhirDataSource = HapiFhirResourceDataSource(service)
    return FhirEngineBuilder(dataSource, this)
      .periodicSyncConfiguration(periodicSyncConfiguration)
      .build()
  }

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as FhirApplication).fhirEngine
  }
}
