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

import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.validation.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Data item for [QuestionnaireItemViewHolder] in [RecyclerView].
 *
 * The view should use `questionnaireItem`, `answer`, `answerOption`, and `validationResult` to
 * render the data item in the UI. The view SHOULD NOT mutate the data using these properties.
 *
 * The view should use the following answer APIs to update the answer(s):
 * - setAnswer (for single answer only)
 * - addAnswer (for repeated answers only)
 * - removeAnswer (for repeated answers only)
 * - clearAnswer
 *
 * The view should call `answersChangedCallback` to notify the view model that the answer(s) have
 * been changed. This will trigger a re-render of the [RecyclerView] UI.
 *
 * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] in the [Questionnaire]
 * @param questionnaireResponseItem the [QuestionnaireResponse.QuestionnaireResponseItemComponent]
 * in the [QuestionnaireResponse]
 * @param validationResult the [ValidationResult] of the answer(s) against the `questionnaireItem`
 * @param answersChangedCallback the callback to notify the view model that the answers have been
 * changed for the [QuestionnaireResponse.QuestionnaireResponseItemComponent]
 * @param resolveAnswerValueSet the callback to resolve the answer value set and return the answer
 * options
 */
data class QuestionnaireItemViewItem(
  val questionnaireItem: Questionnaire.QuestionnaireItemComponent,
  private val questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
  val validationResult: ValidationResult?,
  internal val answersChangedCallback:
    (
      Questionnaire.QuestionnaireItemComponent,
      QuestionnaireResponse.QuestionnaireResponseItemComponent,
      List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>) -> Unit,
  private val resolveAnswerValueSet:
    suspend (String) -> List<Questionnaire.QuestionnaireItemAnswerOptionComponent> =
      {
    emptyList()
  },
) {
  private var _answers:
    MutableList<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> =
    questionnaireResponseItem.answer.toMutableList()

  internal val answers
    get() = _answers.map { it.copy() }

  internal fun setAnswer(
    questionnaireResponseItemAnswerComponent:
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
  ) {
    check(!questionnaireItem.repeats) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} has repeated answers. Use addAnswer instead."
    }
    _answers = mutableListOf(questionnaireResponseItemAnswerComponent)
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, answers)
  }

  internal fun addAnswer(
    questionnaireResponseItemAnswerComponent:
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
  ) {
    check(questionnaireItem.repeats) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} does not allow repeated answers"
    }
    _answers.add(questionnaireResponseItemAnswerComponent)
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, answers)
  }

  internal fun removeAnswer(
    questionnaireResponseItemAnswerComponent:
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
  ) {
    check(questionnaireItem.repeats) {
      "Questionnaire item with linkId ${questionnaireItem.linkId} does not allow repeated answers"
    }
    _answers.removeIf { it.value.equalsDeep(questionnaireResponseItemAnswerComponent.value) }
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, answers)
  }

  internal fun clearAnswer() {
    _answers.clear()
    answersChangedCallback(questionnaireItem, questionnaireResponseItem, answers)
  }

  fun isAnswerOptionSelected(
    answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent
  ): Boolean {
    return _answers.any { it.value.equalsDeep(answerOption.value) }
  }

  /**
   * In a `choice` or `open-choice` type question, the answer options are defined in one of the two
   * elements in the questionnaire:
   *
   * - `Questionnaire.item.answerOption`: a list of permitted answers to the question
   * - `Questionnaire.item.answerValueSet`: a reference to a value set containing a list of
   * permitted answers to the question
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
    _answers.size == other._answers.size &&
      _answers
        .zip(other._answers) { answer, otherAnswer ->
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
    if (validationResult == null || validationResult.isValid) {
      return other.validationResult == null || other.validationResult.isValid
    }
    return validationResult == other.validationResult
  }
}
