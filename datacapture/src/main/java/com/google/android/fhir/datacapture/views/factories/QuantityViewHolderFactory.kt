/*
 * Copyright 2022-2023 Google LLC
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
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.extensions.unitOption
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuantityViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.quantity_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireViewItem: QuestionnaireViewItem

      private lateinit var header: HeaderView
      protected lateinit var textInputLayout: TextInputLayout
      private lateinit var textInputEditText: TextInputEditText
      private lateinit var unitTextInputLayout: TextInputLayout
      private lateinit var unitAutoCompleteTextView: MaterialAutoCompleteTextView
      private var textWatcher: TextWatcher? = null
      private lateinit var context: Context

      override fun init(itemView: View) {
        context = itemView.context
        header = itemView.findViewById(R.id.header)
        textInputLayout = itemView.findViewById(R.id.text_input_layout)
        textInputEditText =
          itemView.findViewById<TextInputEditText?>(R.id.text_input_edit_text).apply {
            setRawInputType(QUANTITY_INPUT_TYPE)
            // Override `setOnEditorActionListener` to avoid crash with `IllegalStateException` if
            // it's not possible to move focus forward.
            // See
            // https://stackoverflow.com/questions/13614101/fatal-crash-focus-search-returned-a-view-that-wasnt-able-to-take-focus/47991577
            setOnEditorActionListener { view, actionId, _ ->
              if (actionId != EditorInfo.IME_ACTION_NEXT) {
                false
              }
              view.focusSearch(View.FOCUS_DOWN)?.requestFocus(View.FOCUS_DOWN) ?: false
            }
            setOnFocusChangeListener { view, focused ->
              if (!focused) {
                (view.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager)
                  .hideSoftInputFromWindow(view.windowToken, 0)

                // Update answer even if the text box loses focus without any change. This will mark
                // the
                // questionnaire response item as being modified in the view model and trigger
                // validation.
                handleInput(textInputEditText.editableText, null)
              }
            }
          }

        unitTextInputLayout = itemView.findViewById(R.id.unit_text_input_layout)
        unitAutoCompleteTextView =
          itemView.findViewById<MaterialAutoCompleteTextView?>(R.id.unit_auto_complete).apply {
            onItemClickListener =
              AdapterView.OnItemClickListener { _, _, position, _ ->
                handleInput(
                  null,
                  questionnaireViewItem.questionnaireItem.unitOption[position],
                )
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
        updateUI()
        textWatcher =
          textInputEditText.doAfterTextChanged { editable: Editable? ->
            handleInput(editable!!, null)
          }
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        textInputLayout.error =
          when (validationResult) {
            is NotValidated,
            Valid -> null
            is Invalid -> validationResult.getSingleStringValidationMessage()
          }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        textInputLayout.isEnabled = !isReadOnly
        textInputEditText.isEnabled = !isReadOnly
        unitTextInputLayout.isEnabled = !isReadOnly
        unitAutoCompleteTextView.isEnabled = !isReadOnly
      }

      private fun handleInput(editable: Editable?, unitDropDown: Coding?) {
        var decimal: BigDecimal? = null
        var unit: Coding? = null

        // Read decimal value and unit from complete answer
        questionnaireViewItem.answers.singleOrNull()?.let {
          val quantity = it.value as Quantity
          decimal = quantity.value
          unit = Coding(quantity.system, quantity.code, quantity.unit)
        }

        // Read decimal value and unit from partial answer
        questionnaireViewItem.draftAnswer?.let {
          when (it) {
            is BigDecimal -> decimal = it
            is Coding -> unit = it
          }
        }

        // Update decimal value and unit
        editable?.toString()?.let { decimal = it.toBigDecimalOrNull() }
        unitDropDown?.let { unit = it }

        if (decimal == null && unit == null) {
          questionnaireViewItem.clearAnswer()
        } else if (decimal == null) {
          questionnaireViewItem.setDraftAnswer(unit)
        } else if (unit == null) {
          questionnaireViewItem.setDraftAnswer(decimal)
        } else {
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Quantity(null, decimal!!.toDouble(), unit!!.system, unit!!.code, unit!!.display)
            }
          )
        }
      }

      private fun updateUI() {
        val text =
          questionnaireViewItem.answers.singleOrNull()?.valueQuantity?.value?.toString()
            ?: questionnaireViewItem.draftAnswer?.let {
              if (it is BigDecimal) it.toString() else ""
            }
              ?: ""
        if (isTextUpdatesRequired(text, textInputEditText.text.toString())) {
          textInputEditText.setText(text)
        }

        val unit =
          questionnaireViewItem.answers.singleOrNull()?.valueQuantity?.let {
            Coding(it.system, it.code, it.unit)
          }
            ?: questionnaireViewItem.draftAnswer?.let { if (it is Coding) it else null }
        unitAutoCompleteTextView.setText(unit?.display ?: "")

        val unitAdapter =
          AnswerOptionDropDownArrayAdapter(
            context,
            R.layout.drop_down_list_item,
            questionnaireViewItem.questionnaireItem.unitOption.map {
              DropDownAnswerOption(it.code, it.display)
            }
          )
        unitAutoCompleteTextView.setAdapter(unitAdapter)
      }

      private fun isTextUpdatesRequired(answerText: String, inputText: String): Boolean {
        if (answerText.isEmpty() && inputText.isEmpty()) {
          return false
        }
        if (answerText.isEmpty() || inputText.isEmpty()) {
          return true
        }
        // Avoid shifting focus by updating text field if the values are the same
        return answerText.toDouble() != inputText.toDouble()
      }
    }
}

const val QUANTITY_INPUT_TYPE = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
