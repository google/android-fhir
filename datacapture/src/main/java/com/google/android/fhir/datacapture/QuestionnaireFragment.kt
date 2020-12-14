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
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

class QuestionnaireFragment(private val questionnaire: Questionnaire) : Fragment() {
    private val viewModel: QuestionnaireViewModel by activityViewModels {
        QuestionnaireViewModelFactory(questionnaire)
    }

    internal var onQuestionnaireSubmittedListener: OnQuestionnaireSubmittedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.questionnaire_fragment, container, false)
        view.findViewById<TextView>(R.id.title).text = viewModel.questionnaire.title

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = QuestionnaireItemAdapter(viewModel)
        recyclerView.adapter = adapter
        adapter.submitList(flatten(viewModel.questionnaire.item))

        view.findViewById<Button>(R.id.submit).setOnClickListener {
            onQuestionnaireSubmittedListener?.onSubmitted(viewModel.questionnaireResponse)
        }
        return view
    }

    private fun flatten(
        items: List<Questionnaire.QuestionnaireItemComponent>
    ): List<Questionnaire.QuestionnaireItemComponent> {
        val flattened = mutableListOf<Questionnaire.QuestionnaireItemComponent>()
        items.forEach { item ->
            if (item.type == Questionnaire.QuestionnaireItemType.GROUP) {
                flattened.addAll(flatten(item.item))
            } else {
                flattened.add(item)
            }
        }
        return flattened
    }

    fun setOnQuestionnaireSubmittedListener(
        onQuestionnaireSubmittedListener: OnQuestionnaireSubmittedListener
    ) {
        this.onQuestionnaireSubmittedListener = onQuestionnaireSubmittedListener
    }

    interface OnQuestionnaireSubmittedListener {
        fun onSubmitted(questionnaireResponse: QuestionnaireResponse)
    }
}
