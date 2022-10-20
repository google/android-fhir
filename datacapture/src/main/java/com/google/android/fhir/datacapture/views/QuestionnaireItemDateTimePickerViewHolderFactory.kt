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

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.utilities.formattedString
import com.google.android.fhir.datacapture.utilities.toLocalizedString
import com.google.android.fhir.datacapture.utilities.toLocalizedTimeString
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.ParseException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemDateTimePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_date_time_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var dateInputLayout: TextInputLayout
      private lateinit var dateInputEditText: TextInputEditText
      private lateinit var timeInputLayout: TextInputLayout
      private lateinit var timeInputEditText: TextInputEditText
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      private var localDate: LocalDate? = null
      private var localTime: LocalTime? = null
      private var textWatcher: TextWatcher? = null
      private val localeDatePattern = "dd-MM-yyyy"
      //        DateTimeFormatterBuilder.getLocalizedDateTimePattern(
      //          FormatStyle.SHORT,
      //          null,
      //          IsoChronology.INSTANCE,
      //          Locale.getDefault()
      //        )

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        dateInputLayout = itemView.findViewById(R.id.date_input_layout)
        dateInputEditText = itemView.findViewById(R.id.date_input_edit_text)
        dateInputEditText.inputType = InputType.TYPE_NULL
        dateInputEditText.setOnFocusChangeListener { view, hasFocus ->
          if (!hasFocus) {
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
              .hideSoftInputFromWindow(view.windowToken, 0)
          }
        }
        dateInputLayout.setEndIconOnClickListener {
          // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
          // and again in TextInputEditText during layout inflation. As a result, it is
          // necessary to access the base context twice to retrieve the application object
          // from the view's context.
          val context = itemView.context.tryUnwrapContext()!!
          val localDateInput =
            localDate
              ?: questionnaireItemViewItem.answers.singleOrNull()?.valueDateTimeType?.localDate
          createMaterialDatePicker(localDateInput)
            .apply {
              addOnPositiveButtonClickListener { epochMilli ->
                with(Instant.ofEpochMilli(epochMilli).atZone(ZONE_ID_UTC).toLocalDate()) {
                  dateInputEditText.setText(this.formattedString(localeDatePattern))
                  localDate = this
                  enableOrDisableTimePicker(enableIt = true)
                  generateLocalDateTime(this, localTime)?.let {
                    updateDateTimeInput(it)
                    updateDateTimeAnswer(it)
                  }
                }
                // Clear focus so that the user can refocus to open the dialog
                dateInputEditText.clearFocus()
              }
            }
            .show(context.supportFragmentManager, TAG)
        }

        timeInputLayout = itemView.findViewById(R.id.time_input_layout)
        timeInputEditText = itemView.findViewById(R.id.time_input_edit_text)
        timeInputEditText.inputType = InputType.TYPE_NULL
        timeInputLayout.setEndIconOnClickListener {
          // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
          // and again in TextInputEditText during layout inflation. As a result, it is
          // necessary to access the base context twice to retrieve the application object
          // from the view's context.
          val context = itemView.context.tryUnwrapContext()!!
          createMaterialTimePicker(context)
            .apply {
              addOnPositiveButtonClickListener {
                with(LocalTime.of(this.hour, this.minute, 0)) {
                  localTime = this
                  timeInputEditText.setText(this.toLocalizedString(context))
                  generateLocalDateTime(localDate, this)?.let {
                    updateDateTimeInput(it)
                    updateDateTimeAnswer(it)
                  }
                  timeInputEditText.clearFocus()
                }
              }
            }
            .show(context.supportFragmentManager, TAG_TIME_PICKER)
        }
      }

      @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        clearPreviousState()
        header.bind(questionnaireItemViewItem.questionnaireItem)
        dateInputLayout.hint = localeDatePattern
        dateInputEditText.removeTextChangedListener(textWatcher)
        val dateTime = questionnaireItemViewItem.answers.singleOrNull()?.valueDateTimeType
        updateDateTimeInput(
          dateTime?.let {
            it.localDateTime.also {
              localDate = it.toLocalDate()
              localTime = it.toLocalTime()
            }
          }
        )
        textWatcher =
          dateInputEditText.doAfterTextChanged { text ->
            if (text == null || text.isNullOrEmpty()) {
              questionnaireItemViewItem.clearAnswer()
              return@doAfterTextChanged
            }
            try {
              localDate = parseDate(text.toString(), dateInputEditText.context.applicationContext)
              enableOrDisableTimePicker(enableIt = true)
              generateLocalDateTime(localDate, localTime)?.run {
                updateDateTimeInput(this)
                updateDateTimeAnswer(this)
              }
            } catch (e: ParseException) {
              displayDateValidationError(
                Invalid(
                  listOf(
                    dateInputEditText.context.getString(
                      R.string.date_format_validation_error_msg,
                      localeDatePattern
                    )
                  )
                )
              )
              if (!timeInputLayout.isEnabled) {
                displayTimeValidationError(Valid)
              }
              if (questionnaireItemViewItem.answers.isNotEmpty()) {
                questionnaireItemViewItem.clearAnswer()
              }
              localDate = null
              enableOrDisableTimePicker(enableIt = false)
            }
          }
      }

      fun displayDateValidationError(validationResult: ValidationResult) {
        dateInputLayout.error =
          when (validationResult) {
            is NotValidated,
            Valid -> null
            is Invalid -> validationResult.getSingleStringValidationMessage()
          }
      }

      fun displayTimeValidationError(validationResult: ValidationResult) {
        timeInputLayout.error =
          when (validationResult) {
            is NotValidated,
            Valid -> null
            is Invalid ->
              if (timeInputLayout.isEnabled) {
                validationResult.getSingleStringValidationMessage()
              } else {
                null
              }
          }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        displayDateValidationError(validationResult)
        displayTimeValidationError(validationResult)
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        // The system outside this delegate should only be able to mark it read only. Otherwise, it
        // will change the state set by this delegate in bindView().
        if (isReadOnly) {
          setReadOnlyInternal(isReadOnly = true)
        }
      }

      private fun setReadOnlyInternal(isReadOnly: Boolean) {
        timeInputEditText.isEnabled = !isReadOnly
        dateInputEditText.isEnabled = !isReadOnly
        timeInputLayout.isEnabled = !isReadOnly
        dateInputLayout.isEnabled = !isReadOnly
      }

      /** Update the date and time input fields in the UI. */
      private fun updateDateTimeInput(localDateTime: LocalDateTime?) {
        enableOrDisableTimePicker(enableIt = localDateTime != null)
        if (isTextUpdateRequired(
            dateInputEditText.context,
            localDateTime,
            dateInputEditText.text.toString()
          )
        ) {
          dateInputEditText.setText(
            localDateTime?.toLocalDate()?.formattedString(localeDatePattern) ?: ""
          )
        }
        timeInputEditText.setText(
          localDateTime?.toLocalizedTimeString(timeInputEditText.context) ?: ""
        )
      }

      /** Updates the recorded answer. */
      private fun updateDateTimeAnswer(localDateTime: LocalDateTime) {
        questionnaireItemViewItem.setAnswer(
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
        )
      }

      private fun generateLocalDateTime(
        localDate: LocalDate?,
        localTime: LocalTime?
      ): LocalDateTime? {
        return when {
          localDate != null && localTime != null -> {
            LocalDateTime.of(localDate, localTime)
          }
          localDate != null -> {
            questionnaireItemViewItem.answers.singleOrNull()?.valueDateTimeType?.let {
              LocalDateTime.of(localDate, it.localTime)
            }
          }
          else -> null
        }
      }

      private fun createMaterialDatePicker(localDate: LocalDate?): MaterialDatePicker<Long> {
        val selectedDateMillis =
          localDate?.atStartOfDay(ZONE_ID_UTC)?.toInstant()?.toEpochMilli()
            ?: MaterialDatePicker.todayInUtcMilliseconds()

        return MaterialDatePicker.Builder.datePicker()
          .setTitleText(R.string.select_date)
          .setSelection(selectedDateMillis)
          .build()
      }

      private fun createMaterialTimePicker(context: Context): MaterialTimePicker {
        val selectedTime =
          questionnaireItemViewItem.answers.singleOrNull()?.valueDateTimeType?.localTime
            ?: LocalTime.now()

        return MaterialTimePicker.Builder()
          .apply {
            setTitleText(R.string.select_time)
            setHour(selectedTime.hour)
            setMinute(selectedTime.minute)
            if (DateFormat.is24HourFormat(context)) {
              setTimeFormat(TimeFormat.CLOCK_24H)
            } else {
              setTimeFormat(TimeFormat.CLOCK_12H)
            }
          }
          .build()
      }

      private fun clearPreviousState() {
        localDate = null
        localTime = null
        setReadOnlyInternal(isReadOnly = false)
      }

      private fun enableOrDisableTimePicker(enableIt: Boolean) {
        timeInputLayout.isEnabled = enableIt
      }

      private fun isTextUpdateRequired(
        context: Context,
        answer: LocalDateTime?,
        inputText: String?
      ): Boolean {
        val inputDate =
          try {
            generateLocalDateTime(parseDate(inputText, context), localTime)
          } catch (e: Exception) {
            null
          }
        if (answer == null || inputDate == null) return true
        return answer.toLocalDate() != inputDate.toLocalDate()
      }
    }
}

private const val TAG_TIME_PICKER = "time-picker"

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

internal val DateTimeType.localDateTime
  get() =
    LocalDateTime.of(
      year,
      month + 1,
      day,
      hour,
      minute,
      second,
    )
