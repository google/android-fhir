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
import androidx.lifecycle.ViewModelProvider
import java.util.Locale
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

class QuestionnaireFragment(private val questionnaire: Questionnaire) : Fragment() {
    private lateinit var viewModel: QuestionnaireViewModel

    internal var onQuestionnaireSubmittedListener: OnQuestionnaireSubmittedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, QuestionnaireViewModelFactory(questionnaire))
            .get(QuestionnaireViewModel::class.java)
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.questionnaire_fragment, container, false)
        view.findViewById<TextView>(R.id.title).text = viewModel.questionnaire.title
        val item = view.findViewById<LinearLayout>(R.id.item)

        viewModel.questionnaire.item.forEach {
            generateAndAttachQuestion(it, item)
        }

        view.findViewById<Button>(R.id.submit).setOnClickListener {
            onQuestionnaireSubmittedListener?.onSubmitted(viewModel.questionnaireResponse)
        }
        return view
    }

    private fun generateAndAttachQuestion(
      questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent,
      viewGroup: ViewGroup
    ) {
        val linkId = questionnaireItemComponent.linkId
        when (questionnaireItemComponent.type) {
            Questionnaire.QuestionnaireItemType.BOOLEAN -> {
                val checkBox = CheckBox(this.context)
                checkBox.text = questionnaireItemComponent.text
                viewGroup.addView(checkBox)
                checkBox.setOnClickListener {
                    viewModel.setAnswer(linkId, checkBox.isChecked)
                }
            }
            Questionnaire.QuestionnaireItemType.STRING -> {
                val questionText = TextView(context)
                questionText.text = questionnaireItemComponent.text
                viewGroup.addView(questionText)
                val input = EditText(context)
                viewGroup.addView(input)
                input.doAfterTextChanged { editable: Editable? ->
                    viewModel.setAnswer(linkId, editable.toString())
                }
            }
            Questionnaire.QuestionnaireItemType.GROUP -> {
                val linearLayout = LinearLayout(context)
                linearLayout.layoutParams =
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                linearLayout.orientation = LinearLayout.VERTICAL
                val title = TextView(context)
                title.text = questionnaireItemComponent.text
                linearLayout.addView(title)
                questionnaireItemComponent.item.forEach {
                    generateAndAttachQuestion(it, linearLayout)
                }
                viewGroup.addView(linearLayout)
            }
            else -> {
                throw IllegalArgumentException(
                    "Unsupported item type ${questionnaireItemComponent.type}"
                )
            }
        }
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
