/*
 * Copyright 2022-2025 Google LLC
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
import android.graphics.drawable.Drawable
import android.text.Spanned
import android.text.method.TextKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.identifierString
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.extensions.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.extensions.toSpanned
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse
import timber.log.Timber

internal object DropDownViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.drop_down_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: HeaderView
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var autoCompleteTextView: MaterialAutoCompleteTextView
      private lateinit var clearInputIcon: ImageView
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var context: AppCompatActivity

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        textInputLayout = itemView.findViewById(R.id.text_input_layout)
        autoCompleteTextView = itemView.findViewById(R.id.auto_complete)
        clearInputIcon = itemView.findViewById(R.id.clear_input_icon)
        context = itemView.context.tryUnwrapContext()!!
        autoCompleteTextView.setOnFocusChangeListener { view, hasFocus ->
          if (!hasFocus) {
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
              .hideSoftInputFromWindow(view.windowToken, 0)
          }
        }
        clearInputIcon.setOnClickListener {
          context.lifecycleScope.launch {
            questionnaireViewItem.clearAnswer()
            autoCompleteTextView.doOnNextLayout { autoCompleteTextView.showDropDown() }
          }
        }
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        cleanupOldState()
        header.bind(questionnaireViewItem)
        with(textInputLayout) {
          hint = questionnaireViewItem.enabledDisplayItems.localizedFlyoverSpanned
          helperText = getRequiredOrOptionalText(questionnaireViewItem, context)
        }
        val answerOptionList =
          this.questionnaireViewItem.enabledAnswerOptions
            .map {
              DropDownAnswerOption(
                it.value.identifierString(context),
                it.value.displayString(context),
                it.itemAnswerOptionImage(context),
              )
            }
            .toMutableList()
        answerOptionList.add(
          0,
          DropDownAnswerOption(
            context.getString(R.string.hyphen),
            context.getString(R.string.hyphen),
            null,
          ),
        )
        val adapter =
          AnswerOptionDropDownArrayAdapter(context, R.layout.drop_down_list_item, answerOptionList)
        val selectedAnswerIdentifier =
          questionnaireViewItem.answers.singleOrNull()?.value?.identifierString(header.context)
        answerOptionList
          .firstOrNull { it.answerId == selectedAnswerIdentifier }
          ?.let {
            autoCompleteTextView.setText(it.answerOptionStringSpanned())
            autoCompleteTextView.setSelection(it.answerOptionStringSpanned().length)
            autoCompleteTextView.setCompoundDrawablesRelative(
              it.answerOptionImage,
              null,
              null,
              null,
            )
          }
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.onItemClickListener =
          AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)
            autoCompleteTextView.setText(selectedItem?.answerOptionStringSpanned(), false)
            autoCompleteTextView.setCompoundDrawablesRelative(
              adapter.getItem(position)?.answerOptionImage,
              null,
              null,
              null,
            )
            val selectedAnswer =
              questionnaireViewItem.enabledAnswerOptions
                .firstOrNull { it.value.identifierString(context) == selectedItem?.answerId }
                ?.value

            context.lifecycleScope.launch {
              if (selectedAnswer == null) {
                questionnaireViewItem.clearAnswer()
              } else {
                questionnaireViewItem.setAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(selectedAnswer),
                )
              }
            }
          }
        val isEditable = questionnaireViewItem.answers.isEmpty()
        if (!isEditable) autoCompleteTextView.clearFocus()
        autoCompleteTextView.keyListener = if (isEditable) TextKeyListener.getInstance() else null
        clearInputIcon.visibility = if (isEditable) View.GONE else View.VISIBLE

        displayValidationResult(questionnaireViewItem.validationResult)
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        textInputLayout.error =
          getValidationErrorMessage(
            textInputLayout.context,
            questionnaireViewItem,
            validationResult,
          )
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        textInputLayout.isEnabled = !isReadOnly
      }

      private fun cleanupOldState() {
        autoCompleteTextView.setAdapter(null)
        autoCompleteTextView.text = null
        autoCompleteTextView.setCompoundDrawablesRelative(null, null, null, null)
      }
    }
}

internal class AnswerOptionDropDownArrayAdapter(
  context: Context,
  private val layoutResourceId: Int,
  answerOption: List<DropDownAnswerOption>,
) : ArrayAdapter<DropDownAnswerOption>(context, layoutResourceId, answerOption) {
  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val listItemView =
      convertView ?: LayoutInflater.from(parent.context).inflate(layoutResourceId, parent, false)
    try {
      val answerOption: DropDownAnswerOption? = getItem(position)
      val answerOptionTextView =
        listItemView?.findViewById<View>(R.id.answer_option_textview) as TextView
      answerOptionTextView.text = answerOption?.answerOptionStringSpanned()
      answerOptionTextView.setCompoundDrawablesRelative(
        answerOption?.answerOptionImage,
        null,
        null,
        null,
      )
    } catch (e: Exception) {
      Timber.w("Could not set data to dropdown UI", e)
    }
    return listItemView
  }
}

internal data class DropDownAnswerOption(
  val answerId: String,
  val answerOptionString: String,
  val answerOptionImage: Drawable? = null,
) {
  override fun toString(): String {
    return this.answerOptionString
  }

  fun answerOptionStringSpanned(): Spanned = answerOptionString.toSpanned()
}
