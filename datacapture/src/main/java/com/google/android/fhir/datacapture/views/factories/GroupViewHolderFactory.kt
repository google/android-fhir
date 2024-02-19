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

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getNestedQuestionnaireResponseItems
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.GroupHeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object GroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.group_header_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: GroupHeaderView
      private lateinit var error: TextView
      private lateinit var addItemButton: Button
      override lateinit var questionnaireViewItem: QuestionnaireViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        error = itemView.findViewById(R.id.error)
        addItemButton = itemView.findViewById(R.id.add_item)
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        header.bind(questionnaireViewItem)
        addItemButton.visibility =
          if (questionnaireViewItem.questionnaireItem.repeats) View.VISIBLE else View.GONE
        addItemButton.setOnClickListener {
          questionnaireViewItem.addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              // TODO(jingtang10): This can be removed since we already do this in the
              // answerChangedCallback in the QuestionnaireViewModel.
              item = questionnaireViewItem.questionnaireItem.getNestedQuestionnaireResponseItems()
            },
          )
        }
        displayValidationResult(questionnaireViewItem.validationResult)
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        when (validationResult) {
          is NotValidated,
          Valid, -> error.visibility = View.GONE
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
