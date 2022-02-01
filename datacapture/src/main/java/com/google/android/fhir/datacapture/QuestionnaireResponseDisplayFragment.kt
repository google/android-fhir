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
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect

open class QuestionnaireResponseDisplayFragment : Fragment() {

  private val viewModel: QuestionnaireResponseDisplayViewModel by viewModels()

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
        .inflate(R.layout.questionnaire_response_display_fragment, container, false)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

    val backButton = view.findViewById<Button>(R.id.back_button)
    backButton.setOnClickListener {}

    val submitButton = view.findViewById<Button>(R.id.submit_button)
    submitButton.setOnClickListener {}

    val editButton = view.findViewById<Button>(R.id.edit_button)
    editButton.setOnClickListener {}

    val questionnaireNameText = view.findViewById<TextView>(R.id.questionnaire_name_text)
    if (!viewModel.questionnaire.title.isNullOrEmpty()) {
      questionnaireNameText.apply {
        visibility = View.VISIBLE
        text = viewModel.questionnaire.title
      }
    }

    val adapter = QuestionnaireResponseItemAdapter()

    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(view.context)

    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      viewModel.questionnaireResponseItemViewItemList.collect { adapter.submitList(it) }
    }
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
    /**
     * A JSON encoded string extra for a prefilled questionnaire response.
     *
     * This is required.
     */
    const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING = "questionnaire-response"
  }
}
