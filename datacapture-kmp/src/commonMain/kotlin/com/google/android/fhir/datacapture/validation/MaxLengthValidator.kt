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

import com.google.fhir.model.r4.Expression
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse

/**
 * A validator to check if the answer exceeds the maximum number of permitted characters.
 *
 * Only primitive types permitted in questionnaires response are subjected to this validation. See
 * https://www.hl7.org/fhir/valueset-item-type.html#expansion
 */
internal object MaxLengthValidator : AnswerConstraintValidator {
  override suspend fun validate(
    questionnaireItem: Questionnaire.Item,
    answer: QuestionnaireResponse.Item.Answer,
    expressionEvaluator: suspend (Expression) -> Any?,
  ): ConstraintValidator.Result {
    if (
      questionnaireItem.maxLength != null &&
        answer.value != null &&
        answer.value!!.asString()?.value!!.value!!.length > questionnaireItem.maxLength!!.value!!
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
