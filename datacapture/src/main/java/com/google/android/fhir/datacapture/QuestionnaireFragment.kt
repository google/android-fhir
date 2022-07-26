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
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import org.hl7.fhir.r4.model.Questionnaire

open class QuestionnaireFragment : Fragment() {
  private val viewModel: QuestionnaireViewModel by viewModels()

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
    val paginationPreviousButton = view.findViewById<View>(R.id.pagination_previous_button)
    paginationPreviousButton.setOnClickListener { viewModel.goToPreviousPage() }
    val paginationNextButton = view.findViewById<View>(R.id.pagination_next_button)
    paginationNextButton.setOnClickListener { viewModel.goToNextPage() }
    requireView().findViewById<Button>(R.id.submit_questionnaire).setOnClickListener {
      setFragmentResult(SUBMIT_REQUEST_KEY, Bundle.EMPTY)
    }
    val adapter = QuestionnaireItemAdapter(getCustomQuestionnaireItemViewHolderFactoryMatchers())
    val reviewPageItemAdapter = QuestionnaireReviewPageItemAdapter()

    val submitButton = requireView().findViewById<Button>(R.id.submit_questionnaire)
    // Reads submit button visibility value initially defined in
    // [R.attr.submitButtonStyleQuestionnaire] style.
    val submitButtonVisibilityInStyle = submitButton.visibility
    viewModel.setShowSubmitButtonFlag(submitButtonVisibilityInStyle == View.VISIBLE)

    val reviewModeEditButton = view.findViewById<View>(R.id.review_mode_edit_button)
    reviewModeEditButton.setOnClickListener { viewModel.setReviewMode(false) }

    val reviewModeButton = view.findViewById<View>(R.id.review_mode_button)
    reviewModeButton.setOnClickListener { viewModel.setReviewMode(true) }

    recyclerView.layoutManager = LinearLayoutManager(view.context)
    // Animation does work well with views that could gain focus
    recyclerView.itemAnimator = null

    // Listen to updates from the view model.
    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      viewModel.questionnaireStateFlow.collect { state ->
        if (state.reviewMode) {
          reviewPageItemAdapter.submitList(state.items)
          reviewModeEditButton.visibility = View.VISIBLE
        } else {
          adapter.submitList(state.items)
          reviewModeEditButton.visibility = View.GONE
        }

        if (state.pagination != null) {
          paginationPreviousButton.visibility = View.VISIBLE
          paginationPreviousButton.isEnabled = state.pagination.hasPreviousPage
          paginationNextButton.visibility = View.VISIBLE
          paginationNextButton.isEnabled = state.pagination.hasNextPage
        } else {
          paginationPreviousButton.visibility = View.GONE
          paginationNextButton.visibility = View.GONE
        }
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      viewModel.reviewModeStateFlow.collect { reviewMode ->
        recyclerView.adapter = if (reviewMode) reviewPageItemAdapter else adapter
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      viewModel.showReviewButtonStateFlow.collect { showReviewButton ->
        reviewModeButton.visibility = if (showReviewButton) View.VISIBLE else View.GONE
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      viewModel.showSubmitButtonStateFlow.collect { showSubmitButton ->
        submitButton.visibility = if (showSubmitButton) View.VISIBLE else View.GONE
      }
    }
  }

  /**
   * Returns a list of [QuestionnaireItemViewHolderFactoryMatcher]s that provide custom views for
   * rendering items in the questionnaire. User-provided custom views will take precedence over
   * canonical views provided by the library. If multiple
   * [QuestionnaireItemViewHolderFactoryMatcher] are applicable for the same item, the behavior is
   * undefined (any of them may be selected).
   */
  open fun getCustomQuestionnaireItemViewHolderFactoryMatchers() =
    emptyList<QuestionnaireItemViewHolderFactoryMatcher>()

  // Returns the current questionnaire response
  fun getQuestionnaireResponse() = viewModel.getQuestionnaireResponse()

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
     * A [Uri] extra for streaming a JSON encoded questionnaire.
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
     * This is required unless [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI] is provided.
     *
     * If this and [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI] are provided,
     * [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI] takes precedence.
     */
    const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING = "questionnaire-response"
    /**
     * A [Uri] extra for streaming a JSON encoded questionnaire response.
     *
     * This is required unless [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] is provided.
     *
     * If this and [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] are provided, this extra takes
     * precedence.
     */
    const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI = "questionnaire-response-uri"

    /** A boolean flag to have a review page at the end of the questionnaire. */
    const val QUESTIONNAIRE_HAS_REVIEW_PAGE = "questionnaire-has-review-page"

    /** A boolean flag to open review page as the entry point of the questionnaire. */
    const val QUESTIONNAIRE_ENTRY_BY_REVIEW_PAGE = "questionnaire-entry-review-page"

    const val SUBMIT_REQUEST_KEY = "submit-request-key"
  }

  /**
   * Data class that holds a matcher function ([matches]) which evaluates whether a given [factory]
   * should be used in creating a
   * [com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolder] that displays the given
   * [Questionnaire.QuestionnaireItemComponent]
   */
  data class QuestionnaireItemViewHolderFactoryMatcher(
    val factory: QuestionnaireItemViewHolderFactory,
    val matches: (Questionnaire.QuestionnaireItemComponent) -> Boolean
  )
}
