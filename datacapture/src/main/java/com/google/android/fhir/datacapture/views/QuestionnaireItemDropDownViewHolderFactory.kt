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

package com.google.android.fhir.datacapture.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.itemAnswerOptionImage
import com.google.android.fhir.datacapture.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemDropDownViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_drop_down_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
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
        cleanupOldState()
        header.bind(questionnaireItemViewItem.questionnaireItem)
        textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.localizedFlyoverSpanned
        val answerOptionList =
          this.questionnaireItemViewItem.answerOption
            .map { DropDownAnswerOption(it.displayString, it.itemAnswerOptionImage(context)) }
            .toMutableList()
        answerOptionList.add(0, DropDownAnswerOption(context.getString(R.string.hyphen), null))
        autoCompleteTextView.setText(
          questionnaireItemViewItem.answers.singleOrNull()?.displayString(header.context) ?: ""
        )
        val adapter =
          AnswerOptionDropDownArrayAdapter(
            context,
            R.layout.questionnaire_item_drop_down_list,
            answerOptionList
          )
        autoCompleteTextView.setText(
          questionnaireItemViewItem.answers.singleOrNull()?.displayString(header.context) ?: ""
        )
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.onItemClickListener =
          AdapterView.OnItemClickListener { _, _, position, _ ->
            autoCompleteTextView.setText(adapter.getItem(position)?.answerOptionString, false)
            if (position == 0) {
              questionnaireItemViewItem.clearAnswer()
            } else {
              questionnaireItemViewItem.setAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                  .setValue(questionnaireItemViewItem.answerOption[position - 1].value)
              )
            }
          }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        textInputLayout.error =
          when (validationResult) {
            is NotValidated,
            Valid -> null
            is Invalid -> validationResult.getSingleStringValidationMessage()
          }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        textInputLayout.isEnabled = !isReadOnly
      }

      private fun cleanupOldState() {
        autoCompleteTextView.setAdapter(null)
      }
    }
}

internal class AnswerOptionDropDownArrayAdapter(
  context: Context,
  private val layoutResourceId: Int,
  answerOption: List<DropDownAnswerOption>
) : ArrayAdapter<DropDownAnswerOption>(context, layoutResourceId, answerOption) {
  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    var convertView = convertView
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.context).inflate(layoutResourceId, parent, false)
    }
    try {
      val answerOption: DropDownAnswerOption? = getItem(position)
      val answerOptionTextView =
        convertView?.findViewById<View>(R.id.answer_option_textview) as TextView
      answerOptionTextView.text = answerOption?.answerOptionString
      answerOptionTextView.setCompoundDrawables(answerOption?.answerOptionImage, null, null, null)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return convertView!!
  }
}

internal data class DropDownAnswerOption(
  val answerOptionString: String,
  val answerOptionImage: Drawable?
)
