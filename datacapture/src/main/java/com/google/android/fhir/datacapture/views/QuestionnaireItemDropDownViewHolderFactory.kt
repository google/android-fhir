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
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type

internal open class QuestionnaireItemDropDownViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_drop_down_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    QuestionnaireItemDropDownViewHolderDelegate()
}

internal open class QuestionnaireItemDropDownViewHolderDelegate :
  QuestionnaireItemViewHolderDelegate {
  private lateinit var header: QuestionnaireItemHeaderView
  private lateinit var textInputLayout: TextInputLayout
  private lateinit var autoCompleteTextView: AutoCompleteTextView
  override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
  private lateinit var context: Context

  override fun init(itemView: View) {
    header = itemView.findViewById(R.id.header)
    textInputLayout = itemView.findViewById(R.id.text_input_layout)
    autoCompleteTextView = itemView.findViewById(R.id.auto_complete)
    context = itemView.context
  }

  override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
    header.bind(questionnaireItemViewItem.questionnaireItem)
    textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.localizedFlyoverSpanned
    val answerOptionString =
      this.questionnaireItemViewItem.answerOption.map { it.displayString }.toMutableList()
    answerOptionString.add(0, context.getString(R.string.hyphen))
    val adapter =
      ArrayAdapter(context, R.layout.questionnaire_item_drop_down_list, answerOptionString)
    autoCompleteTextView.setText(getText(questionnaireItemViewItem.singleAnswerOrNull))
    autoCompleteTextView.setAdapter(adapter)
    autoCompleteTextView.onItemClickListener =
      AdapterView.OnItemClickListener { _, _, position, _ ->
        if (position == 0) {
          questionnaireItemViewItem.singleAnswerOrNull = null
        } else {
          questionnaireItemViewItem.singleAnswerOrNull =
            getValue(questionnaireItemViewItem.answerOption[position - 1].value)
        }
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

  open fun getValue(value: Type): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? =
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
      .setValue(value.castToCoding(value))

  open fun getText(
    answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent?
  ): String = answer?.valueCoding?.display ?: ""
}
