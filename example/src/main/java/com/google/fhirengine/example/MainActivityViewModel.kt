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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.fhirengine.FhirEngine
import com.google.fhirengine.example.api.HapiFhirService
import com.google.fhirengine.example.data.NetworkSearchParameter
import com.google.fhirengine.example.data.ResourceDataSource
import com.google.fhirengine.sync.FhirSynchroniser
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ResourceType

class MainActivityViewModel(
    private val fhirEngine: FhirEngine,
    private val service: HapiFhirService
) : ViewModel() {

    fun requestPatients() {
        viewModelScope.launch {
            // for now, ignore properly handling requests
            val synchroniser = FhirSynchroniser(
                ResourceDataSource(
                    resourceType = ResourceType.Patient,
                    searchParams = listOf(
                        NetworkSearchParameter("address-country", "United States"),
                        // sort descending by last updated time
                        NetworkSearchParameter("_sort", "-_lastUpdated")
//                        NetworkSearchParameter("_lastUpdated", "gt2019-06-05T02:52:37.425-04:00")
                    ),
                    service = service),
                fhirEngine
            )
            synchroniser.synchronise()
        }
    }
}

class MainActivityViewModelFactory(
    private val fhirEngine: FhirEngine,
    private val service: HapiFhirService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(fhirEngine, service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
