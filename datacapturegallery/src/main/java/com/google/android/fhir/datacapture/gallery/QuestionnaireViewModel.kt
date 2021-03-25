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

package com.google.android.fhir.datacapture.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle

class QuestionnaireViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  private var questionnaireJson: String? = null
  val questionnaire: String
    get() {
      if (questionnaireJson == null) {
        questionnaireJson =
          readFileFromAssets(state[QuestionnaireContainerFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
      }
      return questionnaireJson!!
    }
  private var questionnaireResponseJson: String? = null
  val questionnaireResponse: String?
    get() {
      if (questionnaireResponseJson == null) {
        state.get<String>(QuestionnaireContainerFragment.QUESTIONNAIRE_RESPONSE_FILE_PATH_KEY)
          ?.let { questionnaireResponseJson = readFileFromAssets(it) }
      }
      return questionnaireResponseJson
    }

  private fun readFileFromAssets(filename: String): String {
    return getApplication<Application>().assets.open(filename).bufferedReader().use {
      it.readText()
    }
  }
}
