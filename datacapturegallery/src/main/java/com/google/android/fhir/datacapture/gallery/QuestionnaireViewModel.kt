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
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import java.io.File
import kotlinx.coroutines.withContext
import org.apache.commons.io.IOUtils

class QuestionnaireViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  private val backgroundContext = viewModelScope.coroutineContext
  private var questionnaireJson: String? = null
  private var questionnaireUri: Uri? = null
  private var questionnaireResponseJson: String? = null

  suspend fun getQuestionnaireJson() =
    withContext(backgroundContext) {
      questionnaireJson =
        questionnaireJson
          ?: readFileFromAssets(state[QuestionnaireContainerFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
      questionnaireJson
    }

  suspend fun getQuestionnaireUri() =
    withContext(backgroundContext) {
      questionnaireUri =
        questionnaireUri
          ?: createFileUri(state[QuestionnaireContainerFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
      questionnaireUri
    }

  suspend fun getQuestionnaireResponse() =
    withContext(backgroundContext) {
      state.get<String>(QuestionnaireContainerFragment.QUESTIONNAIRE_RESPONSE_FILE_PATH_KEY)?.let {
        path ->
        questionnaireResponseJson?.let { cachedResponse ->
          questionnaireResponseJson =
            questionnaireResponseJson?.let { cachedResponse } ?: readFileFromAssets(path)
          questionnaireResponseJson
        }
      }
        ?: null
    }

  private suspend fun readFileFromAssets(filename: String) =
    withContext(backgroundContext) {
      getApplication<Application>().assets.open(filename).bufferedReader().use { it.readText() }
    }

  private suspend fun createFileUri(filename: String) =
    withContext(backgroundContext) {
      val application = getApplication<Application>()
      val outputFile = File(application.externalCacheDir, filename)
      application.assets.open(filename).use { inputStream ->
        outputFile.outputStream().use { outputStream -> IOUtils.copy(inputStream, outputStream) }
      }
      Uri.fromFile(outputFile)
    }
}
