/*
 * Copyright 2022-2026 Google LLC
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

import com.google.fhir.model.r4.DateTime
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse

internal const val EXTENSION_LAST_LAUNCHED_TIMESTAMP: String =
  "http://github.com/google-android/questionnaire-lastLaunched-timestamp"

/** Pre-order list of all questionnaire response items in the questionnaire. */
val QuestionnaireResponse.Builder.allItems: List<QuestionnaireResponse.Item>
  get() = item.flatMap { it.build().descendant }

/**
 * Packs repeated groups under the same questionnaire response item.
 *
 * Repeated groups need some massaging before the questionnaire view model can interpret them
 * correctly. This is because they are flattened out and nested directly under the parent in the
 * FHIR data format.
 *
 * More details on the structure of questionnaire responses:
 * https://build.fhir.org/questionnaireresponse.html#link.
 *
 * Specifically, this function will go through the items in the questionnaire response, and if
 * multiple questionnaire response items exist for the same repeated group (identified by the link
 * id), they will be converted into answers under the same questionnaire response item so that there
 * is a 1:1 relationship from questionnaire item for the repeated group to questionnaire response
 * item for the same repeated group.
 *
 * This function should be called before the questionnaire view model accepts an
 * application-provided questionnaire response.
 *
 * See also [unpackRepeatedGroups].
 *
 * @throws IllegalArgumentException if more than one sibling questionnaire items (nested under the
 *   questionnaire root or the same parent item) share the same id
 */
internal fun QuestionnaireResponse.Builder.packRepeatedGroups(questionnaire: Questionnaire) {
  item = item.packRepeatedGroups(questionnaire.item).toMutableList()
}

private fun List<QuestionnaireResponse.Item.Builder>.packRepeatedGroups(
  questionnaireItems: List<Questionnaire.Item>,
): List<QuestionnaireResponse.Item.Builder> {
  return groupByAndZipByLinkId(questionnaireItems, map { it.build() }) {
      questionnaireItems,
      questionnaireResponseItems,
      ->
      if (questionnaireItems.isEmpty()) {
        return@groupByAndZipByLinkId questionnaireResponseItems.map { it.toBuilder() }
      }

      val questionnaireItem = questionnaireItems.single()

      val updatedResponseItems =
        questionnaireResponseItems.map { responseItem ->
          responseItem
            .toBuilder()
            .apply { item = item.packRepeatedGroups(questionnaireItem.item).toMutableList() }
            .build()
        }

      if (
        questionnaireItem.type.value == Questionnaire.QuestionnaireItemType.Group &&
          questionnaireItem.repeats?.value == true
      ) {
        listOf(
          QuestionnaireResponse.Item.Builder(questionnaireItem.linkId.toBuilder()).apply {
            linkId = questionnaireItem.linkId.toBuilder()
            answer =
              updatedResponseItems
                .map { responseItem ->
                  QuestionnaireResponse.Item.Answer.Builder().apply {
                    item = responseItem.toBuilder().item.toMutableList()
                  }
                }
                .toMutableList()
          },
        )
      } else {
        updatedResponseItems.map { it.toBuilder() }
      }
    }
    .flatten()
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
internal fun QuestionnaireResponse.Builder.unpackRepeatedGroups(questionnaire: Questionnaire) {
  item = unpackRepeatedGroups(questionnaire.item, item).toMutableList()
}

private fun unpackRepeatedGroups(
  questionnaireItems: List<Questionnaire.Item>,
  questionnaireResponseItems: List<QuestionnaireResponse.Item.Builder>,
): List<QuestionnaireResponse.Item.Builder> {
  return questionnaireItems
    .zipByLinkId(questionnaireResponseItems.map { it.build() }) {
      questionnaireItem,
      questionnaireResponseItem,
      ->
      unpackRepeatedGroups(questionnaireItem, questionnaireResponseItem.toBuilder())
    }
    .flatten()
}

private fun unpackRepeatedGroups(
  questionnaireItem: Questionnaire.Item,
  questionnaireResponseItem: QuestionnaireResponse.Item.Builder,
): List<QuestionnaireResponse.Item.Builder> {
  questionnaireResponseItem.item =
    unpackRepeatedGroups(questionnaireItem.item, questionnaireResponseItem.item).toMutableList()
  questionnaireResponseItem.answer.forEach {
    it.item = unpackRepeatedGroups(questionnaireItem.item, it.item).toMutableList()
  }
  return if (questionnaireItem.isRepeatedGroup) {
    questionnaireResponseItem.answer.map {
      QuestionnaireResponse.Item.Builder(
          com.google.fhir.model.r4.String.Builder().apply {
            value = questionnaireItem.linkId.value
          },
        )
        .apply {
          linkId =
            com.google.fhir.model.r4.String.Builder().apply {
              value = questionnaireItem.linkId.value
            }
          text =
            com.google.fhir.model.r4.String.Builder().apply {
              value = questionnaireItem.localizedTextAnnotatedString?.toString()
            }
          item = it.item
        }
    }
  } else {
    listOf(questionnaireResponseItem)
  }
}

/**
 * Adds a launch timestamp extension to the Questionnaire Response. If the extension @see
 * EXTENSION_LAUNCH_TIMESTAMP already exists, it updates its value; otherwise, it adds a new one.
 */
internal val QuestionnaireResponse.launchTimestamp: DateTime?
  get() {
    val extension = this.extension.firstOrNull { it.url == EXTENSION_LAST_LAUNCHED_TIMESTAMP }
    return extension?.value?.asDateTime()?.value
  }
