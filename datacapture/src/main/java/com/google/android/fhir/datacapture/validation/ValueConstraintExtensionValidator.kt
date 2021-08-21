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
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/** A interface for validating constraints from FHIR extension on a questionnaire response. */
internal open class ValueConstraintExtensionValidator(
  val url: String,
  val predicate:
    (Extension, QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent) -> Boolean,
  val messageGenerator: (Extension, Context) -> String
) : ConstraintValidator {
  override fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    context: Context
  ): ConstraintValidator.ConstraintValidationResult {
    if (questionnaireItem.hasExtension(url) && !questionnaireResponseItem.answer.isEmpty()) {
      val extension = questionnaireItem.getExtensionByUrl(url)
      // TODO(https://github.com/google/android-fhir/issues/487): Validates all answers.
      val answer = questionnaireResponseItem.answer[0]
      if (predicate(extension, answer)) {
        return ConstraintValidator.ConstraintValidationResult(
          false,
          messageGenerator(extension, context)
        )
      }
    }
    return ConstraintValidator.ConstraintValidationResult(true, null)
  }
}
