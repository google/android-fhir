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

package com.google.android.fhir.datacapture.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.google.android.fhir.datacapture.CHOICE_ORIENTATION_HORIZONTAL
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.choiceOrientation
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayout
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemCheckBoxGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_checkbox_group_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var questionTextView: TextView
      private lateinit var checkboxGroup: FlexboxLayout
      private lateinit var errorTextView: TextView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix_text_view)
        checkboxGroup = itemView.findViewById(R.id.checkbox_group)
        questionTextView = itemView.findViewById(R.id.question_text_view)
        errorTextView = itemView.findViewById(R.id.error_text_view)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
        } else {
          prefixTextView.visibility = View.GONE
        }
        val (questionnaireItem, _) = questionnaireItemViewItem
        questionTextView.text = questionnaireItem.localizedTextSpanned
        checkboxGroup.removeAllViews()
        if (questionnaireItem.choiceOrientation == CHOICE_ORIENTATION_HORIZONTAL) {
          checkboxGroup.flexDirection = FlexDirection.ROW
        } else {
          checkboxGroup.flexDirection = FlexDirection.COLUMN
        }
        questionnaireItemViewItem.answerOption.forEach { answerOption ->
          populateViewWithAnswerOption(answerOption)
        }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        errorTextView.text =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        for (i in 0 until checkboxGroup.childCount) {
          val view = checkboxGroup.getChildAt(i)
          view.findViewById<CheckBox>(R.id.check_box).isEnabled = !isReadOnly
        }
      }

      private fun populateViewWithAnswerOption(
        answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent
      ) {
        val singleCheckBox =
          LayoutInflater.from(checkboxGroup.context)
            .inflate(R.layout.questionnaire_item_check_box_view, null)
        val checkbox = singleCheckBox.findViewById<CheckBox>(R.id.check_box)
        checkbox.isChecked = questionnaireItemViewItem.isAnswerOptionSelected(answerOption)
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
          onAnswerChanged(checkboxGroup.context)
        }
        checkboxGroup.addView(singleCheckBox)
        if (questionnaireItemViewItem.questionnaireItem.choiceOrientation ==
            CHOICE_ORIENTATION_HORIZONTAL
        ) {
          (checkbox.layoutParams as ViewGroup.MarginLayoutParams).marginEnd =
            checkboxGroup.context.resources.getDimension(R.dimen.check_box_item_gap).toInt()
        }
      }
    }
}
