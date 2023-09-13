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

/** Pre-order list of all questionnaire response items in the questionnaire. */
val QuestionnaireResponse.allItems: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  get() = item.flatMap { it.descendant }

/**
 * Packs repeated groups under the same questionnaire response item.
 *
 * Repeated groups need some massaging before the questionnaire view model can interpret them
 * correctly. This is because they are flattened out and nested directly under the parent in the
 * FHIR data format.
 *
 * More details: https://build.fhir.org/questionnaireresponse.html#link.
 *
 * This function should be called before the questionnaire view model accepts an
 * application-provided questionnaire response.
 *
 * See also [unpackRepeatedGroups].
 */
internal fun QuestionnaireResponse.packRepeatedGroups() {
  item = item.packRepeatedGroups()
}

private fun List<QuestionnaireResponse.QuestionnaireResponseItemComponent>.packRepeatedGroups():
  List<QuestionnaireResponse.QuestionnaireResponseItemComponent> {
  forEach { it ->
    it.item = it.item.packRepeatedGroups()
    it.answer.forEach { it.item = it.item.packRepeatedGroups() }
  }
  val linkIdToPackedResponseItems =
    groupBy { it.linkId }
      .mapValues { (linkId, questionnaireResponseItems) ->
        questionnaireResponseItems.singleOrNull()
          ?: QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            this.linkId = linkId
            answer =
              questionnaireResponseItems.map {
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  item = it.item
                }
              }
          }
      }
  return map { it.linkId }.distinct().map { linkIdToPackedResponseItems[it]!! }
}

/**
 * Unpacks repeated groups as separate questionnaire response items under their parent.
 *
 * Repeated groups need some massaging for their returned data-format; each instance of the group
 * should be flattened out to be its own item in the parent, rather than an answer to the main item.
 *
 * More details: https://build.fhir.org/questionnaireresponse.html#link.
 *
 * For example, if the group contains 2 questions, and the user answered the group 3 times, this
 * function will return a list with 3 responses; each of those responses will have the linkId of the
 * provided group, and each will contain an item array with 2 items (the answers to the individual
 * questions within this particular group instance).
 *
 * This function should be called before returning the questionnaire response to the application.
 *
 * See also [packRepeatedGroups].
 */
internal fun QuestionnaireResponse.unpackRepeatedGroups(questionnaire: Questionnaire) {
  item = unpackRepeatedGroups(questionnaire.item, item)
}

private fun unpackRepeatedGroups(
  questionnaireItems: List<Questionnaire.QuestionnaireItemComponent>,
  questionnaireResponseItems: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
): List<QuestionnaireResponse.QuestionnaireResponseItemComponent> {
  return questionnaireItems
    .zipByLinkId(questionnaireResponseItems) { questionnaireItem, questionnaireResponseItem ->
      unpackRepeatedGroups(questionnaireItem, questionnaireResponseItem)
    }
    .flatten()
}

private fun unpackRepeatedGroups(
  questionnaireItem: Questionnaire.QuestionnaireItemComponent,
  questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent
): List<QuestionnaireResponse.QuestionnaireResponseItemComponent> {
  questionnaireResponseItem.item =
    unpackRepeatedGroups(questionnaireItem.item, questionnaireResponseItem.item)
  questionnaireResponseItem.answer.forEach {
    it.item = unpackRepeatedGroups(questionnaireItem.item, it.item)
  }
  return if (questionnaireItem.type == Questionnaire.QuestionnaireItemType.GROUP &&
      questionnaireItem.repeats
  ) {
    questionnaireResponseItem.answer.map {
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        linkId = questionnaireItem.linkId
        text = questionnaireItem.localizedTextSpanned?.toString()
        item = it.item
      }
    }
  } else {
    listOf(questionnaireResponseItem)
  }
}
