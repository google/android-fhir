package com.google.fhirengine.example

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.fhirengine.FhirEngine
import com.google.fhirengine.example.api.HapiFhirService
import kotlinx.coroutines.launch

class MainActivityViewModel(
        private val fhirEngine: FhirEngine,
        private val service: HapiFhirService
) : ViewModel() {

    fun requestPatients() {
        viewModelScope.launch {
            // for now, ignore properly handling requests
            val response = service.getPatients()
            Log.d("MainActivityViewModel", "patient count: $response")
            val patients = response.entry.map { it.resource }
            fhirEngine.saveAll(patients)
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