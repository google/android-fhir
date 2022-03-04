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

import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.BOTTOM
import androidx.constraintlayout.widget.ConstraintSet.END
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.constraintlayout.widget.ConstraintSet.START
import androidx.constraintlayout.widget.ConstraintSet.TOP
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import kotlinx.coroutines.flow.collect
import org.hl7.fhir.r4.model.Questionnaire

open class QuestionnaireFragment : Fragment() {
  private val viewModel: QuestionnaireViewModel by viewModels()
  private lateinit var customStyleSubmitButtonVisibility: CustomStyleVisibility

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
    customStyleSubmitButtonVisibility = getSubmitButtonCustomStyleVisibilityAttribute()
    val usesPagination = viewModel.questionnaire.usesPagination()
    if (usesPagination) {
      // Constrain submit button at parent bottom right in paginated layout.
      clearSubmitButtonConstraint()
      updateSubmitButtonConstraintInPagination()
    }
    val onScrollListener =
      if (usesPagination) {
        null
      } else {
        // show submit button only when last item is visible in default layout
        ::showSubmitButtonInDefaultLayout
      }
    val adapter =
      QuestionnaireItemAdapter(
        getCustomQuestionnaireItemViewHolderFactoryMatchers(),
        onScrollListener
      )

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
          if (!state.pagination.hasNextPage &&
              customStyleSubmitButtonVisibility == CustomStyleVisibility.VISIBLE
          ) {
            paginationNextButton.visibility = View.GONE
          }
          showSubmitButtonInPaginatedLayout(state.pagination.hasNextPage)
        } else {
          paginationPreviousButton.visibility = View.GONE
          paginationNextButton.visibility = View.GONE
        }
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

  /**
   * Shows submit button in pagination layout on last page at the right and bottom of the parent if
   * [customStyleSubmitButtonVisibility] is [CustomStyleVisibility.VISIBLE].
   */
  private fun showSubmitButtonInPaginatedLayout(hasNextPage: Boolean) {
    // If [android:visibility] attribute in
    // [R.styleable.QuestionnaireSubmitButtonStyle_submit_button_style] is not
    // [CustomStyleVisibility.VISIBLE] then do not show submit button in paginated layout.
    if (customStyleSubmitButtonVisibility != CustomStyleVisibility.VISIBLE) {
      requireView().findViewById<Button>(R.id.submit_questionnaire).visibility = View.GONE
      return
    }
    val submitButton = requireView().findViewById<Button>(R.id.submit_questionnaire)
    if (hasNextPage) {
      submitButton.visibility = View.GONE
    } else {
      submitButton.visibility = View.VISIBLE
    }
  }

  /**
   * Shows submit button in default layout at the bottom of the parent only when last item in the
   * list [QuestionnaireItemAdapter] is visible and [customStyleSubmitButtonVisibility] is
   * [CustomStyleVisibility.VISIBLE].
   */
  private fun showSubmitButtonInDefaultLayout(visible: Int) {
    // If [android:visibility] attribute in
    // [R.styleable.QuestionnaireSubmitButtonStyle_submit_button_style] is not
    // [CustomStyleVisibility.VISIBLE] then do not show submit button when last item position become
    // visible.
    if (customStyleSubmitButtonVisibility != CustomStyleVisibility.VISIBLE) {
      requireView().findViewById<Button>(R.id.submit_questionnaire).visibility = View.GONE
      return
    }
    val padding = resources.getDimensionPixelOffset(R.dimen.padding)
    requireView().findViewById<Button>(R.id.submit_questionnaire).visibility = visible
    val recyclerView = requireView().findViewById<RecyclerView>(R.id.recycler_view)
    if (visible == View.VISIBLE) {
      recyclerView.updatePadding(bottom = padding)
    } else {
      recyclerView.updatePadding(bottom = 0)
    }
  }

  private fun clearSubmitButtonConstraint() {
    val submitButton = requireView().findViewById<View>(R.id.submit_questionnaire)
    val constraintLayout = requireView().findViewById<ConstraintLayout>(R.id.constraint_layout)
    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    // remove submit view end constraint
    constraintSet.clear(submitButton.id, START)
    constraintSet.clear(submitButton.id, END)
    constraintSet.clear(submitButton.id, TOP)
    constraintSet.clear(submitButton.id, BOTTOM)
    constraintSet.applyTo(constraintLayout)
  }

  /**
   * Constraints submit questionnaire button at the bottom and right of the parent in pagination.
   */
  private fun updateSubmitButtonConstraintInPagination() {
    val submitButton = requireView().findViewById<View>(R.id.submit_questionnaire)
    val constraintLayout = requireView().findViewById<ConstraintLayout>(R.id.constraint_layout)
    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    constraintSet.connect(
      submitButton.id, // submit button to change constraint
      BOTTOM, // put submit button bottom side
      PARENT_ID, // parent to put submit button
      BOTTOM, // parent bottom to put submit button at the bottom of it
    )
    constraintSet.connect(submitButton.id, END, PARENT_ID, END)
    constraintSet.applyTo(constraintLayout)
    val horizontalMargin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
    val verticalMargin = resources.getDimensionPixelOffset(R.dimen.vertical_margin)
    val layoutParams = submitButton.layoutParams as ConstraintLayout.LayoutParams
    layoutParams.marginStart = horizontalMargin
    layoutParams.marginEnd = horizontalMargin
    layoutParams.topMargin = verticalMargin
    layoutParams.bottomMargin = verticalMargin
    submitButton.layoutParams = layoutParams
  }

  /**
   * Reads submit button [android:visibility] attribute in
   * [R.styleable.QuestionnaireSubmitButtonStyle_submit_button_style]. If not present then default
   * value is [CustomStyleVisibility.VISIBLE]
   */
  private fun getSubmitButtonCustomStyleVisibilityAttribute(): CustomStyleVisibility {
    return requireContext().obtainStyledAttributes(R.styleable.QuestionnaireSubmitButtonStyle).use {
      val id =
        it.getResourceId(
          R.styleable.QuestionnaireSubmitButtonStyle_submit_button_visibility,
          R.style.Questionnaire_Widget_MaterialComponents_Button_Submit
        )
      val attributes = intArrayOf(android.R.attr.visibility)
      val typedArray: TypedArray = requireContext().obtainStyledAttributes(id, attributes)
      var submitButtonVisibility = typedArray.getInt(0, CustomStyleVisibility.VISIBLE.ordinal)
      CustomStyleVisibility.values()[submitButtonVisibility]
    }
  }

  /** View visibility declared in style [R.attr.submitButtonStyleQuestionnaire]. */
  private enum class CustomStyleVisibility {
    VISIBLE,
    INVISIBLE,
    GONE
  }

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
    /** A JSON encoded string extra for a prefilled questionnaire response. */
    const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING = "questionnaire-response"
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
