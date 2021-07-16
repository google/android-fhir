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

package com.google.android.fhir.datacapture.views

import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemCheckBoxGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_checkbox_group_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var checkboxGroupHeader: TextView
      private lateinit var checkboxGroup: LinearLayout
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix)
        checkboxGroup = itemView.findViewById(R.id.checkbox_group)
        checkboxGroupHeader = itemView.findViewById(R.id.checkbox_group_header)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefix
        } else {
          prefixTextView.visibility = View.GONE
        }
        val (questionnaireItem, _) = questionnaireItemViewItem
        checkboxGroupHeader.text = questionnaireItem.localizedText
        checkboxGroup.removeAllViews()
        questionnaireItem.answerOption.forEach { answerOption ->
          populateViewWithAnswerOption(answerOption)
        }
      }

      private fun populateViewWithAnswerOption(
        answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent
      ) {
        val singleCheckBox =
          LayoutInflater.from(checkboxGroup.context)
            .inflate(R.layout.questionnaire_item_check_box_view, null)
        val checkbox = singleCheckBox.findViewById<CheckBox>(R.id.check_box)
        checkbox.isChecked = questionnaireItemViewItem.hasAnswerOption(answerOption)
        checkbox.text = answerOption.valueCoding.display
        checkbox.setOnClickListener {
          if (checkbox.isChecked) {
            questionnaireItemViewItem.addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = answerOption.value
              }
            )
          } else {
            questionnaireItemViewItem.removeAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = answerOption.value
              }
            )
          }
        }
        checkboxGroup.addView(singleCheckBox)
      }
    }
}
