/*
 * Copyright 2022-2024 Google LLC
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

import android.content.Context
import com.google.android.fhir.datacapture.extensions.ConstraintSeverityTypes
import com.google.android.fhir.datacapture.extensions.EXTENSION_QUESTIONNAIRE_CONSTRAINT_EXPRESSION
import com.google.android.fhir.datacapture.extensions.EXTENSION_QUESTIONNAIRE_CONSTRAINT_HUMAN
import com.google.android.fhir.datacapture.extensions.EXTENSION_QUESTIONNAIRE_CONSTRAINT_SEVERITY
import com.google.android.fhir.datacapture.extensions.EXTENSION_QUESTIONNAIRE_CONSTRAINT_URL
import com.google.android.fhir.datacapture.extensions.asStringValue
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator
import com.google.android.fhir.datacapture.fhirpath.convertToBoolean
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent

/**
 * TODO: Add constraint support for global case, create a separate validator,
 *   https://github.com/google/android-fhir/issues/2479
 */
internal class ConstraintItemExtensionValidator(
  private val expressionEvaluator: ExpressionEvaluator,
) : QuestionnaireResponseItemConstraintValidator {
  override suspend fun validate(
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
    context: Context,
  ): List<ConstraintValidator.Result> {
    return questionnaireItem.extension
      .filter { extension ->
        /**
         * TODO: Add constraint support for warning case, update the [ConstraintValidator.Result]
         *   data class to also include warning state,
         *   https://github.com/google/android-fhir/issues/2480
         */
        extension.url == EXTENSION_QUESTIONNAIRE_CONSTRAINT_URL &&
          (extension.getExtensionByUrl(EXTENSION_QUESTIONNAIRE_CONSTRAINT_SEVERITY).value
              as CodeType)
            .valueAsString == ConstraintSeverityTypes.ERROR.code
      }
      .map { extension ->
        val expression =
          Expression().apply {
            language = "text/fhirpath"
            expression =
              extension
                .getExtensionByUrl(EXTENSION_QUESTIONNAIRE_CONSTRAINT_EXPRESSION)
                .value
                .asStringValue()
          }
        val isValid =
          expressionEvaluator
            .evaluateExpression(
              questionnaireItem,
              questionnaireResponseItem,
              expression,
            )
            .let { convertToBoolean(it) }
        if (isValid) {
          ConstraintValidator.Result(true, null)
        } else {
          val errorMessage =
            extension
              .getExtensionByUrl(EXTENSION_QUESTIONNAIRE_CONSTRAINT_HUMAN)
              .value
              .asStringValue()
          ConstraintValidator.Result(false, errorMessage)
        }
      }
  }
}
