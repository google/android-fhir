/*
 * Copyright 2021-2023 Google LLC
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.RiskAssessment
import org.hl7.fhir.r4.model.codesystems.RiskProbability

/** ViewModel for screener questionnaire screen {@link ScreenerEncounterFragment}. */
class ScreenerViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  val questionnaire: String
    get() = getQuestionnaireJson()

  val isResourcesSaved = MutableLiveData<Boolean>()

  private val questionnaireResource: Questionnaire
    get() =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(questionnaire)
        as Questionnaire

  private var questionnaireJson: String? = null
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)

  /**
   * Saves screener encounter questionnaire response into the application database.
   *
   * @param questionnaireResponse screener encounter questionnaire response
   */
  fun saveScreenerEncounter(questionnaireResponse: QuestionnaireResponse, patientId: String) {
    viewModelScope.launch {
      val bundle = ResourceMapper.extract(questionnaireResource, questionnaireResponse)
      val subjectReference = Reference("Patient/$patientId")
      val encounterId = generateUuid()
      if (isRequiredFieldMissing(bundle)) {
        isResourcesSaved.value = false
        return@launch
      }
      saveResources(bundle, subjectReference, encounterId)
      generateRiskAssessmentResource(bundle, subjectReference, encounterId)
      isResourcesSaved.value = true
    }
  }

  private suspend fun saveResources(
    bundle: Bundle,
    subjectReference: Reference,
    encounterId: String,
  ) {
    val encounterReference = Reference("Encounter/$encounterId")
    bundle.entry.forEach {
      when (val resource = it.resource) {
        is Observation -> {
          if (resource.hasCode()) {
            resource.id = generateUuid()
            resource.subject = subjectReference
            resource.encounter = encounterReference
            saveResourceToDatabase(resource)
          }
        }
        is Condition -> {
          if (resource.hasCode()) {
            resource.id = generateUuid()
            resource.subject = subjectReference
            resource.encounter = encounterReference
            saveResourceToDatabase(resource)
          }
        }
        is Encounter -> {
          resource.subject = subjectReference
          resource.id = encounterId
          saveResourceToDatabase(resource)
        }
      }
    }
  }

  private fun isRequiredFieldMissing(bundle: Bundle): Boolean {
    bundle.entry.forEach {
      val resource = it.resource
      when (resource) {
        is Observation -> {
          if (resource.hasValueQuantity() && !resource.valueQuantity.hasValueElement()) {
            return true
          }
        }
      // TODO check other resources inputs
      }
    }
    return false
  }

  private suspend fun saveResourceToDatabase(resource: Resource) {
    fhirEngine.create(resource)
  }

  private fun getQuestionnaireJson(): String {
    questionnaireJson?.let {
      return it!!
    }
    questionnaireJson = readFileFromAssets(state[ScreenerFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
    return questionnaireJson!!
  }

  private fun readFileFromAssets(filename: String): String {
    return getApplication<Application>().assets.open(filename).bufferedReader().use {
      it.readText()
    }
  }

  private fun generateUuid(): String {
    return UUID.randomUUID().toString()
  }

  private suspend fun generateRiskAssessmentResource(
    bundle: Bundle,
    subjectReference: Reference,
    encounterId: String,
  ) {
    val spO2 = getSpO2(bundle)
    spO2?.let {
      val isSymptomPresent = isSymptomPresent(bundle)
      val isComorbidityPresent = isComorbidityPresent(bundle)
      val riskProbability = getRiskProbability(isSymptomPresent, isComorbidityPresent, it)
      riskProbability?.let { riskProbability ->
        val riskAssessment =
          RiskAssessment().apply {
            id = generateUuid()
            subject = subjectReference
            encounter = Reference("Encounter/$encounterId")
            addPrediction().apply {
              qualitativeRisk =
                CodeableConcept().apply { addCoding().updateRiskProbability(riskProbability) }
            }
            occurrence = DateTimeType.now()
          }
        saveResourceToDatabase(riskAssessment)
      }
    }
  }

  private fun getRiskProbability(
    isSymptomPresent: Boolean,
    isComorbidityPresent: Boolean,
    spO2: BigDecimal,
  ): RiskProbability? {
    if (spO2 < BigDecimal(90)) {
      return RiskProbability.HIGH
    } else if (spO2 >= BigDecimal(90) && spO2 < BigDecimal(94)) {
      return RiskProbability.MODERATE
    } else if (isSymptomPresent) {
      return RiskProbability.MODERATE
    } else if (spO2 >= BigDecimal(94) && isComorbidityPresent) {
      return RiskProbability.MODERATE
    } else if (spO2 >= BigDecimal(94) && !isComorbidityPresent) {
      return RiskProbability.LOW
    }
    return null
  }

  private fun Coding.updateRiskProbability(riskProbability: RiskProbability) {
    code = riskProbability.toCode()
    display = riskProbability.display
  }

  private fun getSpO2(bundle: Bundle): BigDecimal? {
    return bundle.entry
      .asSequence()
      .filter { it.resource is Observation }
      .map { it.resource as Observation }
      .filter { it.hasCode() && it.code.hasCoding() && it.code.coding.first().code.equals(SPO2) }
      .map { it.valueQuantity.value }
      .firstOrNull()
  }

  private fun isSymptomPresent(bundle: Bundle): Boolean {
    val count =
      bundle.entry
        .filter { it.resource is Observation }
        .map { it.resource as Observation }
        .filter { it.hasCode() && it.code.hasCoding() }
        .flatMap { it.code.coding }
        .map { it.code }
        .filter { isSymptomPresent(it) }
        .count()
    return count > 0
  }

  private fun isSymptomPresent(symptom: String): Boolean {
    return symptoms.contains(symptom)
  }

  private fun isComorbidityPresent(bundle: Bundle): Boolean {
    val count =
      bundle.entry
        .filter { it.resource is Condition }
        .map { it.resource as Condition }
        .filter { it.hasCode() && it.code.hasCoding() }
        .flatMap { it.code.coding }
        .map { it.code }
        .filter { isComorbidityPresent(it) }
        .count()
    return count > 0
  }

  private fun isComorbidityPresent(comorbidity: String): Boolean {
    return comorbidities.contains(comorbidity)
  }

  private companion object {
    const val ASTHMA = "161527007"
    const val LUNG_DISEASE = "13645005"
    const val DEPRESSION = "35489007"
    const val DIABETES = "161445009"
    const val HYPER_TENSION = "161501007"
    const val HEART_DISEASE = "56265001"
    const val HIGH_BLOOD_LIPIDS = "161450003"

    const val FEVER = "386661006"
    const val SHORTNESS_BREATH = "13645005"
    const val COUGH = "49727002"
    const val LOSS_OF_SMELL = "44169009"

    const val SPO2 = "59408-5"

    private val comorbidities: Set<String> =
      setOf(
        ASTHMA,
        LUNG_DISEASE,
        DEPRESSION,
        DIABETES,
        HYPER_TENSION,
        HEART_DISEASE,
        HIGH_BLOOD_LIPIDS,
      )
    private val symptoms: Set<String> = setOf(FEVER, SHORTNESS_BREATH, COUGH, LOSS_OF_SMELL)
  }
}
