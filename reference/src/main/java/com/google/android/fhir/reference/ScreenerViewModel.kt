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
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import ca.uhn.fhir.context.FhirContext
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

const val TAG = "ScreenerViewModel"
/** ViewModel for screener questionnaire screen {@link ScreenerEncounterFragment}. */
class ScreenerViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  val questionnaire: String
    get() = getQuestionnaireJson()

  private val questionnaireResource: Questionnaire
    get() = FhirContext.forR4().newJsonParser().parseResource(questionnaire) as Questionnaire
  private var questionnaireJson: String? = null

  /**
   * Saves screener encounter questionnaire response into the application database.
   *
   * @param questionnaireResponse screener encounter questionnaire response
   */
  fun saveScreenerEncounter(questionnaireResponse: QuestionnaireResponse) {
    // TODO Extract the screener encounter resource and save into the database.
    // Extraction of screener questionnaire response approach is under review.
    val response = FhirContext.forR4().newJsonParser().encodeResourceToString(questionnaireResponse)
    Log.d(TAG, "saveScreenerEncounter: $response")
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
}
