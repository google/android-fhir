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
import com.google.android.fhir.datacapture.enablement.EnablementEvaluator
import com.google.android.fhir.datacapture.extensions.EntryMode
import com.google.android.fhir.datacapture.extensions.addNestedItemsToAnswer
import com.google.android.fhir.datacapture.extensions.allItems
import com.google.android.fhir.datacapture.extensions.answerExpression
import com.google.android.fhir.datacapture.extensions.cqfExpression
import com.google.android.fhir.datacapture.extensions.createQuestionnaireResponseItem
import com.google.android.fhir.datacapture.extensions.entryMode
import com.google.android.fhir.datacapture.extensions.extractAnswerOptions
import com.google.android.fhir.datacapture.extensions.flattened
import com.google.android.fhir.datacapture.extensions.hasDifferentAnswerSet
import com.google.android.fhir.datacapture.extensions.isDisplayItem
import com.google.android.fhir.datacapture.extensions.isFhirPath
import com.google.android.fhir.datacapture.extensions.isHidden
import com.google.android.fhir.datacapture.extensions.isPaginated
import com.google.android.fhir.datacapture.extensions.isXFhirQuery
import com.google.android.fhir.datacapture.extensions.localizedTextSpanned
import com.google.android.fhir.datacapture.extensions.packRepeatedGroups
import com.google.android.fhir.datacapture.extensions.questionnaireLaunchContexts
import com.google.android.fhir.datacapture.extensions.shouldHaveNestedItemsUnderAnswers
import com.google.android.fhir.datacapture.extensions.unpackRepeatedGroups
import com.google.android.fhir.datacapture.extensions.validateLaunchContextExtensions
import com.google.android.fhir.datacapture.extensions.zipByLinkId
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator.detectExpressionCyclicDependency
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator.evaluateCalculatedExpressions
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator.evaluateExpression
import com.google.android.fhir.datacapture.fhirpath.fhirPathEngine
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseItemValidator
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator.checkQuestionnaireResponse
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Element
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.ValueSet
import timber.log.Timber

