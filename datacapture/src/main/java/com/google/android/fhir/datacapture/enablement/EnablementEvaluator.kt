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

package com.google.android.fhir.datacapture.enablement

import java.lang.IllegalStateException
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Evaluator for the enablement status of a [Questionnaire.QuestionnaireItemComponent]. Uses the
 * `enableWhen` constraints and the `enableBehavior` value defined in the
 * [Questionnaire.QuestionnaireItemComponent]. Also depends on the answers (or lack thereof)
 * captured in the specified [QuestionnaireResponse.QuestionnaireResponseItemComponent] s.
 *
 * For example, the following `enableWhen` constraint in a
 * [Questionnaire.QuestionnaireItemComponent]
 * ```
 *     "enableWhen": [
 *       {
 *         "question": "vitaminKgiven",
 *         "operator": "exists",
 *         "answerBoolean": true
 *       }
 *     ],
 * ```
 * specifies that the [Questionnaire.QuestionnaireItemComponent] should be enabled only if the
 * question with ID `vitaminKgiven` has been answered.
 *
 * The enablement status typically determines whether the [Questionnaire.QuestionnaireItemComponent]
 * is shown or hidden. However, it is also possible that only user interaction is enabled or
 * disabled (e.g. grayed out) with the [Questionnaire.QuestionnaireItemComponent] always shown.
 *
 * For more information see
 * [Questionnaire.item.enableWhen](https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.enableWhen)
 * and
 * [Questionnaire.item.enableBehavior](https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.enableBehavior)
 * .
 */
internal object EnablementEvaluator {

  /**
   * Returns whether [questionnaireItem] should be enabled.
   *
   * @param questionnaireResponseItemRetriever function that returns the
   * [QuestionnaireResponse.Item] with the `linkId`, or null if there isn't one.
   *
   * For example, the questionnaireItem might be
   */
  fun evaluate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItemRetriever:
      (linkId: String) -> QuestionnaireResponse.QuestionnaireResponseItemComponent?
  ): Boolean {
    val enableWhenList = questionnaireItem.enableWhen

    // The questionnaire item is enabled by default if there is no `enableWhen` constraint.
    if (enableWhenList.isEmpty()) return true

    // Evaluate single `enableWhen` constraint.
    if (enableWhenList.size == 1) {
      return evaluateEnableWhen(
        questionnaireItem.type,
        enableWhenList.single(),
        questionnaireResponseItemRetriever
      )
    }

    // Evaluate multiple `enableWhen` constraints and aggregate the results according to
    // `enableBehavior` which specifies one of the two behaviors: 1) the questionnaire item is
    // enabled if ALL `enableWhen` constraints are satisfied, or 2) the questionnaire item is
    // enabled if ANY `enableWhen` constraint is satisfied.
    return when (val value = questionnaireItem.enableBehavior) {
      Questionnaire.EnableWhenBehavior.ALL ->
        enableWhenList.all {
          evaluateEnableWhen(questionnaireItem.type, it, questionnaireResponseItemRetriever)
        }
      Questionnaire.EnableWhenBehavior.ANY ->
        enableWhenList.any {
          evaluateEnableWhen(questionnaireItem.type, it, questionnaireResponseItemRetriever)
        }
      else -> throw IllegalStateException("Unrecognized enable when behavior $value")
    }
  }

  /**
   * Returns whether the `enableWhen` constraint is satisfied.
   *
   * @param questionnaireResponseItemRetriever function that returns the
   * [QuestionnaireResponse.Item] with the `linkId`, or null if there isn't one.
   */
  private fun evaluateEnableWhen(
    type: Questionnaire.QuestionnaireItemType,
    enableWhen: Questionnaire.QuestionnaireItemEnableWhenComponent,
    questionnaireResponseItemRetriever:
      (linkId: String) -> QuestionnaireResponse.QuestionnaireResponseItemComponent?
  ): Boolean {
    val responseItem = questionnaireResponseItemRetriever(enableWhen.question) ?: return true
    return if (Questionnaire.QuestionnaireItemOperator.EXISTS == enableWhen.operator) {
      (responseItem.answer.size > 0) == enableWhen.answerBooleanType.booleanValue()
    } else {
      responseItem.contains(enableWhenTypeToPredicate(enableWhen, type))
    }
  }
}

/**
 * Return if any answer in the answer list satisfies the passed predicate.
 *
 * @param predicate boolean predicate function that takes a [QuestionnaireResponse.Item.Answer].
 */
private fun QuestionnaireResponse.QuestionnaireResponseItemComponent.contains(
  predicate: (QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent) -> Boolean
): Boolean {
  return this.answer.any { predicate(it) }
}

/**
 * Returns a predicate based on the `EnableWhen` `operator` and `Answer` value.
 *
 * @param type used to get value based on [Questionnaire.Item.TypeCode].
 */
private fun enableWhenTypeToPredicate(
  enableWhen: Questionnaire.QuestionnaireItemEnableWhenComponent,
  type: Questionnaire.QuestionnaireItemType
): (QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent) -> Boolean {
  when (val operator = enableWhen.operator) {
    Questionnaire.QuestionnaireItemOperator.EQUAL -> return {
        it.value.toString() == enableWhen.answer.toString()
      }
    Questionnaire.QuestionnaireItemOperator.NOT_EQUAL -> return {
        it.value.toString() != enableWhen.answer.toString()
      }
    else -> throw NotImplementedError("Enable when operator $operator is not implemented.")
  }
}
