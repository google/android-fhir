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

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.flyOverText
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.subtitleText
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemDropDownViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_drop_down_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var questionTextView: TextView
      private lateinit var questionSubtitleTextView: TextView
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var autoCompleteTextView: AutoCompleteTextView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      private lateinit var context: Context

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix_text_view)
        questionTextView = itemView.findViewById(R.id.question_text_view)
        questionSubtitleTextView = itemView.findViewById(R.id.subtitle_text_view)
        textInputLayout = itemView.findViewById(R.id.text_input_layout)
        autoCompleteTextView = itemView.findViewById(R.id.auto_complete)
        context = itemView.context
      }

      private fun addContentDescription(){
        textInputLayout.contentDescription = questionnaireItemViewItem.questionnaireItem.linkId + textInputLayout.toString()
        autoCompleteTextView.contentDescription = questionnaireItemViewItem.questionnaireItem.linkId + autoCompleteTextView.toString()
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        addContentDescription()
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
        } else {
          prefixTextView.visibility = View.GONE
        }
        questionTextView.text = questionnaireItemViewItem.questionnaireItem.localizedTextSpanned
        questionSubtitleTextView.text = questionnaireItemViewItem.questionnaireItem.subtitleText
        textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.flyOverText
        val answerOptionString =
          this.questionnaireItemViewItem.answerOption.map { it.displayString }
        val adapter =
          ArrayAdapter(context, R.layout.questionnaire_item_drop_down_list, answerOptionString)
        autoCompleteTextView.setText(
          questionnaireItemViewItem.singleAnswerOrNull?.valueCoding?.display ?: ""
        )
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.onItemClickListener =
          AdapterView.OnItemClickListener { _, _, position, _ ->
            questionnaireItemViewItem.singleAnswerOrNull =
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                .setValue(questionnaireItemViewItem.answerOption[position].valueCoding)
            onAnswerChanged(autoCompleteTextView.context)
          }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        textInputLayout.error =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        textInputLayout.isEnabled = !isReadOnly
      }
    }
}
