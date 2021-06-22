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
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.enablement.EnablementEvaluator
import com.google.android.fhir.datacapture.enablement.QuestionnaireItemWithResponse
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal class QuestionnaireViewModel(state: SavedStateHandle) : ViewModel() {
  /** The current questionnaire as questions are being answered. */
  internal val questionnaire: Questionnaire

  init {
    val questionnaireJson: String = state[QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE]!!
    questionnaire =
      FhirContext.forR4().newJsonParser().parseResource(questionnaireJson) as Questionnaire
  }

  /** The current questionnaire response as questions are being answered. */
  private val questionnaireResponse: QuestionnaireResponse

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
  private val questionnaireResponseItemChangedCallback: (String) -> Unit = { linkId ->
    linkIdToQuestionnaireItemMap[linkId]?.let {
      if (it.hasNestedItemsWithinAnswers) {
        linkIdToQuestionnaireResponseItemMap[linkId]!!.addNestedItemsToAnswer(it)
      }
    }
    modificationCount.value += 1
  }

  private val pageFlow =
    MutableStateFlow<QuestionnairePagination?>(questionnaire.getInitialPagination())

  internal fun goToPreviousPage() {
    pageFlow.value = pageFlow.value!!.previousPage()
  }

  internal fun goToNextPage() {
    pageFlow.value = pageFlow.value!!.nextPage()
  }

  /** [QuestionnaireState] to be displayed in the UI. */
  internal val questionnaireStateFlow: Flow<QuestionnaireState> =
    modificationCount
      .combine(pageFlow) { _, pagination ->
        getQuestionnaireState(
          questionnaireItemList = questionnaire.item,
          questionnaireResponseItemList = questionnaireResponse.item,
          pagination = pagination,
        )
      }
      .stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        initialValue =
          getQuestionnaireState(
            questionnaireItemList = questionnaire.item,
            questionnaireResponseItemList = questionnaireResponse.item,
            pagination = questionnaire.getInitialPagination(),
          )
      )

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
   * Traverses through the list of questionnaire items, the list of questionnaire response items and
   * the list of items in the questionnaire response answer list and populates
   * [questionnaireStateFlow] with matching pairs of questionnaire item and questionnaire response
   * item.
   *
   * The traverse is carried out in the two lists in tandem. The two lists should be structurally
   * identical.
   */
  private fun getQuestionnaireState(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    pagination: QuestionnairePagination?,
  ): QuestionnaireState {
    // TODO(kmost): validate pages before switching between next/prev pages
    val items: List<QuestionnaireItemViewItem> =
      questionnaireItemList
        .asSequence()
        .withIndex()
        .zip(questionnaireResponseItemList.asSequence())
        .flatMap { (questionnaireItemAndIndex, questionnaireResponseItem) ->
          val (index, questionnaireItem) = questionnaireItemAndIndex

          // if the questionnaire is paginated and we're currently working through the paginated
          // groups, make sure that only the current page gets set
          if (pagination != null && pagination.currentPageIndex != index) {
            return@flatMap emptyList()
          }

          val enabled =
            EnablementEvaluator.evaluate(questionnaireItem) { linkId ->
              QuestionnaireItemWithResponse(
                questionnaireItem = (linkIdToQuestionnaireItemMap[linkId]
                    ?: return@evaluate QuestionnaireItemWithResponse(null, null)),
                questionnaireResponseItem = (linkIdToQuestionnaireResponseItemMap[linkId]
                    ?: return@evaluate QuestionnaireItemWithResponse(null, null))
              )
            }
          if (enabled) {
            listOf(
              QuestionnaireItemViewItem(questionnaireItem, questionnaireResponseItem) {
                questionnaireResponseItemChangedCallback(questionnaireItem.linkId)
              }
            ) +
              getQuestionnaireState(
                  questionnaireItemList = questionnaireItem.item,
                  questionnaireResponseItemList =
                    if (questionnaireResponseItem.answer.isEmpty()) {
                      questionnaireResponseItem.item
                    } else {
                      questionnaireResponseItem.answer.first().item
                    },
                  // we're now dealing with nested items, so pagination is no longer a concern
                  pagination = null,
                )
                .items
          } else {
            emptyList()
          }
        }
        .toList()
    return QuestionnaireState(items = items, pagination = pagination)
  }
}

