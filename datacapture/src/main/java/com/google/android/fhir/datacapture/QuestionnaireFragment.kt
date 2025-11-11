/*
 * Copyright 2023-2025 Google LLC
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

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.use
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_URI
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.NavigationViewHolder
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Questionnaire
import timber.log.Timber

/**
 * A [Fragment] for displaying FHIR Questionnaires and getting user responses as FHIR
 * QuestionnaireResponses.
 *
 * For more information, see the
 * [QuestionnaireFragment](https://github.com/google/android-fhir/wiki/SDCL%3A-Use-QuestionnaireFragment)
 * developer guide.
 */
class QuestionnaireFragment : Fragment() {
  private val viewModel: QuestionnaireViewModel by viewModels()

  /**
   * Provides a [QuestionnaireItemViewHolderFactoryMatcher]s which are used to evaluate whether a
   * custom [QuestionnaireItemViewHolderFactory] should be used to render a given questionnaire
   * item. The provider may be provided by the application developer via [DataCaptureConfig],
   * otherwise default no-op implementation is used.
   */
  @VisibleForTesting
  val questionnaireItemViewHolderFactoryMatchersProvider:
    QuestionnaireItemViewHolderFactoryMatchersProvider by lazy {
    requireArguments().getString(EXTRA_MATCHERS_FACTORY)?.let { factoryKey ->
      val provider =
        DataCapture.getConfiguration(requireContext())
          .questionnaireItemViewHolderFactoryMatchersProviderFactory
          ?.get(factoryKey)

      provider?.let {
        object : QuestionnaireItemViewHolderFactoryMatchersProvider() {
          override fun get(): List<QuestionnaireItemViewHolderFactoryMatcher> {
            return it.get().map { matcher ->
              QuestionnaireItemViewHolderFactoryMatcher(
                factory = matcher.factory,
                matches = matcher.matches,
              )
            }
          }
        }
      }
    }
      ?: EmptyQuestionnaireItemViewHolderFactoryMatchersProviderImpl
  }

  /** @suppress */
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    val themeId = getQuestionnaireThemeId(inflater.context)
    val themedContext = ContextThemeWrapper(inflater.context, themeId)

