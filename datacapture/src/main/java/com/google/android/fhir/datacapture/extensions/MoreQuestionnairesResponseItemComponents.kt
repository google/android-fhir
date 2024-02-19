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

package com.google.android.fhir.datacapture.extensions

import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Pre-order list of descendants of the questionnaire response item (inclusive of the current item).
 */
val QuestionnaireResponse.QuestionnaireResponseItemComponent.descendant:
  List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  get() {
    return listOf(this) +
      this.item.flatMap { it.descendant } +
      this.answer.flatMap { answer -> answer.item.flatMap { it.descendant } }
  }

/**
 * Add nested items under the provided `questionnaireItem` to each answer in the questionnaire
 * response item. The hierarchy and order of nested items will be retained as specified in the
 * standard.
 *
 * See https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
fun QuestionnaireResponse.QuestionnaireResponseItemComponent.addNestedItemsToAnswer(
  questionnaireItem: Questionnaire.QuestionnaireItemComponent
) {
  answer.forEach { it.item = questionnaireItem.getNestedQuestionnaireResponseItems() }
}
