/*
 * Copyright 2020 Google LLC
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
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.use
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

    val adapter = QuestionnaireItemAdapter(getCustomQuestionnaireItemViewHolderFactoryMatchers())

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

  companion object {
    const val BUNDLE_KEY_QUESTIONNAIRE = "questionnaire"
    const val BUNDLE_KEY_QUESTIONNAIRE_RESPONSE = "questionnaire-response"
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
