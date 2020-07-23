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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.fhirengine.FhirEngine
import com.google.fhirengine.example.data.SamplePatients
import com.google.fhirengine.search.filter.string
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource

/**
 * The ViewModel helper class for PatientItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientListViewModel(jsonStringPatients: String, jsonStringObservations: String, private val fhirEngine: FhirEngine) :
    ViewModel() {

    init {
        searchPatients()
    }

    private val samplePatients = SamplePatients()
    // private val patients: MutableLiveData<List<SamplePatients.PatientItem>> =
    //    MutableLiveData(samplePatients.getPatientItems(jsonStringPatients))
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
    private var patientResults: List<Patient>? = null
    private var searchedPatients: MutableLiveData<List<SamplePatients.PatientItem>>? = null
    //    MutableLiveData(samplePatients.getPatientItems(jsonStringPatients))

    // fun getPatients(): LiveData<List<SamplePatients.PatientItem>> {
    //     return patients
    // }
    fun getSearchedPatients(): LiveData<List<SamplePatients.PatientItem>>? {
        searchedPatients = MutableLiveData(samplePatients.getPatientItems(patientResults!!))
        return searchedPatients
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

    fun searchPatients () {
 //       viewModelScope.launch {
            patientResults = fhirEngine.search()
                .of(Patient::class.java)
                .filter(
                    string(Patient.ADDRESS_CITY, ParamPrefixEnum.EQUAL, "NAIROBI")
                )
                .run()
            Log.d("PatientListViewModel", "search results: ${patientResults!!.joinToString(" ")}")
            //searchedPatients = MutableLiveData(samplePatients.getPatientItems(patientResults!!))
//        }
    }
}

class PatientListViewModelFactory(
  private val jsonStringPatients: String,
  private val jsonStringObservations: String,
    private val fhirEngine: FhirEngine
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientListViewModel::class.java)) {
            return PatientListViewModel(jsonStringPatients, jsonStringObservations, fhirEngine) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
