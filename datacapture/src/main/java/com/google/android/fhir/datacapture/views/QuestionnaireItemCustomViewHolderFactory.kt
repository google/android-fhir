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
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.QuestionnaireFragment
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
import org.hl7.fhir.r4.model.StringType

object QuestionnaireItemCustomViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_custom_view) {
  override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var questionText: TextView
      private lateinit var questionSubtitleTextView: TextView
      private lateinit var launchButton: Button
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var textInputEditText: TextInputEditText
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      override var fragment: QuestionnaireFragment? = null

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix_text_view)
        questionText = itemView.findViewById(R.id.question_text_view)
        questionSubtitleTextView = itemView.findViewById(R.id.subtitle_text_view)
        launchButton = itemView.findViewById(R.id.launch)
        textInputLayout = itemView.findViewById(R.id.text_input_layout)
        textInputEditText = itemView.findViewById(R.id.text_input_edit_text)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
        } else {
          prefixTextView.visibility = View.GONE
        }
        val questionnaireItem = questionnaireItemViewItem.questionnaireItem
        val answer = questionnaireItemViewItem.singleAnswerOrNull
        questionText.text = questionnaireItem.localizedTextSpanned
        questionSubtitleTextView.text = questionnaireItem.subtitleText

        textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.flyOverText
        textInputEditText.setText(getText(questionnaireItemViewItem.singleAnswerOrNull))
        textInputEditText.doAfterTextChanged { editable: Editable? ->
          questionnaireItemViewItem.singleAnswerOrNull = getValue(editable.toString())
          onAnswerChanged(textInputEditText.context)
        }

        launchButton.setOnClickListener {
          fragment?.startForResult?.launch(Intent("com.google.android.fhir.datacapture.INPUT"))
        }
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
}
