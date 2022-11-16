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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.hl7.fhir.r4.model.Questionnaire

/**
 * A [Fragment] for displaying FHIR Questionnaires and getting user responses as FHIR
 * QuestionnaireResponses.
 *
 * For more information, see the
 * [QuestionnaireFragment](https://github.com/google/android-fhir/wiki/SDCL%3A-Use-QuestionnaireFragment)
 * developer guide.
 */
open class QuestionnaireFragment : Fragment() {
  private val viewModel: QuestionnaireViewModel by viewModels()

  /** @suppress */
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    inflater.context.obtainStyledAttributes(R.styleable.QuestionnaireTheme).use {
      val themeId =
        it.getResourceId(
          // Use the custom questionnaire theme if it is specified
          R.styleable.QuestionnaireTheme_questionnaire_theme,
          // Otherwise, use the default questionnaire theme
          R.style.Theme_Questionnaire
        )
      return inflater
        .cloneInContext(ContextThemeWrapper(inflater.context, themeId))
        .inflate(R.layout.questionnaire_fragment, container, false)
    }
  }

  /** @suppress */
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val questionnaireEditRecyclerView =
      view.findViewById<RecyclerView>(R.id.questionnaire_edit_recycler_view)
    val questionnaireReviewRecyclerView =
      view.findViewById<RecyclerView>(R.id.questionnaire_review_recycler_view)
    val paginationPreviousButton = view.findViewById<View>(R.id.pagination_previous_button)
    paginationPreviousButton.setOnClickListener { viewModel.goToPreviousPage() }
    val paginationNextButton = view.findViewById<View>(R.id.pagination_next_button)
    paginationNextButton.setOnClickListener { viewModel.goToNextPage() }
    view.findViewById<Button>(R.id.submit_questionnaire).setOnClickListener {
      viewModel.validateQuestionnaireAndUpdateUI().let { validationMap ->
        if (validationMap
            .filter { (_, validations) -> validations.filterIsInstance<Invalid>().isNotEmpty() }
            .isEmpty()
        ) {
          setFragmentResult(SUBMIT_REQUEST_KEY, Bundle.EMPTY)
        } else {
          val errorViewModel: QuestionnaireValidationErrorViewModel by activityViewModels()
          errorViewModel.setQuestionnaireAndValidation(viewModel.questionnaire, validationMap)
          QuestionnaireValidationErrorMessageDialogFragment()
            .show(
              requireActivity().supportFragmentManager,
              QuestionnaireValidationErrorMessageDialogFragment.TAG
            )
        }
      }
    }
    val questionnaireProgressIndicator: LinearProgressIndicator =
      view.findViewById(R.id.questionnaire_progress_indicator)
    val questionnaireItemEditAdapter =
      QuestionnaireItemEditAdapter(getCustomQuestionnaireItemViewHolderFactoryMatchers())
    val questionnaireItemReviewAdapter = QuestionnaireItemReviewAdapter()

    val submitButton = requireView().findViewById<Button>(R.id.submit_questionnaire)
    // Reads submit button visibility value initially defined in
    // [R.attr.submitButtonStyleQuestionnaire] style.
    val submitButtonVisibilityInStyle = submitButton.visibility
    viewModel.setShowSubmitButtonFlag(submitButtonVisibilityInStyle == View.VISIBLE)

    val reviewModeEditButton = view.findViewById<View>(R.id.review_mode_edit_button)
    reviewModeEditButton.setOnClickListener { viewModel.setReviewMode(false) }

    val reviewModeButton = view.findViewById<View>(R.id.review_mode_button)
    reviewModeButton.setOnClickListener { viewModel.setReviewMode(true) }

    questionnaireEditRecyclerView.adapter = questionnaireItemEditAdapter
    val linearLayoutManager = LinearLayoutManager(view.context)
    questionnaireEditRecyclerView.layoutManager = linearLayoutManager
    // Animation does work well with views that could gain focus
    questionnaireEditRecyclerView.itemAnimator = null

    questionnaireReviewRecyclerView.adapter = questionnaireItemReviewAdapter
    questionnaireReviewRecyclerView.layoutManager = LinearLayoutManager(view.context)

    // Listen to updates from the view model.
    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      viewModel.questionnaireStateFlow.collect { state ->
        if (state.reviewMode) {
          questionnaireItemReviewAdapter.submitList(state.items)
          questionnaireReviewRecyclerView.visibility = View.VISIBLE
          questionnaireEditRecyclerView.visibility = View.GONE
          reviewModeEditButton.visibility = View.VISIBLE
          questionnaireProgressIndicator.visibility = View.GONE
        } else {
          questionnaireItemEditAdapter.submitList(state.items)
          questionnaireEditRecyclerView.visibility = View.VISIBLE
          questionnaireReviewRecyclerView.visibility = View.GONE
          reviewModeEditButton.visibility = View.GONE
          questionnaireProgressIndicator.visibility = View.VISIBLE
        }

        if (state.pagination.isPaginated && !state.reviewMode) {
          paginationPreviousButton.visibility = View.VISIBLE
          paginationPreviousButton.isEnabled = state.pagination.hasPreviousPage
          paginationNextButton.visibility = View.VISIBLE
          paginationNextButton.isEnabled = state.pagination.hasNextPage
          questionnaireProgressIndicator.updateProgressIndicator(
            calculateProgressPercentage(
              count =
                (state.pagination.currentPageIndex +
                  1), // incremented by 1 due to initialPageIndex starts with 0.
              totalCount = state.pagination.pages.size
            )
          )
        } else {
          paginationPreviousButton.visibility = View.GONE
          paginationNextButton.visibility = View.GONE

          questionnaireEditRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
              override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                questionnaireProgressIndicator.updateProgressIndicator(
                  calculateProgressPercentage(
                    count =
                      (linearLayoutManager.findLastVisibleItemPosition() +
                        1), // incremented by 1 due to findLastVisiblePosition() starts with 0.
                    totalCount = linearLayoutManager.itemCount
                  )
                )
              }
            }
          )
        }

        reviewModeButton.visibility =
          if (state.pagination.showReviewButton) View.VISIBLE else View.GONE

        submitButton.visibility = if (state.pagination.showSubmitButton) View.VISIBLE else View.GONE
      }
    }
  }

  /** Calculates the progress percentage from given [count] and [totalCount] values. */
  internal fun calculateProgressPercentage(count: Int, totalCount: Int): Int {
    return if (totalCount == 0) 0 else (count * 100 / totalCount)
  }

  /**
   * Override this method to specify when custom questionnaire components should be used. It should
   * return a [List] of [QuestionnaireItemViewHolderFactoryMatcher]s which are used to evaluate
   * whether a custom [QuestionnaireItemViewHolderFactory] should be used to render a given
   * questionnaire item.
   *
   * User-provided custom views take precedence over canonical views provided by the library. If
   * multiple [QuestionnaireItemViewHolderFactoryMatcher] are applicable for the same item, the
   * behavior is undefined (any of them may be selected).
   *
   * See the
   * [developer guide](https://github.com/google/android-fhir/wiki/SDCL:-Customize-how-a-Questionnaire-is-displayed#custom-questionnaire-components)
   * for more information.
   */
  open fun getCustomQuestionnaireItemViewHolderFactoryMatchers() =
    emptyList<QuestionnaireItemViewHolderFactoryMatcher>()

  /**
   * Returns a [QuestionnaireResponse][org.hl7.fhir.r4.model.QuestionnaireResponse] populated with
   * any answers that are present on the rendered [QuestionnaireFragment] when it is called.
   */
  fun getQuestionnaireResponse() = viewModel.getQuestionnaireResponse()

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
    const val EXTRA_QUESTIONNAIRE_JSON_STRING = "questionnaire"

    /**
     * A [URI][android.net.Uri] extra for streaming a JSON encoded questionnaire.
     *
     * This is required unless [EXTRA_QUESTIONNAIRE_JSON_STRING] is provided.
     *
     * If this and [EXTRA_QUESTIONNAIRE_JSON_STRING] are provided, this extra takes precedence.
     */
    const val EXTRA_QUESTIONNAIRE_JSON_URI = "questionnaire-uri"

    /**
     * A JSON encoded string extra for a prefilled questionnaire response. This should only be used
     * for questionnaire response with size at most 512KB. For large questionnaire response, use
     * [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI].
     *
     * If this and [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI] are provided,
     * [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI] takes precedence.
     */
    const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING = "questionnaire-response"

    /**
     * A [URI][android.net.Uri] extra for streaming a JSON encoded questionnaire response.
     *
     * If this and [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] are provided, this extra takes
     * precedence.
     */
    const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI = "questionnaire-response-uri"

    /**
     * A [Boolean] extra to control if a review page is shown. By default it will be shown at the
     * end of the questionnaire.
     */
    const val EXTRA_ENABLE_REVIEW_PAGE = "enable-review-page"

    /**
     * An [Boolean] extra to control if the review page is to be opened first. This has no effect if
     * review page is not enabled.
     */
    const val EXTRA_SHOW_REVIEW_PAGE_FIRST = "show-review-page-first"

    const val SUBMIT_REQUEST_KEY = "submit-request-key"
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
}

/**
 * Updates the [LinearProgressIndicator] progress with given value.
 *
 * This method will also set max value of [LinearProgressIndicator] to 100.
 *
 * @param progress The new progress [Integer] value between 0 to 100.
 */
internal fun LinearProgressIndicator.updateProgressIndicator(progress: Int) {
  setProgress(progress)
  max = 100
}
