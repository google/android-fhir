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
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
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

      private fun addContentDescription() {
        textInputLayout.contentDescription =
          questionnaireItemViewItem.questionnaireItem.linkId + "_" + textInputLayout::class.java
        autoCompleteTextView.contentDescription =
          questionnaireItemViewItem.questionnaireItem.linkId +
            "_" +
            autoCompleteTextView::class.java
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        addContentDescription()
        cleanupOldState()
        header.bind(questionnaireItemViewItem.questionnaireItem)

        val answerOptionString =
          this.questionnaireItemViewItem.answerOption.map { it.displayString }.toMutableList()
        answerOptionString.add(0, context.getString(R.string.hyphen))
        val adapter =
          ArrayAdapterSetContentDescription(
            context,
            R.layout.questionnaire_item_drop_down_list,
            answerOptionString
          )
        autoCompleteTextView.setText(
          questionnaireItemViewItem.answers.singleOrNull()?.displayString(header.context) ?: ""
        )
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.onItemClickListener =
          AdapterView.OnItemClickListener { _, _, position, _ ->
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

  class ArrayAdapterSetContentDescription(
    context: Context,
    resource: Int,
    private val objects: MutableList<String>
  ) : ArrayAdapter<String>(context, resource, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
      val view = super.getView(position, convertView, parent)
      view.id = position
      view.contentDescription = objects[position] + "_" + position.toString()
      return view
    }
  }
}
