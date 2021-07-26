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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import java.util.UUID
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource

const val TAG = "ScreenerViewModel"

/** ViewModel for screener questionnaire screen {@link ScreenerEncounterFragment}. */
class ScreenerViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  val questionnaire: String
    get() = getQuestionnaireJson()

  private val questionnaireResource: Questionnaire
    get() = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
  private var questionnaireJson: String? = null
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)
  val isResourcesSaved = MutableLiveData<Boolean>()

  /**
   * Saves screener encounter questionnaire response into the application database.
   *
   * @param questionnaireResponse screener encounter questionnaire response
   */
  fun saveScreenerEncounter(questionnaireResponse: QuestionnaireResponse, patientId: String) {
    viewModelScope.launch {
      val bundle = ResourceMapper.extract(questionnaireResource, questionnaireResponse)
      val reference = Reference("Patient/$patientId")
      val index = ScreenerQuestionnaireIndex()
      saveObservation(bundle, reference, index.observation)
      saveCondition(bundle, reference, index.condition)
      saveEncounter(bundle, reference, index.encounter)
      isResourcesSaved.value = true
    }
  }

  private suspend fun saveObservation(bundle: Bundle, reference: Reference, index: Int) {
    val observation = bundle.entry[index].resource as Observation
    observation.subject = reference
    saveResourceToDatabase(observation)
  }

  private suspend fun saveCondition(bundle: Bundle, reference: Reference, index: Int) {
    val condition = bundle.entry[index].resource as Condition
    condition.subject = reference
    saveResourceToDatabase(condition)
  }

  private suspend fun saveEncounter(bundle: Bundle, reference: Reference, index: Int) {
    val encounter = bundle.entry[index].resource as Encounter
    encounter.subject = reference
    saveResourceToDatabase(encounter)
  }

  private suspend fun saveResourceToDatabase(resource: Resource) {
    resource.id = generateUuid()
    fhirEngine.save(resource)
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

  // TODO refactor resource index while accessing resources from bundle.
  class ScreenerQuestionnaireIndex {
    val observation: Int = 0
    val condition: Int = 2
    val encounter: Int = 6
  }
}
