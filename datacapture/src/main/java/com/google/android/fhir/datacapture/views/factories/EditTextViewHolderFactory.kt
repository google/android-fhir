/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

internal abstract class EditTextViewHolderFactory(@LayoutRes override val resId: Int) :
  QuestionnaireItemViewHolderFactory(resId) {
  abstract override fun getQuestionnaireItemViewHolderDelegate():
    QuestionnaireItemEditTextViewHolderDelegate
}

abstract class QuestionnaireItemEditTextViewHolderDelegate(private val rawInputType: Int) :
  QuestionnaireItemViewHolderDelegate {
  override lateinit var questionnaireViewItem: QuestionnaireViewItem

  private lateinit var header: HeaderView
  protected lateinit var textInputLayout: TextInputLayout
  private lateinit var textInputEditText: TextInputEditText
  private var textWatcher: TextWatcher? = null

  override fun init(itemView: View) {
    header = itemView.findViewById(R.id.header)
    textInputLayout = itemView.findViewById(R.id.text_input_layout)
    textInputEditText = itemView.findViewById(R.id.text_input_edit_text)
    textInputEditText.setRawInputType(rawInputType)
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
    textInputEditText.setOnFocusChangeListener { view, focused ->
      if (!focused) {
        (view.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager)
          .hideSoftInputFromWindow(view.windowToken, 0)

        // Update answer even if the text box loses focus without any change. This will mark the
        // questionnaire response item as being modified in the view model and trigger validation.
        handleInput(textInputEditText.editableText, questionnaireViewItem)
      }
    }
  }

  override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
    header.bind(questionnaireViewItem)
    with(textInputLayout) {
      hint = questionnaireViewItem.enabledDisplayItems.localizedFlyoverSpanned
      helperText = getRequiredOrOptionalText(questionnaireViewItem, context)
    }
    displayValidationResult(questionnaireViewItem.validationResult)

    textInputEditText.removeTextChangedListener(textWatcher)
    updateUI(questionnaireViewItem, textInputEditText, textInputLayout)

    textWatcher =
      textInputEditText.doAfterTextChanged { editable: Editable? ->
        handleInput(editable!!, questionnaireViewItem)
      }
  }

  private fun displayValidationResult(validationResult: ValidationResult) {
    textInputLayout.error =
      getValidationErrorMessage(textInputLayout.context, questionnaireViewItem, validationResult)
  }

  override fun setReadOnly(isReadOnly: Boolean) {
    textInputLayout.isEnabled = !isReadOnly
    textInputEditText.isEnabled = !isReadOnly
  }

  /** Handles user input from the `editable` and updates the questionnaire. */
  abstract fun handleInput(editable: Editable, questionnaireViewItem: QuestionnaireViewItem)

  /** Handles the UI update. */
  abstract fun updateUI(
    questionnaireViewItem: QuestionnaireViewItem,
    textInputEditText: TextInputEditText,
    textInputLayout: TextInputLayout,
  )
}
