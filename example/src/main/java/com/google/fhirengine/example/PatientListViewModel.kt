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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.fhirengine.example.data.SamplePatients

/**
 * The ViewModel helper class for SampleItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientListViewModel(jsonStringPatients: String, jsonStringObservations: String) : ViewModel() {
    private val samplePatients = SamplePatients()
    private val patients: MutableLiveData<List<SamplePatients.PatientItem>> =
        MutableLiveData(samplePatients.getPatientItems(jsonStringPatients))
    private val observations: MutableLiveData<List<SamplePatients.ObservationItem>> =
        MutableLiveData(samplePatients.getObservationItems(jsonStringObservations))
    private val patientsMap: MutableLiveData<Map<String, SamplePatients.PatientItem>> =
        MutableLiveData(samplePatients.getPatientsMap())
    private val observationsMap: MutableLiveData<Map<String, SamplePatients.ObservationItem>> =
        MutableLiveData(samplePatients.getObservationsMap())
    private val patientsMap1: Map<String, SamplePatients.PatientItem> =
        samplePatients.getPatientsMap()
    private val observationsMap1: Map<String, SamplePatients.ObservationItem> =
        samplePatients.getObservationsMap()

    fun getPatients(): LiveData<List<SamplePatients.PatientItem>> {
        return patients
    }

    fun getObservations(): LiveData<List<SamplePatients.ObservationItem>> {
        return observations
    }
    fun getPatientsMap(): Map<String, SamplePatients.PatientItem> {
        return patientsMap1
    }

    fun getObservationsMap(): Map<String, SamplePatients.ObservationItem> {
        return observationsMap1
    }
}

class PatientListViewModelFactory(
  private val jsonStringPatients: String,
  private val jsonStringObservations: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientListViewModel::class.java)) {
            return PatientListViewModel(jsonStringPatients, jsonStringObservations) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
