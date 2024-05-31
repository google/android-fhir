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
import com.google.android.fhir.datacapture.extensions.cqfCalculatedValueExpression
import com.google.android.fhir.datacapture.extensions.hasValue
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type

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
    (
      /*constraintValue*/
      Type,
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
    ) -> Boolean,
  val messageGenerator: (Type, Context) -> String,
) : AnswerConstraintValidator {
  override suspend fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
    context: Context,
    expressionEvaluator: suspend (Expression) -> Type?,
  ): ConstraintValidator.Result {
    if (questionnaireItem.hasExtension(url)) {
      val extension = questionnaireItem.getExtensionByUrl(url)
      val extensionValue =
        extension.value.cqfCalculatedValueExpression?.let { expressionEvaluator(it) }
          ?: extension.value

      // Only checks constraint if both extension and answer have a value
      if (
        extensionValue.hasValue() && answer.value.hasValue() && predicate(extensionValue, answer)
      ) {
        return ConstraintValidator.Result(
          false,
          messageGenerator(extensionValue, context),
        )
      }
    }
    return ConstraintValidator.Result(true, null)
  }
}
