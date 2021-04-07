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

package com.google.android.fhir.datacapture

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.enablement.EnablementEvaluator
import com.google.android.fhir.datacapture.enablement.QuestionnaireItemWithResponse
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal class QuestionnaireViewModel(state: SavedStateHandle) : ViewModel() {
  /** The current questionnaire as questions are being answered. */
  private val questionnaire: Questionnaire

  init {
    val questionnaireJson: String = state[QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE]!!
    questionnaire =
      FhirContext.forR4().newJsonParser().parseResource(questionnaireJson) as Questionnaire
  }

  /** The current questionnaire response as questions are being answered. */
  private var questionnaireResponse: QuestionnaireResponse

  init {
    val questionnaireJsonResponseString: String? =
      state[QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE]
    if (questionnaireJsonResponseString != null) {
      questionnaireResponse =
        FhirContext.forR4().newJsonParser().parseResource(questionnaireJsonResponseString) as
          QuestionnaireResponse
      validateQuestionnaireResponseItems(questionnaire.item, questionnaireResponse.item)
    } else {
      questionnaireResponse =
        QuestionnaireResponse().apply {
          questionnaire = this@QuestionnaireViewModel.questionnaire.id
        }
      // Retain the hierarchy and order of items within the questionnaire as specified in the
      // standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
      questionnaire.item.forEach {
        questionnaireResponse.addItem(it.createQuestionnaireResponseItem())
      }
    }
  }

  /** Map from link IDs to questionnaire response items. */
  private val linkIdToQuestionnaireResponseItemMap =
    createLinkIdToQuestionnaireResponseItemMap(questionnaireResponse.item)

  /** Map from link IDs to questionnaire items. */
  private val linkIdToQuestionnaireItemMap = createLinkIdToQuestionnaireItemMap(questionnaire.item)

  /** Tracks modifications in order to update the UI. */
  private val modificationCount = MutableStateFlow(0)

  /** Callback function to update the UI. */
  private val questionnaireResponseItemChangedCallback = { modificationCount.value += 1 }

  internal val questionnaireItemViewItemList
    get() = getQuestionnaireItemViewItemList(questionnaire.item, questionnaireResponse.item)

  /** [QuestionnaireItemViewItem] s to be displayed in the UI. */
  internal val questionnaireItemViewItemListFlow: Flow<List<QuestionnaireItemViewItem>> =
    modificationCount.map { questionnaireItemViewItemList }

  /** The current [QuestionnaireResponse] captured by the UI. */
  fun getQuestionnaireResponse(): QuestionnaireResponse = questionnaireResponse

  private fun createLinkIdToQuestionnaireResponseItemMap(
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  ): Map<String, QuestionnaireResponse.QuestionnaireResponseItemComponent> {
    val linkIdToQuestionnaireResponseItemMap =
      questionnaireResponseItemList.map { it.linkId to it }.toMap().toMutableMap()
    for (item in questionnaireResponseItemList) {
      linkIdToQuestionnaireResponseItemMap.putAll(
        createLinkIdToQuestionnaireResponseItemMap(item.item)
      )
    }
    return linkIdToQuestionnaireResponseItemMap
  }

  private fun createLinkIdToQuestionnaireItemMap(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>
  ): Map<String, Questionnaire.QuestionnaireItemComponent> {
    val linkIdToQuestionnaireItemMap =
      questionnaireItemList.map { it.linkId to it }.toMap().toMutableMap()
    for (item in questionnaireItemList) {
      linkIdToQuestionnaireItemMap.putAll(createLinkIdToQuestionnaireItemMap(item.item))
    }
    return linkIdToQuestionnaireItemMap
  }

  /**
   * Traverse (DFS) through the list of questionnaire items , the list of questionnaire response
   * items and the list of items in the questionnaire response answer list and populate
   * [questionnaireItemViewItemList] with matching pairs of questionnaire item and questionnaire
   * response item.
   *
   * The traverse is carried out in the two lists in tandem. The two lists should be structurally
   * identical.
   */
  private fun getQuestionnaireItemViewItemList(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  ): List<QuestionnaireItemViewItem> {
    val questionnaireItemViewItemList = mutableListOf<QuestionnaireItemViewItem>()
    val questionnaireItemListIterator = questionnaireItemList.iterator()
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
    while (questionnaireItemListIterator.hasNext() &&
      questionnaireResponseItemListIterator.hasNext()) {
      val questionnaireItem = questionnaireItemListIterator.next()
      val questionnaireResponseItem = questionnaireResponseItemListIterator.next()

      val enabled =
        EnablementEvaluator.evaluate(questionnaireItem) { linkId ->
          QuestionnaireItemWithResponse(
            (linkIdToQuestionnaireItemMap[linkId]
              ?: return@evaluate QuestionnaireItemWithResponse(null, null)),
            (linkIdToQuestionnaireResponseItemMap[linkId]
              ?: return@evaluate QuestionnaireItemWithResponse(null, null))
          )
        }
      if (enabled) {
        questionnaireItemViewItemList.add(
          QuestionnaireItemViewItem(
            questionnaireItem,
            questionnaireResponseItem,
            questionnaireResponseItemChangedCallback
          )
        )
        questionnaireItemViewItemList.addAll(
          getQuestionnaireItemViewItemList(questionnaireItem.item, questionnaireResponseItem.item)
        )
        if (!questionnaireItem.type.equals(Questionnaire.QuestionnaireItemType.GROUP)) {
          questionnaireResponseItem.answer?.forEach {
            if (it.item.size > 0) {
              questionnaireItemViewItemList.addAll(
                getQuestionnaireItemViewItemList(questionnaireItem.item, it.item)
              )
            }
          }
        }
      }
    }
    return questionnaireItemViewItemList
  }
}

