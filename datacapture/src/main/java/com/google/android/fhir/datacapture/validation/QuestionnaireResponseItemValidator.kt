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

package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

object QuestionnaireResponseItemValidator {

  private val validators = mutableListOf(MaxValueValidator, MinValueValidator)
  /**
   * Validates [questionnaireResponseItemBuilder] contains valid answer(s) to [questionnaireItem].
   */
  fun validate(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder
  ): List<ValidationResult> {
    if (questionnaireResponseItemBuilder.answerBuilderList.isEmpty()) {
      return listOf(ValidationResult(true, null))
    }
    val validationResults = mutableListOf<ValidationResult>()
    validators.forEach {
      validationResults.add(it.validate(questionnaireItem, questionnaireResponseItemBuilder))
    }
    return validationResults
  }

  data class ValidationResult(val isValid: Boolean, val message: String?)
}
