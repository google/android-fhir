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
import android.text.Editable
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.flyOverText
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.subtitleText
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
  private lateinit var questionTextView: TextView
  private lateinit var questionSubtitleTextView: TextView
  private lateinit var textInputLayout: TextInputLayout
  private lateinit var textInputEditText: TextInputEditText
  override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

  override fun init(itemView: View) {
    prefixTextView = itemView.findViewById(R.id.prefix_text_view)
    questionTextView = itemView.findViewById(R.id.question_text_view)
    questionSubtitleTextView = itemView.findViewById(R.id.subtitle_text_view)
    textInputLayout = itemView.findViewById(R.id.text_input_layout)
    textInputEditText = itemView.findViewById(R.id.text_input_edit_text)
    textInputEditText.setRawInputType(rawInputType)
    textInputEditText.isSingleLine = isSingleLine
    textInputEditText.doAfterTextChanged { editable: Editable? ->
      questionnaireItemViewItem.singleAnswerOrNull = getValue(editable.toString())
      onAnswerChanged(textInputEditText.context)
    }
  }

  override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem, position: Int) {
    if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
      prefixTextView.visibility = View.VISIBLE
      prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
    } else {
      prefixTextView.visibility = View.GONE
    }
    questionSubtitleTextView.text = questionnaireItemViewItem.questionnaireItem.subtitleText
    textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.flyOverText
    questionTextView.text = questionnaireItemViewItem.questionnaireItem.localizedTextSpanned
    textInputEditText.setText(getText(questionnaireItemViewItem.singleAnswerOrNull))
    textInputEditText.setOnFocusChangeListener { view, focused ->
      if (!focused) {
        (view.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as
            InputMethodManager)
          .hideSoftInputFromWindow(view.windowToken, 0)
      }
    }
    // Override `setOnEditorActionListener` to avoid crash with `IllegalStateException` if it's not
    // possible to move focus forward.
    // See
    // https://stackoverflow.com/questions/13614101/fatal-crash-focus-search-returned-a-view-that-wasnt-able-to-take-focus/47991577
    textInputEditText.setOnEditorActionListener { view, actionId, _ ->
      if (actionId != EditorInfo.IME_ACTION_NEXT) {
        false
      }
      view.focusSearch(FOCUS_DOWN)?.requestFocus(FOCUS_DOWN) ?: false
    }
  }

  override fun displayValidationResult(validationResult: ValidationResult) {
    textInputLayout.error =
      if (validationResult.getSingleStringValidationMessage() == "") null
      else validationResult.getSingleStringValidationMessage()
  }

  override fun setReadOnly(isReadOnly: Boolean) {
    textInputLayout.isEnabled = !isReadOnly
    textInputEditText.isEnabled = !isReadOnly
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
