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
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemBooleanTypePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_boolean_type_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      private lateinit var boolTypeHeader: TextView
      private lateinit var boolTypeYes: RadioButton
      private lateinit var boolTypeNo: RadioButton
      private lateinit var boolTypeEmpty: RadioButton
      private lateinit var radioGroup: RadioGroup

      override fun init(itemView: View) {
        boolTypeYes = itemView.findViewById(R.id.boolean_type_yes)
        boolTypeNo = itemView.findViewById(R.id.boolean_type_no)
        boolTypeEmpty = itemView.findViewById(R.id.boolean_type_empty)
        prefixTextView = itemView.findViewById(R.id.prefix)
        radioGroup = itemView.findViewById(R.id.radio_group_main)
        boolTypeHeader = itemView.findViewById(R.id.bool_header)
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
        boolTypeHeader.text = questionnaireItem.localizedText

        radioGroup.setOnCheckedChangeListener { _, i ->
          when (i) {
            R.id.boolean_type_yes -> {
              questionnaireResponseItem.answer.clear()
              questionnaireResponseItem.answer.add(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                }
              )
            }
            R.id.boolean_type_no -> {
              questionnaireResponseItem.answer.clear()
              questionnaireResponseItem.answer.add(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(false)
                }
              )
            }
            R.id.boolean_type_empty -> {
              questionnaireResponseItem.answer.clear()
            }
          }

          questionnaireItemViewItem.questionnaireResponseItemChangedCallback()
        }
      }
    }
}
