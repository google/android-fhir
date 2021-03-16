package com.google.android.fhir.cqlreference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.fhir.FhirEngine

class CqlLoadActivityViewModelFactory(private val fhirEngine: FhirEngine) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(CqlActivityViewModel::class.java)) {
      return CqlActivityViewModel(fhirEngine) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
