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

import android.content.Context
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.validation.ConstraintValidator.ConstraintValidationResult
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * A validator to check if the answer exceeds the maximum number of permitted characters.
 *
 * <p>Only primitive types permitted in questionnaires response are subjected to this validation.
 * See https://www.hl7.org/fhir/valueset-item-type.html#expansion
 */
internal object PrimitiveTypeAnswerMaxLengthValidator : ConstraintValidator {
  override fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    context: Context
  ): ConstraintValidationResult {
    // TODO(https://github.com/google/android-fhir/issues/487): Validate all answers.
    val answer =
      questionnaireResponseItem.answer.singleOrNull()
        ?: return ConstraintValidationResult(true, null)

    if (questionnaireItem.hasMaxLength() &&
        answer.value.isPrimitive &&
        answer.value.asStringValue().length > questionnaireItem.maxLength
    ) {
      return ConstraintValidationResult(
        false,
        "The maximum number of characters that are permitted in the answer is: " +
          questionnaireItem.maxLength
      )
    }
    return ConstraintValidationResult(true, null)
  }
}
