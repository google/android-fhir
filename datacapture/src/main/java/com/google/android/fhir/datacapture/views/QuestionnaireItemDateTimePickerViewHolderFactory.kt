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

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.entryFormat
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.subtitleText
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.fhir.datacapture.views.DatePickerFragment.Companion.REQUEST_BUNDLE_KEY_DATE
import com.google.android.fhir.datacapture.views.TimePickerFragment.Companion.REQUEST_BUNDLE_KEY_TIME
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemDateTimePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_date_time_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var questionTextView: TextView
      private lateinit var questionSubtitleTextView: TextView
      private lateinit var dateInputLayout: TextInputLayout
      private lateinit var dateInputEditText: TextInputEditText
      private lateinit var timeInputLayout: TextInputLayout
      private lateinit var timeInputEditText: TextInputEditText
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      private var localDate: LocalDate? = null
      private var localTime: LocalTime? = null

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix_text_view)
        questionTextView = itemView.findViewById(R.id.question_text_view)
        questionSubtitleTextView = itemView.findViewById(R.id.subtitle_text_view)
        dateInputLayout = itemView.findViewById(R.id.date_input_layout)
        dateInputEditText = itemView.findViewById(R.id.date_input_edit_text)

        dateInputLayout.setEndIconOnClickListener {
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
            localDate = LocalDate.of(year, month, dayOfMonth)
            dateInputEditText.setText(localDate?.format(LOCAL_DATE_FORMATTER) ?: "")
            var localDateTime: LocalDateTime? = null
            if (localTime != null) {
              localDateTime =
                LocalDateTime.of(
                  year,
                  // Month values are 1-12 in java.time but 0-11 in
                  // DatePickerDialog.
                  month + 1,
                  dayOfMonth,
                  localTime!!.hour,
                  localTime!!.minute,
                  localTime!!.second
                )
            } else {
              questionnaireItemViewItem.singleAnswerOrNull?.valueDateTimeType?.let {
                localDateTime =
                  LocalDateTime.of(year, month + 1, dayOfMonth, it.hour, it.minute, it.second)
              }
              localDateTime?.let {
                updateDateTimeInput(it)
                updateDateTimeAnswer(it)
              }
            }
            // Clear focus so that the user can refocus to open the dialog
            dateInputEditText.clearFocus()
          }

          val selectedDate =
            questionnaireItemViewItem.singleAnswerOrNull?.valueDateTimeType?.localDate

          DatePickerFragment()
            .apply { arguments = bundleOf(REQUEST_BUNDLE_KEY_DATE to selectedDate) }
            .show(context.supportFragmentManager, DatePickerFragment.TAG)
        }

        timeInputLayout = itemView.findViewById(R.id.time_input_layout)
        timeInputEditText = itemView.findViewById(R.id.time_input_edit_text)

        timeInputLayout.setEndIconOnClickListener {
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
            localTime = LocalTime.of(hour, minute, 0)
            timeInputEditText.setText(localTime?.format(LOCAL_TIME_FORMATTER) ?: "")
            var localDateTime: LocalDateTime? = null
            if (localDate != null) {
              localDateTime =
                LocalDateTime.of(
                  localDate!!.year,
                  localDate!!.month + 1,
                  localDate!!.dayOfMonth,
                  hour,
                  minute,
                  0
                )
            } else {
              questionnaireItemViewItem.singleAnswerOrNull?.valueDateTimeType?.let {
                localDateTime = LocalDateTime.of(it.year, it.month + 1, it.day, hour, minute, 0)
              }
            }
            localDateTime?.let {
              updateDateTimeInput(it)
              updateDateTimeAnswer(it)
            }
          }

          val selectedTime =
            questionnaireItemViewItem.singleAnswerOrNull?.valueDateTimeType?.localTime
          TimePickerFragment()
            .apply { arguments = bundleOf(REQUEST_BUNDLE_KEY_TIME to selectedTime) }
            .show(context.supportFragmentManager, DatePickerFragment.TAG)
        }
      }

      private fun addContentDescription() {
        dateInputLayout.contentDescription =
          questionnaireItemViewItem.questionnaireItem.linkId + dateInputLayout.toString()
        dateInputEditText.contentDescription =
          questionnaireItemViewItem.questionnaireItem.linkId + dateInputEditText.toString()
        timeInputLayout.contentDescription =
          questionnaireItemViewItem.questionnaireItem.linkId + timeInputLayout.toString()
        timeInputEditText.contentDescription =
          questionnaireItemViewItem.questionnaireItem.linkId + timeInputEditText.toString()
      }

      @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        addContentDescription()
        questionnaireItemViewItem.questionnaireItem.entryFormat?.let {
          dateInputLayout.helperText = it
        }

        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
        } else {
          prefixTextView.visibility = View.GONE
        }
        questionTextView.text = questionnaireItemViewItem.questionnaireItem.localizedTextSpanned
        questionSubtitleTextView.text = questionnaireItemViewItem.questionnaireItem.subtitleText
        val dateTime = questionnaireItemViewItem.singleAnswerOrNull?.valueDateTimeType
        updateDateTimeInput(
          dateTime?.let {
            LocalDateTime.of(it.year, it.month + 1, it.day, it.hour, it.minute, it.second)
          }
        )
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        dateInputLayout.error =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
        timeInputLayout.error =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        timeInputEditText.isEnabled = !isReadOnly
        dateInputEditText.isEnabled = !isReadOnly
        timeInputLayout.isEnabled = !isReadOnly
        dateInputLayout.isEnabled = !isReadOnly
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
        onAnswerChanged(prefixTextView.context)
      }
    }

  @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
  val LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE!!
  val LOCAL_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME!!
}

internal val DateTimeType.localDate
  get() =
    LocalDate.of(
      year,
      month + 1,
      day,
    )

internal val DateTimeType.localTime
  get() =
    LocalTime.of(
      hour,
      minute,
      second,
    )