    return ComposeView(themedContext).apply {
      setContent {
        QuestionnaireScreen(
          viewModel = viewModel,
          matchersProvider = questionnaireItemViewHolderFactoryMatchersProvider,
        )
      }
    }
  }

  /** @suppress */
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupViewModelCallbacks()
    setupFragmentResultListeners()
  }

  private fun setupViewModelCallbacks() {
    viewModel.setOnCancelButtonClickListener {
      QuestionnaireCancelDialogFragment()
        .show(requireActivity().supportFragmentManager, QuestionnaireCancelDialogFragment.TAG)
    }

    viewModel.setOnSubmitButtonClickListener {
      lifecycleScope.launch {
        viewModel.validateQuestionnaireAndUpdateUI().let { validationMap ->
          if (validationMap.values.flatten().filterIsInstance<Invalid>().isEmpty()) {
            setFragmentResult(SUBMIT_REQUEST_KEY, Bundle.EMPTY)
          } else {
            showValidationErrorDialog(validationMap)
          }
        }
      }
    }
  }

  private fun showValidationErrorDialog(validationMap: Map<String, List<ValidationResult>>) {
    val errorViewModel: QuestionnaireValidationErrorViewModel by activityViewModels()
    errorViewModel.setQuestionnaireAndValidation(viewModel.questionnaire, validationMap)
    val validationErrorMessageDialog = QuestionnaireValidationErrorMessageDialogFragment()
    if (requireArguments().containsKey(EXTRA_SHOW_SUBMIT_ANYWAY_BUTTON)) {
      validationErrorMessageDialog.arguments =
        Bundle().apply {
          putBoolean(
            EXTRA_SHOW_SUBMIT_ANYWAY_BUTTON,
            requireArguments().getBoolean(EXTRA_SHOW_SUBMIT_ANYWAY_BUTTON),
          )
        }
    }
    validationErrorMessageDialog.show(
      requireActivity().supportFragmentManager,
      QuestionnaireValidationErrorMessageDialogFragment.TAG,
    )
  }

  private fun setupFragmentResultListeners() {
    requireActivity().supportFragmentManager.setFragmentResultListener(
      QuestionnaireValidationErrorMessageDialogFragment.RESULT_CALLBACK,
      viewLifecycleOwner,
    ) { _, bundle ->
      when (
        val result = bundle.getString(QuestionnaireValidationErrorMessageDialogFragment.RESULT_KEY)
      ) {
        QuestionnaireValidationErrorMessageDialogFragment.RESULT_VALUE_FIX -> {
          // Go back to the Edit mode if currently in the Review mode.
          viewModel.setReviewMode(false)
        }
        QuestionnaireValidationErrorMessageDialogFragment.RESULT_VALUE_SUBMIT -> {
          setFragmentResult(SUBMIT_REQUEST_KEY, Bundle.EMPTY)
        }
        else -> Timber.e("Unknown fragment result $result")
      }
    }

    /** Listen to Button Clicks from the Cancel Dialog */
    requireActivity().supportFragmentManager.setFragmentResultListener(
      QuestionnaireCancelDialogFragment.REQUEST_KEY,
      viewLifecycleOwner,
    ) { _, bundle ->
      when (val result = bundle.getString(QuestionnaireCancelDialogFragment.RESULT_KEY)) {
        QuestionnaireCancelDialogFragment.RESULT_NO -> {
          // Allow the user to continue with the questionnaire
        }
        QuestionnaireCancelDialogFragment.RESULT_YES -> {
          setFragmentResult(CANCEL_REQUEST_KEY, Bundle.EMPTY)
        }
        else -> Timber.e("Unknown fragment result $result")
      }
    }
  }

  private fun getQuestionnaireThemeId(context: Context): Int {
    return context.obtainStyledAttributes(R.styleable.QuestionnaireTheme).use {
      it.getResourceId(
        R.styleable.QuestionnaireTheme_questionnaire_theme,
        R.style.Theme_Questionnaire,
      )
    }
  }

  /**
   * Returns a [QuestionnaireResponse][org.hl7.fhir.r4.model.QuestionnaireResponse] populated with
   * any answers that are present on the rendered [QuestionnaireFragment] when it is called.
   */
  suspend fun getQuestionnaireResponse() = viewModel.getQuestionnaireResponse()

  fun clearAllAnswers() = viewModel.clearAllAnswers()

  /** Helper to create [QuestionnaireFragment] with appropriate [Bundle] arguments. */
  class Builder {
    private val args = mutableListOf<Pair<String, Any>>()

    /**
     * A JSON encoded string extra for a questionnaire. This should only be used for questionnaires
     * with size at most 512KB. For large questionnaires, use `setQuestionnaire(questionnaireUri:
     * Uri)`.
     *
     * This is required unless `setQuestionnaire(questionnaireUri: Uri)` is provided.
     *
     * If this and `setQuestionnaire(questionnaireUri: Uri)` are provided,
     * [setQuestionnaire(questionnaireUri: Uri)] takes precedence.
     */
    fun setQuestionnaire(questionnaireJson: String) = apply {
      args.add(EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJson)
    }

    /**
     * A [URI][android.net.Uri] extra for streaming a JSON encoded questionnaire.
     *
     * This is required unless `setQuestionnaire(questionnaireJson: String)` is provided.
     *
     * If this and `setQuestionnaire(questionnaireJson: String)` are provided, this extra takes
     * precedence.
     */
    fun setQuestionnaire(questionnaireUri: Uri) = apply {
      args.add(EXTRA_QUESTIONNAIRE_JSON_URI to questionnaireUri)
    }

    /**
     * A JSON encoded string extra for a prefilled questionnaire response. This should only be used
     * for questionnaire response with size at most 512KB. For large questionnaire response, use
     * `setQuestionnaireResponse(questionnaireResponseUri: Uri)`.
     *
     * If this and `setQuestionnaireResponse(questionnaireResponseUri: Uri)` are provided,
     * `setQuestionnaireResponse(questionnaireResponseUri: Uri)` takes precedence.
     */
    fun setQuestionnaireResponse(questionnaireResponseJson: String) = apply {
      args.add(EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING to questionnaireResponseJson)
    }

    /**
     * A [URI][android.net.Uri] extra for streaming a JSON encoded questionnaire response.
     *
     * If this and `setQuestionnaireResponse(questionnaireResponseJson: String)` are provided, this
     * extra takes precedence.
     */
    fun setQuestionnaireResponse(questionnaireResponseUri: Uri) = apply {
      args.add(EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI to questionnaireResponseUri)
    }

    /**
     * The launch context allows information to be passed into questionnaire based on the context in
     * which the questionnaire is being evaluated. For example, what patient, what encounter, what
     * user, etc. is "in context" at the time the questionnaire response is being completed:
     * https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-launchContext.html
     *
     * @param launchContextMap map of launchContext name and serialized resources
     */
    fun setQuestionnaireLaunchContextMap(launchContextMap: Map<String, String>) = apply {
      args.add(EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP to launchContextMap)
    }

    /**
     * An [Boolean] extra to control if the questionnaire is read-only. If review page and read-only
     * are both enabled, read-only will take precedence.
     */
    fun setIsReadOnly(value: Boolean) = apply { args.add(EXTRA_READ_ONLY to value) }

    /**
     * A [Boolean] extra to control if a review page is shown. By default it will be shown at the
     * end of the questionnaire.
     */
    fun showReviewPageBeforeSubmit(value: Boolean) = apply {
      args.add(EXTRA_ENABLE_REVIEW_PAGE to value)
    }

    /**
     * A [Boolean] extra to control if the review page is to be opened first. This has no effect if
     * review page is not enabled.
     */
    fun showReviewPageFirst(value: Boolean) = apply {
      args.add(EXTRA_SHOW_REVIEW_PAGE_FIRST to value)
    }

    /** A [Boolean] extra to control whether the asterisk text is shown. */
    fun showAsterisk(value: Boolean) = apply { args.add(EXTRA_SHOW_ASTERISK_TEXT to value) }

    /** A [Boolean] extra to control whether the required text is shown. */
    fun showRequiredText(value: Boolean) = apply { args.add(EXTRA_SHOW_REQUIRED_TEXT to value) }

    /** A [Boolean] extra to control whether the optional text is shown. */
    fun showOptionalText(value: Boolean) = apply { args.add(EXTRA_SHOW_OPTIONAL_TEXT to value) }

    /**
     * A matcher to provide [QuestionnaireItemViewHolderFactoryMatcher]s for custom
     * [Questionnaire.QuestionnaireItemType]. The application needs to provide a
     * [QuestionnaireItemViewHolderFactoryMatchersProviderFactory] in the [DataCaptureConfig] so
     * that the [QuestionnaireFragment] can get instance of
     * [QuestionnaireItemViewHolderFactoryMatchersProvider].
     */
    fun setCustomQuestionnaireItemViewHolderFactoryMatchersProvider(
      matchersProviderFactory: String,
    ) = apply { args.add(EXTRA_MATCHERS_FACTORY to matchersProviderFactory) }

    /**
     * A [Boolean] extra to show or hide the Submit button in the questionnaire. Default is true.
     */
    fun setShowSubmitButton(value: Boolean) = apply { args.add(EXTRA_SHOW_SUBMIT_BUTTON to value) }

    /** To accept a configurable text for the submit button */
    fun setSubmitButtonText(text: String) = apply { args.add(EXTRA_SUBMIT_BUTTON_TEXT to text) }

    /**
     * A [Boolean] extra to show or hide the Cancel button in the questionnaire. Default is true.
     */
    fun setShowCancelButton(value: Boolean) = apply { args.add(EXTRA_SHOW_CANCEL_BUTTON to value) }

    /**
     * A [Boolean] extra to show questionnaire page as a default/long scroll with the
     * previous/next/submit buttons anchored to bottom/end of page. Default is false.
     */
    fun setShowNavigationInDefaultLongScroll(value: Boolean) = apply {
      args.add(EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL to value)
    }

    /** Setter to show/hide the Submit anyway button. This button is visible by default. */
    fun setShowSubmitAnywayButton(value: Boolean) = apply {
      args.add(EXTRA_SHOW_SUBMIT_ANYWAY_BUTTON to value)
    }

    @VisibleForTesting fun buildArgs() = bundleOf(*args.toTypedArray())

    /** @return A [QuestionnaireFragment] with provided [Bundle] arguments. */
    fun build(): QuestionnaireFragment {
      return QuestionnaireFragment().apply { arguments = buildArgs() }
    }
  }

  /**
   * Extras that can be passed to [QuestionnaireFragment] to define its behavior. When you create a
   * QuestionnaireFragment, one of [EXTRA_QUESTIONNAIRE_JSON_URI] or
   * [EXTRA_QUESTIONNAIRE_JSON_STRING] is required.
   */
  companion object {
    /**
     * A JSON encoded string extra for a questionnaire. This should only be used for questionnaires
     * with size at most 512KB. For large questionnaires, use [EXTRA_QUESTIONNAIRE_JSON_URI].
     *
     * This is required unless [EXTRA_QUESTIONNAIRE_JSON_URI] is provided.
     *
     * If this and [EXTRA_QUESTIONNAIRE_JSON_URI] are provided, [EXTRA_QUESTIONNAIRE_JSON_URI] takes
     * precedence.
     */
    internal const val EXTRA_QUESTIONNAIRE_JSON_STRING = "questionnaire"

    /**
     * A [URI][android.net.Uri] extra for streaming a JSON encoded questionnaire.
     *
     * This is required unless [EXTRA_QUESTIONNAIRE_JSON_STRING] is provided.
     *
     * If this and [EXTRA_QUESTIONNAIRE_JSON_STRING] are provided, this extra takes precedence.
     */
    internal const val EXTRA_QUESTIONNAIRE_JSON_URI = "questionnaire-uri"

    /**
     * A JSON encoded string extra for a prefilled questionnaire response. This should only be used
     * for questionnaire response with size at most 512KB. For large questionnaire response, use
     * [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI].
     *
     * If this and [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI] are provided,
     * [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI] takes precedence.
     */
    internal const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING = "questionnaire-response"

    /**
     * A map of launchContext name and JSON encoded strings extra for each questionnaire context.
     */
    internal const val EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP = "questionnaire-launch-contexts"

    /**
     * A [URI][android.net.Uri] extra for streaming a JSON encoded questionnaire response.
     *
     * If this and [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] are provided, this extra takes
     * precedence.
     */
    internal const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI = "questionnaire-response-uri"

    /**
     * A [Boolean] extra to control if a review page is shown. By default it will be shown at the
     * end of the questionnaire.
     */
    internal const val EXTRA_ENABLE_REVIEW_PAGE = "enable-review-page"

    /**
     * A [Boolean] extra to control if the review page is to be opened first. This has no effect if
     * review page is not enabled.
     */
    internal const val EXTRA_SHOW_REVIEW_PAGE_FIRST = "show-review-page-first"

    /**
     * An [Boolean] extra to control if the questionnaire is read-only. If review page and read-only
     * are both enabled, read-only will take precedence.
     */
    internal const val EXTRA_READ_ONLY = "read-only"

    internal const val EXTRA_MATCHERS_FACTORY = "matcher_factory_class"

    const val SUBMIT_REQUEST_KEY = "submit-request-key"

    const val CANCEL_REQUEST_KEY = "cancel-request-key"

    /**
     * A [Boolean] extra to show or hide the Submit button in the questionnaire. Default is true.
     */
    internal const val EXTRA_SHOW_SUBMIT_BUTTON = "show-submit-button"

    /**
     * A [Boolean] extra to show or hide the Cancel button in the questionnaire. Default is false.
     */
    internal const val EXTRA_SHOW_CANCEL_BUTTON = "show-cancel-button"

    internal const val EXTRA_SHOW_OPTIONAL_TEXT = "show-optional-text"

    internal const val EXTRA_SHOW_ASTERISK_TEXT = "show-asterisk-text"

    internal const val EXTRA_SHOW_REQUIRED_TEXT = "show-required-text"

    internal const val EXTRA_SUBMIT_BUTTON_TEXT = "submit-button-text"

    internal const val EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL =
      "show-navigation-in-default-long-scroll"

    /**
     * A [Boolean] extra to show or hide the Submit anyway button in the questionnaire. Default is
     * true.
     */
    internal const val EXTRA_SHOW_SUBMIT_ANYWAY_BUTTON = "show-submit-anyway-button"

    /** Test tag for QuestionnaireEditList */
    const val QUESTIONNAIRE_EDIT_LIST = "questionnaire_edit_list"

    fun builder() = Builder()
  }

  /**
   * Data class that holds a matcher function ([matches]) which evaluates whether a given [factory]
   * should be used to display a given [Questionnaire.QuestionnaireItemComponent].
   *
   * See the
   * [developer guide](https://github.com/google/android-fhir/wiki/SDCL:-Customize-how-a-Questionnaire-is-displayed#custom-questionnaire-components)
   * for more information.
   */
  data class QuestionnaireItemViewHolderFactoryMatcher(
    /** The custom [QuestionnaireItemViewHolderFactory] to use. */
    val factory: QuestionnaireItemViewHolderFactory,
    /**
     * A predicate function which, given a [Questionnaire.QuestionnaireItemComponent], returns true
     * if the factory should apply to that item.
     */
    val matches: (Questionnaire.QuestionnaireItemComponent) -> Boolean,
  )

  /**
   * Provides the [QuestionnaireItemViewHolderFactoryMatcher]s which are used to evaluate whether a
   * custom [QuestionnaireItemViewHolderFactory] should be used to render a given questionnaire
   * item.
   *
   * **NOTE**:
   *
   * User-provided custom views take precedence over canonical views provided by the library. If
   * multiple [QuestionnaireItemViewHolderFactoryMatcher] are applicable for the same item, the
   * behavior is undefined (any of them may be selected).
   *
   * See the
   * [developer guide](https://github.com/google/android-fhir/wiki/SDCL:-Customize-how-a-Questionnaire-is-displayed#custom-questionnaire-components)
   * for more information.
   */
  abstract class QuestionnaireItemViewHolderFactoryMatchersProvider {
    /**
     * Implementation should specify when custom questionnaire components should be used.
     *
     * @return A [List] of [QuestionnaireItemViewHolderFactoryMatcher]s which are used to evaluate
     *   whether a custom [QuestionnaireItemViewHolderFactory] should be used to render a given
     *   questionnaire item.
     */
    abstract fun get(): List<QuestionnaireItemViewHolderFactoryMatcher>
  }

  /** No-op implementation that provides no custom [QuestionnaireItemViewHolderFactoryMatcher]s . */
  private object EmptyQuestionnaireItemViewHolderFactoryMatchersProviderImpl :
    QuestionnaireItemViewHolderFactoryMatchersProvider() {
    override fun get() = emptyList<QuestionnaireItemViewHolderFactoryMatcher>()
  }
}

