/*
 * Copyright 2023 Google LLC
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
import android.text.Spanned
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.localizedTextSpanned
import com.google.android.fhir.datacapture.extensions.toSpanned
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent

/**
 * Data item for [QuestionnaireItemViewHolder] in [RecyclerView].
 *
 * The view should use [questionnaireItem], [answers], [enabledAnswerOptions], [validationResult]
 * and [enabledDisplayItems] to render the data item in the UI. The view SHOULD NOT mutate the data
 * using these properties.
 *
 * The view should use the following answer APIs to update the answer(s):
 * - [setAnswer] (for single and repeated answers)
 * - [clearAnswer] (for single and repeated answers)
 * - [addAnswer] (for repeated answers only)
 * - [removeAnswer] (for repeated answers only)
 *
 * Updates to the answers using these APIs will invoke [answersChangedCallback] to notify the view
 * model that the answer(s) have been changed. This will trigger a re-render of the [RecyclerView]
 * UI.
 *
 * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] in the [Questionnaire]
 * @param questionnaireResponseItem the [QuestionnaireResponse.QuestionnaireResponseItemComponent]
 *   in the [QuestionnaireResponse]
 * @param validationResult the [ValidationResult] of the answer(s) against the `questionnaireItem`
 * @param answersChangedCallback the callback to notify the view model that the answers have been
 *   changed for the [QuestionnaireResponse.QuestionnaireResponseItemComponent]
 * @param resolveAnswerValueSet the callback to resolve the answer value set and return the answer
 * @param resolveAnswerExpression the callback to resolve answer options when answer-expression
 *   extension exists options
 * @param draftAnswer the draft input that cannot be stored in the [QuestionnaireResponse].
 * @param enabledDisplayItems the enabled display items in the given [questionnaireItem]
 * @param showOptionalText the optional text is being added to the end of the question text
 * @param questionViewTextConfiguration configuration to show asterisk, required and optional text
 *   in the header view.
 */
data class QuestionnaireViewItem(
  val questionnaireItem: Questionnaire.QuestionnaireItemComponent,
  private val questionnaireResponseItem: QuestionnaireResponseItemComponent,
  val validationResult: ValidationResult,
  internal val answersChangedCallback:
    suspend (
      Questionnaire.QuestionnaireItemComponent,
      QuestionnaireResponseItemComponent,
      List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>,
      Any?,
    ) -> Unit,
  val enabledAnswerOptions: List<Questionnaire.QuestionnaireItemAnswerOptionComponent> =
    questionnaireItem.answerOption.ifEmpty { emptyList() },
  val draftAnswer: Any? = null,
  val enabledDisplayItems: List<Questionnaire.QuestionnaireItemComponent> = emptyList(),
  val questionViewTextConfiguration: QuestionTextConfiguration = QuestionTextConfiguration(),
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

  /** Updates the answers. This will override any existing answers and removes the draft answer. */
  suspend fun setAnswer(
    vararg questionnaireResponseItemAnswerComponent:
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
  ) {
    check(questionnaireItem.repeats || questionnaireResponseItemAnswerComponent.size <= 1) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} has repeated answers."
    }
    answersChangedCallback(
      questionnaireItem,
      questionnaireResponseItem,
      questionnaireResponseItemAnswerComponent.toList(),
      null,
    )
  }

  /** Clears existing answers and any draft answer. */
  suspend fun clearAnswer() {
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, listOf(), null)
  }

  /** Adds an answer to the existing answers and removes the draft answer. */
  suspend fun addAnswer(
    questionnaireResponseItemAnswerComponent:
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
  ) {
    check(questionnaireItem.repeats) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} does not allow repeated answers"
    }
    answersChangedCallback(
      questionnaireItem,
      questionnaireResponseItem,
      answers + questionnaireResponseItemAnswerComponent,
      null,
    )
  }

  /** Removes an answer from the existing answers, as well as any draft answer. */
  suspend fun removeAnswer(
    vararg questionnaireResponseItemAnswerComponent:
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
  ) {
    check(questionnaireItem.repeats) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} does not allow repeated answers"
    }
    answersChangedCallback(
      questionnaireItem,
      questionnaireResponseItem,
      answers.filterNot { ans ->
        questionnaireResponseItemAnswerComponent.any { ans.value.equalsDeep(it.value) }
      },
      null,
    )
  }

  /**
   * Updates the draft answer stored in `QuestionnaireViewModel`. This clears any actual answer for
   * the question.
   */
  suspend fun setDraftAnswer(draftAnswer: Any? = null) {
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, listOf(), draftAnswer)
  }

  /**
   * Returns a given answer (The respondent's answer(s) to the question) along with [displayString]
   * if question is answered else 'Not Answered'
   */
  fun answerString(context: Context): String {
    if (!questionnaireResponseItem.hasAnswer()) return context.getString(R.string.not_answered)
    return questionnaireResponseItem.answer.joinToString { it.value.displayString(context) }
  }

  fun isAnswerOptionSelected(
    answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent,
  ): Boolean {
    return answers.any { it.value.equalsDeep(answerOption.value) }
  }

  /**
   * Fetches the question title that should be displayed to user. The title is first fetched from
   * [Questionnaire.QuestionnaireResponseItemComponent] (derived from cqf-expression), otherwise it
   * is derived from [localizedTextSpanned] of [QuestionnaireResponse.QuestionnaireItemComponent]
   */
  val questionText: Spanned? by lazy {
    questionnaireResponseItem.text?.toSpanned() ?: questionnaireItem.localizedTextSpanned
  }

  /**
   * Returns whether this [QuestionnaireViewItem] and the `other` [QuestionnaireViewItem] have the
   * same [Questionnaire.QuestionnaireItemComponent] and
   * [QuestionnaireResponse.QuestionnaireResponseItemComponent].
   *
   * This is useful for determining if two [QuestionnaireViewItem]s are representing the same
   * question and answer in the [Questionnaire] and [QuestionnaireResponse]. This can be used to
   * update the [RecyclerView] UI.
   */
  internal fun hasTheSameItem(other: QuestionnaireViewItem) =
    questionnaireItem === other.questionnaireItem &&
      questionnaireResponseItem === other.questionnaireResponseItem

  /**
   * Returns whether this [QuestionnaireViewItem] and the `other` [QuestionnaireViewItem] have the
   * same response.
   *
   * This is useful for determining if the [QuestionnaireViewItem] has outdated answer(s) or
   * question text and therefore needs to be updated in the [RecyclerView] UI.
   */
  internal fun hasTheSameResponse(other: QuestionnaireViewItem) =
    answers.size == other.answers.size &&
      answers
        .zip(other.answers) { answer, otherAnswer ->
          answer.value != null &&
            otherAnswer.value != null &&
            answer.value.equalsShallow(otherAnswer.value)
        }
        .all { it } &&
      draftAnswer == other.draftAnswer &&
      questionText == other.questionText

  /**
   * Returns whether this [QuestionnaireViewItem] and the `other` [QuestionnaireViewItem] have the
   * same [ValidationResult].
   *
   * This is useful for determining if the [QuestionnaireViewItem] has outdated [ValidationResult]
   * and therefore needs to be updated in the [RecyclerView] UI.
   */
  internal fun hasTheSameValidationResult(other: QuestionnaireViewItem): Boolean {
    if (validationResult is NotValidated || validationResult is Valid) {
      return other.validationResult is NotValidated || other.validationResult is Valid
    }
    return validationResult == other.validationResult
  }
}
