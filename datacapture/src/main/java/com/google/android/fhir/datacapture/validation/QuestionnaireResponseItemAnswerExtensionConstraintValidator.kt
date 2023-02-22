/*
 * Copyright 2022 Google LLC
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

/**
 * Validates [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] using a constraint in
 * an extension.
 */
internal open class QuestionnaireResponseItemAnswerExtensionConstraintValidator(
  val url: String,
  val predicate:
    (Extension, QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent) -> Boolean,
  val messageGenerator: (Extension, Context) -> String
) : QuestionnaireResponseItemAnswerConstraintValidator {
  override fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
    context: Context
  ): QuestionnaireResponseItemAnswerConstraintValidator.Result {
    if (questionnaireItem.hasExtension(url)) {
      val extension = questionnaireItem.getExtensionByUrl(url)
      if (predicate(extension, answer)) {
        return QuestionnaireResponseItemAnswerConstraintValidator.Result(
          false,
          messageGenerator(extension, context)
        )
      }
    }
    return QuestionnaireResponseItemAnswerConstraintValidator.Result(true, null)
  }
}
