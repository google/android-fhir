package com.google.fhirengine.example

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.fhirengine.example.data.SamplePatients

/**
 * The ViewModel helper class for SampleItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientListViewModel(jsonString: String) : ViewModel() {
    private val patients: MutableLiveData<List<SamplePatients.PatientItem>> =
        MutableLiveData(SamplePatients.getPatientItems(jsonString))

    fun getPatients(): LiveData<List<SamplePatients.PatientItem>> {
        return patients
    }
}