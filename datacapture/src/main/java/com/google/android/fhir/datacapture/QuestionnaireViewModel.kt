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
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.ValueSet

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
    questionnaireResponse =
      QuestionnaireResponse().apply {
        questionnaire = this@QuestionnaireViewModel.questionnaire.id
      }
    // Retain the hierarchy and order of items within the questionnaire as specified in the
    // standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
    questionnaire.item.forEach {
      questionnaireResponse.addItem(it.createQuestionnaireResponseItem())
    }
    val questionnaireJsonResponseString: String? =
      state[QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE]
    if (questionnaireJsonResponseString != null) {
      val questionnaireResponseInput =
        FhirContext.forR4().newJsonParser().parseResource(questionnaireJsonResponseString) as
          QuestionnaireResponse
      validateQuestionnaireResponseItems(questionnaireResponse.item, questionnaireResponseInput.item)
    }
  }

  /** Map from link IDs to questionnaire response items. */
  private val linkIdToQuestionnaireResponseItemMap =
    createLinkIdToQuestionnaireResponseItemMap(questionnaireResponse.item)

  /** Map from link IDs to questionnaire items. */
  private val linkIdToQuestionnaireItemMap = createLinkIdToQuestionnaireItemMap(questionnaire.item)

  /** Tracks modifications in order to update the UI. */
  private val modificationCount = MutableStateFlow(0)

  /**
   * Callback function to update the UI which takes the linkId of the question whose answer(s) has
   * been changed.
   */
  private val questionnaireResponseItemChangedCallback: (String) -> Unit = { linkId ->
    linkIdToQuestionnaireItemMap[linkId]?.let { questionnaireItem ->
      if (questionnaireItem.hasNestedItemsWithinAnswers) {
        linkIdToQuestionnaireResponseItemMap[linkId]?.let { questionnaireResponseItem ->
          questionnaireResponseItem.addNestedItemsToAnswer(questionnaireItem)
          questionnaireResponseItem.answer.singleOrNull()?.item?.forEach {
            nestedQuestionnaireResponseItem ->
            linkIdToQuestionnaireResponseItemMap[nestedQuestionnaireResponseItem.linkId] =
              nestedQuestionnaireResponseItem
          }
        }
      }
    }
    modificationCount.value += 1
  }

  private val pageFlow = MutableStateFlow(questionnaire.getInitialPagination())

  private val answerValueSetMap =
    mutableMapOf<String, List<Questionnaire.QuestionnaireItemAnswerOptionComponent>>()

  /**
   * Returns current [QuestionnaireResponse] captured by the UI which includes answers of enabled
   * questions.
   */
  fun getQuestionnaireResponse(): QuestionnaireResponse {
    return questionnaireResponse.copy().apply {
      item = getEnabledResponseItems(this@QuestionnaireViewModel.questionnaire.item, item)
    }
  }

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

  @PublishedApi
  internal suspend fun resolveAnswerValueSet(
    uri: String
  ): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> {
    // If cache hit, return it
    if (answerValueSetMap.contains(uri)) {
      return answerValueSetMap[uri]!!
    }

    val options =
      if (uri.startsWith("#")) {
        questionnaire.contained
          .firstOrNull {
            it.id.equals(uri) &&
              it.resourceType == ResourceType.ValueSet &&
              (it as ValueSet).hasExpansion()
          }
          ?.let {
            val valueSet = it as ValueSet
            valueSet.expansion.contains.filterNot { it.abstract || it.inactive }.map { component ->
              Questionnaire.QuestionnaireItemAnswerOptionComponent(
                Coding(component.system, component.code, component.display)
              )
            }
          }
      } else {
        // Ask the client to provide the answers from an external expanded Valueset.
        DataCaptureConfig.valueSetResolverExternal?.resolve(uri)?.map { coding ->
          Questionnaire.QuestionnaireItemAnswerOptionComponent(coding.copy())
        }
      }
        ?: emptyList()
    // save it so that we avoid have cache misses.
    answerValueSetMap[uri] = options
    return options
  }

  private fun createLinkIdToQuestionnaireResponseItemMap(
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  ): MutableMap<String, QuestionnaireResponse.QuestionnaireResponseItemComponent> {
    val linkIdToQuestionnaireResponseItemMap =
      questionnaireResponseItemList.map { it.linkId to it }.toMap().toMutableMap()
    for (item in questionnaireResponseItemList) {
      linkIdToQuestionnaireResponseItemMap.putAll(
        createLinkIdToQuestionnaireResponseItemMap(item.item)
      )
      item.answer.forEach {
        linkIdToQuestionnaireResponseItemMap.putAll(
          createLinkIdToQuestionnaireResponseItemMap(it.item)
        )
      }
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
              linkIdToQuestionnaireResponseItemMap[linkId]
            }

          if (!enabled || questionnaireItem.isHidden) {
            return@flatMap emptyList()
          }

          listOf(
            QuestionnaireItemViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              { resolveAnswerValueSet(it) }
            ) { questionnaireResponseItemChangedCallback(questionnaireItem.linkId) }
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
        }
        .toList()
    return QuestionnaireState(items = items, pagination = pagination)
  }

  private fun getEnabledResponseItems(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
  ): List<QuestionnaireResponse.QuestionnaireResponseItemComponent> {
    return questionnaireItemList
      .asSequence()
      .zip(questionnaireResponseItemList.asSequence())
      .filter { (questionnaireItem, _) ->
        EnablementEvaluator.evaluate(questionnaireItem) { linkId ->
          linkIdToQuestionnaireResponseItemMap[linkId] ?: return@evaluate null
        }
      }
      .map { (questionnaireItem, questionnaireResponseItem) ->
        // Nested group items
        questionnaireResponseItem.item =
          getEnabledResponseItems(questionnaireItem.item, questionnaireResponseItem.item)
        // Nested question items
        questionnaireResponseItem.answer.forEach {
          it.item = getEnabledResponseItems(questionnaireItem.item, it.item)
        }
        questionnaireResponseItem
      }
      .toList()
  }

  /**
   * Traverse (DFS) through the list of questionnaire items and the list of questionnaire response
   * items and check if the linkId of the matching pairs of questionnaire item and questionnaire
   * response item are equal. The traverse is carried out in the two lists in tandem. The two lists
   * should be structurally identical.
   */
  private fun validateQuestionnaireResponseItems(
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    questionnaireResponseInputItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  ) {
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
    val questionnaireResponseInputItemListIterator = questionnaireResponseInputItemList.iterator()
    while (questionnaireResponseInputItemListIterator.hasNext()) {
      // TODO: Validate type and item nesting within answers for repeated answers
      // https://github.com/google/android-fhir/issues/286
      val questionnaireResponseInputItem = questionnaireResponseInputItemListIterator.next()
      if(questionnaireResponseItemListIterator.hasNext()){
        val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
        if (!questionnaireResponseItem.linkId.equals(questionnaireResponseInputItem.linkId))
          throw IllegalArgumentException(
            "Mismatching linkIds for questionnaire item ${questionnaireResponseItem.linkId} and " +
              "questionnaire response item ${questionnaireResponseInputItem.linkId}"
          )
        questionnaireResponseItem.answer = questionnaireResponseInputItem.answer
        if (questionnaireResponseInputItem.hasItem()) {
          validateQuestionnaireResponseItems(questionnaireResponseItem.item, questionnaireResponseInputItem.item)
        } else {
          if (questionnaireResponseInputItem.answer.isNotEmpty())
            validateQuestionnaireResponseItems(
              questionnaireResponseItem.answer.first().item,
              questionnaireResponseInputItem.answer.first().item
            )
        }
      }else{
        //Input response has more items
        throw IllegalArgumentException(
          "No matching questionnaire item for questionnaire response item ${questionnaireResponseInputItem.linkId}"
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
          (extension.value as? CodeableConcept)?.coding?.any { coding -> coding.code == "page" } ==
            true
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
