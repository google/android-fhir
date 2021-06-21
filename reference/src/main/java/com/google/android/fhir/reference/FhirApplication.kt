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
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineBuilder
import com.google.android.fhir.reference.api.HapiFhirService
import com.google.android.fhir.reference.data.HapiFhirResourceDataSource
import com.google.android.fhir.sync.Sync
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ResourceType

class FhirApplication : Application() {
  //     only initiate the FhirEngine when used for the first time, not when the app is created
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as FhirApplication).fhirEngine
  }

  override fun onCreate() {
    super.onCreate()
    GlobalScope.launch {
      Sync.oneTimeSync(
        fhirEngine,
        HapiFhirResourceDataSource(HapiFhirService.create(FhirContext.forR4().newJsonParser())),
        mapOf(ResourceType.Patient to mapOf("address-city" to "NAIROBI"))
      )
    }
  }

  private fun constructFhirEngine(): FhirEngine {
    return FhirEngineBuilder(this).build()
  }
}
