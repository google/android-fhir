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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.fhirengine.FhirEngine
import com.google.fhirengine.sync.SyncConfiguration
import com.google.fhirengine.sync.SyncData
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ResourceType

class CqlActivityViewModel(
    private val fhirEngine: FhirEngine
) : ViewModel() {

    init {
        requestPatients()
    }

    private fun requestPatients() {
        viewModelScope.launch {
            val syncData = listOf(
                SyncData(
                    // For the purpose of demo, sync patients that live in Nairobi.
                    resourceType = ResourceType.Patient,
                    // add "_revinclude" to "Observation:subject" to return Observations for
                    // the patients.
                    params = mapOf("address-city" to "NAIROBI")
                )
            )

            val syncConfig = SyncConfiguration(syncData = syncData)
            val result = fhirEngine.sync(syncConfig)
            Log.d("CqlActivityViewModel", "sync result: $result")
        }
    }
}

class CqlLoadActivityViewModelFactory(
    private val fhirEngine: FhirEngine
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CqlActivityViewModel::class.java)) {
            return CqlActivityViewModel(fhirEngine) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
