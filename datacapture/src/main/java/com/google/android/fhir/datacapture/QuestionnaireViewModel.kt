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
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItemProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal class QuestionnaireViewModel(state: SavedStateHandle) : ViewModel() {
  /** The current questionnaire as questions are being answered. */
  private val questionnaire: Questionnaire
  private var questionnaireItemBeingSearched: Questionnaire.QuestionnaireItemComponent? = null

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
      validateQuestionniareResponseItems(questionnaire.item, questionnaireResponse.item)
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

  /**
   * Callback function to update the UI. Based on the link Id of the
   * [Questionnaire.QuestionnaireItemComponent] answered
   */
  private val questionnaireResponseItemAnsweredCallback: (String) -> Unit = { linkId ->
    run { updateQuestionnaireResponseItemComponent(linkId) }
  }
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
            questionnaireResponseItemChangedCallback,
            questionnaireResponseItemAnsweredCallback,
            questionnaireItem.createQuestionnaireItemViewItemProperty()
          )
        )
        if (questionnaireResponseItem.item.isEmpty() &&
            questionnaireItem.item.isNotEmpty() &&
            questionnaireResponseItem.answer.isNotEmpty()
        ) {
          questionnaireItemViewItemList.addAll(
            getQuestionnaireItemViewItemList(
              questionnaireItem.item,
              questionnaireResponseItem.answer.first().item
            )
          )
        } else {
          questionnaireItemViewItemList.addAll(
            getQuestionnaireItemViewItemList(questionnaireItem.item, questionnaireResponseItem.item)
          )
        }
      }
    }
    return questionnaireItemViewItemList
  }

  private fun updateQuestionnaireResponseItemComponent(linkId: String) {
    questionnaire.item.getItemWithLinkId(linkId)
    questionnaireItemBeingSearched?.let {
      questionnaireResponse.item.addNestedItemsOfItemWithLinkId(linkId)
    }
    modificationCount.value += 1
  }

  /** Traverse (DFS) through the list of questionnaire items and find the item with given linkId */
  private fun MutableList<Questionnaire.QuestionnaireItemComponent>.getItemWithLinkId(
    linkId: String
  ) {
    val questionnaireItemIterator = this.iterator()
    while (questionnaireItemIterator.hasNext()) {
      val questionnaireItem = questionnaireItemIterator.next()
      if (questionnaireItem.linkId == linkId) {
        questionnaireItemBeingSearched = questionnaireItem
      } else if (questionnaireItem.item.isNotEmpty()) {
        questionnaireItem.item.getItemWithLinkId(linkId)
      }
    }
  }

  /**
   * Traverse (DFS) through the list of questionnaire response items and add nested items to the
   * [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] of the
   * [QuestionnaireResponse.QuestionnaireResponseItemComponent] with the given linkId
   */
  private fun MutableList<
    QuestionnaireResponse.QuestionnaireResponseItemComponent>.addNestedItemsOfItemWithLinkId(
    linkId: String
  ) {
    val questionnaireResponseItemIterator = this.iterator()
    var index = 0
    while (questionnaireResponseItemIterator.hasNext()) {
      val questionnaireResponseItem = questionnaireResponseItemIterator.next()
      if (questionnaireResponseItem.linkId == linkId) {
        this[index] = this[index].addNestedItemsToAnswer()
        break
      } else if (!questionnaireResponseItem.hasItem() && questionnaireResponseItem.hasAnswer()) {
        if (questionnaireResponseItem.answer.first().hasItem()) {
          questionnaireResponseItem.answer.first().item.addNestedItemsOfItemWithLinkId(linkId)
        }
      } else if (questionnaireResponseItem.hasItem()) {
        questionnaireResponseItem.item.addNestedItemsOfItemWithLinkId(linkId)
      }
      index++
    }
  }

  /**
   * Add items within [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] from the
   * provided parent [Questionnaire.QuestionnaireItemComponent] with nested items. The hierarchy and
   * order of child items will be retained as specified in the standard. See
   * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
   */
  private fun QuestionnaireResponse.QuestionnaireResponseItemComponent.addNestedItemsToAnswer():
    QuestionnaireResponse.QuestionnaireResponseItemComponent {
    return QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
      linkId = this@addNestedItemsToAnswer.linkId
      answer = this@addNestedItemsToAnswer.answer
      answer.first().item = questionnaireItemBeingSearched?.createListOfItemInAnswer()
    }
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
    if (this@createQuestionnaireResponseItem.type != Questionnaire.QuestionnaireItemType.GROUP &&
        this@createQuestionnaireResponseItem.item.count() > 0
    ) {
      if (this.answer.isNotEmpty()) {
        this@createQuestionnaireResponseItem.createListOfItemInAnswer().forEach {
          this.answer.first().addItem(it)
        }
      }
    } else if (this@createQuestionnaireResponseItem.type ==
        Questionnaire.QuestionnaireItemType.GROUP
    ) {
      this@createQuestionnaireResponseItem.item.forEach {
        this.addItem(it.createQuestionnaireResponseItem())
      }
    }
  }
}

/**
 * Creates a List of [QuestionnaireResponse.QuestionnaireResponseItemComponent] from the provided
 * [Questionnaire.QuestionnaireItemComponent].
 *
 * The hierarchy and order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
private fun Questionnaire.QuestionnaireItemComponent.createListOfItemInAnswer():
  List<QuestionnaireResponse.QuestionnaireResponseItemComponent> {
  val listOfNestedItems = mutableListOf<QuestionnaireResponse.QuestionnaireResponseItemComponent>()
  this.item.forEach { listOfNestedItems.add(it.createQuestionnaireResponseItem()) }
  return listOfNestedItems
}

/**
 * Creates an instance of [QuestionnaireItemViewItemProperty] from the provided
 * [Questionnaire.QuestionnaireItemComponent].
 */
private fun Questionnaire.QuestionnaireItemComponent.createQuestionnaireItemViewItemProperty():
  QuestionnaireItemViewItemProperty {
  val questionnaireItemViewItemProperty = QuestionnaireItemViewItemProperty()
  if (this.item.isNotEmpty() && this.type != Questionnaire.QuestionnaireItemType.GROUP) {
    questionnaireItemViewItemProperty.canModifyStructure = true
  }
  return questionnaireItemViewItemProperty
}

/**
 * Traverse (DFS) through the list of questionnaire items and the list of questionnaire response
 * items and check if the linkid of the matching pairs of questionnaire item and questionnaire
 * response item are equal. The traverse is carried out in the two lists in tandem. The two lists
 * should be structurally identical.
 */
private fun validateQuestionniareResponseItems(
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
      validateQuestionniareResponseItems(questionnaireItem.item, questionnaireResponseItem.item)
    } else {
      validateQuestionniareResponseItems(
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
