/*
 * Copyright 2021 Google LLC
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

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport
import com.google.android.fhir.compareTo
import com.google.android.fhir.datacapture.createLinkIdToQuestionnaireResponseItemMap
import com.google.android.fhir.datacapture.enableWhenExpression
import com.google.android.fhir.equals
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.utils.FHIRPathEngine

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

  /** Returns whether [questionnaireItem] should be enabled. */
  fun evaluate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponse: QuestionnaireResponse
  ): Boolean {
    val enableWhenList = questionnaireItem.enableWhen
    val enableWhenExpression = questionnaireItem.enableWhenExpression

    // The questionnaire item is enabled by default if there is no `enableWhen` constraint and no
    // `enableWhenExpression`.
    if (enableWhenList.isEmpty() && enableWhenExpression == null) return true

    // Evaluate `enableWhenExpression`.
    if (enableWhenExpression != null && enableWhenExpression.hasExpression()) {
      return fhirPathEngine.convertToBoolean(
        fhirPathEngine.evaluate(questionnaireResponse, enableWhenExpression.expression)
      )
    }

    // Evaluate single `enableWhen` constraint.
    if (enableWhenList.size == 1) {
      return evaluateEnableWhen(enableWhenList.single(), questionnaireResponse)
    }

    // Evaluate multiple `enableWhen` constraints and aggregate the results according to
    // `enableBehavior` which specifies one of the two behaviors: 1) the questionnaire item is
    // enabled if ALL `enableWhen` constraints are satisfied, or 2) the questionnaire item is
    // enabled if ANY `enableWhen` constraint is satisfied.
    return when (val value = questionnaireItem.enableBehavior) {
      Questionnaire.EnableWhenBehavior.ALL ->
        enableWhenList.all { evaluateEnableWhen(it, questionnaireResponse) }
      Questionnaire.EnableWhenBehavior.ANY ->
        enableWhenList.any { evaluateEnableWhen(it, questionnaireResponse) }
      else -> throw IllegalStateException("Unrecognized enable when behavior $value")
    }
  }
}

/** Returns whether the `enableWhen` constraint is satisfied. */
private fun evaluateEnableWhen(
  enableWhen: Questionnaire.QuestionnaireItemEnableWhenComponent,
  questionnaireResponse: QuestionnaireResponse
): Boolean {
  val questionnaireResponseItem =
    linkIdToQuestionnaireResponseItemMap(questionnaireResponse)[(enableWhen.question)]
  return if (Questionnaire.QuestionnaireItemOperator.EXISTS == enableWhen.operator) {
    (questionnaireResponseItem == null || questionnaireResponseItem.answer.isEmpty()) !=
      enableWhen.answerBooleanType.booleanValue()
  } else {
    // The `enableWhen` constraint evaluates to true if at least one answer has a value that
    // satisfies the `enableWhen` operator and answer, with the exception of the `Exists` operator.
    // See https://www.hl7.org/fhir/valueset-questionnaire-enable-operator.html.
    questionnaireResponseItem?.answer?.any { enableWhen.predicate(it) } ?: false
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

// Create fhirPathEngine instance
val fhirPathEngine: FHIRPathEngine =
  with(FhirContext.forCached(FhirVersionEnum.R4)) {
    FHIRPathEngine(HapiWorkerContext(this, DefaultProfileValidationSupport(this)))
  }

/** Map from link IDs to questionnaire response items. */
private fun linkIdToQuestionnaireResponseItemMap(questionnaireResponse: QuestionnaireResponse) =
  createLinkIdToQuestionnaireResponseItemMap(questionnaireResponse.item)
