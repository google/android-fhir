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

import com.google.fhir.r4.core.EnableWhenBehaviorCode
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireItemOperatorCode
import com.google.fhir.r4.core.QuestionnaireResponse
import java.lang.IllegalStateException

/**
 * Evaluator for the enablement status of a [Questionnaire.Item]. Uses the `enableWhen` constraints
 * and the `enableBehavior` value defined in the [Questionnaire.Item]. Also depends on the answers
 * (or lack thereof) captured in the specified [QuestionnaireResponse.Item]s.
 *
 * For example, the following `enableWhen` constraint in a [Questionnaire.Item]
 *     "enableWhen": [
 *       {
 *         "question": "vitaminKgiven",
 *         "operator": "exists",
 *         "answerBoolean": true
 *       }
 *     ],
 * specifies that the [Questionnaire.Item] should be enabled only if the question with ID
 * `vitaminKgiven` has been answered.
 *
 * The enablement status typically determines whether the [Questionnaire.Item] is shown or hidden.
 * However, it is also possible that only user interaction is enabled or disabled (e.g. grayed out)
 * with the [Questionnaire.Item] always shown.
 *
 * For more information see [Questionnaire.item.enableWhen](https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.enableWhen)
 * and [Questionnaire.item.enableBehavior](https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.enableBehavior).
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
        questionnaireItem: Questionnaire.Item,
        questionnaireResponseItemRetriever: (linkId: String) -> QuestionnaireResponse.Item?
    ): Boolean {
        val enableWhenList = questionnaireItem.enableWhenList

        // The questionnaire item is enabled by default if there is no `enableWhen` constraint.
        if (enableWhenList.isEmpty()) return true

        // Evaluate single `enableWhen` constraint.
        if (enableWhenList.size == 1) {
            return evaluateEnableWhen(
                enableWhenList.single(),
                questionnaireResponseItemRetriever
            )
        }

        // Evaluate multiple `enableWhen` constraints and aggregate the results according to
        // `enableBehavior` which specifies one of the two behaviors: 1) the questionnaire item is
        // enabled if ALL `enableWhen` constraints are satisfied, or 2) the questionnaire item is
        // enabled if ANY `enableWhen` constraint is satisfied.
        return when (val value = questionnaireItem.enableBehavior.value) {
            EnableWhenBehaviorCode.Value.ALL ->
                enableWhenList.all {
                    evaluateEnableWhen(it, questionnaireResponseItemRetriever)
                }
            EnableWhenBehaviorCode.Value.ANY ->
                enableWhenList.any {
                    evaluateEnableWhen(it, questionnaireResponseItemRetriever)
                }
            else ->
                throw IllegalStateException("Unrecognized enable when behavior $value")
        }
    }

    /**
     * Returns whether the `enableWhen` constraint is satisfied.
     *
     * @param questionnaireResponseItemRetriever function that returns the
     * [QuestionnaireResponse.Item] with the `linkId`, or null if there isn't one.
     */
    private fun evaluateEnableWhen(
        enableWhen: Questionnaire.Item.EnableWhen,
        questionnaireResponseItemRetriever: (linkId: String) -> QuestionnaireResponse.Item?
    ): Boolean {
        val responseItem =
            questionnaireResponseItemRetriever(enableWhen.question.value) ?: return true
        return when (val operator = enableWhen.operator.value) {
            QuestionnaireItemOperatorCode.Value.EXISTS ->
                (responseItem.answerCount > 0) == enableWhen.answer.boolean.value
            else -> throw NotImplementedError("Enable when operator $operator is not implemented.")
        }
    }
}
