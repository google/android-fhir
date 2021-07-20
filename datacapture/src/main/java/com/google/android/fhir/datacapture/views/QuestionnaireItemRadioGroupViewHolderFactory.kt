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

import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemRadioGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_radio_group_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var radioHeader: TextView
      private lateinit var radioGroup: RadioGroup
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix)
        radioGroup = itemView.findViewById(R.id.radio_group)
        radioHeader = itemView.findViewById(R.id.radio_header)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefix
        } else {
          prefixTextView.visibility = View.GONE
        }
        val (questionnaireItem, questionnaireResponseItem) = questionnaireItemViewItem
        val answer = questionnaireResponseItem.answer.singleOrNull()?.valueCoding
        radioHeader.text = questionnaireItem.localizedText
        radioGroup.removeAllViews()
        radioGroup.setOnCheckedChangeListener(null)
        var index = 0
        questionnaireItem.answerOption.forEach {
          radioGroup.addView(
            RadioButton(radioGroup.context).apply {
              id = index++ // Use the answer option index as radio button ID
              text = it.displayString
              layoutParams =
                ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT
                )
              isChecked = it.valueCoding.equalsDeep(answer)
            }
          )
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
          // if-else block to prevent over-writing of "items" nested within "answer"
          if (questionnaireResponseItem.answer.size > 0) {
            questionnaireResponseItem.answer.apply {
              this[0].value = questionnaireItem.answerOption[checkedId].value
            }
          } else {
            questionnaireResponseItem.answer.apply {
              clear()
              add(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = questionnaireItem.answerOption[checkedId].value
                }
              )
            }
          }

          questionnaireItemViewItem.questionnaireResponseItemChangedCallback()
        }
      }
    }
}
