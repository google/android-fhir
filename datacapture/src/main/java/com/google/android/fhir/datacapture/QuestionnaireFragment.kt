/*
 * Copyright 2021 Google LLC
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
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import kotlinx.coroutines.flow.collect
import org.hl7.fhir.r4.model.Questionnaire

/**
 * A [Fragment] for displaying FHIR Questionnaires and getting user responses as FHIR
 * QuestionnareResponses.
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
    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
    val paginationPreviousButton = view.findViewById<View>(R.id.pagination_previous_button)
    paginationPreviousButton.setOnClickListener { viewModel.goToPreviousPage() }
    val paginationNextButton = view.findViewById<View>(R.id.pagination_next_button)
    paginationNextButton.setOnClickListener { viewModel.goToNextPage() }
    requireView().findViewById<Button>(R.id.submit_questionnaire).setOnClickListener {
      setFragmentResult(SUBMIT_REQUEST_KEY, Bundle.EMPTY)
    }
    val adapter = QuestionnaireItemAdapter(getCustomQuestionnaireItemViewHolderFactoryMatchers())
    val submitButton = requireView().findViewById<Button>(R.id.submit_questionnaire)
    // Reads submit button visibility value initially defined in
    // [R.attr.submitButtonStyleQuestionnaire] style.
    val submitButtonVisibilityInStyle = submitButton.visibility

    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(view.context)

    // Listen to updates from the view model.
    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      viewModel.questionnaireStateFlow.collect { state ->
        adapter.submitList(state.items)

        if (state.pagination != null) {
          paginationPreviousButton.visibility = View.VISIBLE
          paginationPreviousButton.isEnabled = state.pagination.hasPreviousPage
          paginationNextButton.visibility = View.VISIBLE
          paginationNextButton.isEnabled = state.pagination.hasNextPage
          if (!state.pagination.hasNextPage && submitButtonVisibilityInStyle == View.VISIBLE) {
            paginationNextButton.visibility = View.GONE
            submitButton.visibility = View.VISIBLE
          } else {
            submitButton.visibility = View.GONE
          }
        } else {
          paginationPreviousButton.visibility = View.GONE
          paginationNextButton.visibility = View.GONE
          if (submitButtonVisibilityInStyle == View.VISIBLE) {
            recyclerView.updatePadding(
              bottom = resources.getDimensionPixelOffset(R.dimen.recyclerview_bottom_padding)
            )
          } else {
            recyclerView.updatePadding(bottom = 0)
          }
        }
      }
    }
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
