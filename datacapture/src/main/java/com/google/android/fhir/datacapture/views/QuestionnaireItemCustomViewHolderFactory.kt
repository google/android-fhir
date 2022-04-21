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

import android.content.Intent
import android.text.Editable
import android.view.View
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

object QuestionnaireItemCustomViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_custom_view) {
  override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var launchButton: Button
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var textInputEditText: TextInputEditText
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      override var fragment: QuestionnaireFragment? = null

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        launchButton = itemView.findViewById(R.id.launch)
        textInputLayout = itemView.findViewById(R.id.text_input_layout)
        textInputEditText = itemView.findViewById(R.id.text_input_edit_text)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        val questionnaireItem = questionnaireItemViewItem.questionnaireItem
        val answer = questionnaireItemViewItem.singleAnswerOrNull

        header.bind(questionnaireItem)

        textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.localizedFlyoverSpanned
        textInputEditText.setText(getText(questionnaireItemViewItem.singleAnswerOrNull))
        textInputEditText.doAfterTextChanged { editable: Editable? ->
          questionnaireItemViewItem.singleAnswerOrNull = getValue(editable.toString())
          onAnswerChanged(textInputEditText.context)
        }

        launchButton.setOnClickListener { fragment?.startForResult?.launch(Intent(INPUT_ACTION)) }
      }

      override fun setFragmentForResult(hostFragment: QuestionnaireFragment?) {
        fragment = hostFragment
        fragment?.intentLiveData?.observe(fragment!!) {
          val result = it?.getStringExtra("result") ?: ""
          textInputEditText.setText(result)
          questionnaireItemViewItem?.singleAnswerOrNull = getValue(result)
          onAnswerChanged(textInputEditText.context)
        }
      }

      fun getText(answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent?): String {
        return answer?.valueStringType?.value ?: ""
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        textInputLayout.isEnabled = false
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        textInputLayout.error =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
      }

      fun getValue(text: String): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? {
        return text.let {
          if (it.isEmpty()) {
            null
          } else {
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(StringType(it))
          }
        }
      }
    }

  internal const val INPUT_ACTION = "com.google.android.fhir.datacapture.INPUT"
}
