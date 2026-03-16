/*
 * Copyright 2022-2026 Google LLC
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

package com.google.android.fhir.catalog.ui.questionnaire

import android_fhir.catalog.generated.resources.Res
import androidx.lifecycle.ViewModel
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse

class QuestionnaireViewModel : ViewModel() {
  private val fhirJson = FhirR4Json()

  fun getQuestionnaireResponseJson(response: QuestionnaireResponse): String {
    return fhirJson.encodeToString(response)
  }

  suspend fun getQuestionnaire(fileName: String) = Res.readBytes("files/$fileName").decodeToString()

  /** Validates questionnaire response and returns list of invalid field names. */
  suspend fun validateAndGetErrors(
    questionnaireJson: String,
    response: QuestionnaireResponse,
    launchContextMap: Map<String, String>? = null,
  ): List<String> {
    val questionnaire = fhirJson.decodeFromString(questionnaireJson) as Questionnaire
    val parentMap = buildQuestionnaireItemParentMap(questionnaire)
    val launchContextResources = launchContextMap?.mapValues { fhirJson.decodeFromString(it.value) }

    val validationResults =
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        response,
        parentMap,
        launchContextResources,
      )

    return buildList {
      validationResults.forEach { (linkId, results) ->
        val hasError = results.any { it is Invalid }
        if (hasError) {
          // Find the questionnaire item with this linkId to get its text
          findItemText(questionnaire.item, linkId)?.let { add(it) }
        }
      }
    }
  }

  private fun buildQuestionnaireItemParentMap(
    questionnaire: Questionnaire,
  ): Map<Questionnaire.Item, Questionnaire.Item> {
    val map = mutableMapOf<Questionnaire.Item, Questionnaire.Item>()
    fun traverse(item: Questionnaire.Item) {
      for (child in item.item) {
        map[child] = item
        traverse(child)
      }
    }
    for (item in questionnaire.item) {
      traverse(item)
    }
    return map
  }

  private fun findItemText(items: List<Questionnaire.Item>, linkId: String): String? {
    for (item in items) {
      if (item.linkId.value == linkId) {
        return item.text?.value
      }
      val nested = findItemText(item.item, linkId)
      if (nested != null) return nested
    }
    return null
  }
}
