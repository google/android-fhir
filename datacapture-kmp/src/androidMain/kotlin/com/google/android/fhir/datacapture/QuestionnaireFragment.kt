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
import androidx.annotation.VisibleForTesting
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.res.use
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewFactory
import com.google.fhir.model.r4.Questionnaire
import kotlinx.coroutines.launch
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
   * custom [QuestionnaireItemViewFactory] should be used to render a given questionnaire item. The
   * provider may be provided by the application developer via [DataCaptureConfig], otherwise
   * default no-op implementation is used.
   */
  @VisibleForTesting
  val questionnaireItemViewHolderFactoryMatchersProvider:
    QuestionnaireItemViewHolderFactoryMatchersProvider by lazy {
    requireArguments().getString(EXTRA_MATCHERS_FACTORY)?.let { factoryKey ->
      val provider =
        DataCapture.getConfiguration()
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
    DataCapture.initialize(requireContext())
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
   * Returns a [QuestionnaireResponse][QuestionnaireResponse] populated with any answers that are
   * present on the rendered [QuestionnaireFragment] when it is called.
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
    fun builder() = Builder()
  }

  /** No-op implementation that provides no custom [QuestionnaireItemViewHolderFactoryMatcher]s . */
  private object EmptyQuestionnaireItemViewHolderFactoryMatchersProviderImpl :
    QuestionnaireItemViewHolderFactoryMatchersProvider() {
    override fun get() = emptyList<QuestionnaireItemViewHolderFactoryMatcher>()
  }
}
