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

package com.google.android.fhir.datacapture

import android.app.Application
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.datacapture.enablement.EnablementEvaluator
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator.detectExpressionCyclicDependency
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator.evaluateCalculatedExpressions
import com.google.android.fhir.datacapture.utilities.fhirPathEngine
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseItemValidator
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator.checkQuestionnaireResponse
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.android.fhir.search.search
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.ValueSet
import timber.log.Timber

internal class QuestionnaireViewModel(application: Application, state: SavedStateHandle) :
  AndroidViewModel(application) {

  private val parser: IParser by lazy { FhirContext.forCached(FhirVersionEnum.R4).newJsonParser() }
  private val fhirEngine by lazy { FhirEngineProvider.getInstance(application) }

  /** The current questionnaire as questions are being answered. */
  internal val questionnaire: Questionnaire
  private lateinit var currentPageItems: List<QuestionnaireItemViewItem>

  init {
    questionnaire =
      when {
        state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_URI) -> {
          if (state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING)) {
            Timber.w(
              "Both EXTRA_QUESTIONNAIRE_JSON_URI & EXTRA_QUESTIONNAIRE_JSON_STRING are provided. " +
                "EXTRA_QUESTIONNAIRE_JSON_URI takes precedence."
            )
          }
          val uri: Uri = state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_URI]!!
          parser.parseResource(application.contentResolver.openInputStream(uri)) as Questionnaire
        }
        state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING) -> {
          val questionnaireJson: String =
            state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING]!!
          parser.parseResource(questionnaireJson) as Questionnaire
        }
        else ->
          error(
            "Neither EXTRA_QUESTIONNAIRE_JSON_URI nor EXTRA_QUESTIONNAIRE_JSON_STRING is supplied."
          )
      }
  }

  @VisibleForTesting
  val entryMode: EntryMode by lazy { questionnaire.entryMode ?: EntryMode.RANDOM }

  /** The current questionnaire response as questions are being answered. */
  private val questionnaireResponse: QuestionnaireResponse

  /**
   * True if the user has tapped the next/previous pagination buttons on the current page. This is
   * needed to avoid spewing validation errors before any questions are answered.
   */
  private var isPaginationButtonPressed = false
  init {
    when {
      state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI) -> {
        if (state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING)) {
          Timber.w(
            "Both EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI & EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING are provided. " +
              "EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI takes precedence."
          )
        }
        val uri: Uri = state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI]!!
        questionnaireResponse =
          parser.parseResource(application.contentResolver.openInputStream(uri))
            as QuestionnaireResponse
        checkQuestionnaireResponse(questionnaire, questionnaireResponse)
      }
      state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING) -> {
        val questionnaireResponseJson: String =
          state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING]!!
        questionnaireResponse =
          parser.parseResource(questionnaireResponseJson) as QuestionnaireResponse
        checkQuestionnaireResponse(questionnaire, questionnaireResponse)
      }
      else -> {
        questionnaireResponse =
          QuestionnaireResponse().apply {
            questionnaire = this@QuestionnaireViewModel.questionnaire.url
          }
        // Retain the hierarchy and order of items within the questionnaire as specified in the
        // standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
        questionnaire.item.forEach {
          questionnaireResponse.addItem(it.createQuestionnaireResponseItem())
        }
      }
    }
  }

  /**
   * The pre-order traversal trace of the items in the [QuestionnaireResponse]. This essentially
   * represents the order in which all items are displayed in the UI.
   */
  private val questionnaireResponseItemPreOrderList =
    mutableListOf<QuestionnaireResponse.QuestionnaireResponseItemComponent>()

  init {
    /**
     * Adds all items in the [QuestionnaireResponse] to the pre-order list. Note that each
     * questionnaire response item may either have child items (in the case of a group type
     * question) or have answer items with nested questions.
     */
    fun buildPreOrderList(item: QuestionnaireResponse.QuestionnaireResponseItemComponent) {
      questionnaireResponseItemPreOrderList.add(item)
      for (child in item.item) {
        buildPreOrderList(child)
      }
      for (answer in item.answer) {
        for (answerItem in answer.item) {
          buildPreOrderList(answerItem)
        }
      }
    }

    for (item in questionnaireResponse.item) {
      buildPreOrderList(item)
    }
  }

  /** The map from each item in the [Questionnaire] to its parent. */
  private var questionnaireItemParentMap:
    Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>

  init {
    /** Adds each child-parent pair in the [Questionnaire] to the parent map. */
    fun buildParentList(
      item: Questionnaire.QuestionnaireItemComponent,
      questionnaireItemToParentMap: ItemToParentMap
    ) {
      for (child in item.item) {
        questionnaireItemToParentMap[child] = item
        buildParentList(child, questionnaireItemToParentMap)
      }
    }

    questionnaireItemParentMap = buildMap {
      for (item in questionnaire.item) {
        buildParentList(item, this)
      }
    }
  }

  /** The map from each item in the [QuestionnaireResponse] to its parent. */
  private val questionnaireResponseItemParentMap =
    mutableMapOf<
      QuestionnaireResponse.QuestionnaireResponseItemComponent,
      QuestionnaireResponse.QuestionnaireResponseItemComponent
    >()

  init {
    /** Adds each child-parent pair in the [QuestionnaireResponse] to the parent map. */
    fun buildParentList(item: QuestionnaireResponse.QuestionnaireResponseItemComponent) {
      for (child in item.item) {
        questionnaireResponseItemParentMap[child] = item
        buildParentList(child)
      }
    }

    for (item in questionnaireResponse.item) {
      buildParentList(item)
    }
  }

  /** The pages of the questionnaire, or null if the questionnaire is not paginated. */
  @VisibleForTesting var pages: List<QuestionnairePage>? = questionnaire.getInitialPages()

  /**
   * The flow representing the index of the current page, or null if the questionnaire is not
   * paginated.
   */
  @VisibleForTesting val currentPageIndexFlow = MutableStateFlow(getInitialPageIndex())

  /** Flag to support fragment for review-feature */
  private val enableReviewPage: Boolean

  init {
    enableReviewPage = state[QuestionnaireFragment.EXTRA_ENABLE_REVIEW_PAGE] ?: false
  }

  /** Flag to open fragment first in data-collection or review-mode */
  private val showReviewPageFirst: Boolean

  init {
    showReviewPageFirst =
      enableReviewPage && state[QuestionnaireFragment.EXTRA_SHOW_REVIEW_PAGE_FIRST] ?: false
  }

  /** Tracks modifications in order to update the UI. */
  private val modificationCount = MutableStateFlow(0)

  /** Toggles review mode. */
  private val reviewFlow = MutableStateFlow(showReviewPageFirst)

  /** Flag to show/hide submit button. */
  private var showSubmitButtonFlag = false

  /**
   * Contains [QuestionnaireResponse.QuestionnaireResponseItemComponent]s that have been modified by
   * the user. [QuestionnaireResponse.QuestionnaireResponseItemComponent]s that have not been
   * modified by the user will not be validated. This is to avoid spamming the user with a sea of
   * validation errors when the questionnaire is loaded initially.
   */
  private val modifiedQuestionnaireResponseItemSet =
    mutableSetOf<QuestionnaireResponse.QuestionnaireResponseItemComponent>()

  /**
   * Callback function to update the view model after the answer(s) to a question have been changed.
   * This is passed to the [QuestionnaireItemViewItem] in its constructor so that it can invoke this
   * callback function after the UI widget has updated the answer(s).
   *
   * This function updates the [QuestionnaireResponse] held in memory using the answer(s) provided
   * by the UI. Subsequently it should also trigger the recalculation of any relevant expressions,
   * enablement states, and validation results throughout the questionnaire.
   *
   * This callback function has 3 params:
   * - the reference to the [Questionnaire.QuestionnaireItemComponent] in the [Questionnaire]
   * - the reference to the [QuestionnaireResponse.QuestionnaireResponseItemComponent] in the
   * [QuestionnaireResponse]
   * - a [List] of [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] which are the
   * new answers to the question.
   */
  private val answersChangedCallback:
    (
      Questionnaire.QuestionnaireItemComponent,
      QuestionnaireResponse.QuestionnaireResponseItemComponent,
      List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>
    ) -> Unit =
    { questionnaireItem, questionnaireResponseItem, answers ->
      // TODO(jingtang10): update the questionnaire response item pre-order list and the parent map
      questionnaireResponseItem.answer = answers.toList()
      if (questionnaireItem.hasNestedItemsWithinAnswers) {
        questionnaireResponseItem.addNestedItemsToAnswer(questionnaireItem)
      }
      modifiedQuestionnaireResponseItemSet.add(questionnaireResponseItem)

      updateDependentQuestionnaireResponseItems(questionnaireItem)

      modificationCount.update { it + 1 }
    }

  private val answerValueSetMap =
    mutableMapOf<String, List<Questionnaire.QuestionnaireItemAnswerOptionComponent>>()

  /**
   * The answer expression referencing an x-fhir-query has its evaluated data cached to avoid
   * reloading resources unnecessarily. The value is updated each time an item with answer
   * expression is evaluating the latest answer options.
   */
  private val answerExpressionMap =
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
    when (entryMode) {
      EntryMode.PRIOR_EDIT,
      EntryMode.RANDOM -> {
        val previousPageIndex =
          pages!!.indexOfLast { it.index < currentPageIndexFlow.value!! && it.enabled }
        check(previousPageIndex != -1) {
          "Can't call goToPreviousPage() if no preceding page is enabled"
        }
        currentPageIndexFlow.value = previousPageIndex
      }
      else -> {
        Timber.w("Previous questions and submitted answers cannot be viewed or edited.")
      }
    }
  }

  internal fun goToNextPage() {
    when (entryMode) {
      EntryMode.PRIOR_EDIT,
      EntryMode.SEQUENTIAL -> {
        if (!isPaginationButtonPressed) {
          // Force update validation results for all questions on the current page. This is needed
          // when the user has not answered any questions so no validation has been done.
          isPaginationButtonPressed = true
          modificationCount.update { it + 1 }
        }

        if (currentPageItems.all { it.validationResult is Valid }) {
          isPaginationButtonPressed = false
          val nextPageIndex =
            pages!!.indexOfFirst { it.index > currentPageIndexFlow.value!! && it.enabled }
          check(nextPageIndex != -1) { "Can't call goToNextPage() if no following page is enabled" }
          currentPageIndexFlow.value = nextPageIndex
        }
      }
      EntryMode.RANDOM -> {
        val nextPageIndex =
          pages!!.indexOfFirst { it.index > currentPageIndexFlow.value!! && it.enabled }
        check(nextPageIndex != -1) { "Can't call goToNextPage() if no following page is enabled" }
        currentPageIndexFlow.value = nextPageIndex
      }
    }
  }

  internal fun setReviewMode(reviewModeFlag: Boolean) {
    reviewFlow.value = reviewModeFlag
  }

  internal fun setShowSubmitButtonFlag(showSubmitButton: Boolean) {
    showSubmitButtonFlag = showSubmitButton
  }

  /** [QuestionnaireState] to be displayed in the UI. */
  internal val questionnaireStateFlow: Flow<QuestionnaireState> =
    combine(modificationCount, currentPageIndexFlow, reviewFlow) { _, pagination, reviewFlow ->
        if (reviewFlow) {
          getQuestionnaireState(
            questionnaireItemList = questionnaire.item,
            questionnaireResponseItemList = questionnaireResponse.item,
            currentPageIndex = null,
            reviewMode = reviewFlow
          )
        } else {
          getQuestionnaireState(
            questionnaireItemList = questionnaire.item,
            questionnaireResponseItemList = questionnaireResponse.item,
            currentPageIndex = pagination,
            reviewMode = reviewFlow
          )
        }
      }
      .stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        initialValue =
          getQuestionnaireState(
              questionnaireItemList = questionnaire.item,
              questionnaireResponseItemList = questionnaireResponse.item,
              currentPageIndex = getInitialPageIndex(),
              reviewMode = enableReviewPage
            )
            .also { detectExpressionCyclicDependency(questionnaire.item) }
            .also {
              questionnaire.item.flattened().forEach {
                updateDependentQuestionnaireResponseItems(it)
              }
            }
      )

  fun updateDependentQuestionnaireResponseItems(
    updatedQuestionnaireItem: Questionnaire.QuestionnaireItemComponent
  ) {
    evaluateCalculatedExpressions(
        updatedQuestionnaireItem,
        questionnaire,
        questionnaireResponse,
        questionnaireItemParentMap
      )
      .forEach { (questionnaireItem, calculatedAnswers) ->
        // update all response item with updated values
        questionnaireResponseItemPreOrderList
          // Item answer should not be modified and touched by user;
          // https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-calculatedExpression.html
          .filter {
            it.linkId == questionnaireItem.linkId &&
              !modifiedQuestionnaireResponseItemSet.contains(it)
          }
          .forEach { questionnaireResponseItem ->
            // update and notify only if new answer has changed to prevent any event loop
            if (questionnaireResponseItem.answer.hasDifferentAnswerSet(calculatedAnswers)) {
              questionnaireResponseItem.answer =
                calculatedAnswers.map {
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = it
                  }
                }
            }
          }
      }
  }

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
          .firstOrNull { resource ->
            resource.id.equals(uri) &&
              resource.resourceType == ResourceType.ValueSet &&
              (resource as ValueSet).hasExpansion()
          }
          ?.let { resource ->
            val valueSet = resource as ValueSet
            valueSet.expansion.contains
              .filterNot { it.abstract || it.inactive }
              .map { component ->
                Questionnaire.QuestionnaireItemAnswerOptionComponent(
                  Coding(component.system, component.code, component.display)
                )
              }
          }
      } else {
        // Ask the client to provide the answers from an external expanded Valueset.
        DataCapture.getConfiguration(getApplication())
          .valueSetResolverExternal
          ?.resolve(uri)
          ?.map { coding -> Questionnaire.QuestionnaireItemAnswerOptionComponent(coding.copy()) }
      }
        ?: emptyList()
    // save it so that we avoid have cache misses.
    answerValueSetMap[uri] = options
    return options
  }

  // TODO persist previous answers incase options are changing and new list does not have selected
  // answer and FHIRPath in x-fhir-query
  // https://build.fhir.org/ig/HL7/sdc/expressions.html#x-fhir-query-enhancements
  @PublishedApi
  internal suspend fun resolveAnswerExpression(
    item: Questionnaire.QuestionnaireItemComponent
  ): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> {
    // Check cache first for database queries
    val answerExpression = item.answerExpression ?: return emptyList()
    if (answerExpression.isXFhirQuery && answerExpressionMap.contains(answerExpression.expression)
    ) {
      return answerExpressionMap[answerExpression.expression]!!
    }

    val options = loadAnswerExpressionOptions(item, answerExpression)

    if (answerExpression.isXFhirQuery) answerExpressionMap[answerExpression.expression] = options

    return options
  }

  private suspend fun loadAnswerExpressionOptions(
    item: Questionnaire.QuestionnaireItemComponent,
    expression: Expression
  ): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> {
    val data =
      if (expression.isXFhirQuery) fhirEngine.search(expression.expression)
      else if (expression.isFhirPath)
        fhirPathEngine.evaluate(questionnaireResponse, expression.expression)
      else
        throw UnsupportedOperationException(
          "${expression.language} not supported for answer-expression yet"
        )

    return item.extractAnswerOptions(data)
  }

  /**
   * Traverses through the list of questionnaire items, the list of questionnaire response items and
   * the list of items in the questionnaire response answer list and populates
   * [questionnaireStateFlow] with matching pairs of questionnaire item and questionnaire response
   * item.
   *
   * The traverse is carried out in the two lists in tandem.
   */
  private fun getQuestionnaireState(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    currentPageIndex: Int?,
    reviewMode: Boolean
  ): QuestionnaireState {

    val showReviewButton =
      enableReviewPage &&
        !reviewFlow.value &&
        (currentPageIndex == null ||
          !QuestionnairePagination(pages = pages!!, currentPageIndex = currentPageIndex)
            .hasNextPage)

    val showSubmitButton =
      showSubmitButtonFlag &&
        !showReviewButton &&
        (currentPageIndex == null ||
          !QuestionnairePagination(pages = pages!!, currentPageIndex = currentPageIndex)
            .hasNextPage)

    if (currentPageIndex == null) {
      // Single-page questionnaire
      return QuestionnaireState(
        items = getQuestionnaireItemViewItems(questionnaireItemList, questionnaireResponseItemList),
        pagination =
          QuestionnairePagination(false, emptyList(), -1, showSubmitButton, showReviewButton),
        reviewMode = reviewMode
      )
    }

    // Paginated questionnaire
    pages =
      questionnaireItemList.zip(questionnaireResponseItemList).mapIndexed {
        index,
        (questionnaireItem, questionnaireResponseItem) ->
        QuestionnairePage(
          index,
          EnablementEvaluator.evaluate(
            questionnaireItem,
            questionnaireResponseItem,
            questionnaireResponse
          ) { item, linkId -> findEnableWhenQuestionnaireResponseItem(item, linkId) }
        )
      }
    return QuestionnaireState(
      items =
        getQuestionnaireItemViewItems(
          questionnaireItemList[currentPageIndex],
          questionnaireResponseItemList[currentPageIndex]
        ),
      pagination =
        QuestionnairePagination(
          true,
          pages!!,
          currentPageIndex,
          showSubmitButton,
          showReviewButton
        ),
      reviewMode = reviewMode
    )
  }

  /**
   * Returns the list of [QuestionnaireItemViewItem]s generated for the questionnaire items and
   * questionnaire response items.
   */
  private fun getQuestionnaireItemViewItems(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
  ): List<QuestionnaireItemViewItem> {
    var responseIndex = 0
    return questionnaireItemList
      .asSequence()
      .flatMap { questionnaireItem ->
        var questionnaireResponseItem = questionnaireItem.createQuestionnaireResponseItem()
        // If there is an enabled questionnaire response available then we use that. Or else we
        // just use an empty questionnaireResponse Item
        if (responseIndex < questionnaireResponseItemList.size &&
            questionnaireItem.linkId == questionnaireResponseItemList[responseIndex].linkId
        ) {
          questionnaireResponseItem = questionnaireResponseItemList[responseIndex]
          responseIndex += 1
        }

        getQuestionnaireItemViewItems(questionnaireItem, questionnaireResponseItem)
      }
      .toList()
  }

  /**
   * Returns the list of [QuestionnaireItemViewItem]s generated for the questionnaire item and
   * questionnaire response item.
   */
  private fun getQuestionnaireItemViewItems(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent
  ): List<QuestionnaireItemViewItem> {
    val enabled =
      EnablementEvaluator.evaluate(
        questionnaireItem,
        questionnaireResponseItem,
        questionnaireResponse
      ) { item, linkId -> findEnableWhenQuestionnaireResponseItem(item, linkId) }

    if (!enabled || questionnaireItem.isHidden) {
      return emptyList()
    }

    val validationResult =
      if (modifiedQuestionnaireResponseItemSet.contains(questionnaireResponseItem) ||
          isPaginationButtonPressed
      ) {
        QuestionnaireResponseItemValidator.validate(
          questionnaireItem,
          questionnaireResponseItem.answer,
          this@QuestionnaireViewModel.getApplication()
        )
      } else {
        NotValidated
      }
    val items =
      listOf(
        QuestionnaireItemViewItem(
          questionnaireItem,
          questionnaireResponseItem,
          validationResult = validationResult,
          answersChangedCallback = answersChangedCallback,
          resolveAnswerValueSet = { resolveAnswerValueSet(it) },
          resolveAnswerExpression = { resolveAnswerExpression(it) }
        )
      ) +
        getQuestionnaireItemViewItems(
          // If nested display item is identified as instructions or flyover, then do not create
          // questionnaire state for it.
          questionnaireItemList =
            questionnaireItem.item.filterNot {
              it.type == Questionnaire.QuestionnaireItemType.DISPLAY &&
                (it.isInstructionsCode || it.isFlyoverCode || it.isHelpCode)
            },
          questionnaireResponseItemList =
            if (questionnaireResponseItem.answer.isEmpty()) {
              questionnaireResponseItem.item
            } else {
              questionnaireResponseItem.answer.first().item
            },
        )
    currentPageItems = items
    return items
  }

  private fun getEnabledResponseItems(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
  ): List<QuestionnaireResponse.QuestionnaireResponseItemComponent> {
    val responseItemKeys = questionnaireResponseItemList.map { it.linkId }
    return questionnaireItemList
      .asSequence()
      .filter { responseItemKeys.contains(it.linkId) }
      .zip(questionnaireResponseItemList.asSequence())
      .filter { (questionnaireItem, questionnaireResponseItem) ->
        EnablementEvaluator.evaluate(
          questionnaireItem,
          questionnaireResponseItem,
          questionnaireResponse
        ) { item, linkId ->
          findEnableWhenQuestionnaireResponseItem(item, linkId) ?: return@evaluate null
        }
      }
      .map { (questionnaireItem, questionnaireResponseItem) ->
        questionnaireResponseItem.text = questionnaireItem.localizedTextSpanned?.toString()
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
   * Checks if this questionnaire uses pagination via the "page" extension.
   *
   * If any one group has a "page" extension, it is assumed that the whole questionnaire is a
   * well-formed, paginated questionnaire (eg, each top-level group should be its own page).
   *
   * If this questionnaire uses pagination, returns the [QuestionnairePagination] that you would see
   * when first opening this questionnaire. Otherwise, returns `null`.
   */
  private fun getInitialPageIndex(): Int? =
    if (questionnaire.isPaginated) {
      0 // Always begin with the first page
    } else {
      null
    }

  private fun Questionnaire.getInitialPages() =
    if (questionnaire.isPaginated) {
      // Assume all pages are enabled to begin with
      item.indices.map { QuestionnairePage(it, true) }
    } else {
      null
    }

  /**
   * Find a questionnaire response item in [QuestionnaireResponse] with the given `linkId` starting
   * from the `origin`.
   *
   * This is used by the enableWhen logic to evaluate if a question should be enabled/displayed.
   *
   * If multiple questionnaire response items are present for the same question (same linkId),
   * either as a result of repeated group or nested question under repeated answers, this returns
   * the nearest question occurrence reachable by tracing first the "ancestor" axis and then the
   * "preceding" axis and then the "following" axis.
   *
   * See
   * https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.enableWhen.question.
   */
  private fun findEnableWhenQuestionnaireResponseItem(
    origin: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    linkId: String,
  ): QuestionnaireResponse.QuestionnaireResponseItemComponent? {
    // Find the nearest ancestor with the linkId
    var parent = questionnaireResponseItemParentMap[origin]
    while (parent != null) {
      if (parent.linkId == linkId) {
        return parent
      }
      parent = questionnaireResponseItemParentMap[parent]
    }

    // Find the nearest item preceding the origin
    val itemIndex = questionnaireResponseItemPreOrderList.indexOf(origin)
    for (index in itemIndex - 1 downTo 0) {
      if (questionnaireResponseItemPreOrderList[index].linkId == linkId) {
        return questionnaireResponseItemPreOrderList[index]
      }
    }

    // Find the nearest item succeeding the origin
    for (index in itemIndex + 1 until questionnaireResponseItemPreOrderList.size) {
      if (questionnaireResponseItemPreOrderList[index].linkId == linkId) {
        return questionnaireResponseItemPreOrderList[index]
      }
    }

    return null
  }
}

typealias ItemToParentMap =
  MutableMap<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>

/** Questionnaire state for the Fragment to consume. */
internal data class QuestionnaireState(
  /** The items that should be currently-rendered into the Fragment. */
  val items: List<QuestionnaireItemViewItem>,
  /** The pagination state of the questionnaire. */
  val pagination: QuestionnairePagination,
  /** Tracks reviewMode in order to update the UI. */
  val reviewMode: Boolean,
)

/**
 * Pagination information of the questionnaire. This is used for the UI to render pagination
 * controls. Includes information for each page and the current page index.
 */
internal data class QuestionnairePagination(
  val isPaginated: Boolean = false,
  val pages: List<QuestionnairePage>,
  val currentPageIndex: Int,
  val showSubmitButton: Boolean = false,
  val showReviewButton: Boolean = false
)

/** A single page in the questionnaire. This is used for the UI to render pagination controls. */
internal data class QuestionnairePage(
  val index: Int,
  val enabled: Boolean,
)

internal val QuestionnairePagination.hasPreviousPage: Boolean
  get() = pages.any { it.index < currentPageIndex && it.enabled }

internal val QuestionnairePagination.hasNextPage: Boolean
  get() = pages.any { it.index > currentPageIndex && it.enabled }