/**
 * Add items within [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] from the
 * provided parent [Questionnaire.QuestionnaireItemComponent] with nested items. The hierarchy and
 * order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
private fun QuestionnaireResponse.QuestionnaireResponseItemComponent.addNestedItemsToAnswer(
  questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent
) {
  if (answer.isNotEmpty()) {
    answer.first().item = questionnaireItemComponent.listOfItemInAnswer()
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
    if (hasNestedItemsWithinAnswers && answer.isNotEmpty()) {
      this.addNestedItemsToAnswer(this@createQuestionnaireResponseItem)
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
private inline fun Questionnaire.QuestionnaireItemComponent.listOfItemInAnswer() =
  item.map { it.createQuestionnaireResponseItem() }.toList()

/**
 * Returns a list of answers from the initial values of the questionnaire item. `null` if no intial
 * value.
 */
private fun Questionnaire.QuestionnaireItemComponent.createQuestionnaireResponseItemAnswers():
  MutableList<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? {
  if (initial.isEmpty()) {
    return null
  }

  if (type == Questionnaire.QuestionnaireItemType.GROUP ||
      type == Questionnaire.QuestionnaireItemType.DISPLAY
  ) {
    throw IllegalArgumentException(
      "Questionnaire item $linkId has initial value(s) and is a group or display item. See rule que-8 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
    )
  }

  if (initial.size > 1 && !repeats) {
    throw IllegalArgumentException(
      "Questionnaire item $linkId can only have multiple initial values for repeating items. See rule que-13 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
    )
  }

  return mutableListOf(
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
      value = initial[0].value
    }
  )
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

/**
 * Checks if this questionnaire uses pagination via the "page" extension.
 *
 * If any one group has a "page" extension, it is assumed that the whole questionnaire is a
 * well-formed, paginated questionnaire (eg, each top-level group should be its own page).
 *
 * If this questionnaire uses pagination, returns the [QuestionnairePagination] that you would see
 * when first opening this questionnaire. Otherwise, returns `null`.
 */
private fun Questionnaire.getInitialPagination(): QuestionnairePagination? {
  val usesPagination =
    item.any { item ->
      item.extension.any { extension ->
        (extension.value as? CodeableConcept)?.coding?.any { coding -> coding.code == "page" }
          ?: false
      }
    }
  return if (usesPagination) {
    QuestionnairePagination(
      currentPageIndex = 0,
      lastPageIndex = item.size - 1,
    )
  } else {
    null
  }
}

/** Questionnaire state for the Fragment to consume. */
internal data class QuestionnaireState(
  /** The items that should be currently-rendered into the Fragment. */
  val items: List<QuestionnaireItemViewItem>,
  /** The pagination state of the questionnaire. If `null`, the questionnaire is not paginated. */
  val pagination: QuestionnairePagination?,
)

internal data class QuestionnairePagination(
  val currentPageIndex: Int,
  val lastPageIndex: Int,
)

internal val QuestionnairePagination.hasPreviousPage: Boolean
  get() = currentPageIndex > 0
internal val QuestionnairePagination.hasNextPage: Boolean
  get() = currentPageIndex < lastPageIndex

internal fun QuestionnairePagination.previousPage(): QuestionnairePagination {
  check(hasPreviousPage) { "Can't call previousPage() if hasPreviousPage is false ($this)" }
  return copy(currentPageIndex = currentPageIndex - 1)
}

internal fun QuestionnairePagination.nextPage(): QuestionnairePagination {
  check(hasNextPage) { "Can't call nextPage() if hasNextPage is false ($this)" }
  return copy(currentPageIndex = currentPageIndex + 1)
}