@Composable
private fun QuestionnaireScreen(
  viewModel: QuestionnaireViewModel,
  matchersProvider: QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider,
) {
  val questionnaireState by viewModel.questionnaireStateFlow.collectAsStateWithLifecycle()

  Box(modifier = Modifier.fillMaxSize()) {
    when (val displayMode = questionnaireState.displayMode) {
      is DisplayMode.InitMode -> {
        // Empty state - nothing to show
      }
      is DisplayMode.EditMode -> {
        EditModeContent(
          state = questionnaireState,
          displayMode = displayMode,
          matchersProvider = matchersProvider,
          bottomNavItem = questionnaireState.bottomNavItem,
        )
      }
      is DisplayMode.ReviewMode -> {
        ReviewModeContent(
          state = questionnaireState,
          displayMode = displayMode,
          onEditClick = { viewModel.setReviewMode(false) },
          bottomNavItem = questionnaireState.bottomNavItem,
        )
      }
    }
  }
}

@Composable
private fun EditModeContent(
  state: QuestionnaireState,
  displayMode: DisplayMode.EditMode,
  matchersProvider: QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider,
  bottomNavItem: QuestionnaireAdapterItem.Navigation?,
) {
  var progress by remember { mutableIntStateOf(0) }

  LaunchedEffect(displayMode) {
    if (displayMode.pagination.isPaginated) {
      progress =
        calculateProgressPercentage(
          count = displayMode.pagination.currentPageIndex + 1,
          totalCount = displayMode.pagination.pages.size,
        )
    }
  }
  Scaffold(
    topBar = {
      LinearProgressIndicator(
        progress = { progress / 100f },
        modifier = Modifier.height(4.dp).fillMaxWidth(),
      )
    },
    bottomBar = {
      if (bottomNavItem != null) {
        BottomNavigationContainer(bottomNavItem)
      }
    },
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      QuestionnaireEditList(
        items = state.items,
        displayMode = displayMode,
        questionnaireItemViewHolderMatchers = matchersProvider.get(),
        onUpdateProgressIndicator = { currentPage, totalCount ->
          progress = calculateProgressPercentage(count = (currentPage + 1), totalCount = totalCount)
        },
      )
    }
  }
}

