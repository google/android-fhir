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
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import ca.uhn.fhir.context.FhirContext
import org.hl7.fhir.r4.model.Questionnaire

class QuestionnaireFragment(private val questionnaire: Questionnaire, private val mapper: ViewPicker) : Fragment() {
    private val viewModel: QuestionnaireViewModel by activityViewModels {
        QuestionnaireViewModelFactory(questionnaire)
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.questionnaire_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.title).text = viewModel.questionnaire.title

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = QuestionnaireItemAdapter(viewModel.questionnaireItemViewItemList, mapper)
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.submit).setOnClickListener {
            val serializedResponse = FhirContext.forR4().newJsonParser()
                .encodeResourceToString(viewModel.questionnaireResponse)
            setFragmentResult(
                QUESTIONNAIRE_RESPONSE_REQUEST_KEY,
                bundleOf(QUESTIONNAIRE_RESPONSE_BUNDLE_KEY to serializedResponse)
            )
        }
    }

    companion object {
        const val QUESTIONNAIRE_RESPONSE_REQUEST_KEY = "questionnaire-response-request-key"
        const val QUESTIONNAIRE_RESPONSE_BUNDLE_KEY = "questionnaire-response-bundle-key"
    }
}
