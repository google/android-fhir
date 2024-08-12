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
import com.google.android.fhir.datacapture.extensions.asStringValue
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type

/**
 * A validator to check if the answer exceeds the maximum number of permitted characters.
 *
 * Only primitive types permitted in questionnaires response are subjected to this validation. See
 * https://www.hl7.org/fhir/valueset-item-type.html#expansion
 */
internal object MaxLengthValidator : AnswerConstraintValidator {
  override suspend fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
    context: Context,
    expressionEvaluator: suspend (Expression) -> Type?,
  ): ConstraintValidator.Result {
    if (
      questionnaireItem.hasMaxLength() &&
        answer.value.isPrimitive &&
        answer.value.asStringValue().length > questionnaireItem.maxLength
    ) {
      return ConstraintValidator.Result(
        false,
        "The maximum number of characters that are permitted in the answer is: " +
          questionnaireItem.maxLength,
      )
    }
    return ConstraintValidator.Result(true, null)
  }
}
