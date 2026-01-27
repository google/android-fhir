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

import com.google.android.fhir.datacapture.extensions.cqfCalculatedValueExpression
import com.google.fhir.model.r4.Expression
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse

/**
 * Validates [QuestionnaireResponse.Item.Answer] against a constraint defined in an extension.
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
    (
      /*constraintValue*/
      Any,
      QuestionnaireResponse.Item.Answer,
    ) -> Boolean,
  val messageGenerator: suspend (Any) -> String,
) : AnswerConstraintValidator {
  override suspend fun validate(
    questionnaireItem: Questionnaire.Item,
    answer: QuestionnaireResponse.Item.Answer,
    expressionEvaluator: suspend (Expression) -> Any?,
  ): ConstraintValidator.Result {
    if (questionnaireItem.extension.isNotEmpty()) {
      val extension = questionnaireItem.extension.find { it.url == url }
      val extensionValue =
        extension.cqfCalculatedValueExpression?.let { expressionEvaluator(it) } ?: extension?.value

      // Only checks constraint if both extension and answer have a value
      if (extensionValue != null && answer.value != null && predicate(extensionValue, answer)) {
        return ConstraintValidator.Result(
          false,
          messageGenerator(extensionValue),
        )
      }
    }
    return ConstraintValidator.Result(true, null)
  }
}
