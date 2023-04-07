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

package com.google.android.fhir.datacapture.enablement

import com.google.android.fhir.compareTo
import com.google.android.fhir.datacapture.extensions.allItems
import com.google.android.fhir.datacapture.extensions.enableWhenExpression
import com.google.android.fhir.datacapture.fhirpath.evaluateToBoolean
import com.google.android.fhir.equals
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Evaluator for the enablement status of a [Questionnaire.QuestionnaireItemComponent].
 *
 * This is done by locating the relevant [QuestionnaireResponse.QuestionnaireResponseItemComponent]s
 * specified by the linkIds in the `enableWhen` constraints, and checking if the answers (or lack
 * thereof) satisfy the criteria in the `enableWhen` constraints. The `enableBehavior` value is then
 * used to combine the evaluation results of different `enableWhen` constraints.
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
 * question with linkId `vitaminKgiven` has been answered.
 *
 * The enablement status typically determines whether the [Questionnaire.QuestionnaireItemComponent]
 * is shown or hidden. However, it is also possible that only user interaction is enabled or
 * disabled (e.g. grayed out) with the [Questionnaire.QuestionnaireItemComponent] always shown.
 *
 * The evaluator does not track the changes in the `questionnaire` and `questionnaireResponse`.
 * Therefore, a new evaluator should be created if they were modified.
 *
 * For more information see
 * [Questionnaire.item.enableWhen](https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.enableWhen)
 * and
 * [Questionnaire.item.enableBehavior](https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.enableBehavior)
 * .
 */
internal class EnablementEvaluator(val questionnaireResponse: QuestionnaireResponse) {
  /**
   * The pre-order traversal trace of the items in the [QuestionnaireResponse]. This essentially
   * represents the order in which all items are displayed in the UI.
   */
  private val questionnaireResponseItemPreOrderList = questionnaireResponse.allItems

  /** The map from each item in the [QuestionnaireResponse] to its parent. */
  private val questionnaireResponseItemParentMap =
    mutableMapOf<
      QuestionnaireResponse.QuestionnaireResponseItemComponent,
      QuestionnaireResponse.QuestionnaireResponseItemComponent
    >()

  init {
    /** Adds each child-parent pair in the [QuestionnaireResponse] to the parent map. */
    fun buildParentList(item: QuestionnaireResponse.QuestionnaireResponseItemComponent) {
      for (child in item.item) {
        questionnaireResponseItemParentMap[child] = item
        buildParentList(child)
      }
      for (answer in item.answer) {
        for (nestedItem in answer.item) {
          buildParentList(nestedItem)
        }
      }
    }

    for (item in questionnaireResponse.item) {
      buildParentList(item)
    }
  }

