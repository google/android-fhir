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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import java.util.UUID
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/** ViewModel for patient registration screen {@link AddPatientFragment}. */
class AddPatientViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  val questionnaire: String
    get() = getQuestionnaireJson()
  val savedPatient = MutableLiveData<Patient?>()

  private val questionnaireResource: Questionnaire
    get() =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(questionnaire)
        as Questionnaire
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)
  private var questionnaireJson: String? = null

  /**
   * Saves patient registration questionnaire response into the application database.
   *
   * @param questionnaireResponse patient registration questionnaire response
   */
  fun savePatient(questionnaireResponse: QuestionnaireResponse) {
    viewModelScope.launch {
      if (QuestionnaireResponseValidator.validateQuestionnaireResponse(
            questionnaireResource,
            questionnaireResponse,
            getApplication()
          )
          .values
          .flatten()
          .any { it is Invalid }
      ) {
        savedPatient.value = null
        return@launch
      }
      val entry = ResourceMapper.extract(questionnaireResource, questionnaireResponse).entryFirstRep
      if (entry.resource !is Patient) {
        return@launch
      }
      val patient = entry.resource as Patient
      patient.id = generateUuid()
      fhirEngine.create(patient)
      savedPatient.value = patient
    }
  }

  private fun getQuestionnaireJson(): String {
    questionnaireJson?.let {
      return it
    }
    questionnaireJson = readFileFromAssets(state[AddPatientFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
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
