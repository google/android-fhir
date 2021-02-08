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
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.reference.data.SamplePatients
import com.google.android.fhir.search.filter.string
import org.hl7.fhir.r4.model.Patient

private const val PATIENTS_JSON_FILENAME = "sample_patients_bundle.json"
private const val OBSERVATIONS_JSON_FILENAME = "sample_observations_bundle.json"

/**
 * The ViewModel helper class for PatientItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientListViewModel(application: Application, private val fhirEngine: FhirEngine) :
    AndroidViewModel(application) {

    // Make sample Fhir Patients and Observations available, in case needed for demo.
    private val jsonStringPatients = getAssetFileAsString(PATIENTS_JSON_FILENAME)
    private val jsonStringObservations = getAssetFileAsString(OBSERVATIONS_JSON_FILENAME)

    private val samplePatients = SamplePatients()

    private val observations = samplePatients.getObservationItems(jsonStringObservations)
    private val liveObservations: MutableLiveData<List<ObservationItem>> =
        MutableLiveData(observations)

    private var patientResults: List<Patient> = getSearchResults()
    private var searchedPatients = samplePatients.getPatientItems(patientResults)
    private val _liveSearchedPatients: MutableLiveData<List<PatientItem>> = MutableLiveData()
    val liveSearchedPatients: LiveData<List<PatientItem>> = _liveSearchedPatients

    fun getSearchedPatients(): LiveData<List<PatientItem>> {
        searchedPatients = samplePatients.getPatientItems(patientResults)
        _liveSearchedPatients.value = searchedPatients
        Log.d("PatientListViewModel", "getSearchedPatients(): " +
            "patientResults[${patientResults.count()}], searchedPatients[${searchedPatients
                .count()}]")
        return liveSearchedPatients
    }

    fun getPatientItem(id: String): PatientItem? {
        return searchedPatients.associateBy { it.id }[id]
    }

    fun getObservations(): LiveData<List<ObservationItem>> {
        return liveObservations
    }
    fun getPatientsMap(): Map<String, PatientItem> {
        return searchedPatients.associateBy { it.id }
    }

    fun getObservationsMap(): Map<String, ObservationItem> {
        return observations.associateBy { it.id }
    }

    private fun getSearchResults(): List<Patient> {
        val searchResults: List<Patient> = fhirEngine.search()
            .of(Patient::class.java)
            .filter(string(Patient.ADDRESS_CITY, ParamPrefixEnum.EQUAL, "NAIROBI"))
            .run()
        Log.d("PatientListViewModel", "${searchResults.count()} search results: " +
            "${searchResults.joinToString(" ")}")
        return searchResults
    }

    fun searchPatients() {
        patientResults = getSearchResults()
        searchedPatients = samplePatients.getPatientItems(patientResults)
        _liveSearchedPatients.value = searchedPatients
    }

    private fun getAssetFileAsString(filename: String): String {
        return this.getApplication<Application>().applicationContext
            .assets.open(filename).bufferedReader().use {
                it.readText()
            }
    }

    /**
     * The Patient's details for display purposes.
     */
    data class PatientItem(
        val id: String,
        val name: String,
        val gender: String,
        val dob: String,
        val html: String,
        val phone: String
    ) {
        override fun toString(): String = name
    }

    /**
     * The Observation's details for display purposes.
     */
    data class ObservationItem(
        val id: String,
        val code: String,
        val effective: String,
        val value: String
    ) {
        override fun toString(): String = code
    }
}

class PatientListViewModelFactory(
    private val application: Application,
    private val fhirEngine: FhirEngine
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientListViewModel::class.java)) {
            return PatientListViewModel(application, fhirEngine) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