  /**
   * Returns whether [questionnaireItem] should be enabled.
   *
   * @param questionnaireResponseItem the corresponding questionnaire response item.
   */
  fun evaluate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
  ): Boolean {
    val enableWhenList = questionnaireItem.enableWhen
    val enableWhenExpression = questionnaireItem.enableWhenExpression

    // The questionnaire item is enabled by default if there is no `enableWhen` constraint and no
    // `enableWhenExpression`.
    if (enableWhenList.isEmpty() && enableWhenExpression == null) return true

    // Evaluate `enableWhenExpression`.
    if (enableWhenExpression != null && enableWhenExpression.hasExpression()) {
      return evaluateToBoolean(
        questionnaireResponse,
        questionnaireResponseItem,
        enableWhenExpression.expression
      )
    }

    // Evaluate single `enableWhen` constraint.
    if (enableWhenList.size == 1) {
      return evaluateEnableWhen(
        enableWhenList.single(),
        questionnaireItem,
        questionnaireResponseItem,
      )
    }

    // Evaluate multiple `enableWhen` constraints and aggregate the results according to
    // `enableBehavior` which specifies one of the two behaviors: 1) the questionnaire item is
    // enabled if ALL `enableWhen` constraints are satisfied, or 2) the questionnaire item is
    // enabled if ANY `enableWhen` constraint is satisfied.
    return when (val value = questionnaireItem.enableBehavior) {
      Questionnaire.EnableWhenBehavior.ALL ->
        enableWhenList.all { evaluateEnableWhen(it, questionnaireItem, questionnaireResponseItem) }
      Questionnaire.EnableWhenBehavior.ANY ->
        enableWhenList.any { evaluateEnableWhen(it, questionnaireItem, questionnaireResponseItem) }
      else -> throw IllegalStateException("Unrecognized enable when behavior $value")
    }
  }

  /**
   * Returns whether the `enableWhen` constraint is satisfied for the `questionnaireResponseItem`.
   */
  private fun evaluateEnableWhen(
    enableWhen: Questionnaire.QuestionnaireItemEnableWhenComponent,
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
  ): Boolean {
    val targetQuestionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent? =
      if (questionnaireItem.type == Questionnaire.QuestionnaireItemType.DISPLAY &&
          questionnaireResponseItem.linkId == enableWhen.question
      )
        questionnaireResponseItem
      else findEnableWhenQuestionnaireResponseItem(questionnaireResponseItem, enableWhen.question)
    return if (Questionnaire.QuestionnaireItemOperator.EXISTS == enableWhen.operator) {
      // True iff the answer value of the enable when is equal to whether an answer exists in the
      // target questionnaire response item
      enableWhen.answerBooleanType.booleanValue() ==
        !(targetQuestionnaireResponseItem == null ||
          targetQuestionnaireResponseItem.answer.isEmpty())
    } else {
      // The `enableWhen` constraint evaluates to true if at least one answer has a value that
      // satisfies the `enableWhen` operator and answer, with the exception of the `Exists`
      // operator.
      // See https://www.hl7.org/fhir/valueset-questionnaire-enable-operator.html.
      targetQuestionnaireResponseItem?.answer?.any { enableWhen.predicate(it) } ?: false
    }
  }

  /**
   * Find a questionnaire response item in [QuestionnaireResponse] with the given `linkId` starting
   * from the `origin`.
   *
   * This is used by the enableWhen logic to evaluate if a question should be enabled/displayed.
   *
   * If multiple questionnaire response items are present for the same question (same linkId),
   * either as a result of repeated group or nested question under repeated answers, this returns
   * the nearest question occurrence reachable by tracing first the "ancestor" axis and then the
   * "preceding" axis and then the "following" axis.
   *
   * See
   * https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.enableWhen.question.
   */
  private fun findEnableWhenQuestionnaireResponseItem(
    origin: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    linkId: String,
  ): QuestionnaireResponse.QuestionnaireResponseItemComponent? {
    // Find the nearest ancestor with the linkId
    var parent = questionnaireResponseItemParentMap[origin]
    while (parent != null) {
      if (parent.linkId == linkId) {
        return parent
      }
      parent = questionnaireResponseItemParentMap[parent]
    }

    // Find the nearest item preceding the origin
    val itemIndex = questionnaireResponseItemPreOrderList.indexOf(origin)
    for (index in itemIndex - 1 downTo 0) {
      if (questionnaireResponseItemPreOrderList[index].linkId == linkId) {
        return questionnaireResponseItemPreOrderList[index]
      }
    }

    // Find the nearest item succeeding the origin
    for (index in itemIndex + 1 until questionnaireResponseItemPreOrderList.size) {
      if (questionnaireResponseItemPreOrderList[index].linkId == linkId) {
        return questionnaireResponseItemPreOrderList[index]
      }
    }

    return null
  }
}

/**
 * The predicate to evaluate the status of the enableWhen on the `EnableWhen` `operator` and
 * `Answer` value.
 */
private val Questionnaire.QuestionnaireItemEnableWhenComponent.predicate:
  (QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent) -> Boolean
  get() = {
    when (operator) {
      Questionnaire.QuestionnaireItemOperator.EQUAL -> {
        equals(it.value, answer)
      }
      Questionnaire.QuestionnaireItemOperator.NOT_EQUAL -> {
        !equals(it.value, answer)
      }
      Questionnaire.QuestionnaireItemOperator.GREATER_THAN -> {
        it.value > answer
      }
      Questionnaire.QuestionnaireItemOperator.GREATER_OR_EQUAL -> {
        it.value >= answer
      }
      Questionnaire.QuestionnaireItemOperator.LESS_THAN -> {
        it.value < answer
      }
      Questionnaire.QuestionnaireItemOperator.LESS_OR_EQUAL -> {
        it.value <= answer
      }
      else -> throw NotImplementedError("Enable when operator $operator is not implemented.")
    }
  }
