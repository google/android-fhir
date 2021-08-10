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
      if (isRequiredFieldMissing(bundle)) {
        isResourcesSaved.value = false
        return@launch
      }
      saveResources(bundle, reference)
      isResourcesSaved.value = true
    }
  }

  private suspend fun saveResources(bundle: Bundle, reference: Reference) {
    bundle.entry.forEach {
      val resource = it.resource
      when (resource) {
        is Observation -> resource.subject = reference
        is Condition -> resource.subject = reference
        is Encounter -> resource.subject = reference
      }
      saveResourceToDatabase(resource)
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
}
