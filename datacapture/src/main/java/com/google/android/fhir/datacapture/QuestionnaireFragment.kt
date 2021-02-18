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
import kotlinx.coroutines.flow.collect

class QuestionnaireFragment : Fragment() {
    private val viewModel: QuestionnaireViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflater.context.obtainStyledAttributes(R.styleable.QuestionnaireTheme).use {
            val themeId = it.getResourceId(
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
        val adapter = QuestionnaireItemAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        // Listen to updates from the view model.
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.questionnaireItemViewItemListFlow.collect { adapter.submitList(it) }
        }
    }

    // Returns the current questionnaire response
    fun getQuestionnaireResponse() = viewModel.getQuestionnaireResponse()

    companion object {
        const val BUNDLE_KEY_QUESTIONNAIRE = "questionnaire"
        const val BUNDLE_KEY_QUESTIONNAIRE_RESPONSE = "questionnaire-response"
    }
}
