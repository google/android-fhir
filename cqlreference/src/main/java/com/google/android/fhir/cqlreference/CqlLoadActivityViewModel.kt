package com.google.android.fhir.cqlreference

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.SyncConfiguration
import com.google.android.fhir.sync.SyncData
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ResourceType

class CqlActivityViewModel(private val fhirEngine: FhirEngine) : ViewModel() {

  init {
    requestPatients()
  }

  private fun requestPatients() {
    viewModelScope.launch {
      val syncData =
        listOf(
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
