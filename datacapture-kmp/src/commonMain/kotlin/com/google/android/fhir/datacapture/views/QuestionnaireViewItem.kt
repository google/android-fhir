/*
 * Copyright 2023-2026 Google LLC
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

import androidx.compose.ui.text.AnnotatedString
import com.google.android.fhir.datacapture.extensions.elementValue
import com.google.android.fhir.datacapture.extensions.isHelpCode
import com.google.android.fhir.datacapture.extensions.localizedTextAnnotatedString
import com.google.android.fhir.datacapture.extensions.maxValue
import com.google.android.fhir.datacapture.extensions.minValue
import com.google.android.fhir.datacapture.extensions.toAnnotatedString
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse

/**
 * Data item for [QuestionnaireItemViewHolder] in [androidx.compose.foundation.lazy.LazyColumn].
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
 * model that the answer(s) have been changed. This will trigger a refresh of the
 * [androidx.compose.foundation.lazy.LazyColumn] UI.
 *
 * @param questionnaireItem the [Questionnaire.Item] in the [Questionnaire]
 * @param questionnaireResponseItem the [QuestionnaireResponse.Item] in the [QuestionnaireResponse]
 * @param validationResult the [ValidationResult] of the answer(s) against the `questionnaireItem`
 * @param answersChangedCallback the callback to notify the view model that the answers have been
 *   changed for the [QuestionnaireResponse.Item]
 * @param enabledAnswerOptions the enabled answer options in [questionnaireItem]
 * @param minAnswerValue the inclusive lower bound on the range of allowed answer values, that may
 *   be used for widgets that check for bounds and change behavior based on the min allowed answer
 *   value, e.g the Slider widget
 * @param maxAnswerValue the inclusive upper bound on the range of allowed answer values, that may
 *   be used for widgets that check for bounds and change behavior based on the max allowed answer
 *   value, e.g the Slider widget
 * @param draftAnswer the draft input that cannot be stored in the [QuestionnaireResponse].
 * @param enabledDisplayItems the enabled display items in the given [questionnaireItem]
 * @param questionViewTextConfiguration configuration to show asterisk, required and optional text
 *   in the header view.
 */
data class QuestionnaireViewItem(
  val questionnaireItem: Questionnaire.Item,
  private val questionnaireResponseItem: QuestionnaireResponse.Item,
  val validationResult: ValidationResult,
  internal val answersChangedCallback:
    suspend (
      Questionnaire.Item,
      QuestionnaireResponse.Item,
      List<QuestionnaireResponse.Item.Answer>,
      Any?,
    ) -> Unit,
  val enabledAnswerOptions: List<Questionnaire.Item.AnswerOption> =
    questionnaireItem.answerOption.ifEmpty { emptyList() },
  val minAnswerValue: Any? = questionnaireItem.minValue,
  val maxAnswerValue: Any? = questionnaireItem.maxValue,
  val draftAnswer: Any? = null,
  val enabledDisplayItems: List<Questionnaire.Item> = emptyList(),
  val questionViewTextConfiguration: QuestionTextConfiguration = QuestionTextConfiguration(),
  val isHelpCardOpen: Boolean = questionnaireItem.isHelpCode,
  val helpCardStateChangedCallback: (Boolean, QuestionnaireResponse.Item) -> Unit = { _, _ -> },
) {

  fun getQuestionnaireResponseItem(): QuestionnaireResponse.Item = questionnaireResponseItem

  /**
   * A read-only list of answers to be rendered in the view.
   *
   * The view should call the APIs provided in this class ([setAnswer], [addAnswer], [removeAnswer]
   * and [clearAnswer]) to modify the answers. This is to make sure any updates to the answers are
   * propagated to the view model and a subsequent UI refresh will be triggered (e.g. in case the
   * enablement status or validation results of this or other questions are affected).
   */
  val answers: List<QuestionnaireResponse.Item.Answer> = questionnaireResponseItem.answer

  /** Updates the answers. This will override any existing answers and removes the draft answer. */
  suspend fun setAnswer(
    vararg questionnaireResponseItemAnswerComponent: QuestionnaireResponse.Item.Answer,
  ) {
    check(
      questionnaireItem.repeats?.value == true ||
        questionnaireResponseItemAnswerComponent.size <= 1,
    ) {
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
    questionnaireResponseItemAnswerComponent: QuestionnaireResponse.Item.Answer,
  ) {
    check(questionnaireItem.repeats?.value == true) {
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
    vararg questionnaireResponseItemAnswerComponent: QuestionnaireResponse.Item.Answer,
  ) {
    check(questionnaireItem.repeats?.value == true) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} does not allow repeated answers"
    }
    answersChangedCallback(
      questionnaireItem,
      questionnaireResponseItem,
      answers.filterNot { ans ->
        questionnaireResponseItemAnswerComponent.any { ans.value == it.value }
      },
      null,
    )
  }

  internal suspend fun removeAnswerAt(index: Int) {
    check(questionnaireItem.repeats?.value == true) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} does not allow repeated answers"
    }
    require(index in answers.indices) {
      "removeAnswerAt($index), but ${questionnaireItem.linkId} only has ${answers.size} answers"
    }
    answersChangedCallback(
      questionnaireItem,
      questionnaireResponseItem,
      answers.filterIndexed { currentIndex, _ -> currentIndex != index },
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
   * Fetches the question title that should be displayed to user. The title is first fetched from
   * [QuestionnaireResponse.Item] (derived from cqf-expression), otherwise it is derived from
   * [localizedTextAnnotatedString] of [QuestionnaireResponse.Item]
   */
  val questionText: AnnotatedString? by lazy {
    questionnaireResponseItem.text?.value?.toAnnotatedString()
      ?: questionnaireItem.localizedTextAnnotatedString
  }

  /**
   * Returns a given answer (The respondent's answer(s) to the question) along with [displayString]
   * if question is answered else 'Not Answered'
   */

  //  fun answerString(context: Context): String {
  //    if (!questionnaireResponseItem.hasAnswer()) return context.getString(R.string.not_answered)
  //    return questionnaireResponseItem.answer.joinToString { it.value.displayString(context) }
  //  }
  //

  fun isAnswerOptionSelected(
    answerOption: Questionnaire.Item.AnswerOption,
  ): Boolean {
    return answers.any { it.elementValue == answerOption.elementValue }
  }

  /**
   * Returns whether this [QuestionnaireViewItem] and the `other` [QuestionnaireViewItem] have the
   * same [Questionnaire.Item] and [QuestionnaireResponse.Item].
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
   * question text and therefore needs to be updated in the list UI.
   */
  internal fun hasTheSameResponse(other: QuestionnaireViewItem) =
    answers.size == other.answers.size &&
      answers
        .zip(other.answers) { answer, otherAnswer ->
          answer.value != null && otherAnswer.value != null && answer.value == otherAnswer.value
        }
        .all { it } &&
      draftAnswer == other.draftAnswer &&
      questionText == other.questionText

  /**
   * Returns whether this [QuestionnaireViewItem] and the `other` [QuestionnaireViewItem] have the
   * same [ValidationResult].
   *
   * This is useful for determining if the [QuestionnaireViewItem] has outdated [ValidationResult]
   * and therefore needs to be updated in the list UI.
   */
  internal fun hasTheSameValidationResult(other: QuestionnaireViewItem): Boolean {
    if (validationResult is NotValidated || validationResult is Valid) {
      return other.validationResult is NotValidated || other.validationResult is Valid
    }
    return validationResult == other.validationResult
  }
}
