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

package com.google.android.fhir.datacapture.views

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.answerExpression
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Data item for [QuestionnaireItemViewHolder] in [RecyclerView].
 *
 * The view should use [questionnaireItem], [answers], [answerOption], and [validationResult] to
 * render the data item in the UI. The view SHOULD NOT mutate the data using these properties.
 *
 * The view should use the following answer APIs to update the answer(s):
 * - [setAnswer] (for single answer only)
 * - [addAnswer] (for repeated answers only)
 * - [removeAnswer] (for repeated answers only)
 * - [clearAnswer] (for both single and repated answers)
 *
 * Updates to the answers using these APIs will invoke [answersChangedCallback] to notify the view
 * model that the answer(s) have been changed. This will trigger a re-render of the [RecyclerView]
 * UI.
 *
 * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] in the [Questionnaire]
 * @param questionnaireResponseItem the [QuestionnaireResponse.QuestionnaireResponseItemComponent]
 * in the [QuestionnaireResponse]
 * @param validationResult the [ValidationResult] of the answer(s) against the `questionnaireItem`
 * @param answersChangedCallback the callback to notify the view model that the answers have been
 * changed for the [QuestionnaireResponse.QuestionnaireResponseItemComponent]
 * @param resolveAnswerValueSet the callback to resolve the answer value set and return the answer
 * @param resolveAnswerExpression the callback to resolve answer options when answer-expression
 * extension exists options
 */
data class QuestionnaireItemViewItem(
  val questionnaireItem: Questionnaire.QuestionnaireItemComponent,
  private val questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
  val validationResult: ValidationResult,
  internal val answersChangedCallback:
    (
      Questionnaire.QuestionnaireItemComponent,
      QuestionnaireResponse.QuestionnaireResponseItemComponent,
      List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>
    ) -> Unit,
  private val resolveAnswerValueSet:
    suspend (String) -> List<Questionnaire.QuestionnaireItemAnswerOptionComponent> =
    {
      emptyList()
    },
  private val resolveAnswerExpression:
    suspend (Questionnaire.QuestionnaireItemComponent) -> List<
        Questionnaire.QuestionnaireItemAnswerOptionComponent> =
    {
      emptyList()
    }
) {

  /**
   * A read-only list of answers to be rendered in the view.
   *
   * The view should call the APIs provided in this class ([setAnswer], [addAnswer], [removeAnswer]
   * and [clearAnswer]) to modify the answers. This is to make sure any updates to the answers are
   * propagated to the view model and a subsequent UI refresh will be triggered (e.g. in case the
   * enablement status or validation results of this or other questions are affected).
   *
   * This is a deep copy of the answers in the
   * [QuestionnaireResponse.QuestionnaireResponseItemComponent] so that proper comparisons can be
   * carried out for the [RecyclerView.Adapter] to decide which items need to be updated.
   */
  val answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> =
    questionnaireResponseItem.answer.map { it.copy() }

  fun setAnswer(
    questionnaireResponseItemAnswerComponent:
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
  ) {
    check(!questionnaireItem.repeats) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} has repeated answers. Use addAnswer instead."
    }
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, listOf(questionnaireResponseItemAnswerComponent))
  }

  internal fun answerString(context: Context): String {
    if (!questionnaireResponseItem.hasAnswer()) return context.getString(R.string.not_answered)
    return questionnaireResponseItem.answer.joinToString { it.displayString(context) }
  }

  internal fun addAnswer(
    questionnaireResponseItemAnswerComponent:
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
  ) {
    check(questionnaireItem.repeats) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} does not allow repeated answers"
    }
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, answers + questionnaireResponseItemAnswerComponent)
  }

  internal fun removeAnswer(
    questionnaireResponseItemAnswerComponent:
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
  ) {
    check(questionnaireItem.repeats) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} does not allow repeated answers"
    }
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, answers.toMutableList().apply {
      removeIf { it.value.equalsDeep(questionnaireResponseItemAnswerComponent.value) }
    })
  }

  fun clearAnswer() {
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, listOf())
  }

  fun isAnswerOptionSelected(
    answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent
  ): Boolean {
    return answers.any { it.value.equalsDeep(answerOption.value) }
  }

  /**
   * In a `choice` or `open-choice` type question, the answer options are defined in one of the
   * three elements in the questionnaire:
   *
   * - `Questionnaire.item.answerOption`: a list of permitted answers to the question
   * - `Questionnaire.item.answerValueSet`: a reference to a value set containing a list of
   * permitted answers to the question
   * - `Extension answer-expression`: an expression based extension which defines the x-fhir-query
   * or fhirpath to evaluate permitted answer options
   *
   * This property returns the answer options defined in one of the sources above. If the answer
   * options are defined in `Questionnaire.item.answerValueSet`, the answer value set will be
   * expanded.
   */
  internal val answerOption: List<Questionnaire.QuestionnaireItemAnswerOptionComponent>
    get() =
      runBlocking(Dispatchers.IO) {
        when {
          questionnaireItem.answerOption.isNotEmpty() -> questionnaireItem.answerOption
          !questionnaireItem.answerValueSet.isNullOrEmpty() ->
            resolveAnswerValueSet(questionnaireItem.answerValueSet)
          questionnaireItem.answerExpression != null -> resolveAnswerExpression(questionnaireItem)
          else -> emptyList()
        }
      }

  /**
   * Returns whether this [QuestionnaireItemViewItem] and the `other` [QuestionnaireItemViewItem]
   * have the same [Questionnaire.QuestionnaireItemComponent] and
   * [QuestionnaireResponse.QuestionnaireResponseItemComponent].
   *
   * This is useful for determining if two [QuestionnaireItemViewItem]s are representing the same
   * question and answer in the [Questionnaire] and [QuestionnaireResponse]. This can be used to
   * update the [RecyclerView] UI.
   */
  internal fun hasTheSameItem(other: QuestionnaireItemViewItem) =
    questionnaireItem === other.questionnaireItem &&
      questionnaireResponseItem === other.questionnaireResponseItem

  /**
   * Returns whether this [QuestionnaireItemViewItem] and the `other` [QuestionnaireItemViewItem]
   * have the same answers.
   *
   * This is useful for determining if the [QuestionnaireItemViewItem] has outdated answer(s) and
   * therefore needs to be updated in the [RecyclerView] UI.
   */
  internal fun hasTheSameAnswer(other: QuestionnaireItemViewItem) =
    answers.size == other.answers.size &&
      answers
        .zip(other.answers) { answer, otherAnswer ->
          answer.value != null &&
            otherAnswer.value != null &&
            answer.value.equalsShallow(otherAnswer.value)
        }
        .all { it }

  /**
   * Returns whether this [QuestionnaireItemViewItem] and the `other` [QuestionnaireItemViewItem]
   * have the same [ValidationResult].
   *
   * This is useful for determining if the [QuestionnaireItemViewItem] has outdated
   * [ValidationResult] and therefore needs to be updated in the [RecyclerView] UI.
   */
  internal fun hasTheSameValidationResult(other: QuestionnaireItemViewItem): Boolean {
    if (validationResult is NotValidated || validationResult is Valid) {
      return other.validationResult is NotValidated || other.validationResult is Valid
    }
    return validationResult == other.validationResult
  }
}
