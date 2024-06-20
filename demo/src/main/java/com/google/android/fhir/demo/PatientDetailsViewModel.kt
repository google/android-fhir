/*
 * Copyright 2023-2024 Google LLC
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.revInclude
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
import org.hl7.fhir.r4.model.Resource
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
  private val patientId: String,
) : AndroidViewModel(application) {
  val livePatientData = MutableLiveData<List<PatientDetailData>>()

  /** Emits list of [PatientDetailData]. */
  fun getPatientDetailData() {
    viewModelScope.launch { livePatientData.value = getPatientDetailDataModel() }
  }

  private suspend fun getPatientDetailDataModel(): List<PatientDetailData> {
    val searchResult =
      fhirEngine.search<Patient> {
        filter(Resource.RES_ID, { value = of(patientId) })

        revInclude<RiskAssessment>(RiskAssessment.SUBJECT)
        revInclude<Observation>(Observation.SUBJECT)
        revInclude<Condition>(Condition.SUBJECT)
      }
    val data = mutableListOf<PatientDetailData>()

    searchResult.first().let {
      data.addPatientDetailData(
        it.resource,
        getRiskItem(
          it.revIncluded?.get(ResourceType.RiskAssessment to RiskAssessment.SUBJECT.paramName)
            as List<RiskAssessment>?,
        ),
      )

      it.revIncluded?.get(ResourceType.Observation to Observation.SUBJECT.paramName)?.let {
        data.addObservationsData(it as List<Observation>)
      }
      it.revIncluded?.get(ResourceType.Condition to Condition.SUBJECT.paramName)?.let {
        data.addConditionsData(it as List<Condition>)
      }
    }
    return data
  }

  private fun getRiskItem(riskAssessments: List<RiskAssessment>?) =
    riskAssessments
      ?.filter { it.hasOccurrence() }
      ?.maxByOrNull { it.occurrenceDateTimeType.value }
      .let {
        RiskAssessmentItem(
          getRiskAssessmentStatusColor(it),
          getRiskAssessmentStatus(it),
          getLastContactedDate(it),
          getPatientDetailsCardColor(it),
        )
      }

  private fun MutableList<PatientDetailData>.addPatientDetailData(
    patient: Patient,
    riskAssessment: RiskAssessmentItem,
  ) {
    patient
      .toPatientItem(0)
      .apply { riskItem = riskAssessment }
      .let { patientItem ->
        add(PatientDetailOverview(patientItem, firstInGroup = true))
        add(
          PatientDetailProperty(
            PatientProperty(getString(R.string.patient_property_mobile), patientItem.phone),
          ),
        )
        add(
          PatientDetailProperty(
            PatientProperty(getString(R.string.patient_property_id), patientItem.resourceId),
          ),
        )
        add(
          PatientDetailProperty(
            PatientProperty(
              getString(R.string.patient_property_address),
              "${patientItem.city}, ${patientItem.country} ",
            ),
          ),
        )
        add(
          PatientDetailProperty(
            PatientProperty(
              getString(R.string.patient_property_dob),
              patientItem.dob?.localizedString ?: "",
            ),
          ),
        )
        add(
          PatientDetailProperty(
            PatientProperty(
              getString(R.string.patient_property_gender),
              patientItem.gender.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
              },
            ),
            lastInGroup = true,
          ),
        )
      }
  }

  private fun MutableList<PatientDetailData>.addObservationsData(observations: List<Observation>) {
    if (observations.isNotEmpty()) {
      add(PatientDetailHeader(getString(R.string.header_observation)))

      observations
        .take(MAX_RESOURCE_COUNT)
        .map { createObservationItem(it, getApplication<Application>().resources) }
        .mapIndexed { index, observationItem ->
          PatientDetailObservation(
            observationItem,
            firstInGroup = index == 0,
            lastInGroup = index == observations.size - 1,
          )
        }
        .let { addAll(it) }
    }
  }

  private fun MutableList<PatientDetailData>.addConditionsData(conditions: List<Condition>) {
    if (conditions.isNotEmpty()) {
      add(PatientDetailHeader(getString(R.string.header_conditions)))
      conditions
        .take(MAX_RESOURCE_COUNT)
        .map { createConditionItem(it, getApplication<Application>().resources) }
        .mapIndexed { index, conditionItem ->
          PatientDetailCondition(
            conditionItem,
            firstInGroup = index == 0,
            lastInGroup = index == conditions.size - 1,
          )
        }
        .let { addAll(it) }
    }
  }

  private val LocalDate.localizedString: String
    get() {
      val date = Date.from(atStartOfDay(ZoneId.systemDefault())?.toInstant())
      return if (isAndroidIcuSupported()) {
        DateFormat.getDateInstance(DateFormat.DEFAULT).format(date)
      } else {
        SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault()).format(date)
      }
    }

  // Android ICU is supported API level 24 onwards.
  private fun isAndroidIcuSupported() = true

  private fun getString(resId: Int) = getApplication<Application>().resources.getString(resId)

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
    return getString(R.string.unknown)
  }

  private fun getLastContactedDate(riskAssessment: RiskAssessment?): String {
    riskAssessment?.let {
      if (it.hasOccurrence()) {
        return LocalDate.parse(
            it.occurrenceDateTimeType.valueAsString,
            DateTimeFormatter.ISO_DATE_TIME,
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
      resources: Resources,
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
        valueString,
      )
    }

    /** Creates ConditionItem objects with displayable values from the Fhir Condition objects. */
    private fun createConditionItem(
      condition: Condition,
      resources: Resources,
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
        value,
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
  override val lastInGroup: Boolean = false,
) : PatientDetailData

data class PatientDetailProperty(
  val patientProperty: PatientProperty,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false,
) : PatientDetailData

data class PatientDetailOverview(
  val patient: PatientListViewModel.PatientItem,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false,
) : PatientDetailData

data class PatientDetailObservation(
  val observation: PatientListViewModel.ObservationItem,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false,
) : PatientDetailData

data class PatientDetailCondition(
  val condition: PatientListViewModel.ConditionItem,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false,
) : PatientDetailData

data class PatientProperty(val header: String, val value: String)

class PatientDetailsViewModelFactory(
  private val application: Application,
  private val fhirEngine: FhirEngine,
  private val patientId: String,
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
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
  var patientCardColor: Int,
)

/**
 * The logical (unqualified) part of the ID. For example, if the ID is
 * "http://example.com/fhir/Patient/123/_history/456", then this value would be "123".
 */
private val Resource.logicalId: String
  get() {
    return this.idElement?.idPart.orEmpty()
  }
