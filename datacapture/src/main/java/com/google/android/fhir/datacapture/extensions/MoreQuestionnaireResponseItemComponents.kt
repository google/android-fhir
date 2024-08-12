/*
 * Copyright 2023-2024 Google LLC
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

package com.google.android.fhir.datacapture.extensions

import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Pre-order list of descendants of the questionnaire response item (inclusive of the current item).
 */
val QuestionnaireResponse.QuestionnaireResponseItemComponent.descendant:
  List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  get() =
    mutableListOf<QuestionnaireResponse.QuestionnaireResponseItemComponent>().also {
      appendDescendantTo(it)
    }

private fun QuestionnaireResponse.QuestionnaireResponseItemComponent.appendDescendantTo(
  output: MutableList<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
) {
  output.add(this)
  item.forEach { it.appendDescendantTo(output) }
  answer.forEach { answer -> answer.item.forEach { it.appendDescendantTo(output) } }
}

/**
 * Copies nested items under `questionnaireItem` to each answer without children. The hierarchy and
 * order of nested items will be retained as specified in the standard.
 *
 * Existing answers with nested items will not be modified because the nested items may contain
 * answers already.
 *
 * This should be used when
 * - a new answer is added to a question with nested questions, or
 * - a new answer is added to a repeated group (in which case this indicates a new instance of the
 *   repeated group will be added to the final questionnaire response).
 *
 * See https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
internal fun QuestionnaireResponse.QuestionnaireResponseItemComponent
  .copyNestedItemsToChildlessAnswers(
  questionnaireItem: Questionnaire.QuestionnaireItemComponent,
) {
  answer
    .filter { it.item.isEmpty() }
    .forEach { it.item = questionnaireItem.createNestedQuestionnaireResponseItems() }
}
