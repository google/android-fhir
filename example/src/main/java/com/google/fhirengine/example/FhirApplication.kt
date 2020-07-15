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

package com.google.fhirengine.example

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import ca.uhn.fhir.context.FhirContext
import com.google.fhirengine.FhirEngine
import com.google.fhirengine.FhirEngineBuilder
import com.google.fhirengine.example.api.HapiFhirService.Companion.create
import com.google.fhirengine.example.data.HapiFhirResourceDataSource
import com.google.fhirengine.sync.FhirDataSource
import com.google.fhirengine.sync.SyncConfiguration
import com.google.fhirengine.sync.SyncData
import org.hl7.fhir.r4.model.ResourceType
import java.util.ArrayList

class FhirApplication : Application() {

    // only initiate the FhirEngine when used for the first time, not when the app is created
    private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

    private fun constructFhirEngine(): FhirEngine {
        val parser = FhirContext.forR4().newJsonParser()
        val service = create(parser)
        val params = mutableMapOf("address-country" to "United States")
        val syncData: MutableList<SyncData> = ArrayList()
        syncData.add(SyncData(ResourceType.Patient, params))
        val configuration = SyncConfiguration(syncData, Constraints.Builder().build(), false)
        val dataSource: FhirDataSource = HapiFhirResourceDataSource(service)
        return FhirEngineBuilder(configuration, dataSource, this).build()
    }

    companion object {
        @JvmStatic
        fun fhirEngine(context: Context) =
            (context.applicationContext as FhirApplication).fhirEngine
    }
}