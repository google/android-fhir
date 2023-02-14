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
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.utilities.canonicalizeDatePattern
import com.google.android.fhir.datacapture.utilities.format
import com.google.android.fhir.datacapture.utilities.getDateSeparator
import com.google.android.fhir.datacapture.utilities.parseDate
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
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
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
      private lateinit var canonicalizedDatePattern: String
      private lateinit var textWatcher: DatePatternTextWatcher

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        dateInputLayout = itemView.findViewById(R.id.date_input_layout)
        dateInputEditText = itemView.findViewById(R.id.date_input_edit_text)
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
                  localDate = this
                  dateInputEditText.setText(localDate?.format(canonicalizedDatePattern))
                  enableOrDisableTimePicker(enableIt = true)
                  generateLocalDateTime(this, localTime)?.let {
                    updateDateTimeInput(it, canonicalizedDatePattern)
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
          showMaterialTimePicker(context, INPUT_MODE_CLOCK)
        }
        timeInputEditText.setOnClickListener {
          showMaterialTimePicker(itemView.context, INPUT_MODE_KEYBOARD)
        }
      }

      @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        clearPreviousState()
        header.bind(questionnaireItemViewItem.questionnaireItem)
        val localeDatePattern = getLocalizedDateTimePattern()
        // Special character used in date pattern
        val datePatternSeparator = getDateSeparator(localeDatePattern)
        textWatcher = DatePatternTextWatcher(datePatternSeparator)
        canonicalizedDatePattern = canonicalizeDatePattern(localeDatePattern)
        dateInputLayout.hint = canonicalizedDatePattern
        dateInputEditText.removeTextChangedListener(textWatcher)
        val dateTime = questionnaireItemViewItem.answers.singleOrNull()?.valueDateTimeType
        updateDateTimeInput(
          dateTime?.let {
            it.localDateTime.also {
              localDate = it.toLocalDate()
              localTime = it.toLocalTime()
            }
          },
          canonicalizedDatePattern
        )
        dateInputEditText.addTextChangedListener(textWatcher)
      }

      fun displayDateValidationError(validationResult: ValidationResult) {
        // Since the partial answer is still displayed in the text field, do not erase the error
        // text if the answer is cleared and the validation result is valid.
        if (questionnaireItemViewItem.answers.isEmpty() && validationResult == Valid) {
          return
        }
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
      private fun updateDateTimeInput(
        localDateTime: LocalDateTime?,
        canonicalizedDatePattern: String
      ) {
        enableOrDisableTimePicker(enableIt = localDateTime != null)
        val partialAnswerToDisplay = questionnaireItemViewItem.partialAnswer as? String
        val textToDisplayInTheTextField =
          localDateTime?.toLocalDate()?.format(canonicalizedDatePattern) ?: partialAnswerToDisplay

        // Since pull request #1822 has been merged, the same date format style is now used for both
        // accepting user date input and displaying the answer in the text field. For instance, the
        // "MM/dd/yyyy" format is employed to accept and display the date value. As a result, it is
        // possible to simply compare the
        // text field text to the partial or valid answer to determine whether the text field text
        // should be overridden or not.
        if (dateInputEditText.text.toString() != textToDisplayInTheTextField) {
          dateInputEditText.setText(textToDisplayInTheTextField)
          displayDateValidationError(Valid)
          enableOrDisableTimePicker(enableIt = true)
        }
        // Show an error text
        if (!partialAnswerToDisplay.isNullOrBlank()) {
          displayValidationResult(
            Invalid(
              listOf(
                dateInputEditText.context.getString(
                  R.string.date_format_validation_error_msg,
                  canonicalizedDatePattern,
                  canonicalizedDatePattern
                    .replace("dd", "01")
                    .replace("MM", "01")
                    .replace("yyyy", "2023")
                )
              )
            )
          )
        } else {
          displayValidationResult(NotValidated)
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

      private fun clearPreviousState() {
        localDate = null
        localTime = null
        setReadOnlyInternal(isReadOnly = false)
      }

      private fun enableOrDisableTimePicker(enableIt: Boolean) {
        timeInputLayout.isEnabled = enableIt
      }

      private fun showMaterialTimePicker(context: Context, inputMode: Int) {
        val selectedTime =
          questionnaireItemViewItem.answers.singleOrNull()?.valueDateTimeType?.localTime
            ?: LocalTime.now()
        val timeFormat =
          if (DateFormat.is24HourFormat(context)) {
            TimeFormat.CLOCK_24H
          } else {
            TimeFormat.CLOCK_12H
          }
        MaterialTimePicker.Builder()
          .setTitleText(R.string.select_time)
          .setHour(selectedTime.hour)
          .setMinute(selectedTime.minute)
          .setTimeFormat(timeFormat)
          .setInputMode(inputMode)
          .build()
          .apply {
            addOnPositiveButtonClickListener {
              with(LocalTime.of(this.hour, this.minute, 0)) {
                localTime = this
                timeInputEditText.setText(this.toLocalizedString(context))
                generateLocalDateTime(localDate, this)?.let {
                  updateDateTimeInput(it, canonicalizedDatePattern)
                  updateDateTimeAnswer(it)
                }
                timeInputEditText.clearFocus()
              }
            }
          }
          .show(context.tryUnwrapContext()!!.supportFragmentManager, TAG_TIME_PICKER)
      }

      private fun updateAnswerAfterTextChanged(text: String?) {
        if (text.isNullOrEmpty()) {
          questionnaireItemViewItem.clearAnswer()
          questionnaireItemViewItem.updatePartialAnswer(text.toString())
          return
        }
        try {
          localDate = parseDate(text, canonicalizedDatePattern)
          questionnaireItemViewItem.updatePartialAnswer(text.toString())
          displayDateValidationError(Valid)
          enableOrDisableTimePicker(enableIt = true)
          generateLocalDateTime(localDate, localTime)?.run {
            updateDateTimeInput(this, canonicalizedDatePattern)
            updateDateTimeAnswer(this)
          }
        } catch (e: ParseException) {
          displayDateValidationError(
            Invalid(
              listOf(
                dateInputEditText.context.getString(
                  R.string.date_format_validation_error_msg,
                  canonicalizedDatePattern,
                  canonicalizedDatePattern
                    .replace("dd", "31")
                    .replace("MM", "01")
                    .replace("yyyy", "2023")
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
          questionnaireItemViewItem.updatePartialAnswer(text.toString())
          localDate = null
          enableOrDisableTimePicker(enableIt = false)
        }
      }

      /** Automatically appends date separator (e.g. "/") during date input. */
      inner class DatePatternTextWatcher(private val datePatternSeparator: Char) : TextWatcher {
        private var isDeleting = false

        override fun beforeTextChanged(
          charSequence: CharSequence,
          start: Int,
          count: Int,
          after: Int
        ) {
          isDeleting = count > after
        }

        override fun onTextChanged(
          charSequence: CharSequence,
          start: Int,
          before: Int,
          count: Int
        ) {}

        override fun afterTextChanged(editable: Editable) {
          handleDateFormatAfterTextChange(
            editable,
            canonicalizedDatePattern,
            datePatternSeparator,
            isDeleting
          )
          updateAnswerAfterTextChanged(editable.toString())
        }
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
