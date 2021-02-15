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
import com.google.fhir.r4.core.QuestionnaireItemTypeCode
import com.google.fhir.r4.core.QuestionnaireResponse
import com.google.fhir.shaded.protobuf.Message
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
                questionnaireItem.type,
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
                    evaluateEnableWhen(
                        questionnaireItem.type, it, questionnaireResponseItemRetriever)
                }
            EnableWhenBehaviorCode.Value.ANY ->
                enableWhenList.any {
                    evaluateEnableWhen(
                        questionnaireItem.type, it, questionnaireResponseItemRetriever)
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
        type: Questionnaire.Item.TypeCode,
        enableWhen: Questionnaire.Item.EnableWhen,
        questionnaireResponseItemRetriever: (linkId: String) -> QuestionnaireResponse.Item?
    ): Boolean {
        val responseItem =
            questionnaireResponseItemRetriever(enableWhen.question.value) ?: return true
        return if (QuestionnaireItemOperatorCode.Value.EXISTS == enableWhen.operator.value)
            (responseItem.answerCount > 0) == enableWhen.answer.boolean.value else
            responseItem.contains(enableWhen.toPredicate(type))
    }
}

/**
 * Return if any answer in the answer list satisfies the passed predicate.
 *
 * @param predicate boolean predicate function that takes a [QuestionnaireResponse.Item.Answer].
 */
private fun QuestionnaireResponse.Item.contains(
    predicate: (QuestionnaireResponse.Item.Answer) -> Boolean
): Boolean {
    return this.answerList.any {
        predicate(it)
    }
}

/**
 * Returns a predicate based on the `EnableWhen` `operator` and `Answer` value.
 *
 * @param type used to get value based on [Questionnaire.Item.TypeCode].
 */
private fun Questionnaire.Item.EnableWhen.toPredicate(type: Questionnaire.Item.TypeCode):
        (QuestionnaireResponse.Item.Answer) -> Boolean {
    val enableWhenAnswerValue = try {
        this.answer.getValueForType(type)
    } catch (exception: IllegalArgumentException) {
        this.answer.toByteString()
    }
    return {
        val answerValue = try {
            it.getValueForType(type)
        } catch (exception: java.lang.IllegalArgumentException) {
            it.value.toByteString()
        }
        when (val operator = this.operator.value) {
            QuestionnaireItemOperatorCode.Value.EQUALS ->
                answerValue == enableWhenAnswerValue
            QuestionnaireItemOperatorCode.Value.NOT_EQUAL_TO ->
                answerValue != enableWhenAnswerValue
            else -> throw NotImplementedError("Enable when operator $operator is not implemented.")
        }
    }
}

/**
 * TODO: move to a shared library with QuestionnaireResponse.Item.Answer.getValueForType
 * Returns the value of the [Questionnaire.Item.EnableWhen.AnswerX] for the [type].
 *
 * Used to retrieve the value to set the field in the extracted FHIR resource.
 *
 * @throws IllegalArgumentException if [type] is not supported (for example, questions of type
 * [QuestionnaireItemTypeCode.Value.URL] do not have an explicit EnableWhen answer).
 */
private fun Questionnaire.Item.EnableWhen.AnswerX.getValueForType(
    type: Questionnaire.Item.TypeCode
): Message = when (val value = type.value) {
    QuestionnaireItemTypeCode.Value.DATE -> this.date
    QuestionnaireItemTypeCode.Value.BOOLEAN -> this.boolean
    QuestionnaireItemTypeCode.Value.DECIMAL -> this.decimal
    QuestionnaireItemTypeCode.Value.INTEGER -> this.integer
    QuestionnaireItemTypeCode.Value.DATE_TIME -> this.dateTime
    QuestionnaireItemTypeCode.Value.TIME -> this.time
    QuestionnaireItemTypeCode.Value.STRING, QuestionnaireItemTypeCode.Value.TEXT ->
    this.stringValue
    else -> throw IllegalArgumentException("Unsupported value type $value")
}

/**
 * TODO: Replace with https://github.com/google/android-fhir/pull/222/files#diff-4d2985aba5e8203b97cc5dc2eac7dbbd0c7832be34a27036d1ecfb8e4a3a3e50R150
 * moved to shared library
 */
private fun QuestionnaireResponse.Item.Answer.getValueForType(
    type: Questionnaire.Item.TypeCode
): Message = when (val value = type.value) {
    QuestionnaireItemTypeCode.Value.DATE -> this.value.date
    QuestionnaireItemTypeCode.Value.BOOLEAN -> this.value.boolean
    QuestionnaireItemTypeCode.Value.DECIMAL -> this.value.decimal
    QuestionnaireItemTypeCode.Value.INTEGER -> this.value.integer
    QuestionnaireItemTypeCode.Value.DATE_TIME -> this.value.dateTime
    QuestionnaireItemTypeCode.Value.TIME -> this.value.time
    QuestionnaireItemTypeCode.Value.STRING, QuestionnaireItemTypeCode.Value.TEXT ->
        this.value.stringValue
    QuestionnaireItemTypeCode.Value.URL -> this.value.uri
    else -> throw IllegalArgumentException("Unsupported value type $value")
}
