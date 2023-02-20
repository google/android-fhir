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

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.getNestedQuestionnaireResponseItems
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_group_header_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: QuestionnaireGroupTypeHeaderView
      private lateinit var error: TextView
      private lateinit var addItemButton: Button
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        error = itemView.findViewById(R.id.error)
        addItemButton = itemView.findViewById(R.id.add_item)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        header.bind(questionnaireItemViewItem.questionnaireItem)
        addItemButton.visibility =
          if (questionnaireItemViewItem.questionnaireItem.repeats) View.VISIBLE else View.GONE
        addItemButton.setOnClickListener {
          questionnaireItemViewItem.addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              item =
                questionnaireItemViewItem.questionnaireItem.getNestedQuestionnaireResponseItems()
            }
          )
        }
        displayValidationResult(questionnaireItemViewItem.validationResult)
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        when (validationResult) {
          is NotValidated,
          Valid -> error.visibility = View.GONE
          is Invalid -> {
            error.text = validationResult.getSingleStringValidationMessage()
            error.visibility = View.VISIBLE
          }
        }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        // No user input
      }
    }
}
