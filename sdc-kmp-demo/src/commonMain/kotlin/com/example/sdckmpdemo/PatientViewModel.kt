package com.example.sdckmpdemo

import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Patient
import android_fhir.sdc_kmp_demo.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

private val json = Json {
  prettyPrint = true
}

class PatientViewModel {
  private val _patients = MutableStateFlow<List<Patient>>(emptyList())
  val patients: StateFlow<List<Patient>> = _patients.asStateFlow()

  init {
    CoroutineScope(Dispatchers.Main).launch {
      val jsonString = Res.readBytes("files/list.json").decodeToString()
      val jsonArray = json.parseToJsonElement(jsonString) as JsonArray
      val patients = jsonArray.map { patientJson ->
        FhirR4Json().decodeFromString(json.encodeToString(patientJson)) as Patient
      }
      _patients.update { patients }
    }
  }
}