/**
 * Creates a [QuestionnaireResponse.QuestionnaireResponseItemComponent] from the provided
 * [Questionnaire.QuestionnaireItemComponent].
 *
 * The hierarchy and order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
private fun Questionnaire.QuestionnaireItemComponent.createQuestionnaireResponseItem():
  QuestionnaireResponse.QuestionnaireResponseItemComponent {
  return QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
    linkId = this@createQuestionnaireResponseItem.linkId
    answer = createQuestionnaireResponseItemAnswers()
    this@createQuestionnaireResponseItem.item.forEach {
      this.addItem(it.createQuestionnaireResponseItem())
    }
  }
}

/** Here we are setting initial value as an Answer to a Question */
private fun Questionnaire.QuestionnaireItemComponent.createQuestionnaireResponseItemAnswers():
  MutableList<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? {
  if (initial.isEmpty()) {
    return null
  } else if (initial.isNotEmpty()) {

    if (type == Questionnaire.QuestionnaireItemType.GROUP ||
        type == Questionnaire.QuestionnaireItemType.DISPLAY
    ) {
      throw IllegalArgumentException(
        "Questionnaire item $linkId has initial value(s) and is a group or display item. See rule que-8 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
      )
    }

    if (initial.size > 1 &&
        !repeats &&
        type != Questionnaire.QuestionnaireItemType.GROUP &&
        type != Questionnaire.QuestionnaireItemType.DISPLAY
    ) {
      throw IllegalArgumentException(
        "Questionnaire item $linkId can only have multiple initial values for repeating items. See rule que-13 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
      )
    }

    if (type != Questionnaire.QuestionnaireItemType.GROUP &&
        type != Questionnaire.QuestionnaireItemType.DISPLAY &&
        initial.size == 1
    ) {
      return mutableListOf(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = initial[0].value
        }
      )
    }

    if (type != Questionnaire.QuestionnaireItemType.GROUP &&
        type != Questionnaire.QuestionnaireItemType.DISPLAY &&
        repeats &&
        initial.size > 1
    ) {
      return mutableListOf(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = initial[0].value
        }
      )
    }
  }
  return null
}

/**
 * Traverse (DFS) through the list of questionnaire items and the list of questionnaire response
 * items and check if the linkid of the matching pairs of questionnaire item and questionnaire
 * response item are equal. The traverse is carried out in the two lists in tandem. The two lists
 * should be structurally identical.
 */
private fun validateQuestionnaireResponseItems(
  questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
  questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
) {
  val questionnaireItemListIterator = questionnaireItemList.iterator()
  val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
  while (questionnaireItemListIterator.hasNext() &&
    questionnaireResponseItemListIterator.hasNext()) {
    // TODO: Validate type and item nesting within answers for repeated answers
    // https://github.com/google/android-fhir/issues/286
    val questionnaireItem = questionnaireItemListIterator.next()
    val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
    if (!questionnaireItem.linkId.equals(questionnaireResponseItem.linkId))
      throw IllegalArgumentException(
        "Mismatching linkIds for questionnaire item ${questionnaireItem.linkId} and " +
          "questionnaire response item ${questionnaireResponseItem.linkId}"
      )
    if (questionnaireItem.type.equals(Questionnaire.QuestionnaireItemType.GROUP)) {
      validateQuestionnaireResponseItems(questionnaireItem.item, questionnaireResponseItem.item)
    } else {
      validateQuestionnaireResponseItems(
        questionnaireItem.item,
        questionnaireResponseItem.answer.first().item
      )
    }
  }
  if (questionnaireItemListIterator.hasNext() xor questionnaireResponseItemListIterator.hasNext()) {
    if (questionnaireItemListIterator.hasNext()) {
      throw IllegalArgumentException(
        "No matching questionnaire response item for questionnaire item ${questionnaireItemListIterator.next().linkId}"
      )
    } else {
      throw IllegalArgumentException(
        "No matching questionnaire item for questionnaire response item ${questionnaireResponseItemListIterator.next().linkId}"
      )
    }
  }
}
