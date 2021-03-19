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

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

object MaxValueValidator : ConstraintValidator {

  private const val MAX_VALUE_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/maxValue"

  override fun validate(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder
  ): QuestionnaireResponseItemValidator.ValidationResult {
    val extension =
      questionnaireItem.getExtensionsByUrl(MAX_VALUE_EXTENSION_URL).firstOrNull()
        ?: return QuestionnaireResponseItemValidator.ValidationResult(true, null)
    return maxValueIntegerValidator(extension, questionnaireResponseItemBuilder)
  }

  private fun maxValueIntegerValidator(
    extension: Extension,
    questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder
  ): QuestionnaireResponseItemValidator.ValidationResult {
    val response = questionnaireResponseItemBuilder.getAnswerBuilder(0).value
    when {
      extension.value.hasInteger() && response.hasInteger() -> {
        val answer = questionnaireResponseItemBuilder.getAnswerBuilder(0).value.integer.value
        if (answer > extension.value.integer.value) {
          return QuestionnaireResponseItemValidator.ValidationResult(
            false,
            validationMessageGenerator(extension)
          )
        }
      }
    }
    return QuestionnaireResponseItemValidator.ValidationResult(true, null)
  }

  private fun validationMessageGenerator(extension: Extension): String {
    return "Maximum value allowed is:" + extension.value.integer.value
  }
}