internal class QuestionnaireViewModel(application: Application, state: SavedStateHandle) :
  AndroidViewModel(application) {

  private val parser: IParser by lazy { FhirContext.forCached(FhirVersionEnum.R4).newJsonParser() }
  private val xFhirQueryResolver: XFhirQueryResolver? by lazy {
    DataCapture.getConfiguration(application).xFhirQueryResolver
  }

  /** The current questionnaire as questions are being answered. */
  internal val questionnaire: Questionnaire

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

  /** The current questionnaire response as questions are being answered. */
  private val questionnaireResponse: QuestionnaireResponse

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
    questionnaireResponse.packRepeatedGroups()
  }

  /**
   * The launch context allows information to be passed into questionnaire based on the context in
   * which the questionnaire is being evaluated. For example, what patient, what encounter, what
   * user, etc. is "in context" at the time the questionnaire response is being completed:
   * https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-launchContext.html
   */
  private val questionnaireLaunchContextMap: Map<String, Resource>?

  init {
    questionnaireLaunchContextMap =
      if (state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_JSON_STRINGS)) {

        val launchContextJsonStrings: List<String> =
          state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_JSON_STRINGS]!!

        val launchContexts = launchContextJsonStrings.map { parser.parseResource(it) as Resource }
        questionnaire.questionnaireLaunchContexts?.let { launchContextExtensions ->
          validateLaunchContextExtensions(launchContextExtensions)
          launchContexts.associateBy { it.resourceType.name.lowercase() }
        }
      } else {
        null
      }
  }

  /** The map from each item in the [Questionnaire] to its parent. */
  private var questionnaireItemParentMap:
    Map<QuestionnaireItemComponent, QuestionnaireItemComponent>

  init {
    /** Adds each child-parent pair in the [Questionnaire] to the parent map. */
    fun buildParentList(
      item: QuestionnaireItemComponent,
      questionnaireItemToParentMap: ItemToParentMap,
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

  @VisibleForTesting
  val entryMode: EntryMode by lazy { questionnaire.entryMode ?: EntryMode.RANDOM }

  /** Flag to determine if the questionnaire should be read-only. */
  private val isReadOnly = state[QuestionnaireFragment.EXTRA_READ_ONLY] ?: false

  /** Flag to support fragment for review-feature */
  private val shouldEnableReviewPage =
    state[QuestionnaireFragment.EXTRA_ENABLE_REVIEW_PAGE] ?: false

  /** Flag to open fragment first in data-collection or review-mode */
  private val shouldShowReviewPageFirst =
    shouldEnableReviewPage && state[QuestionnaireFragment.EXTRA_SHOW_REVIEW_PAGE_FIRST] ?: false

  /** Flag to show/hide submit button. Default is true. */
  private var shouldShowSubmitButton = state[QuestionnaireFragment.EXTRA_SHOW_SUBMIT_BUTTON] ?: true

  /** Flag to control whether asterisk text is shown for required questions. */
  private val showAsterisk = state[QuestionnaireFragment.EXTRA_SHOW_ASTERISK_TEXT] ?: false

  /** Flag to control whether asterisk text is shown for required questions. */
  private val showRequiredText = state[QuestionnaireFragment.EXTRA_SHOW_REQUIRED_TEXT] ?: true

  /** Flag to control whether optional text is shown. */
  private val showOptionalText = state[QuestionnaireFragment.EXTRA_SHOW_OPTIONAL_TEXT] ?: false

  /** The pages of the questionnaire, or null if the questionnaire is not paginated. */
  @VisibleForTesting var pages: List<QuestionnairePage>? = null

  /**
   * The flow representing the index of the current page. This value is meaningless if the
   * questionnaire is not paginated or in review mode.
   */
  @VisibleForTesting val currentPageIndexFlow: MutableStateFlow<Int?> = MutableStateFlow(null)

  /** Tracks modifications in order to update the UI. */
  private val modificationCount = MutableStateFlow(0)

  /** Toggles review mode. */
  private val isInReviewModeFlow = MutableStateFlow(shouldShowReviewPageFirst)

  /**
   * Contains [QuestionnaireResponseItemComponent]s that have been modified by the user.
   * [QuestionnaireResponseItemComponent]s that have not been modified by the user will not be
   * validated. This is to avoid spamming the user with a sea of validation errors when the
   * questionnaire is loaded initially.
   */
  private val modifiedQuestionnaireResponseItemSet =
    mutableSetOf<QuestionnaireResponseItemComponent>()

  private lateinit var currentPageItems: List<QuestionnaireAdapterItem>

  /**
   * True if the user has tapped the next/previous pagination buttons on the current page. This is
   * needed to avoid spewing validation errors before any questions are answered.
   */
  private var forceValidation = false

  /**
   * Map of [QuestionnaireResponseItemAnswerComponent] for
   * [Questionnaire.QuestionnaireItemComponent]s that are disabled now. The answers will be used to
   * pre-populate the [QuestionnaireResponseItemComponent] once the item is enabled again.
   */
  private val responseItemToAnswersMapForDisabledQuestionnaireItem =
    mutableMapOf<
      QuestionnaireResponseItemComponent, List<QuestionnaireResponseItemAnswerComponent>>()

  /**
   * Map from [QuestionnaireResponseItemComponent] to draft answers, e.g "02/02" for date with
   * missing year part.
   *
   * This is used to maintain draft answers on the screen especially when the widgets are being
   * recycled as a result of scrolling. Draft answers cannot be saved in [QuestionnaireResponse]
   * because they might be incomplete and unparsable. Without this map, incomplete and unparsable
   * answers would be lost.
   *
   * When the draft answer becomes valid, its entry in the map is removed, e.g, "02/02/2023" is
   * valid answer and should not be in this map.
   */
  private val draftAnswerMap = mutableMapOf<QuestionnaireResponseItemComponent, Any>()

  /**
   * Callback function to update the view model after the answer(s) to a question have been changed.
   * This is passed to the [QuestionnaireViewItem] in its constructor so that it can invoke this
   * callback function after the UI widget has updated the answer(s).
   *
   * This function updates the [QuestionnaireResponse] held in memory using the answer(s) provided
   * by the UI. Subsequently it should also trigger the recalculation of any relevant expressions,
   * enablement states, and validation results throughout the questionnaire.
   *
   * This callback function has 4 params:
   * - the reference to the [Questionnaire.QuestionnaireItemComponent] in the [Questionnaire]
   * - the reference to the [QuestionnaireResponseItemComponent] in the [QuestionnaireResponse]
   * - a [List] of [QuestionnaireResponseItemAnswerComponent] which are the new answers to the
   * question.
   * - partial answer, the entered input is not a valid answer
   */
  private val answersChangedCallback:
    (
      QuestionnaireItemComponent,
      QuestionnaireResponseItemComponent,
      List<QuestionnaireResponseItemAnswerComponent>,
      Any?
    ) -> Unit =
    { questionnaireItem, questionnaireResponseItem, answers, draftAnswer ->
      // TODO(jingtang10): update the questionnaire response item pre-order list and the parent map
      questionnaireResponseItem.answer = answers.toList()
      when {
        (questionnaireResponseItem.answer.isNotEmpty()) -> {
          draftAnswerMap.remove(questionnaireResponseItem)
        }
        else -> {
          if (draftAnswer == null) {
            draftAnswerMap.remove(questionnaireResponseItem)
          } else {
            draftAnswerMap[questionnaireResponseItem] = draftAnswer
          }
        }
      }
      if (questionnaireItem.shouldHaveNestedItemsUnderAnswers) {
        questionnaireResponseItem.addNestedItemsToAnswer(questionnaireItem)
      }
      modifiedQuestionnaireResponseItemSet.add(questionnaireResponseItem)

      updateDependentQuestionnaireResponseItems(questionnaireItem, questionnaireResponseItem)

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
      // Use the view model's questionnaire and questionnaire response for calculating enabled items
      // because the calculation relies on references to the questionnaire response items.
      item =
        getEnabledResponseItems(
            this@QuestionnaireViewModel.questionnaire.item,
            questionnaireResponse.item
          )
          .map { it.copy() }
      unpackRepeatedGroups(this@QuestionnaireViewModel.questionnaire)
    }
  }

  /**
   * Validates entire questionnaire and return the validation results. As a side effect, it triggers
   * the UI update to show errors in case there are any validation errors.
   */
  internal fun validateQuestionnaireAndUpdateUI(): Map<String, List<ValidationResult>> =
    QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        questionnaireResponse,
        getApplication()
      )
      .also { result ->
        if (result.values.flatten().filterIsInstance<Invalid>().isNotEmpty()) {
          // Update UI of current page if necessary
          validateCurrentPageItems {}
        }
      }

  internal fun goToPreviousPage() {
    when (entryMode) {
      EntryMode.PRIOR_EDIT,
      EntryMode.RANDOM, -> {
        val previousPageIndex =
          pages!!.indexOfLast {
            it.index < currentPageIndexFlow.value!! && it.enabled && !it.hidden
          }
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
      EntryMode.SEQUENTIAL, -> {
        validateCurrentPageItems {
          val nextPageIndex =
            pages!!.indexOfFirst {
              it.index > currentPageIndexFlow.value!! && it.enabled && !it.hidden
            }
          check(nextPageIndex != -1) { "Can't call goToNextPage() if no following page is enabled" }
          currentPageIndexFlow.value = nextPageIndex
        }
      }
      EntryMode.RANDOM -> {
        val nextPageIndex =
          pages!!.indexOfFirst {
            it.index > currentPageIndexFlow.value!! && it.enabled && !it.hidden
          }
        check(nextPageIndex != -1) { "Can't call goToNextPage() if no following page is enabled" }
        currentPageIndexFlow.value = nextPageIndex
      }
    }
  }

  internal fun setReviewMode(reviewModeFlag: Boolean) {
    if (reviewModeFlag) {
      when (entryMode) {
        EntryMode.PRIOR_EDIT,
        EntryMode.SEQUENTIAL, -> {
          validateCurrentPageItems { isInReviewModeFlow.value = true }
        }
        EntryMode.RANDOM -> {
          isInReviewModeFlow.value = true
        }
      }
    } else {
      isInReviewModeFlow.value = false
    }
  }

  /** [QuestionnaireState] to be displayed in the UI. */
  internal val questionnaireStateFlow: StateFlow<QuestionnaireState> =
    combine(modificationCount, currentPageIndexFlow, isInReviewModeFlow) { _, _, _ ->
        getQuestionnaireState()
      }
      .stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        initialValue =
          getQuestionnaireState()
            .also { detectExpressionCyclicDependency(questionnaire.item) }
            .also {
              questionnaire.item.flattened().forEach { qItem ->
                updateDependentQuestionnaireResponseItems(
                  qItem,
                  questionnaireResponse.allItems.find { it.linkId == qItem.linkId }
                )
              }
            }
      )

  private fun updateDependentQuestionnaireResponseItems(
    updatedQuestionnaireItem: QuestionnaireItemComponent,
    updatedQuestionnaireResponseItem: QuestionnaireResponseItemComponent?,
  ) {
    evaluateCalculatedExpressions(
        updatedQuestionnaireItem,
        updatedQuestionnaireResponseItem,
        questionnaire,
        questionnaireResponse,
        questionnaireItemParentMap
      )
      .forEach { (questionnaireItem, calculatedAnswers) ->
        // update all response item with updated values
        questionnaireResponse.allItems
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
                  QuestionnaireResponseItemAnswerComponent().apply { value = it }
                }
            }
          }
      }
  }

  internal suspend fun resolveAnswerValueSet(
    uri: String,
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

  // TODO persist previous answers in case options are changing and new list does not have selected
  // answer and FHIRPath in x-fhir-query
  // https://build.fhir.org/ig/HL7/sdc/expressions.html#x-fhir-query-enhancements
  internal suspend fun resolveAnswerExpression(
    item: QuestionnaireItemComponent,
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

  private fun resolveCqfExpression(
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
    element: Element,
  ): List<Base> {
    val cqfExpression = element.cqfExpression ?: return emptyList()

    if (!cqfExpression.isFhirPath) {
      throw UnsupportedOperationException("${cqfExpression.language} not supported yet")
    }
    return evaluateExpression(
      questionnaire,
      questionnaireResponse,
      questionnaireItem,
      questionnaireResponseItem,
      cqfExpression,
      questionnaireItemParentMap
    )
  }

  private suspend fun loadAnswerExpressionOptions(
    item: QuestionnaireItemComponent,
    expression: Expression,
  ): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> {
    val data =
      if (expression.isXFhirQuery) {
        checkNotNull(xFhirQueryResolver) {
          "XFhirQueryResolver cannot be null. Please provide the XFhirQueryResolver via DataCaptureConfig."
        }

        val xFhirExpressionString =
          ExpressionEvaluator.createXFhirQueryFromExpression(
            expression,
            questionnaireLaunchContextMap
          )
        xFhirQueryResolver!!.resolve(xFhirExpressionString)
      } else if (expression.isFhirPath) {
        fhirPathEngine.evaluate(questionnaireResponse, expression.expression)
      } else {
        throw UnsupportedOperationException(
          "${expression.language} not supported for answer-expression yet"
        )
      }

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
  private fun getQuestionnaireState(): QuestionnaireState {
    val questionnaireItemList = questionnaire.item
    val questionnaireResponseItemList = questionnaireResponse.item

    // Only display items on the current page while editing a paginated questionnaire, otherwise,
    // display all items.
    val questionnaireItemViewItems =
      if (!isReadOnly && !isInReviewModeFlow.value && questionnaire.isPaginated) {
        pages = getQuestionnairePages()
        if (currentPageIndexFlow.value == null) {
          currentPageIndexFlow.value = pages!!.first { it.enabled && !it.hidden }.index
        }
        getQuestionnaireAdapterItems(
          questionnaireItemList[currentPageIndexFlow.value!!],
          questionnaireResponseItemList[currentPageIndexFlow.value!!]
        )
      } else {
        getQuestionnaireAdapterItems(questionnaireItemList, questionnaireResponseItemList)
      }

    // Reviewing the questionnaire or the questionnaire is read-only
    if (isReadOnly || isInReviewModeFlow.value) {
      return QuestionnaireState(
        items = questionnaireItemViewItems,
        displayMode =
          DisplayMode.ReviewMode(
            showEditButton = !isReadOnly,
            showSubmitButton = !isReadOnly && shouldShowSubmitButton
          )
      )
    }

    // Editing the questionnaire
    val questionnairePagination =
      if (!questionnaire.isPaginated) {
        val showReviewButton = shouldEnableReviewPage && !isInReviewModeFlow.value
        val showSubmitButton = shouldShowSubmitButton && !showReviewButton
        QuestionnairePagination(false, emptyList(), -1, showSubmitButton, showReviewButton)
      } else {
        val hasNextPage =
          QuestionnairePagination(pages = pages!!, currentPageIndex = currentPageIndexFlow.value!!)
            .hasNextPage
        val showReviewButton = shouldEnableReviewPage && !hasNextPage
        val showSubmitButton = shouldShowSubmitButton && !showReviewButton && !hasNextPage
        QuestionnairePagination(
          true,
          pages!!,
          currentPageIndexFlow.value!!,
          showSubmitButton,
          showReviewButton
        )
      }

    return QuestionnaireState(
      items = questionnaireItemViewItems,
      displayMode = DisplayMode.EditMode(questionnairePagination)
    )
  }

  /**
   * Returns the list of [QuestionnaireViewItem]s generated for the questionnaire items and
   * questionnaire response items.
   */
  private fun getQuestionnaireAdapterItems(
    questionnaireItemList: List<QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponseItemComponent>,
  ): List<QuestionnaireAdapterItem> {
    return questionnaireItemList
      .zipByLinkId(questionnaireResponseItemList) { questionnaireItem, questionnaireResponseItem ->
        getQuestionnaireAdapterItems(questionnaireItem, questionnaireResponseItem)
      }
      .flatten()
  }

  /**
   * Returns the list of [QuestionnaireViewItem]s generated for the questionnaire item and
   * questionnaire response item.
   */
  private fun getQuestionnaireAdapterItems(
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
  ): List<QuestionnaireAdapterItem> {
    // Hidden questions should not get QuestionnaireItemViewItem instances
    if (questionnaireItem.isHidden) return emptyList()
    val enabled =
      EnablementEvaluator(questionnaireResponse)
        .evaluate(questionnaireItem, questionnaireResponseItem)
    // Disabled questions should not get QuestionnaireItemViewItem instances
    if (!enabled) {
      cacheDisabledQuestionnaireItemAnswers(questionnaireResponseItem)
      return emptyList()
    }

    restoreFromDisabledQuestionnaireItemAnswersCache(questionnaireResponseItem)

    // Determine the validation result, which will be displayed on the item itself
    val validationResult =
      if (modifiedQuestionnaireResponseItemSet.contains(questionnaireResponseItem) ||
          forceValidation ||
          isInReviewModeFlow.value
      ) {
        QuestionnaireResponseItemValidator.validate(
          questionnaireItem,
          questionnaireResponseItem.answer,
          this@QuestionnaireViewModel.getApplication()
        )
      } else {
        NotValidated
      }

    // Set question text dynamically from CQL expression
    questionnaireResponseItem.apply {
      resolveCqfExpression(questionnaireItem, this, questionnaireItem.textElement)
        .firstOrNull()
        ?.let { text = it.primitiveValue() }
    }

    val items = buildList {
      // Add an item for the question itself
      add(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            questionnaireItem,
            questionnaireResponseItem,
            validationResult = validationResult,
            answersChangedCallback = answersChangedCallback,
            resolveAnswerValueSet = { resolveAnswerValueSet(it) },
            resolveAnswerExpression = { resolveAnswerExpression(it) },
            draftAnswer = draftAnswerMap[questionnaireResponseItem],
            enabledDisplayItems =
              questionnaireItem.item.filter {
                it.isDisplayItem &&
                  EnablementEvaluator(questionnaireResponse).evaluate(it, questionnaireResponseItem)
              },
            questionViewTextConfiguration =
              QuestionTextConfiguration(
                showAsterisk = showAsterisk,
                showRequiredText = showRequiredText,
                showOptionalText = showOptionalText
              )
          )
        )
      )

      // Add nested questions after the parent item. We need to get the questionnaire items and
      // (possibly multiple sets of) matching questionnaire response items and generate the adapter
      // items. There are three different cases:
      // 1. Questions nested under a non-repeated group: Simply take the nested question items and
      // the nested question response items and "zip" them.
      // 2. Questions nested under a question: In this case, the nested questions are repeated for
      // each answer to the parent question. Therefore, we need to take the questions and lists of
      // questionnaire response items nested under each answer and generate multiple sets of adapter
      // items.
      // 3. Questions nested under a repeated group: In the in-memory questionnaire response in the
      // view model, we create dummy answers for each repeated group. As a result the processing of
      // this case is similar to the case of questions nested under a question.
      // For background, see https://build.fhir.org/questionnaireresponse.html#link.
      buildList {
          // Case 1
          add(questionnaireResponseItem.item)
          // Case 2 and 3
          addAll(questionnaireResponseItem.answer.map { it.item })
        }
        .forEach { nestedResponseItemList ->
          addAll(
            getQuestionnaireAdapterItems(
              // If nested display item is identified as instructions or flyover, then do not create
              // questionnaire state for it.
              questionnaireItemList = questionnaireItem.item.filterNot { it.isDisplayItem },
              questionnaireResponseItemList = nestedResponseItemList,
            )
          )
        }
    }
    currentPageItems = items
    return items
  }

  /**
   * If the item is not enabled, clear the answers that it may have from the previous enabled state.
   * This will also prevent any questionnaire item that depends on the answer of this questionnaire
   * item to be wrongly evaluated as well.
   */
  private fun cacheDisabledQuestionnaireItemAnswers(
    questionnaireResponseItem: QuestionnaireResponseItemComponent
  ) {
    if (questionnaireResponseItem.hasAnswer()) {
      responseItemToAnswersMapForDisabledQuestionnaireItem[questionnaireResponseItem] =
        questionnaireResponseItem.answer
      questionnaireResponseItem.answer = listOf()
    }
  }

  /**
   * If the questionnaire item was previously disabled, check the cache to restore previous answers.
   */
  private fun restoreFromDisabledQuestionnaireItemAnswersCache(
    questionnaireResponseItem: QuestionnaireResponseItemComponent
  ) {
    if (responseItemToAnswersMapForDisabledQuestionnaireItem.contains(questionnaireResponseItem)) {
      questionnaireResponseItem.answer =
        responseItemToAnswersMapForDisabledQuestionnaireItem.remove(questionnaireResponseItem)
    }
  }

  private fun getEnabledResponseItems(
    questionnaireItemList: List<QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponseItemComponent>,
  ): List<QuestionnaireResponseItemComponent> {
    val enablementEvaluator = EnablementEvaluator(questionnaireResponse)
    val responseItemKeys = questionnaireResponseItemList.map { it.linkId }
    return questionnaireItemList
      .asSequence()
      .filter { responseItemKeys.contains(it.linkId) }
      .zip(questionnaireResponseItemList.asSequence())
      .filter { (questionnaireItem, questionnaireResponseItem) ->
        enablementEvaluator.evaluate(
          questionnaireItem,
          questionnaireResponseItem,
        )
      }
      .map { (questionnaireItem, questionnaireResponseItem) ->
        questionnaireResponseItem.apply {
          if (text.isNullOrBlank()) {
            text = questionnaireItem.localizedTextSpanned?.toString()
          }
          // Nested group items
          item = getEnabledResponseItems(questionnaireItem.item, questionnaireResponseItem.item)
          // Nested question items
          answer.forEach { it.item = getEnabledResponseItems(questionnaireItem.item, it.item) }
        }
      }
      .toList()
  }

  /**
   * Gets a list of [QuestionnairePage]s for a paginated questionnaire, or `null` if the
   * questionnaire is not paginated.
   */
  private fun getQuestionnairePages(): List<QuestionnairePage>? =
    if (questionnaire.isPaginated) {
      questionnaire.item.zip(questionnaireResponse.item).mapIndexed {
        index,
        (questionnaireItem, questionnaireResponseItem),
        ->
        QuestionnairePage(
          index,
          EnablementEvaluator(questionnaireResponse)
            .evaluate(
              questionnaireItem,
              questionnaireResponseItem,
            ),
          questionnaireItem.isHidden
        )
      }
    } else {
      null
    }

  /**
   * Validates the current page items if any are [NotValidated], and then, invokes [block] if they
   * are all [Valid].
   */
  private fun validateCurrentPageItems(block: () -> Unit) {
    if (currentPageItems.filterIsInstance<QuestionnaireAdapterItem.Question>().any {
        it.item.validationResult is NotValidated
      }
    ) {
      // Force update validation results for all questions on the current page. This is needed
      // when the user has not answered any questions so no validation has been done.
      forceValidation = true
      // Results in a new questionnaire state being generated synchronously, i.e., the current
      // thread will be suspended until the new state is generated.
      modificationCount.update { it + 1 }
      forceValidation = false
    }

    if (currentPageItems.filterIsInstance<QuestionnaireAdapterItem.Question>().all {
        it.item.validationResult is Valid
      }
    ) {
      block()
    }
  }
}

