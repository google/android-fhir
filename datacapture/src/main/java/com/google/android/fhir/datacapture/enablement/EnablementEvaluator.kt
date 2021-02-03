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

/** Evaluator for the enablement status of a [Questionnaire.Item]. */
object EnablementEvaluator {

    /**
     * Returns whether [questionnaireItem] should be enabled.
     *
     * @param questionnaireResponseItemRetriever function that returns the
     * [QuestionnaireResponse.Item] with the `linkId`.
     */
    fun evaluate(
        questionnaireItem: Questionnaire.Item,
        questionnaireResponseItemRetriever: (String) -> QuestionnaireResponse.Item?
    ): Boolean {
        if (questionnaireItem.enableWhenList.isEmpty()) {
            return true
        }

        if (questionnaireItem.enableWhenList.size == 1) {
            return evaluateEnableWhen(
                questionnaireItem.enableWhenList.single(),
                questionnaireResponseItemRetriever
            )
        }

        return when (val value = questionnaireItem.enableBehavior.value) {
            EnableWhenBehaviorCode.Value.ALL ->
                questionnaireItem.enableWhenList.all {
                    evaluateEnableWhen(it, questionnaireResponseItemRetriever)
                }
            EnableWhenBehaviorCode.Value.ANY ->
                questionnaireItem.enableWhenList.any {
                    evaluateEnableWhen(it, questionnaireResponseItemRetriever)
                }
            else ->
                throw IllegalStateException("Unrecognized enable when behavior $value")
        }
    }

    /**
     * Returns whether the `enableWhen` expression is satisfied.
     *
     * @param questionnaireResponseItemRetriever function that returns the
     * [QuestionnaireResponse.Item] with the `linkId`.
     */
    private fun evaluateEnableWhen(
        enableWhen: Questionnaire.Item.EnableWhen,
        questionnaireResponseItemRetriever: (String) -> QuestionnaireResponse.Item?
    ): Boolean {
        val responseItem =
            questionnaireResponseItemRetriever(enableWhen.question.value) ?: return true
        return when (enableWhen.operator.value) {
            QuestionnaireItemOperatorCode.Value.EXISTS ->
                (responseItem.answerCount > 0) == enableWhen.answer.boolean.value
            else -> throw NotImplementedError("Not implemented yet.")
        }
    }
}
