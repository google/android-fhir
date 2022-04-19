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

import android.view.View
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.material.slider.Slider
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemSliderViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_slider) {
  override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var slider: Slider
      private lateinit var error: TextView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        slider = itemView.findViewById(R.id.slider)
        error = itemView.findViewById(R.id.error)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        header.bind(questionnaireItemViewItem.questionnaireItem)
        val answer = questionnaireItemViewItem.singleAnswerOrNull
        slider.valueFrom = 0.0F
        slider.valueTo = 100.0F
        slider.stepSize = 10.0F
        val sliderValue = answer?.valueIntegerType?.value?.toString() ?: "0.0"
        slider.value = sliderValue.toFloat()

        slider.addOnChangeListener { _, newValue, _ ->
          // Responds to when slider's value is changed
          questionnaireItemViewItem.singleAnswerOrNull =
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(IntegerType(newValue.toInt()))
          onAnswerChanged(slider.context)
        }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        error.text =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        slider.isEnabled = !isReadOnly
      }
    }
}
