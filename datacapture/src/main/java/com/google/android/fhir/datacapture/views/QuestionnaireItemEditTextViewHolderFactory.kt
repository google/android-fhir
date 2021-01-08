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
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

object QuestionnaireItemEditTextViewHolderFactory : QuestionnaireItemViewHolderFactory(
  R.layout.questionnaire_item_edit_text_view
) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var textInputEditText: TextInputEditText
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        textInputLayout = itemView.findViewById(R.id.textInputLayout)
        textInputEditText = itemView.findViewById(R.id.textInputEditText)
        textInputEditText.doAfterTextChanged { editable: Editable? ->
          questionnaireItemViewItem.singleAnswerOrNull =
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .apply {
                value = StringType(editable.toString())
              }
        }
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        textInputLayout.hint = questionnaireItemViewItem.questionnaireItemComponent.text
        textInputEditText.setText(
          questionnaireItemViewItem.singleAnswerOrNull?.valueStringType?.value ?: ""
        )
      }
    }
}