@Composable
private fun ReviewModeContent(
  state: QuestionnaireState,
  displayMode: DisplayMode.ReviewMode,
  onEditClick: () -> Unit,
  bottomNavItem: QuestionnaireAdapterItem.Navigation?,
) {
  Scaffold(
    topBar = {
      QuestionnaireTitleBar(
        showEditButton = displayMode.showEditButton,
        onEditClick = onEditClick,
        modifier = Modifier.fillMaxWidth(),
      )
    },
    bottomBar = {
      if (bottomNavItem != null) {
        BottomNavigationContainer(bottomNavItem)
      }
    },
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      QuestionnaireReviewList(items = state.items)
    }
  }
}

@Composable
fun QuestionnaireTitleBar(
  showEditButton: Boolean,
  onEditClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(R.string.questionnaire_review_mode_title),
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.weight(1f),
    )

    if (showEditButton) {
      IconButton(onClick = onEditClick) {
        Icon(
          painter = painterResource(R.drawable.ic_outline_edit_24),
          contentDescription = "Edit",
        )
      }
    }
  }
}

@Composable
private fun BottomNavigationContainer(
  navigationItem: QuestionnaireAdapterItem.Navigation,
  modifier: Modifier = Modifier,
) {
  AndroidView(
    factory = { context ->
      FrameLayout(context).apply {
        LayoutInflater.from(context).inflate(R.layout.pagination_navigation_view, this, true)
      }
    },
    update = { view ->
      NavigationViewHolder(view).bind(navigationItem.questionnaireNavigationUIState)
    },
    modifier = modifier.fillMaxWidth(),
  )
}

/** Calculates the progress percentage from given [count] and [totalCount] values. */
private fun calculateProgressPercentage(count: Int, totalCount: Int): Int {
  return if (totalCount == 0) 0 else (count * 100 / totalCount)
}
