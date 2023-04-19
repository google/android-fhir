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
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.get
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.search
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.RiskAssessment
import org.hl7.fhir.r4.model.codesystems.RiskProbability

/**
 * The ViewModel helper class for PatientItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientDetailsViewModel(
  application: Application,
  private val fhirEngine: FhirEngine,
  private val patientId: String
) : AndroidViewModel(application) {
  val livePatientData = MutableLiveData<List<PatientDetailData>>()

  /** Emits list of [PatientDetailData]. */
  fun getPatientDetailData() {
    viewModelScope.launch { livePatientData.value = getPatientDetailDataModel() }
  }

  private suspend fun getPatient(): PatientListViewModel.PatientItem {
    val patient = fhirEngine.get<Patient>(patientId)
    return patient.toPatientItem(0)
  }

  private suspend fun getPatientObservations(): List<PatientListViewModel.ObservationItem> {
    val observations: MutableList<PatientListViewModel.ObservationItem> = mutableListOf()
    fhirEngine
      .search<Observation> { filter(Observation.SUBJECT, { value = "Patient/$patientId" }) }
      .take(MAX_RESOURCE_COUNT)
      .map { createObservationItem(it, getApplication<Application>().resources) }
      .let { observations.addAll(it) }
    return observations
  }

  private suspend fun getPatientConditions(): List<PatientListViewModel.ConditionItem> {
    val conditions: MutableList<PatientListViewModel.ConditionItem> = mutableListOf()
    fhirEngine
      .search<Condition> { filter(Condition.SUBJECT, { value = "Patient/$patientId" }) }
      .take(MAX_RESOURCE_COUNT)
      .map { createConditionItem(it, getApplication<Application>().resources) }
      .let { conditions.addAll(it) }
    return conditions
  }

  private suspend fun getPatientDetailDataModel(): List<PatientDetailData> {
    val data = mutableListOf<PatientDetailData>()
    val patient = getPatient()
    patient.riskItem = getPatientRiskAssessment()

    val observations = getPatientObservations()
    val conditions = getPatientConditions()

    patient.let { patientItem ->
      data.add(PatientDetailOverview(patientItem, firstInGroup = true))
      data.add(
        PatientDetailProperty(
          PatientProperty(getString(R.string.patient_property_id), patientItem.resourceId)
        )
      )
      data.add(
        PatientDetailProperty(
          PatientProperty(
            getString(R.string.patient_property_dob),
            patientItem.dob?.localizedString ?: ""
          )
        )
      )
      data.add(
        PatientDetailProperty(
          PatientProperty(
            getString(R.string.patient_property_gender),
            patientItem.gender.replaceFirstChar {
              if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }
          ),
          lastInGroup = true
        )
      )
    }

    if (observations.isNotEmpty()) {
      data.add(PatientDetailHeader(getString(R.string.header_observation)))

      val observationDataModel =
        observations.mapIndexed { index, observationItem ->
          PatientDetailObservation(
            observationItem,
            firstInGroup = index == 0,
            lastInGroup = index == observations.size - 1
          )
        }
      data.addAll(observationDataModel)
    }

    if (conditions.isNotEmpty()) {
      data.add(PatientDetailHeader(getString(R.string.header_conditions)))
      val conditionDataModel =
        conditions.mapIndexed { index, conditionItem ->
          PatientDetailCondition(
            conditionItem,
            firstInGroup = index == 0,
            lastInGroup = index == conditions.size - 1
          )
        }
      data.addAll(conditionDataModel)
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

  private suspend fun getPatientRiskAssessment(): RiskAssessmentItem {
    val riskAssessment =
      fhirEngine
        .search<RiskAssessment> { filter(RiskAssessment.SUBJECT, { value = "Patient/$patientId" }) }
        .filter { it.hasOccurrence() }
        .sortedByDescending { it.occurrenceDateTimeType.value }
        .firstOrNull()
    val patient = fhirEngine.get(ResourceType.Patient, patientId)
    return RiskAssessmentItem(
      getRiskAssessmentStatusColor(riskAssessment),
      getRiskAssessmentStatus(riskAssessment),
      if (patient.hasMeta() && patient.meta.hasLastUpdated()) patient.meta.lastUpdated.toString()
      else "",
      getPatientDetailsCardColor(riskAssessment)
    )
  }

  private fun getRiskAssessmentStatusColor(riskAssessment: RiskAssessment?): Int {
    riskAssessment?.let {
      return when (it.prediction.first().qualitativeRisk.coding.first().code) {
        RiskProbability.LOW.toCode() -> ContextCompat.getColor(getApplication(), R.color.low_risk)
        RiskProbability.MODERATE.toCode() ->
          ContextCompat.getColor(getApplication(), R.color.moderate_risk)
        RiskProbability.HIGH.toCode() -> ContextCompat.getColor(getApplication(), R.color.high_risk)
        else -> ContextCompat.getColor(getApplication(), R.color.unknown_risk)
      }
    }
    return ContextCompat.getColor(getApplication(), R.color.unknown_risk)
  }

  private fun getPatientDetailsCardColor(riskAssessment: RiskAssessment?): Int {
    riskAssessment?.let {
      return when (it.prediction.first().qualitativeRisk.coding.first().code) {
        RiskProbability.LOW.toCode() ->
          ContextCompat.getColor(getApplication(), R.color.low_risk_background)
        RiskProbability.MODERATE.toCode() ->
          ContextCompat.getColor(getApplication(), R.color.moderate_risk_background)
        RiskProbability.HIGH.toCode() ->
          ContextCompat.getColor(getApplication(), R.color.high_risk_background)
        else -> ContextCompat.getColor(getApplication(), R.color.unknown_risk_background)
      }
    }
    return ContextCompat.getColor(getApplication(), R.color.unknown_risk_background)
  }

  private fun getRiskAssessmentStatus(riskAssessment: RiskAssessment?): String {
    riskAssessment?.let {
      return StringUtils.upperCase(it.prediction.first().qualitativeRisk.coding.first().display)
    }
    return "ACTIVE" // getString(R.string.patient_is_active)
  }

  private fun getLastContactedDate(riskAssessment: RiskAssessment?): String {
    riskAssessment?.let {
      if (it.hasOccurrence()) {
        return LocalDate.parse(
            it.occurrenceDateTimeType.valueAsString,
            DateTimeFormatter.ISO_DATE_TIME
          )
          .localizedString
      }
    }
    return getString(R.string.none)
  }

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
  private val fhirEngine: FhirEngine,
  private val patientId: String
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass.isAssignableFrom(PatientDetailsViewModel::class.java)) {
      "Unknown ViewModel class"
    }
    return PatientDetailsViewModel(application, fhirEngine, patientId) as T
  }
}

data class RiskAssessmentItem(
  var riskStatusColor: Int,
  var riskStatus: String,
  var lastContacted: String,
  var patientCardColor: Int
)
