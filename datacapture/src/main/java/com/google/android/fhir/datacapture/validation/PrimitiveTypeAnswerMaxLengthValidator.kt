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

import com.google.android.fhir.datacapture.validation.ConstraintValidator.ConstraintValidationResult
import org.hl7.fhir.r4.model.PrimitiveType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * A validator to check if the answer exceeds the maximum number of permitted characters.
 *
 * <p>Only the following primitive types are subjected to this validation:
 * 1. BooleanType
 * 2. DecimalType
 * 3. IntegerType
 * 4. DateType
 * 5. TimeType
 * 6. StringType
 * 7. UriType
 */
internal object PrimitiveTypeAnswerMaxLengthValidator : ConstraintValidator {
  override fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent
  ): ConstraintValidationResult {
    // TODO(https://github.com/google/android-fhir/issues/487): Validate all answers.
    val answer = questionnaireResponseItem.answer[0].value
    if (questionnaireItem.hasMaxLength() &&
        answer.isPrimitive &&
        (answer as PrimitiveType<*>).asStringValue().length > questionnaireItem.maxLength
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
