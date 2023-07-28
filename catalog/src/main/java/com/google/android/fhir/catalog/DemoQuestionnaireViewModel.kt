/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.catalog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.catalog.DemoQuestionnaireFragment.Companion.QUESTIONNAIRE_FILE_PATH_KEY
import com.google.android.fhir.catalog.DemoQuestionnaireFragment.Companion.QUESTIONNAIRE_FILE_WITH_VALIDATION_PATH_KEY
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.QuestionnaireResponse

class DemoQuestionnaireViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  private val backgroundContext = viewModelScope.coroutineContext
  private var questionnaireJson: String? = null
  private var questionnaireWithValidationJson: String? = null

  init {
    viewModelScope.launch {
      if (!state.get<String>(QUESTIONNAIRE_FILE_PATH_KEY).isNullOrEmpty()) {
        getQuestionnaireJson()
      }

      if (!state.get<String>(QUESTIONNAIRE_FILE_WITH_VALIDATION_PATH_KEY).isNullOrEmpty()) {
        getQuestionnaireWithValidationJson()
      }
    }
  }

  fun getQuestionnaireResponseJson(response: QuestionnaireResponse) =
    FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().encodeResourceToString(response)

  suspend fun getQuestionnaireJson(): String {
    return withContext(backgroundContext) {
      if (questionnaireJson == null) {
        questionnaireJson = readFileFromAssets(state[QUESTIONNAIRE_FILE_PATH_KEY]!!)
      }
      questionnaireJson!!
    }
  }

  suspend fun getQuestionnaireWithValidationJson(): String {
    return withContext(backgroundContext) {
      if (questionnaireWithValidationJson == null) {
        questionnaireWithValidationJson =
          readFileFromAssets(state[QUESTIONNAIRE_FILE_WITH_VALIDATION_PATH_KEY]!!)
      }
      questionnaireWithValidationJson!!
    }
  }

  private suspend fun readFileFromAssets(filename: String) =
    withContext(backgroundContext) {
      getApplication<Application>().assets.open(filename).bufferedReader().use { it.readText() }
    }
}
