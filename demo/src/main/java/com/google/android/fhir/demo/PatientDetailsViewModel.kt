/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.demo

import android.app.Application
import android.content.res.Resources
import android.icu.text.DateFormat
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.get
import com.google.android.fhir.json.JsonEngine
import com.google.android.fhir.logicalId
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient

/**
 * The ViewModel helper class for PatientItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientDetailsViewModel(
  application: Application,
  private val jsonEngine: JsonEngine,
  private val patientId: String
) : AndroidViewModel(application) {
  val livePatientData = MutableLiveData<List<PatientDetailData>>()

  val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  /** Emits list of [PatientDetailData]. */
  fun getPatientDetailData() {
    viewModelScope.launch { livePatientData.value = getPatientDetailDataModel() }
  }

  private suspend fun getPatient(): PatientListViewModel.PatientItem {
    val patient = jsonEngine.get("Patient", patientId)
    return parser.parseResource(Patient::class.java, patient.toString()).toPatientItem(0)
  }

  private suspend fun getPatientDetailDataModel(): List<PatientDetailData> {
    val data = mutableListOf<PatientDetailData>()
    val patient = getPatient()
    patient.let {
      data.add(PatientDetailOverview(it, firstInGroup = true))
      data.add(
        PatientDetailProperty(
          PatientProperty(getString(R.string.patient_property_mobile), it.phone)
        )
      )
      data.add(
        PatientDetailProperty(
          PatientProperty(getString(R.string.patient_property_id), it.resourceId)
        )
      )
      data.add(
        PatientDetailProperty(
          PatientProperty(
            getString(R.string.patient_property_address),
            "${it.city}, ${it.country} "
          )
        )
      )
      data.add(
        PatientDetailProperty(
          PatientProperty(getString(R.string.patient_property_dob), it.dob?.localizedString ?: "")
        )
      )
      data.add(
        PatientDetailProperty(
          PatientProperty(
            getString(R.string.patient_property_gender),
            it.gender.capitalize(Locale.ROOT)
          ),
          lastInGroup = true
        )
      )
    }
    return data
  }

  private val LocalDate.localizedString: String
    get() {
      val date = Date.from(atStartOfDay(ZoneId.systemDefault())?.toInstant())
      return if (isAndroidIcuSupported())
        DateFormat.getDateInstance(DateFormat.DEFAULT).format(date)
      else SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault()).format(date)
    }

  // Android ICU is supported API level 24 onwards.
  private fun isAndroidIcuSupported() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

  private fun getString(resId: Int) = getApplication<Application>().resources.getString(resId)

  companion object {
    /**
     * Creates ObservationItem objects with displayable values from the Fhir Observation objects.
     */
    private fun createObservationItem(
      observation: Observation,
      resources: Resources
    ): PatientListViewModel.ObservationItem {
      val observationCode = observation.code.text ?: observation.code.codingFirstRep.display

      // Show nothing if no values available for datetime and value quantity.
      val dateTimeString =
        if (observation.hasEffectiveDateTimeType()) {
          observation.effectiveDateTimeType.asStringValue()
        } else {
          resources.getText(R.string.message_no_datetime).toString()
        }
      val value =
        if (observation.hasValueQuantity()) {
          observation.valueQuantity.value.toString()
        } else if (observation.hasValueCodeableConcept()) {
          observation.valueCodeableConcept.coding.firstOrNull()?.display ?: ""
        } else {
          ""
        }
      val valueUnit =
        if (observation.hasValueQuantity()) {
          observation.valueQuantity.unit ?: observation.valueQuantity.code
        } else {
          ""
        }
      val valueString = "$value $valueUnit"

      return PatientListViewModel.ObservationItem(
        observation.logicalId,
        observationCode,
        dateTimeString,
        valueString
      )
    }

    /** Creates ConditionItem objects with displayable values from the Fhir Condition objects. */
    private fun createConditionItem(
      condition: Condition,
      resources: Resources
    ): PatientListViewModel.ConditionItem {
      val observationCode = condition.code.text ?: condition.code.codingFirstRep.display ?: ""

      // Show nothing if no values available for datetime and value quantity.
      val dateTimeString =
        if (condition.hasOnsetDateTimeType()) {
          condition.onsetDateTimeType.asStringValue()
        } else {
          resources.getText(R.string.message_no_datetime).toString()
        }
      val value =
        if (condition.hasVerificationStatus()) {
          condition.verificationStatus.codingFirstRep.code
        } else {
          ""
        }
      return PatientListViewModel.ConditionItem(
        condition.logicalId,
        observationCode,
        dateTimeString,
        value
      )
    }
  }
}

interface PatientDetailData {
  val firstInGroup: Boolean
  val lastInGroup: Boolean
}

data class PatientDetailHeader(
  val header: String,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientDetailProperty(
  val patientProperty: PatientProperty,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientDetailOverview(
  val patient: PatientListViewModel.PatientItem,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientDetailObservation(
  val observation: PatientListViewModel.ObservationItem,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientDetailCondition(
  val condition: PatientListViewModel.ConditionItem,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientProperty(val header: String, val value: String)

class PatientDetailsViewModelFactory(
  private val application: Application,
  private val jsonEngine: JsonEngine,
  private val patientId: String
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass.isAssignableFrom(PatientDetailsViewModel::class.java)) {
      "Unknown ViewModel class"
    }
    return PatientDetailsViewModel(application, jsonEngine, patientId) as T
  }
}

data class RiskAssessmentItem(
  var riskStatusColor: Int,
  var riskStatus: String,
  var lastContacted: String,
  var patientCardColor: Int
)
