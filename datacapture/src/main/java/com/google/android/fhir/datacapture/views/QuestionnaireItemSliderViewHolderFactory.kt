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
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.sliderStepValue
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.MaxValueConstraintValidator
import com.google.android.fhir.datacapture.validation.MinValueConstraintValidator
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.material.slider.Slider
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
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
        val answer = questionnaireItemViewItem.answers.singleOrNull()
        val minValue = getMinValue(questionnaireItemViewItem.questionnaireItem)
        val maxValue = getMaxValue(questionnaireItemViewItem.questionnaireItem)
        if (minValue >= maxValue) {
          throw IllegalStateException("minValue $minValue must be smaller than maxValue $maxValue")
        }

        with(slider) {
          clearOnChangeListeners()
          valueFrom = minValue
          valueTo = maxValue
          stepSize =
            (questionnaireItemViewItem.questionnaireItem.sliderStepValue
                ?: SLIDER_DEFAULT_STEP_SIZE)
              .toFloat()
          value = answer?.valueIntegerType?.value?.toFloat() ?: valueFrom

          addOnChangeListener { _, newValue, _ ->
            // Responds to when slider's value is changed
            questionnaireItemViewItem.setAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                .setValue(IntegerType(newValue.toInt()))
            )
          }
        }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        error.text =
          when (validationResult) {
            is NotValidated,
            Valid -> null
            is Invalid -> validationResult.getSingleStringValidationMessage()
          }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        slider.isEnabled = !isReadOnly
      }
    }
}

private const val SLIDER_DEFAULT_STEP_SIZE = 1
private const val SLIDER_DEFAULT_VALUE_FROM = 0.0F
private const val SLIDER_DEFAULT_VALUE_TO = 100.0F

private fun getMinValue(questionnaireItem: Questionnaire.QuestionnaireItemComponent) =
  when (val minValue = MinValueConstraintValidator.getMinValue(questionnaireItem)) {
    is IntegerType -> minValue.value.toFloat()
    null -> SLIDER_DEFAULT_VALUE_FROM
    else -> throw IllegalArgumentException("Cannot support data type: ${minValue.fhirType()}}")
  }

private fun getMaxValue(questionnaireItem: Questionnaire.QuestionnaireItemComponent) =
  when (val maxValue = MaxValueConstraintValidator.getMaxValue(questionnaireItem)) {
    is IntegerType -> maxValue.value.toFloat()
    null -> SLIDER_DEFAULT_VALUE_TO
    else -> throw IllegalArgumentException("Cannot support data type: ${maxValue.fhirType()}}")
  }