typealias ItemToParentMap = MutableMap<QuestionnaireItemComponent, QuestionnaireItemComponent>

/** Questionnaire state for the Fragment to consume. */
internal data class QuestionnaireState(
  val items: List<QuestionnaireAdapterItem>,
  val displayMode: DisplayMode,
)

internal sealed class DisplayMode {
  class EditMode(val pagination: QuestionnairePagination) : DisplayMode()
  data class ReviewMode(val showEditButton: Boolean, val showSubmitButton: Boolean) : DisplayMode()
}

/**
 * Pagination information of the questionnaire. This is used for the UI to render pagination
 * controls. Includes information for each page and the current page index.
 */
internal data class QuestionnairePagination(
  val isPaginated: Boolean = false,
  val pages: List<QuestionnairePage>,
  val currentPageIndex: Int,
  val showSubmitButton: Boolean = false,
  val showReviewButton: Boolean = false,
)

/** A single page in the questionnaire. This is used for the UI to render pagination controls. */
internal data class QuestionnairePage(
  val index: Int,
  val enabled: Boolean,
  val hidden: Boolean,
)

internal val QuestionnairePagination.hasPreviousPage: Boolean
  get() = pages.any { it.index < currentPageIndex && it.enabled }

internal val QuestionnairePagination.hasNextPage: Boolean
  get() = pages.any { it.index > currentPageIndex && it.enabled }
