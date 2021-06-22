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

import android.text.Editable
import android.view.View
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseItemValidator
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.material.textfield.TextInputEditText
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal abstract class QuestionnaireItemEditTextViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_edit_text_view) {
  abstract override fun getQuestionnaireItemViewHolderDelegate():
    QuestionnaireItemEditTextViewHolderDelegate
}

internal abstract class QuestionnaireItemEditTextViewHolderDelegate(
  private val rawInputType: Int,
  private val isSingleLine: Boolean
) : QuestionnaireItemViewHolderDelegate {
  private lateinit var prefixTextView: TextView
  private lateinit var textQuestion: TextView
  private lateinit var textInputEditText: TextInputEditText
  private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

  override fun init(itemView: View) {
    prefixTextView = itemView.findViewById(R.id.prefix)
    textQuestion = itemView.findViewById(R.id.question)
    textInputEditText = itemView.findViewById(R.id.textInputEditText)
    textInputEditText.setRawInputType(rawInputType)
    textInputEditText.isSingleLine = isSingleLine
    textInputEditText.doAfterTextChanged { editable: Editable? ->
      questionnaireItemViewItem.singleAnswerOrNull = getValue(editable.toString())
      questionnaireItemViewItem.questionnaireResponseItemChangedCallback()
    }
    textInputEditText.setOnFocusChangeListener { view, hasFocus ->
      if (!hasFocus) {
        applyValidationResult(
          QuestionnaireResponseItemValidator.validate(
            questionnaireItemViewItem.questionnaireItem,
            questionnaireItemViewItem.questionnaireResponseItem,
            view.context
          )
        )
      }
    }
  }

  private fun applyValidationResult(validationResult: ValidationResult) {
    val validationMessage =
      validationResult.validationMessages.joinToString {
        it.plus(System.getProperty("line.separator"))
      }
    textInputEditText.error = if (validationMessage == "") null else validationMessage
  }

  override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
    this.questionnaireItemViewItem = questionnaireItemViewItem
    if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
      prefixTextView.visibility = View.VISIBLE
      prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefix
    } else {
      prefixTextView.visibility = View.GONE
    }
    textQuestion.text = questionnaireItemViewItem.questionnaireItem.localizedText
    textInputEditText.setText(getText(questionnaireItemViewItem.singleAnswerOrNull))
  }

  /** Returns the answer that should be recorded given the text input by the user. */
  abstract fun getValue(
    text: String
  ): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent?

  /**
   * Returns the text that should be displayed in the [TextInputEditText] from the existing answer
   * to the question (may be input by the user or previously recorded).
   */
  abstract fun getText(
    answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent?
  ): String
}
