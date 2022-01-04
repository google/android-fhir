/*
 * Copyright 2021 Google LLC
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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.withContext

class GalleryQuestionnaireViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  private val backgroundContext = viewModelScope.coroutineContext
  private var questionnaireJson: String? = null

  suspend fun getQuestionnaireJson(): String {
    return withContext(backgroundContext) {
      if (questionnaireJson == null) {
        questionnaireJson =
          readFileFromAssets(state[QuestionnaireContainerFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
      }
      questionnaireJson!!
    }
  }

  private suspend fun readFileFromAssets(filename: String) =
    withContext(backgroundContext) {
      getApplication<Application>().assets.open(filename).bufferedReader().use { it.readText() }
    }
}
