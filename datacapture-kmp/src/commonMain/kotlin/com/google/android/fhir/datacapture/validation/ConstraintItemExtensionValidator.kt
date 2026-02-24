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

package com.google.android.fhir.datacapture.validation

import com.google.android.fhir.datacapture.extensions.ConstraintSeverityTypes
import com.google.android.fhir.datacapture.extensions.EXTENSION_QUESTIONNAIRE_CONSTRAINT_EXPRESSION
import com.google.android.fhir.datacapture.extensions.EXTENSION_QUESTIONNAIRE_CONSTRAINT_HUMAN
import com.google.android.fhir.datacapture.extensions.EXTENSION_QUESTIONNAIRE_CONSTRAINT_SEVERITY
import com.google.android.fhir.datacapture.extensions.EXTENSION_QUESTIONNAIRE_CONSTRAINT_URL
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator
import com.google.android.fhir.datacapture.fhirpath.convertToBoolean
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Expression
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.String

/**
 * TODO: Add constraint support for global case, create a separate validator,
 *   https://github.com/google/android-fhir/issues/2479
 */
internal class ConstraintItemExtensionValidator(
  private val expressionEvaluator: ExpressionEvaluator,
) : QuestionnaireResponseItemConstraintValidator {
  override suspend fun validate(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItem: QuestionnaireResponse.Item,
  ): List<ConstraintValidator.Result> {
    return questionnaireItem.extension
      .filter { extension ->
        /**
         * TODO: Add constraint support for warning case, update the [ConstraintValidator.Result]
         *   data class to also include warning state,
         *   https://github.com/google/android-fhir/issues/2480
         */
        extension.url == EXTENSION_QUESTIONNAIRE_CONSTRAINT_URL &&
          ConstraintSeverityTypes.ERROR.code ==
            extension.extension
              .find { it.url == EXTENSION_QUESTIONNAIRE_CONSTRAINT_SEVERITY }
              ?.value
              ?.asCode()
              ?.value
              ?.value
      }
      .map { extension ->
        val expression =
          Expression.Builder(
              language = Enumeration(value = Expression.ExpressionLanguage.Text_Fhirpath),
            )
            .apply {
              expression =
                String.Builder().apply {
                  value =
                    extension.extension
                      .find { it.url == EXTENSION_QUESTIONNAIRE_CONSTRAINT_EXPRESSION }
                      ?.value
                      ?.asString()
                      ?.value
                      ?.value
                }
            }
            .build()
        val isValid = convertToBoolean(expressionEvaluator.evaluateExpression(expression))
        if (isValid) {
          ConstraintValidator.Result(true, null)
        } else {
          val errorMessage =
            extension.extension
              .find { it.url == EXTENSION_QUESTIONNAIRE_CONSTRAINT_HUMAN }
              ?.value
              ?.asString()
              ?.value
              ?.value
          ConstraintValidator.Result(false, errorMessage)
        }
      }
  }
}
