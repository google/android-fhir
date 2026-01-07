/*
 * Copyright 2023-2026 Google LLC
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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.submit_questionnaire
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.google.android.fhir.datacapture.enablement.EnablementEvaluator
import com.google.android.fhir.datacapture.expressions.EnabledAnswerOptionsEvaluator
import com.google.android.fhir.datacapture.extensions.EXTENSION_LAST_LAUNCHED_TIMESTAMP
import com.google.android.fhir.datacapture.extensions.EntryMode
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.allItems
import com.google.android.fhir.datacapture.extensions.calculatedExpression
import com.google.android.fhir.datacapture.extensions.copyNestedItemsToChildlessAnswers
import com.google.android.fhir.datacapture.extensions.cqfExpression
import com.google.android.fhir.datacapture.extensions.createQuestionnaireResponseItem
import com.google.android.fhir.datacapture.extensions.entryMode
import com.google.android.fhir.datacapture.extensions.filterByCodeInNameExtension
import com.google.android.fhir.datacapture.extensions.forEachItemPair
import com.google.android.fhir.datacapture.extensions.hasDifferentAnswerSet
import com.google.android.fhir.datacapture.extensions.isDisplayItem
import com.google.android.fhir.datacapture.extensions.isHelpCode
import com.google.android.fhir.datacapture.extensions.isHidden
import com.google.android.fhir.datacapture.extensions.isPaginated
import com.google.android.fhir.datacapture.extensions.isRepeatedGroup
import com.google.android.fhir.datacapture.extensions.localizedTextAnnotatedString
import com.google.android.fhir.datacapture.extensions.maxValueCqfCalculatedValueExpression
import com.google.android.fhir.datacapture.extensions.minValueCqfCalculatedValueExpression
import com.google.android.fhir.datacapture.extensions.packRepeatedGroups
import com.google.android.fhir.datacapture.extensions.questionnaireLaunchContexts
import com.google.android.fhir.datacapture.extensions.shouldHaveNestedItemsUnderAnswers
import com.google.android.fhir.datacapture.extensions.unpackRepeatedGroups
import com.google.android.fhir.datacapture.extensions.validateLaunchContextExtensions
import com.google.android.fhir.datacapture.extensions.zipByLinkId
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator
import com.google.android.fhir.datacapture.fhirpath.convertToString
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseItemValidator
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator.checkQuestionnaireResponse
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.Canonical
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Date
import com.google.fhir.model.r4.DateTime
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Quantity
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Reference
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.Time
import com.google.fhir.model.r4.Uri
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalTime::class)
internal class QuestionnaireViewModel(state: Map<String, Any>) : ViewModel() {
  private val jsonR4 = FhirR4Json()
  private val xFhirQueryResolver: XFhirQueryResolver? by lazy {
    DataCapture.getConfiguration().xFhirQueryResolver
  }
  private val externalValueSetResolver: ExternalAnswerValueSetResolver? by lazy {
    DataCapture.getConfiguration().valueSetResolverExternal
  }

  /** The current questionnaire as questions are being answered. */
  internal val questionnaire: Questionnaire

  init {
    questionnaire =
      when {
        state.contains(EXTRA_QUESTIONNAIRE_JSON_URI) -> {
          if (state.contains(EXTRA_QUESTIONNAIRE_JSON_STRING)) {
            Logger.w(
              "Both EXTRA_QUESTIONNAIRE_JSON_URI & EXTRA_QUESTIONNAIRE_JSON_STRING are provided. " +
                "EXTRA_QUESTIONNAIRE_JSON_URI takes precedence.",
            )
          }
          val uriPath: String = state[EXTRA_QUESTIONNAIRE_JSON_URI] as String
          jsonR4.decodeFromString(readFileContent(uriPath)) as Questionnaire
        }
        state.contains(EXTRA_QUESTIONNAIRE_JSON_STRING) -> {
          val questionnaireJson: String = state[EXTRA_QUESTIONNAIRE_JSON_STRING] as String
          jsonR4.decodeFromString(questionnaireJson) as Questionnaire
        }
        else ->
          error(
            "Neither EXTRA_QUESTIONNAIRE_JSON_URI nor EXTRA_QUESTIONNAIRE_JSON_STRING is supplied.",
          )
      }
  }

  /** The current questionnaire response as questions are being answered. */
  private val questionnaireResponse: MutableStateFlow<QuestionnaireResponse> =
    MutableStateFlow(
      QuestionnaireResponse.Builder(
          Enumeration(),
        )
        .build(),
    )

  init {
    when {
      state.contains(EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI) -> {
        if (state.contains(EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING)) {
          Logger.w(
            "Both EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI & EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING are provided. " +
              "EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI takes precedence.",
          )
        }

        questionnaireResponse.value =
          jsonR4.decodeFromString(
            readFileContent(state[EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI]!! as String),
          ) as QuestionnaireResponse

        addMissingResponseItems(
          questionnaire.item,
          questionnaireResponse.value.item.toMutableList(),
        )
        checkQuestionnaireResponse(questionnaire, questionnaireResponse.value)
      }
      state.contains(EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING) -> {
        val questionnaireResponseJson: String =
          state[EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] as String
        questionnaireResponse.value =
          jsonR4.decodeFromString(questionnaireResponseJson) as QuestionnaireResponse
        addMissingResponseItems(
          questionnaire.item,
          questionnaireResponse.value.item.toMutableList(),
        )
        checkQuestionnaireResponse(questionnaire, questionnaireResponse.value)
      }
      else -> {
        questionnaireResponse.value =
          QuestionnaireResponse.Builder(
              status =
                Enumeration(value = QuestionnaireResponse.QuestionnaireResponseStatus.In_Progress),
            )
            .apply {
              questionnaire =
                Canonical.Builder().apply {
                  value = this@QuestionnaireViewModel.questionnaire.url?.value
                }

              val dateTime =
                DateTime(
                  value =
                    FhirDateTime.DateTime(
                      dateTime =
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                      utcOffset = UtcOffset.ZERO,
                    ),
                )
              // Add extension for questionnaire launch time stamp
              val timeStampExtension =
                extension.firstOrNull { it.url == EXTENSION_LAST_LAUNCHED_TIMESTAMP }
              timeStampExtension?.apply { value?.let { Extension.Value.DateTime(dateTime) } }
                ?: extension.add(
                  Extension.Builder(EXTENSION_LAST_LAUNCHED_TIMESTAMP).apply {
                    value = Extension.Value.DateTime(dateTime)
                  },
                )
            }
            .also { builder ->
              // Retain the hierarchy and order of items within the questionnaire as specified in
              // the standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
              builder.item.addAll(
                questionnaire.item
                  .filterNot { it.isRepeatedGroup }
                  .map { it.createQuestionnaireResponseItem() },
              )

              builder.packRepeatedGroups(questionnaire)
            }
            .build()
      }
    }
  }

  /**
   * The launch context allows information to be passed into questionnaire based on the context in
   * which the questionnaire is being evaluated. For example, what patient, what encounter, what
   * user, etc. is "in context" at the time the questionnaire response is being completed:
   * https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-launchContext.html
   */
  @Suppress("UNCHECKED_CAST")
  private val questionnaireLaunchContextMap: Map<String, Resource>? =
    if (state.contains(EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP)) {

      val launchContextMapString: Map<String, String> =
        state[EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP] as Map<String, String>

      val launchContextMapResource =
        launchContextMapString.mapValues { jsonR4.decodeFromString(it.value) }
      questionnaire.questionnaireLaunchContexts?.let { launchContextExtensions ->
        validateLaunchContextExtensions(launchContextExtensions)
        filterByCodeInNameExtension(launchContextMapResource, launchContextExtensions)
      }
    } else {
      null
    }

  /** The map from each item in the [Questionnaire] to its parent. */
  private var questionnaireItemParentMap: Map<Questionnaire.Item, Questionnaire.Item>

  init {
    /** Adds each child-parent pair in the [Questionnaire] to the parent map. */
    fun buildParentList(
      item: Questionnaire.Item,
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
  private val isReadOnly = state[EXTRA_READ_ONLY] as Boolean? ?: false

  /** Flag to support fragment for review-feature */
  private val shouldEnableReviewPage = state[EXTRA_ENABLE_REVIEW_PAGE] as Boolean? ?: false

  /** Flag to open fragment first in data-collection or review-mode */
  private val shouldShowReviewPageFirst =
    shouldEnableReviewPage && state[EXTRA_SHOW_REVIEW_PAGE_FIRST] as Boolean? ?: false

  /** Flag to show/hide submit button. Default is true. */
  private var shouldShowSubmitButton = state[EXTRA_SHOW_SUBMIT_BUTTON] as Boolean? ?: true

  /** Flag to show questionnaire page as default/long scroll. Default is false. */
  private var shouldSetNavigationInLongScroll =
    state[EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL] as Boolean? ?: false

  private var submitButtonText = state[EXTRA_SUBMIT_BUTTON_TEXT] as String? ?: ""

  private var onSubmitButtonClickListener: () -> Unit = {}

  private var onCancelButtonClickListener: () -> Unit = {}

  /** Flag to show/hide cancel button. Default is false */
  private var shouldShowCancelButton = state[EXTRA_SHOW_CANCEL_BUTTON] as Boolean? ?: false

  /** Flag to control whether asterisk text is shown for required questions. */
  private val showAsterisk = state[EXTRA_SHOW_ASTERISK_TEXT] as Boolean? ?: false

  /** Flag to control whether asterisk text is shown for required questions. */
  private val showRequiredText = state[EXTRA_SHOW_REQUIRED_TEXT] as Boolean? ?: true

  /** Flag to control whether optional text is shown. */
  private val showOptionalText = state[EXTRA_SHOW_OPTIONAL_TEXT] as Boolean? ?: false

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
  private val openedHelpCardSet: MutableSet<QuestionnaireResponse.Item> = mutableSetOf()

  /** Callback to save the help card state. */
  private val helpCardStateChangedCallback: (Boolean, QuestionnaireResponse.Item) -> Unit =
    { shouldBeVisible, questionnaireResponseItem ->
      if (shouldBeVisible) {
        openedHelpCardSet.add(questionnaireResponseItem)
      } else {
        openedHelpCardSet.remove(questionnaireResponseItem)
      }
    }

  /**
   * Contains [QuestionnaireResponse.Item]s that have been modified by the user.
   * [QuestionnaireResponse.Item]s that have not been modified by the user will not be validated.
   * This is to avoid spamming the user with a sea of validation errors when the questionnaire is
   * loaded initially.
   */
  private val modifiedQuestionnaireResponseItemSet = mutableSetOf<QuestionnaireResponse.Item>()

  private lateinit var currentPageItems: List<QuestionnaireAdapterItem>

  /**
   * Map of [QuestionnaireResponse.Item.Answer] for [Questionnaire.Item]s that are disabled now. The
   * answers will be used to pre-populate the [QuestionnaireResponse.Item] once the item is enabled
   * again.
   */
  private val responseItemToAnswersMapForDisabledQuestionnaireItem =
    mutableMapOf<
      QuestionnaireResponse.Item,
      List<QuestionnaireResponse.Item.Answer>,
    >()

  /**
   * Map from [QuestionnaireResponse.Item] to draft answers, e.g "02/02" for date with missing year
   * part.
   *
   * This is used to maintain draft answers on the screen especially when the widgets are being
   * recycled as a result of scrolling. Draft answers cannot be saved in [QuestionnaireResponse]
   * because they might be incomplete and unparsable. Without this map, incomplete and unparsable
   * answers would be lost.
   *
   * When the draft answer becomes valid, its entry in the map is removed, e.g, "02/02/2023" is
   * valid answer and should not be in this map.
   */
  private val draftAnswerMap = mutableMapOf<QuestionnaireResponse.Item, Any>()

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
   * - the reference to the [Questionnaire.Item] in the [Questionnaire]
   * - the reference to the [QuestionnaireResponse.Item] in the [QuestionnaireResponse]
   * - a [List] of [QuestionnaireResponse.Item.Answer] which are the new answers to the question.
   * - partial answer, the entered input is not a valid answer
   */
  private val answersChangedCallback:
    suspend (
      Questionnaire.Item,
      QuestionnaireResponse.Item,
      List<QuestionnaireResponse.Item.Answer>,
      Any?,
    ) -> Unit =
    { questionnaireItem, questionnaireResponseItem, answers, draftAnswer ->
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

      (questionnaireResponseItem.answer as? MutableList)?.let { answerList ->
        answerList.clear()
        answerList.addAll(answers)
      }

      if (questionnaireItem.shouldHaveNestedItemsUnderAnswers) {
        questionnaireResponseItem.toBuilder().copyNestedItemsToChildlessAnswers(questionnaireItem)

        // If nested items are added to the answer, the enablement evaluator needs to be
        // reinitialized in order for it to rebuild the pre-order map and parent map of
        // questionnaire response items to reflect the new structure of the questionnaire response
        // to correctly calculate calculate enable when statements.
        enablementEvaluator =
          EnablementEvaluator(
            questionnaire,
            questionnaireResponse.value,
            questionnaireItemParentMap,
            questionnaireLaunchContextMap,
            xFhirQueryResolver,
          )
      }
      modifiedQuestionnaireResponseItemSet.add(questionnaireResponseItem)

      updateAnswerWithAffectedCalculatedExpression(questionnaireItem)

      modificationCount.update { it + 1 }
    }

  private val expressionEvaluator: ExpressionEvaluator =
    ExpressionEvaluator(
      questionnaire,
      questionnaireResponse.value,
      questionnaireItemParentMap,
      questionnaireLaunchContextMap,
      xFhirQueryResolver,
    )

  private var enablementEvaluator: EnablementEvaluator =
    EnablementEvaluator(
      questionnaire,
      questionnaireResponse.value,
      questionnaireItemParentMap,
      questionnaireLaunchContextMap,
      xFhirQueryResolver,
    )

  private val answerOptionsEvaluator: EnabledAnswerOptionsEvaluator =
    EnabledAnswerOptionsEvaluator(
      questionnaire,
      questionnaireResponse.value,
      questionnaireItemParentMap,
      questionnaireLaunchContextMap,
      xFhirQueryResolver,
      externalValueSetResolver,
    )

  private val questionnaireResponseItemValidator: QuestionnaireResponseItemValidator =
    QuestionnaireResponseItemValidator(expressionEvaluator)

  /**
   * Adds empty [QuestionnaireResponse.Item]s to `responseItems` so that each [Questionnaire.Item]
   * in `questionnaireItems` has at least one corresponding [QuestionnaireResponse.Item]. This is
   * because user-provided [QuestionnaireResponse] might not contain answers to unanswered or
   * disabled questions. This function should only be used for unpacked questionnaire.
   */
  @VisibleForTesting
  internal fun addMissingResponseItems(
    questionnaireItems: List<Questionnaire.Item>,
    responseItems: MutableList<QuestionnaireResponse.Item>,
  ) {
    // To associate the linkId to QuestionnaireResponse.Item, do not use associateBy().
    // Instead, use groupBy().
    // This is because a questionnaire response may have multiple
    // QuestionnaireResponseItemComponents with the same linkId.
    val responseItemMap = responseItems.groupBy { it.linkId }

    // Clear the response item list, and then add the missing and existing response items back to
    // the list
    responseItems.clear()

    questionnaireItems.forEach {
      if (responseItemMap[it.linkId].isNullOrEmpty()) {
        responseItems.add(it.createQuestionnaireResponseItem().build())
      } else {
        if (
          it.type.value == Questionnaire.QuestionnaireItemType.Group && it.repeats?.value != true
        ) {
          addMissingResponseItems(
            questionnaireItems = it.item,
            responseItems = responseItemMap[it.linkId]!!.single().item.toMutableList(),
          )
        }
        if (
          it.type.value == Questionnaire.QuestionnaireItemType.Group && it.repeats?.value == true
        ) {
          responseItemMap[it.linkId]!!.forEach { rItem ->
            addMissingResponseItems(
              questionnaireItems = it.item,
              responseItems = rItem.item.toMutableList(),
            )
          }
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
    questionnaireResponse.value =
      questionnaireResponse.value
        .toBuilder()
        .apply {
          // Use the view model's questionnaire and questionnaire response for calculating enabled
          // items
          // because the calculation relies on references to the questionnaire response items.
          item =
            getEnabledResponseItems(
                this@QuestionnaireViewModel.questionnaire.item,
                questionnaireResponse.value.item,
              )
              .toMutableList()

          unpackRepeatedGroups(this@QuestionnaireViewModel.questionnaire)
          // Use authored as a submission time stamp
          authored =
            DateTime.Builder().apply {
              value =
                FhirDateTime.DateTime(
                  dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                  utcOffset = UtcOffset.ZERO,
                )
            }
        }
        .build()
    return questionnaireResponse.value
  }

  /** Clears all the answers from the questionnaire response by iterating through each item. */
  fun clearAllAnswers() {
    questionnaireResponse.value =
      questionnaireResponse.value
        .toBuilder()
        .apply { this.allItems.map { it.toBuilder().answer.apply { this.clear() } } }
        .build()
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
        questionnaireResponse.value,
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
        Logger.w("Previous questions and submitted answers cannot be viewed or edited.")
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
          initializeCalculatedExpressions()
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
            bottomNavItem = null,
          ),
      )

  /**
   * Travers all [com.google.android.fhir.datacapture.extensions.calculatedExpression] within a
   * [Questionnaire] and evaluate them.
   */
  private suspend fun initializeCalculatedExpressions() {
    expressionEvaluator.detectExpressionCyclicDependency(questionnaire.item)
    questionnaire.forEachItemPair(questionnaireResponse.value) {
      questionnaireItem,
      questionnaireResponseItem,
      ->
      if (questionnaireItem.calculatedExpression != null) {
        updateAnswerWithCalculatedExpression(questionnaireItem, questionnaireResponseItem)
      }
    }
  }

  /**
   * Updates all items that has
   * [com.google.android.fhir.datacapture.extensions.calculatedExpression] that reference the given
   * [questionnaireItem] within their calculations.
   *
   * If item X has a [com.google.android.fhir.datacapture.extensions.calculatedExpression], but that
   * item does not reference the given [questionnaireItem], then item X should not be calculated.
   *
   * Only items that have not been modified by the user will be updated to prevent any event loops.
   *
   * @param questionnaireItem The questionnaire item referenced by other items through
   *   [com.google.android.fhir.datacapture.extensions.calculatedExpression].
   */
  private suspend fun updateAnswerWithAffectedCalculatedExpression(
    questionnaireItem: Questionnaire.Item,
  ) {
    expressionEvaluator
      .evaluateAllAffectedCalculatedExpressions(
        questionnaireItem,
      )
      .forEach { (questionnaireItem, calculatedAnswers) ->
        // update all response item with updated values
        questionnaireResponse.value
          .toBuilder()
          .allItems
          // Item answer should not be modified and touched by user;
          // https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-calculatedExpression.html
          .filter {
            it.linkId == questionnaireItem.linkId &&
              !modifiedQuestionnaireResponseItemSet.contains(it)
          }
          .forEach { questionnaireResponseItem ->
            // update and notify only if new answer has changed to prevent any event loop
            if (questionnaireResponseItem.answer.hasDifferentAnswerSet(calculatedAnswers)) {
              questionnaireResponseItem.toBuilder().apply {
                answer =
                  calculatedAnswers.mapNotNullTo(mutableListOf()) {
                    when (it) {
                      is FhirR4Boolean -> QuestionnaireResponse.Item.Answer.Value.Boolean(it)
                      is Decimal -> QuestionnaireResponse.Item.Answer.Value.Decimal(it)
                      is Integer -> QuestionnaireResponse.Item.Answer.Value.Integer(it)
                      is FhirR4String -> QuestionnaireResponse.Item.Answer.Value.String(it)
                      is Coding -> QuestionnaireResponse.Item.Answer.Value.Coding(it)
                      is Reference -> QuestionnaireResponse.Item.Answer.Value.Reference(it)
                      is Date -> QuestionnaireResponse.Item.Answer.Value.Date(it)
                      is DateTime -> QuestionnaireResponse.Item.Answer.Value.DateTime(it)
                      is Time -> QuestionnaireResponse.Item.Answer.Value.Time(it)
                      is Uri -> QuestionnaireResponse.Item.Answer.Value.Uri(it)
                      is Attachment -> QuestionnaireResponse.Item.Answer.Value.Attachment(it)
                      is Quantity -> QuestionnaireResponse.Item.Answer.Value.Quantity(it)
                      else -> null
                    }?.let { item ->
                      QuestionnaireResponse.Item.Answer.Builder().apply { value = item }
                    }
                  }
              }
            }
          }
      }
  }

  /**
   * Updates the answer(s) in the questionnaire response item with the evaluation result of the
   * calculated expression if
   * - there is a calculated expression in the questionnaire item, and
   * - there is no user provided answer to the questionnaire response item (user input should always
   *   take precedence over calculated answers).
   *
   * Do nothing, otherwise.
   */
  private suspend fun updateAnswerWithCalculatedExpression(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItem: QuestionnaireResponse.Item,
  ) {
    if (questionnaireItem.calculatedExpression == null) return
    if (modifiedQuestionnaireResponseItemSet.contains(questionnaireResponseItem)) return
    val answers = expressionEvaluator.evaluateCalculatedExpression(questionnaireItem)
    if (answers.isEmpty()) return
    if (questionnaireResponseItem.answer.hasDifferentAnswerSet(answers)) {
      questionnaireResponseItem.toBuilder().apply {
        answer =
          answers.mapNotNullTo(mutableListOf()) {
            when (it) {
              is FhirR4Boolean -> QuestionnaireResponse.Item.Answer.Value.Boolean(it)
              is Decimal -> QuestionnaireResponse.Item.Answer.Value.Decimal(it)
              is Integer -> QuestionnaireResponse.Item.Answer.Value.Integer(it)
              is FhirR4String -> QuestionnaireResponse.Item.Answer.Value.String(it)
              is Coding -> QuestionnaireResponse.Item.Answer.Value.Coding(it)
              is Reference -> QuestionnaireResponse.Item.Answer.Value.Reference(it)
              is Date -> QuestionnaireResponse.Item.Answer.Value.Date(it)
              is DateTime -> QuestionnaireResponse.Item.Answer.Value.DateTime(it)
              is Time -> QuestionnaireResponse.Item.Answer.Value.Time(it)
              is Uri -> QuestionnaireResponse.Item.Answer.Value.Uri(it)
              is Attachment -> QuestionnaireResponse.Item.Answer.Value.Attachment(it)
              is Quantity -> QuestionnaireResponse.Item.Answer.Value.Quantity(it)
              else -> null
            }?.let { item -> QuestionnaireResponse.Item.Answer.Builder().apply { value = item } }
          }
      }
    }
  }

  private fun removeDisabledAnswers(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItem: QuestionnaireResponse.Item,
    disabledAnswers: List<QuestionnaireResponse.Item.Answer>,
  ) {
    val validAnswers =
      questionnaireResponseItem.answer.filterNot { ans ->
        disabledAnswers.any { ans.value === it.value }
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
    val questionnaireResponseItemList = questionnaireResponse.value.item

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
                submitButtonText.ifEmpty { getString(Res.string.submit_questionnaire) },
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
      val bottomNavigation = QuestionnaireAdapterItem.Navigation(bottomNavigationViewState)

      return QuestionnaireState(
        items =
          if (shouldSetNavigationInLongScroll) {
            questionnaireItemViewItems + bottomNavigation
          } else {
            questionnaireItemViewItems
          },
        displayMode =
          DisplayMode.ReviewMode(
            showEditButton = !isReadOnly,
            showNavAsScroll = shouldSetNavigationInLongScroll,
          ),
        bottomNavItem = if (!shouldSetNavigationInLongScroll) bottomNavigation else null,
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
              submitButtonText.ifEmpty { getString(Res.string.submit_questionnaire) },
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
    val bottomNavigation = QuestionnaireAdapterItem.Navigation(bottomNavigationUiViewState)

    return QuestionnaireState(
      items =
        if (shouldSetNavigationInLongScroll) {
          questionnaireItemViewItems + bottomNavigation
        } else {
          questionnaireItemViewItems
        },
      displayMode = DisplayMode.EditMode(questionnairePagination, shouldSetNavigationInLongScroll),
      bottomNavItem = if (!shouldSetNavigationInLongScroll) bottomNavigation else null,
    )
  }

  /**
   * Returns the list of [QuestionnaireViewItem]s generated for the questionnaire items and
   * questionnaire response items.
   */
  private suspend fun getQuestionnaireAdapterItems(
    questionnaireItemList: List<Questionnaire.Item>,
    questionnaireResponseItemList: List<QuestionnaireResponse.Item>,
    parentIdPrefix: String = "",
  ): List<QuestionnaireAdapterItem> {
    return questionnaireItemList
      .zipByLinkId(questionnaireResponseItemList) { questionnaireItem, questionnaireResponseItem ->
        getQuestionnaireAdapterItems(questionnaireItem, questionnaireResponseItem, parentIdPrefix)
      }
      .flatten()
  }

  /**
   * Returns the list of [QuestionnaireViewItem]s generated for the questionnaire item and
   * questionnaire response item.
   */
  private suspend fun getQuestionnaireAdapterItems(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItem: QuestionnaireResponse.Item,
    parentIdPrefix: String = "",
  ): List<QuestionnaireAdapterItem> {
    // Hidden questions should not get QuestionnaireItemViewItem instances
    if (questionnaireItem.isHidden) return emptyList()
    val enabled =
      enablementEvaluator.evaluate(
        questionnaireItem,
        questionnaireResponseItem,
      )
    //    // Disabled questions should not get QuestionnaireItemViewItem instances
    if (!enabled) {
      cacheDisabledQuestionnaireItemAnswers(questionnaireResponseItem)
      return emptyList()
    }

    restoreFromDisabledQuestionnaireItemAnswersCache(questionnaireResponseItem)

    // Determine the validation result, which will be displayed on the item itself
    val validationResult =
      if (
        modifiedQuestionnaireResponseItemSet.contains(questionnaireResponseItem) ||
          isInReviewModeFlow.value
      ) {
        questionnaireResponseItemValidator.validate(
          questionnaireItem,
          questionnaireResponseItem,
        )
      } else {
        NotValidated
      }

    // Set question text dynamically from CQL expression
    questionnaireItem.text?.cqfExpression?.let { expression ->
      val result = expressionEvaluator.evaluateExpressionValue(expression) ?: emptyList()

      questionnaireResponseItem.text?.toBuilder()?.apply { this.value = convertToString(result) }
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
                  expressionEvaluator.evaluateExpressionValue(it)
                },
              maxAnswerValue =
                questionnaireItem.maxValueCqfCalculatedValueExpression?.let {
                  expressionEvaluator.evaluateExpressionValue(it)
                },
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
          .apply {
            if (parentIdPrefix.isNotEmpty()) {
              id = "${parentIdPrefix}${questionnaireItem.linkId}"
            }
          }
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

      // Case 1: Non-repeated group - process nested items directly with current prefix
      if (!questionnaireItem.isRepeatedGroup && questionnaireResponseItem.item.isNotEmpty()) {
        addAll(
          getQuestionnaireAdapterItems(
            questionnaireItemList = questionnaireItem.item.filterNot { it.isDisplayItem },
            questionnaireResponseItemList = questionnaireResponseItem.item,
            parentIdPrefix = parentIdPrefix,
          ),
        )
      }

      // Case 2 and 3: Questions nested under answers (for questions with nested items or repeated
      // groups)
      questionnaireResponseItem.answer
        .map { it.item }
        .forEachIndexed { index, nestedResponseItemList ->
          val currentIdPrefix =
            if (!questionnaireItem.isRepeatedGroup) {
              // Case 2: Questions nested under a question (not a repeated group)
              if (parentIdPrefix.isEmpty()) {
                "${index}_${question.item.questionnaireItem.linkId}_"
              } else {
                "${parentIdPrefix}${index}_${question.item.questionnaireItem.linkId}_"
              }
            } else {
              // Case 3: Build hierarchical ID prefix for nested repeated groups
              "${parentIdPrefix}${index}_${question.item.questionnaireItem.linkId}_"
            }

          if (questionnaireItem.isRepeatedGroup) {
            // Case 3
            add(
              QuestionnaireAdapterItem.RepeatedGroupHeader(
                id = "${parentIdPrefix}${index}_${question.item.questionnaireItem.linkId}",
                index = index,
                onDeleteClicked = { viewModelScope.launch { question.item.removeAnswerAt(index) } },
                responses = nestedResponseItemList,
                title = question.item.questionText?.toString() ?: "",
              ),
            )
          }
          addAll(
            getQuestionnaireAdapterItems(
              // If nested display item is identified as instructions or flyover, then do not
              // create questionnaire state for it.
              questionnaireItemList = questionnaireItem.item.filterNot { it.isDisplayItem },
              questionnaireResponseItemList = nestedResponseItemList,
              parentIdPrefix = currentIdPrefix,
            ),
          )
        }

      if (questionnaireItem.isRepeatedGroup) {
        add(
          QuestionnaireAdapterItem.RepeatedGroupAddButton(
            id = "${parentIdPrefix}${question.item.questionnaireItem.linkId}_add_btn",
            item = question.item,
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
    questionnaireResponseItem: QuestionnaireResponse.Item,
  ) {
    if (questionnaireResponseItem.answer.isNotEmpty()) {
      responseItemToAnswersMapForDisabledQuestionnaireItem[questionnaireResponseItem] =
        questionnaireResponseItem.answer
      questionnaireResponseItem.toBuilder().apply { answer = mutableListOf() }
    }
  }

  /**
   * If the questionnaire item was previously disabled, check the cache to restore previous answers.
   */
  private fun restoreFromDisabledQuestionnaireItemAnswersCache(
    questionnaireResponseItem: QuestionnaireResponse.Item,
  ) {
    if (responseItemToAnswersMapForDisabledQuestionnaireItem.contains(questionnaireResponseItem)) {
      questionnaireResponseItem.toBuilder().apply {
        answer =
          responseItemToAnswersMapForDisabledQuestionnaireItem
            .remove(questionnaireResponseItem)
            ?.map { it.toBuilder() }
            ?.toMutableList()
            ?: mutableListOf()
      }
    }
  }

  private suspend fun getEnabledResponseItems(
    questionnaireItemList: List<Questionnaire.Item>,
    questionnaireResponseItemList: List<QuestionnaireResponse.Item>,
  ): List<QuestionnaireResponse.Item.Builder> {
    val responseItemKeys = questionnaireResponseItemList.map { it.linkId }
    val result = mutableListOf<QuestionnaireResponse.Item.Builder>()

    for ((questionnaireItem, questionnaireResponseItem) in
      questionnaireItemList.zip(questionnaireResponseItemList)) {
      if (
        responseItemKeys.contains(questionnaireItem.linkId) &&
          enablementEvaluator.evaluate(questionnaireItem, questionnaireResponseItem)
      ) {
        questionnaireResponseItem.toBuilder().apply {
          if (text?.value.isNullOrBlank()) {
            text =
              text.apply {
                this?.value = questionnaireItem.localizedTextAnnotatedString?.toString()
              }
          }
          // Nested group items
          item =
            getEnabledResponseItems(
                questionnaireItem.item,
                questionnaireResponseItem.item,
              )
              .toMutableList()

          // Nested question items
          answer.forEach {
            it.item =
              getEnabledResponseItems(
                  questionnaireItem.item,
                  it.item.map { itemBuilder -> itemBuilder.build() },
                )
                .toMutableList()
          }
        }
        result.add(questionnaireResponseItem.toBuilder())
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
      questionnaire.item.zip(questionnaireResponse.value.item).mapIndexed {
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
      // Add all items on the current page to modifiedQuestionnaireResponseItemSet.
      // This will ensure that all fields are validated even when they're not filled by the user
      currentPageItems.filterIsInstance<QuestionnaireAdapterItem.Question>().forEach {
        modifiedQuestionnaireResponseItemSet.add(it.item.getQuestionnaireResponseItem())
      }
      // Results in a new questionnaire state being generated synchronously, i.e., the current
      // thread will be suspended until the new state is generated.
      modificationCount.update { it + 1 }
    }

    if (
      currentPageItems.filterIsInstance<QuestionnaireAdapterItem.Question>().all {
        it.item.validationResult is Valid
      }
    ) {
      block()
    }
  }

  fun readFileContent(filePath: String): String {
    val path = Path(filePath) // Create a Path object from the file path
    return if (SystemFileSystem.exists(path)) {
      SystemFileSystem.source(path).use { source -> source.buffered().readString() }
    } else {
      Logger.e("File not found at: $filePath")
      throw Error("File not found at $filePath")
    }
  }
}

typealias ItemToParentMap = MutableMap<Questionnaire.Item, Questionnaire.Item>

/** Questionnaire state for the Fragment to consume. */
internal data class QuestionnaireState(
  val items: List<QuestionnaireAdapterItem>,
  val displayMode: DisplayMode,
  val bottomNavItem: QuestionnaireAdapterItem.Navigation?,
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
  get() = pages.any { it.index < currentPageIndex && it.enabled && !it.hidden }

internal val QuestionnairePagination.hasNextPage: Boolean
  get() = pages.any { it.index > currentPageIndex && it.enabled && !it.hidden }
