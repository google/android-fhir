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
import com.google.android.fhir.datacapture.expressions.EnabledAnswerOptionsEvaluator
import com.google.android.fhir.datacapture.extensions.EntryMode
import com.google.android.fhir.datacapture.extensions.allItems
import com.google.android.fhir.datacapture.extensions.copyNestedItemsToChildlessAnswers
import com.google.android.fhir.datacapture.extensions.cqfExpression
import com.google.android.fhir.datacapture.extensions.createQuestionnaireResponseItem
import com.google.android.fhir.datacapture.extensions.entryMode
import com.google.android.fhir.datacapture.extensions.filterByCodeInNameExtension
import com.google.android.fhir.datacapture.extensions.flattened
import com.google.android.fhir.datacapture.extensions.hasDifferentAnswerSet
import com.google.android.fhir.datacapture.extensions.isDisplayItem
import com.google.android.fhir.datacapture.extensions.isHelpCode
import com.google.android.fhir.datacapture.extensions.isHidden
import com.google.android.fhir.datacapture.extensions.isPaginated
import com.google.android.fhir.datacapture.extensions.isRepeatedGroup
import com.google.android.fhir.datacapture.extensions.localizedTextSpanned
import com.google.android.fhir.datacapture.extensions.maxValue
import com.google.android.fhir.datacapture.extensions.maxValueCqfCalculatedValueExpression
import com.google.android.fhir.datacapture.extensions.minValue
import com.google.android.fhir.datacapture.extensions.minValueCqfCalculatedValueExpression
import com.google.android.fhir.datacapture.extensions.packRepeatedGroups
import com.google.android.fhir.datacapture.extensions.questionnaireLaunchContexts
import com.google.android.fhir.datacapture.extensions.shouldHaveNestedItemsUnderAnswers
import com.google.android.fhir.datacapture.extensions.unpackRepeatedGroups
import com.google.android.fhir.datacapture.extensions.validateLaunchContextExtensions
import com.google.android.fhir.datacapture.extensions.zipByLinkId
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator
import com.google.android.fhir.datacapture.mapping.asExpectedType
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.hl7.fhir.r4.model.Resource
import timber.log.Timber

