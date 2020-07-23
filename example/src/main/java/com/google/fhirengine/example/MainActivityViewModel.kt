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
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.fhirengine.FhirEngine
import com.google.fhirengine.search.filter.string
import com.google.fhirengine.sync.SyncConfiguration
import com.google.fhirengine.sync.SyncData
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

class MainActivityViewModel(
  private val fhirEngine: FhirEngine
) : ViewModel() {

    init {
        requestPatients()
    }

    private fun requestPatients() {
        viewModelScope.launch {
            val syncData = listOf(
                SyncData(
                    resourceType = ResourceType.Patient,
                    params = mapOf("address-city" to "NAIROBI")
                    //     "_revinclude" to "Observation:subject")
                )
            )
            // params = mapOf("identifier" to "d7db5ce4-8baf-41d7-81e8-3f706b3295e3",

            val syncConfig = SyncConfiguration(syncData = syncData)
            val result = fhirEngine.sync(syncConfig)
            Log.d("MainActivityViewModel", "sync result: $result")

            var results: List<Resource> = fhirEngine.search()
                .of(Patient::class.java)
                .filter(
                    string(Patient.ADDRESS_CITY, ParamPrefixEnum.EQUAL, "NAIROBI")
                )
                .run()
            //
            // results = fhirEngine.search()
            //     .of(Observation::class.java)
            //     .filter(
            //         string(Observation.SUBJECT, ParamPrefixEnum.EQUAL, "Patient/1393673")
            //             .and(string(Observation.INCLUDE_SUBJECT.value, ParamPrefixEnum.EQUAL))
            //     )
            //     .run()
            //
            Log.d("MainActivityViewModel", "search results: ${results.joinToString(" ")}")
        }
    }
}

class MainActivityViewModelFactory(
  private val fhirEngine: FhirEngine
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(fhirEngine) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
