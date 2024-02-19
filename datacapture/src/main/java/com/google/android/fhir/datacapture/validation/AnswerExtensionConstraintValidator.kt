/*
 * Copyright 2022-2023 Google LLC
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
 * Validates [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] against a constraint
 * defined in an extension.
 *
 * For example, an implementation may validate answers against a min value defined in the minValue
 * extension (see http://hl7.org/fhir/R4/extension-minvalue.html).
 *
 * @param url the URL of the extension that defines the constraint
 * @param predicate the predicate that determines if the answer is valid
 * @param messageGenerator a generator of error message if the answer is not valid
 */
internal open class AnswerExtensionConstraintValidator(
  val url: String,
  val predicate:
    (Extension, QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent) -> Boolean,
  val messageGenerator: (Extension, Context) -> String,
) : AnswerConstraintValidator {
  override fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
    context: Context,
  ): AnswerConstraintValidator.Result {
    if (questionnaireItem.hasExtension(url)) {
      val extension = questionnaireItem.getExtensionByUrl(url)
      if (predicate(extension, answer)) {
        return AnswerConstraintValidator.Result(false, messageGenerator(extension, context))
      }
    }
    return AnswerConstraintValidator.Result(true, null)
  }
}
