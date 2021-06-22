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

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseItemValidator
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemDateTimePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_date_time_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var textDateQuestion: TextView
      private lateinit var dateInputEditText: TextInputEditText
      private lateinit var textTimeQuestion: TextView
      private lateinit var timeInputEditText: TextInputEditText
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix)
        textDateQuestion = itemView.findViewById(R.id.date_question)
        dateInputEditText = itemView.findViewById(R.id.dateInputEditText)
        // Disable direct text input to only allow input from the date picker dialog
        dateInputEditText.keyListener = null
        dateInputEditText.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
          // Do not show the date picker dialog when losing focus.
          if (!hasFocus) {
            applyValidationResult(
              QuestionnaireResponseItemValidator.validate(
                questionnaireItemViewItem.questionnaireItem,
                questionnaireItemViewItem.questionnaireResponseItem,
                view.context
              )
            )
            return@setOnFocusChangeListener
          }

          // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
          // and again in TextInputEditText during layout inflation. As a result, it is
          // necessary to access the base context twice to retrieve the application object
          // from the view's context.
          val context = itemView.context.tryUnwrapContext()!!
          context.supportFragmentManager.setFragmentResultListener(
            DatePickerFragment.RESULT_REQUEST_KEY,
            context
          ) { _, result ->
            val year = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_YEAR)
            val month = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_MONTH)
            val dayOfMonth = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_DAY_OF_MONTH)
            val localDateTime =
              LocalDateTime.of(
                year,
                // Month values are 1-12 in java.time but 0-11 in
                // DatePickerDialog.
                month + 1,
                dayOfMonth,
                0,
                0,
                0
              )
            updateDateTimeInput(localDateTime)
            updateDateTimeAnswer(localDateTime)
          }
          DatePickerFragment().show(context.supportFragmentManager, DatePickerFragment.TAG)
          // Clear focus so that the user can refocus to open the dialog
          textDateQuestion.clearFocus()
        }

        textTimeQuestion = itemView.findViewById(R.id.time_question)
        timeInputEditText = itemView.findViewById(R.id.timeInputEditText)
        // Disable direct text input to only allow input from the time picker dialog
        timeInputEditText.keyListener = null
        timeInputEditText.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
          // Do not show the date picker dialog when losing focus.
          if (!hasFocus) return@setOnFocusChangeListener

          // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
          // and again in TextInputEditText during layout inflation. As a result, it is
          // necessary to access the base context twice to retrieve the application object
          // from the view's context.
          val context = itemView.context.tryUnwrapContext()!!
          context.supportFragmentManager.setFragmentResultListener(
            TimePickerFragment.RESULT_REQUEST_KEY,
            context
          ) { _, result ->
            val hour = result.getInt(TimePickerFragment.RESULT_BUNDLE_KEY_HOUR)
            val minute = result.getInt(TimePickerFragment.RESULT_BUNDLE_KEY_MINUTE)
            val localDate = questionnaireItemViewItem.singleAnswerOrNull!!.valueDateTimeType
            val localDateTime =
              LocalDateTime.of(localDate.year, localDate.month + 1, localDate.day, hour, minute, 0)
            updateDateTimeInput(localDateTime)
            updateDateTimeAnswer(localDateTime)
          }
          TimePickerFragment().show(context.supportFragmentManager, TimePickerFragment.TAG)
          // Clear focus so that the user can refocus to open the dialog
          textTimeQuestion.clearFocus()
        }
      }

      @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefix
        } else {
          prefixTextView.visibility = View.GONE
        }
        textDateQuestion.text = questionnaireItemViewItem.questionnaireItem.localizedText
        textTimeQuestion.text = questionnaireItemViewItem.questionnaireItem.localizedText
        val dateTime = questionnaireItemViewItem.singleAnswerOrNull?.valueDateTimeType
        updateDateTimeInput(
          dateTime?.let {
            LocalDateTime.of(it.year, it.month + 1, it.day, it.hour, it.minute, it.second)
          }
        )
      }

      /** Update the date and time input fields in the UI. */
      fun updateDateTimeInput(localDateTime: LocalDateTime?) {
        timeInputEditText.isEnabled = localDateTime != null
        dateInputEditText.setText(localDateTime?.format(LOCAL_DATE_FORMATTER) ?: "")
        timeInputEditText.setText(localDateTime?.format(LOCAL_TIME_FORMATTER) ?: "")
      }

      /** Updates the recorded answer. */
      fun updateDateTimeAnswer(localDateTime: LocalDateTime) {
        questionnaireItemViewItem.singleAnswerOrNull =
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
            .setValue(
              DateTimeType(
                Date(
                  localDateTime.year - 1900,
                  localDateTime.monthValue - 1,
                  localDateTime.dayOfMonth,
                  localDateTime.hour,
                  localDateTime.minute,
                  localDateTime.second
                )
              )
            )
        questionnaireItemViewItem.questionnaireResponseItemChangedCallback()
      }

      private fun applyValidationResult(validationResult: ValidationResult) {
        val validationMessage =
          validationResult.validationMessages.joinToString {
            it.plus(System.getProperty("line.separator"))
          }
        dateInputEditText.error = if (validationMessage == "") null else validationMessage
        timeInputEditText.error = if (validationMessage == "") null else validationMessage
      }
    }

  @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
  val LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE!!
  val LOCAL_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME!!
}