internal class QuestionnaireViewModel(application: Application, state: SavedStateHandle) :
  AndroidViewModel(application) {

  private val parser: IParser by lazy { FhirContext.forCached(FhirVersionEnum.R4).newJsonParser() }
  private val xFhirQueryResolver: XFhirQueryResolver? by lazy {
    DataCapture.getConfiguration(application).xFhirQueryResolver
  }
  private val externalValueSetResolver: ExternalAnswerValueSetResolver? by lazy {
    DataCapture.getConfiguration(application).valueSetResolverExternal
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
                "EXTRA_QUESTIONNAIRE_JSON_URI takes precedence.",
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
            "Neither EXTRA_QUESTIONNAIRE_JSON_URI nor EXTRA_QUESTIONNAIRE_JSON_STRING is supplied.",
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
              "EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI takes precedence.",
          )
        }
        val uri: Uri = state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI]!!
        questionnaireResponse =
          parser.parseResource(application.contentResolver.openInputStream(uri))
            as QuestionnaireResponse
        addMissingResponseItems(questionnaire.item, questionnaireResponse.item)
        checkQuestionnaireResponse(questionnaire, questionnaireResponse)
      }
      state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING) -> {
        val questionnaireResponseJson: String =
          state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING]!!
        questionnaireResponse =
          parser.parseResource(questionnaireResponseJson) as QuestionnaireResponse
        addMissingResponseItems(questionnaire.item, questionnaireResponse.item)
        checkQuestionnaireResponse(questionnaire, questionnaireResponse)
      }
      else -> {
        questionnaireResponse =
          QuestionnaireResponse().apply {
            questionnaire = this@QuestionnaireViewModel.questionnaire.url
          }
        // Retain the hierarchy and order of items within the questionnaire as specified in the
        // standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
        questionnaire.item
          .filterNot { it.isRepeatedGroup }
          .forEach { questionnaireResponse.addItem(it.createQuestionnaireResponseItem()) }
      }
    }
    questionnaireResponse.packRepeatedGroups(questionnaire)
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
      if (state.contains(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP)) {

        val launchContextMapString: Map<String, String> =
          state[QuestionnaireFragment.EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP]!!

        val launchContextMapResource =
          launchContextMapString.mapValues { parser.parseResource(it.value) as Resource }
        questionnaire.questionnaireLaunchContexts?.let { launchContextExtensions ->
          validateLaunchContextExtensions(launchContextExtensions)
          filterByCodeInNameExtension(launchContextMapResource, launchContextExtensions)
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

  /** Flag to show questionnaire page as default/long scroll. Default is false. */
  private var shouldSetNavigationInLongScroll =
    state[QuestionnaireFragment.EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL] ?: false

  private var submitButtonText =
    state[QuestionnaireFragment.EXTRA_SUBMIT_BUTTON_TEXT]
      ?: application.getString(R.string.submit_questionnaire)

  private var onSubmitButtonClickListener: () -> Unit = {}

  private var onCancelButtonClickListener: () -> Unit = {}

  /** Flag to show/hide cancel button. Default is false */
  private var shouldShowCancelButton =
    state[QuestionnaireFragment.EXTRA_SHOW_CANCEL_BUTTON] ?: false

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

  /** Tracks which help card has been opened. */
  private val openedHelpCardSet: MutableSet<QuestionnaireResponseItemComponent> = mutableSetOf()

  /** Callback to save the help card state. */
  private val helpCardStateChangedCallback: (Boolean, QuestionnaireResponseItemComponent) -> Unit =
    { shouldBeVisible, questionnaireResponseItem ->
      if (shouldBeVisible) {
        openedHelpCardSet.add(questionnaireResponseItem)
      } else {
        openedHelpCardSet.remove(questionnaireResponseItem)
      }
    }

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
      QuestionnaireResponseItemComponent,
      List<QuestionnaireResponseItemAnswerComponent>,
    >()

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
   *   question.
   * - partial answer, the entered input is not a valid answer
   */
  private val answersChangedCallback:
    suspend (
      QuestionnaireItemComponent,
      QuestionnaireResponseItemComponent,
      List<QuestionnaireResponseItemAnswerComponent>,
      Any?,
    ) -> Unit =
    { questionnaireItem, questionnaireResponseItem, answers, draftAnswer ->
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
        questionnaireResponseItem.copyNestedItemsToChildlessAnswers(questionnaireItem)

        // If nested items are added to the answer, the enablement evaluator needs to be
        // reinitialized in order for it to rebuild the pre-order map and parent map of
        // questionnaire response items to reflect the new structure of the questionnaire response
        // to correctly calculate calculate enable when statements.
        enablementEvaluator =
          EnablementEvaluator(
            questionnaire,
            questionnaireResponse,
            questionnaireItemParentMap,
            questionnaireLaunchContextMap,
            xFhirQueryResolver,
          )
      }
      modifiedQuestionnaireResponseItemSet.add(questionnaireResponseItem)

      updateDependentQuestionnaireResponseItems(questionnaireItem, questionnaireResponseItem)

      modificationCount.update { it + 1 }
    }

  private val expressionEvaluator: ExpressionEvaluator =
    ExpressionEvaluator(
      questionnaire,
      questionnaireResponse,
      questionnaireItemParentMap,
      questionnaireLaunchContextMap,
      xFhirQueryResolver,
    )

  private var enablementEvaluator: EnablementEvaluator =
    EnablementEvaluator(
      questionnaire,
      questionnaireResponse,
      questionnaireItemParentMap,
      questionnaireLaunchContextMap,
      xFhirQueryResolver,
    )

  private val answerOptionsEvaluator: EnabledAnswerOptionsEvaluator =
    EnabledAnswerOptionsEvaluator(
      questionnaire,
      questionnaireResponse,
      questionnaireItemParentMap,
      questionnaireLaunchContextMap,
      xFhirQueryResolver,
      externalValueSetResolver,
    )

  private val questionnaireResponseItemValidator: QuestionnaireResponseItemValidator =
    QuestionnaireResponseItemValidator(expressionEvaluator)

  /**
   * Adds empty [QuestionnaireResponseItemComponent]s to `responseItems` so that each
   * [QuestionnaireItemComponent] in `questionnaireItems` has at least one corresponding
   * [QuestionnaireResponseItemComponent]. This is because user-provided [QuestionnaireResponse]
   * might not contain answers to unanswered or disabled questions. Note : this only applies to
   * [QuestionnaireItemComponent]s nested under a group.
   */
  private fun addMissingResponseItems(
    questionnaireItems: List<QuestionnaireItemComponent>,
    responseItems: MutableList<QuestionnaireResponseItemComponent>,
  ) {
    // To associate the linkId to QuestionnaireResponseItemComponent, do not use associateBy().
    // Instead, use groupBy().
    // This is because a questionnaire response may have multiple
    // QuestionnaireResponseItemComponents with the same linkId.
    val responseItemMap = responseItems.groupBy { it.linkId }

    // Clear the response item list, and then add the missing and existing response items back to
    // the list
    responseItems.clear()

    questionnaireItems.forEach {
      if (responseItemMap[it.linkId].isNullOrEmpty()) {
        responseItems.add(it.createQuestionnaireResponseItem())
      } else {
        if (it.type == Questionnaire.QuestionnaireItemType.GROUP && !it.repeats) {
          addMissingResponseItems(
            questionnaireItems = it.item,
            responseItems = responseItemMap[it.linkId]!!.single().item,
          )
        }
        responseItems.addAll(responseItemMap[it.linkId]!!)
      }
    }
  }

  /**
   * Returns current [QuestionnaireResponse] captured by the UI which includes answers of enabled
   * questions.
   */
  suspend fun getQuestionnaireResponse(): QuestionnaireResponse {
    return questionnaireResponse.copy().apply {
      // Use the view model's questionnaire and questionnaire response for calculating enabled items
      // because the calculation relies on references to the questionnaire response items.
      item =
        getEnabledResponseItems(
            this@QuestionnaireViewModel.questionnaire.item,
            questionnaireResponse.item,
          )
          .map { it.copy() }
      unpackRepeatedGroups(this@QuestionnaireViewModel.questionnaire)
    }
  }

  /** Clears all the answers from the questionnaire response by iterating through each item. */
  fun clearAllAnswers() {
    questionnaireResponse.allItems.forEach { it.answer = emptyList() }
    draftAnswerMap.clear()
    modifiedQuestionnaireResponseItemSet.clear()
    responseItemToAnswersMapForDisabledQuestionnaireItem.clear()
    modificationCount.update { it + 1 }
  }

  /**
   * Validates entire questionnaire and return the validation results. As a side effect, it triggers
   * the UI update to show errors in case there are any validation errors.
   */
  internal suspend fun validateQuestionnaireAndUpdateUI(): Map<String, List<ValidationResult>> =
    QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        questionnaireResponse,
        getApplication(),
        questionnaireItemParentMap,
        questionnaireLaunchContextMap,
        xFhirQueryResolver,
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

  internal fun setOnSubmitButtonClickListener(onClickAction: () -> Unit) {
    onSubmitButtonClickListener = onClickAction
  }

  internal fun setOnCancelButtonClickListener(onClickAction: () -> Unit) {
    onCancelButtonClickListener = onClickAction
  }

  internal fun setShowSubmitButtonFlag(showSubmitButton: Boolean) {
    this.shouldShowSubmitButton = showSubmitButton
  }

  internal fun setShowCancelButtonFlag(showCancelButton: Boolean) {
    this.shouldShowCancelButton = showCancelButton
  }

  /** [QuestionnaireState] to be displayed in the UI. */
  internal val questionnaireStateFlow: StateFlow<QuestionnaireState> =
    combine(modificationCount, currentPageIndexFlow, isInReviewModeFlow) { _, _, _ ->
        getQuestionnaireState()
      }
      .withIndex()
      .onEach {
        if (it.index == 0) {
          expressionEvaluator.detectExpressionCyclicDependency(questionnaire.item)
          questionnaire.item.flattened().forEach { qItem ->
            updateDependentQuestionnaireResponseItems(
              qItem,
              questionnaireResponse.allItems.find { qrItem -> qrItem.linkId == qItem.linkId },
            )
          }
          modificationCount.update { count -> count + 1 }
        }
      }
      .map { it.value }
      .stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        initialValue =
          QuestionnaireState(
            items = emptyList(),
            displayMode = DisplayMode.InitMode,
            bottomNavItems = emptyList(),
          ),
      )

  private suspend fun updateDependentQuestionnaireResponseItems(
    questionnaireItem: QuestionnaireItemComponent,
    updatedQuestionnaireResponseItem: QuestionnaireResponseItemComponent?,
  ) {
    expressionEvaluator
      .evaluateCalculatedExpressions(
        questionnaireItem,
        updatedQuestionnaireResponseItem,
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
                  val value = it.asExpectedType(questionnaireItem.type)
                  QuestionnaireResponseItemAnswerComponent().setValue(value)
                }
            }
          }
      }
  }

  private fun removeDisabledAnswers(
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
    disabledAnswers: List<QuestionnaireResponseItemAnswerComponent>,
  ) {
    val validAnswers =
      questionnaireResponseItem.answer.filterNot { ans ->
        disabledAnswers.any { ans.value.equalsDeep(it.value) }
      }
    viewModelScope.launch {
      answersChangedCallback(questionnaireItem, questionnaireResponseItem, validAnswers, null)
    }
  }

  /**
   * Traverses through the list of questionnaire items, the list of questionnaire response items and
   * the list of items in the questionnaire response answer list and populates
   * [questionnaireStateFlow] with matching pairs of questionnaire item and questionnaire response
   * item.
   *
   * The traverse is carried out in the two lists in tandem.
   */
  private suspend fun getQuestionnaireState(): QuestionnaireState {
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
          questionnaireResponseItemList[currentPageIndexFlow.value!!],
        )
      } else {
        getQuestionnaireAdapterItems(questionnaireItemList, questionnaireResponseItemList)
      }

    // Reviewing the questionnaire or the questionnaire is read-only
    if (isReadOnly || isInReviewModeFlow.value) {
      val showSubmitButton = !isReadOnly && shouldShowSubmitButton
      val bottomNavigationViewState =
        QuestionnaireNavigationUIState(
          navSubmit =
            if (showSubmitButton) {
              QuestionnaireNavigationViewUIState.Enabled(
                submitButtonText,
                onSubmitButtonClickListener,
              )
            } else {
              QuestionnaireNavigationViewUIState.Hidden
            },
          navCancel =
            if (!isReadOnly && shouldShowCancelButton) {
              QuestionnaireNavigationViewUIState.Enabled(
                onClickAction = onCancelButtonClickListener,
              )
            } else {
              QuestionnaireNavigationViewUIState.Hidden
            },
        )
      val bottomNavigationItems =
        listOf(QuestionnaireAdapterItem.Navigation(bottomNavigationViewState))

      return QuestionnaireState(
        items =
          if (shouldSetNavigationInLongScroll) {
            questionnaireItemViewItems + bottomNavigationItems
          } else {
            questionnaireItemViewItems
          },
        displayMode =
          DisplayMode.ReviewMode(
            showEditButton = !isReadOnly,
            showNavAsScroll = shouldSetNavigationInLongScroll,
          ),
        bottomNavItems =
          if (!shouldSetNavigationInLongScroll) bottomNavigationItems else emptyList(),
      )
    }

    val showReviewButton: Boolean
    val showSubmitButton: Boolean
    val showCancelButton: Boolean
    // Editing the questionnaire
    val questionnairePagination =
      if (!questionnaire.isPaginated) {
        showReviewButton = shouldEnableReviewPage && !isInReviewModeFlow.value
        showSubmitButton = shouldShowSubmitButton && !showReviewButton
        showCancelButton = shouldShowCancelButton && !showReviewButton
        QuestionnairePagination(
          false,
          emptyList(),
          -1,
        )
      } else {
        val hasNextPage =
          QuestionnairePagination(pages = pages!!, currentPageIndex = currentPageIndexFlow.value!!)
            .hasNextPage
        showReviewButton = shouldEnableReviewPage && !hasNextPage
        showSubmitButton = shouldShowSubmitButton && !showReviewButton && !hasNextPage
        showCancelButton = shouldShowCancelButton
        QuestionnairePagination(
          true,
          pages!!,
          currentPageIndexFlow.value!!,
        )
      }

    val bottomNavigationUiViewState =
      QuestionnaireNavigationUIState(
        navPrevious =
          when {
            questionnairePagination.isPaginated && questionnairePagination.hasPreviousPage -> {
              QuestionnaireNavigationViewUIState.Enabled { goToPreviousPage() }
            }
            else -> {
              QuestionnaireNavigationViewUIState.Hidden
            }
          },
        navNext =
          when {
            questionnairePagination.isPaginated && questionnairePagination.hasNextPage -> {
              QuestionnaireNavigationViewUIState.Enabled { goToNextPage() }
            }
            else -> {
              QuestionnaireNavigationViewUIState.Hidden
            }
          },
        navSubmit =
          if (showSubmitButton) {
            QuestionnaireNavigationViewUIState.Enabled(
              submitButtonText,
              onSubmitButtonClickListener,
            )
          } else {
            QuestionnaireNavigationViewUIState.Hidden
          },
        navReview =
          if (showReviewButton) {
            QuestionnaireNavigationViewUIState.Enabled { setReviewMode(true) }
          } else {
            QuestionnaireNavigationViewUIState.Hidden
          },
        navCancel =
          if (showCancelButton) {
            QuestionnaireNavigationViewUIState.Enabled(onClickAction = onCancelButtonClickListener)
          } else {
            QuestionnaireNavigationViewUIState.Hidden
          },
      )
    val bottomNavigationItems =
      listOf(QuestionnaireAdapterItem.Navigation(bottomNavigationUiViewState))

    return QuestionnaireState(
      items =
        if (shouldSetNavigationInLongScroll) {
          questionnaireItemViewItems + bottomNavigationItems
        } else {
          questionnaireItemViewItems
        },
      displayMode = DisplayMode.EditMode(questionnairePagination, shouldSetNavigationInLongScroll),
      bottomNavItems = if (!shouldSetNavigationInLongScroll) bottomNavigationItems else emptyList(),
    )
  }

  /**
   * Returns the list of [QuestionnaireViewItem]s generated for the questionnaire items and
   * questionnaire response items.
   */
  private suspend fun getQuestionnaireAdapterItems(
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
  private suspend fun getQuestionnaireAdapterItems(
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
  ): List<QuestionnaireAdapterItem> {
    // Hidden questions should not get QuestionnaireItemViewItem instances
    if (questionnaireItem.isHidden) return emptyList()
    val enabled =
      enablementEvaluator.evaluate(
        questionnaireItem,
        questionnaireResponseItem,
      )
    // Disabled questions should not get QuestionnaireItemViewItem instances
    if (!enabled) {
      cacheDisabledQuestionnaireItemAnswers(questionnaireResponseItem)
      return emptyList()
    }

    restoreFromDisabledQuestionnaireItemAnswersCache(questionnaireResponseItem)

    // Determine the validation result, which will be displayed on the item itself
    val validationResult =
      if (
        modifiedQuestionnaireResponseItemSet.contains(questionnaireResponseItem) ||
          forceValidation ||
          isInReviewModeFlow.value
      ) {
        questionnaireResponseItemValidator.validate(
          questionnaireItem,
          questionnaireResponseItem,
          this@QuestionnaireViewModel.getApplication(),
        )
      } else {
        NotValidated
      }

    // Set question text dynamically from CQL expression
    questionnaireItem.textElement.cqfExpression?.let { expression ->
      expressionEvaluator
        .evaluateExpressionValue(questionnaireItem, questionnaireResponseItem, expression)
        ?.primitiveValue()
        ?.let { questionnaireResponseItem.text = it }
    }

    val (enabledQuestionnaireAnswerOptions, disabledQuestionnaireResponseAnswers) =
      answerOptionsEvaluator.evaluate(
        questionnaireItem,
        questionnaireResponseItem,
      )
    if (disabledQuestionnaireResponseAnswers.isNotEmpty()) {
      removeDisabledAnswers(
        questionnaireItem,
        questionnaireResponseItem,
        disabledQuestionnaireResponseAnswers,
      )
    }

    val items = buildList {
      val itemHelpCard = questionnaireItem.item.firstOrNull { it.isHelpCode }
      val isHelpCard = itemHelpCard != null
      val isHelpCardOpen = openedHelpCardSet.contains(questionnaireResponseItem)
      // Add an item for the question itself

      val question =
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            questionnaireItem,
            questionnaireResponseItem,
            validationResult = validationResult,
            answersChangedCallback = answersChangedCallback,
            enabledAnswerOptions = enabledQuestionnaireAnswerOptions,
            minAnswerValue =
              questionnaireItem.minValueCqfCalculatedValueExpression?.let {
                expressionEvaluator.evaluateExpressionValue(
                  questionnaireItem,
                  questionnaireResponseItem,
                  it,
                )
              }
                ?: questionnaireItem.minValue,
            maxAnswerValue =
              questionnaireItem.maxValueCqfCalculatedValueExpression?.let {
                expressionEvaluator.evaluateExpressionValue(
                  questionnaireItem,
                  questionnaireResponseItem,
                  it,
                )
              }
                ?: questionnaireItem.maxValue,
            draftAnswer = draftAnswerMap[questionnaireResponseItem],
            enabledDisplayItems =
              questionnaireItem.item.filter {
                it.isDisplayItem &&
                  enablementEvaluator.evaluate(
                    it,
                    questionnaireResponseItem,
                  )
              },
            questionViewTextConfiguration =
              QuestionTextConfiguration(
                showAsterisk = showAsterisk,
                showRequiredText = showRequiredText,
                showOptionalText = showOptionalText,
              ),
            isHelpCardOpen = isHelpCard && isHelpCardOpen,
            helpCardStateChangedCallback = helpCardStateChangedCallback,
          ),
        )
      add(question)

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
          if (!questionnaireItem.isRepeatedGroup) {
            add(questionnaireResponseItem.item)
          }
          // Case 2 and 3
          addAll(questionnaireResponseItem.answer.map { it.item })
        }
        .forEachIndexed { index, nestedResponseItemList ->
          if (questionnaireItem.isRepeatedGroup) {
            // Case 3
            add(
              QuestionnaireAdapterItem.RepeatedGroupHeader(
                index = index,
                onDeleteClicked = { viewModelScope.launch { question.item.removeAnswerAt(index) } },
                responses = nestedResponseItemList,
                title = question.item.questionText?.toString() ?: "",
              ),
            )
          }
          addAll(
            getQuestionnaireAdapterItems(
              // If nested display item is identified as instructions or flyover, then do not create
              // questionnaire state for it.
              questionnaireItemList = questionnaireItem.item.filterNot { it.isDisplayItem },
              questionnaireResponseItemList = nestedResponseItemList,
            ),
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
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
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
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
  ) {
    if (responseItemToAnswersMapForDisabledQuestionnaireItem.contains(questionnaireResponseItem)) {
      questionnaireResponseItem.answer =
        responseItemToAnswersMapForDisabledQuestionnaireItem.remove(questionnaireResponseItem)
    }
  }

  private suspend fun getEnabledResponseItems(
    questionnaireItemList: List<QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponseItemComponent>,
  ): List<QuestionnaireResponseItemComponent> {
    val responseItemKeys = questionnaireResponseItemList.map { it.linkId }
    val result = mutableListOf<QuestionnaireResponseItemComponent>()

    for ((questionnaireItem, questionnaireResponseItem) in
      questionnaireItemList.zip(questionnaireResponseItemList)) {
      if (
        responseItemKeys.contains(questionnaireItem.linkId) &&
          enablementEvaluator.evaluate(questionnaireItem, questionnaireResponseItem)
      ) {
        questionnaireResponseItem.apply {
          if (text.isNullOrBlank()) {
            text = questionnaireItem.localizedTextSpanned?.toString()
          }
          // Nested group items
          item = getEnabledResponseItems(questionnaireItem.item, questionnaireResponseItem.item)
          // Nested question items
          answer.forEach { it.item = getEnabledResponseItems(questionnaireItem.item, it.item) }
        }
        result.add(questionnaireResponseItem)
      }
    }
    return result
  }

  /**
   * Gets a list of [QuestionnairePage]s for a paginated questionnaire, or `null` if the
   * questionnaire is not paginated.
   */
  private suspend fun getQuestionnairePages(): List<QuestionnairePage>? =
    if (questionnaire.isPaginated) {
      questionnaire.item.zip(questionnaireResponse.item).mapIndexed {
        index,
        (questionnaireItem, questionnaireResponseItem),
        ->
        QuestionnairePage(
          index,
          enablementEvaluator.evaluate(
            questionnaireItem,
            questionnaireResponseItem,
          ),
          questionnaireItem.isHidden,
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
    if (
      currentPageItems.filterIsInstance<QuestionnaireAdapterItem.Question>().any {
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

    if (
      currentPageItems.filterIsInstance<QuestionnaireAdapterItem.Question>().all {
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
  val bottomNavItems: List<QuestionnaireAdapterItem.Navigation>,
)

internal sealed class DisplayMode {
  class EditMode(val pagination: QuestionnairePagination, val showNavAsScroll: Boolean) :
    DisplayMode()

  data class ReviewMode(
    val showEditButton: Boolean,
    val showNavAsScroll: Boolean,
  ) : DisplayMode()

  // Sentinel displayMode that's used in setting the initial default QuestionnaireState
  object InitMode : DisplayMode()
}

/**
 * Pagination information of the questionnaire. This is used for the UI to render pagination
 * controls. Includes information for each page and the current page index.
 */
internal data class QuestionnairePagination(
  val isPaginated: Boolean = false,
  val pages: List<QuestionnairePage>,
  val currentPageIndex: Int,
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
