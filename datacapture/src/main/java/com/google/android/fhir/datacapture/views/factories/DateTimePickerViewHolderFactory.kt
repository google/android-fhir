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

package com.google.android.fhir.datacapture.views.factories

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.canonicalizeDatePattern
import com.google.android.fhir.datacapture.extensions.format
import com.google.android.fhir.datacapture.extensions.getDateSeparator
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.parseDate
import com.google.android.fhir.datacapture.extensions.toLocalizedString
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
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
import java.time.format.DateTimeParseException
import java.util.Date
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object DateTimePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.date_time_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: HeaderView
      private lateinit var dateInputLayout: TextInputLayout
      private lateinit var dateInputEditText: TextInputEditText
      private lateinit var timeInputLayout: TextInputLayout
      private lateinit var timeInputEditText: TextInputEditText
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
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
            questionnaireViewItem.answers.singleOrNull()?.valueDateTimeType?.localDate
          buildMaterialDatePicker(localDateInput)
            .apply {
              addOnPositiveButtonClickListener { epochMilli ->
                with(Instant.ofEpochMilli(epochMilli).atZone(ZONE_ID_UTC).toLocalDate()) {
                  dateInputEditText.setText(this?.format(canonicalizedDatePattern))
                  timeInputLayout.isEnabled = true
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
        timeInputLayout.isEnabled = false
        timeInputLayout.setEndIconOnClickListener {
          // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
          // and again in TextInputEditText during layout inflation. As a result, it is
          // necessary to access the base context twice to retrieve the application object
          // from the view's context.
          val context = itemView.context.tryUnwrapContext()!!
          buildMaterialTimePicker(context, INPUT_MODE_CLOCK)
        }
        timeInputEditText.setOnClickListener {
          buildMaterialTimePicker(itemView.context, INPUT_MODE_KEYBOARD)
        }
        val localeDatePattern = getLocalizedDateTimePattern()
        // Special character used in date pattern
        val datePatternSeparator = getDateSeparator(localeDatePattern)
        textWatcher = DatePatternTextWatcher(datePatternSeparator)
        canonicalizedDatePattern = canonicalizeDatePattern(localeDatePattern)
      }

      @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        clearPreviousState()
        header.bind(questionnaireViewItem)
        with(dateInputLayout) {
          // Use 'mm' for month instead of 'MM' to avoid confusion.
          // See https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat.
          hint = canonicalizedDatePattern.lowercase()
          helperText = getRequiredOrOptionalText(questionnaireViewItem, context)
        }
        dateInputEditText.removeTextChangedListener(textWatcher)

        val questionnaireItemViewItemDateTimeAnswer =
          questionnaireViewItem.answers.singleOrNull()?.valueDateTimeType?.localDateTime

        val dateStringToDisplay =
          questionnaireItemViewItemDateTimeAnswer?.toLocalDate()?.format(canonicalizedDatePattern)
            ?: questionnaireViewItem.draftAnswer as? String

        // Determine whether the text field text should be overridden or not.
        if (dateInputEditText.text.toString() != dateStringToDisplay) {
          dateInputEditText.setText(dateStringToDisplay)
        }

        enableOrDisableTimePicker(questionnaireViewItem, dateStringToDisplay)

        // If there is no set answer in the QuestionnaireItemViewItem, make the time field empty.
        timeInputEditText.setText(
          questionnaireItemViewItemDateTimeAnswer
            ?.toLocalTime()
            ?.toLocalizedString(timeInputEditText.context)
            ?: ""
        )
        dateInputEditText.addTextChangedListener(textWatcher)
      }

      private fun displayDateValidationError(validationResult: ValidationResult) {
        dateInputLayout.error =
          getValidationErrorMessage(
            dateInputLayout.context,
            questionnaireViewItem,
            validationResult
          )
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        // The system outside this delegate should only be able to mark it read only. Otherwise, it
        // will change the state set by this delegate in bindView().
        if (isReadOnly) {
          dateInputEditText.isEnabled = false
          dateInputLayout.isEnabled = false
          timeInputEditText.isEnabled = false
          timeInputLayout.isEnabled = false
        }
      }

      private fun buildMaterialDatePicker(localDate: LocalDate?): MaterialDatePicker<Long> {
        val selectedDateMillis =
          localDate?.atStartOfDay(ZONE_ID_UTC)?.toInstant()?.toEpochMilli()
            ?: MaterialDatePicker.todayInUtcMilliseconds()

        return MaterialDatePicker.Builder.datePicker()
          .setTitleText(R.string.select_date)
          .setSelection(selectedDateMillis)
          .build()
      }

      private fun buildMaterialTimePicker(context: Context, inputMode: Int) {
        val selectedTime =
          questionnaireViewItem.answers.singleOrNull()?.valueDateTimeType?.localTime
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
                timeInputEditText.setText(this.toLocalizedString(context))
                setQuestionnaireItemViewItemAnswer(
                  LocalDateTime.of(
                    parseDate(dateInputEditText.text.toString(), canonicalizedDatePattern),
                    this
                  )
                )
                timeInputEditText.clearFocus()
              }
            }
          }
          .show(context.tryUnwrapContext()!!.supportFragmentManager, TAG_TIME_PICKER)
      }

      /** Set the answer in the [QuestionnaireResponse]. */
      private fun setQuestionnaireItemViewItemAnswer(localDateTime: LocalDateTime) {
        questionnaireViewItem.setAnswer(
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

      private fun clearPreviousState() {
        dateInputEditText.isEnabled = true
        dateInputLayout.isEnabled = true
      }

      /* If the passed in date can be parsed, then enable the time picker, otherwise, keep the time
      picker disabled and display an error
       */
      private fun enableOrDisableTimePicker(
        questionnaireViewItem: QuestionnaireViewItem,
        dateToDisplay: String?
      ) =
        try {
          if (dateToDisplay != null) {
            parseDate(dateToDisplay, canonicalizedDatePattern)
            timeInputLayout.isEnabled = true
          }
          displayDateValidationError(questionnaireViewItem.validationResult)
        } catch (e: ParseException) {
          timeInputLayout.isEnabled = false
          displayDateValidationError(
            Invalid(
              listOf(invalidDateErrorText(dateInputEditText.context, canonicalizedDatePattern))
            )
          )
        } catch (e: DateTimeParseException) {
          timeInputLayout.isEnabled = false
          displayDateValidationError(
            Invalid(
              listOf(invalidDateErrorText(dateInputEditText.context, canonicalizedDatePattern))
            )
          )
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
          // Always set the draft answer because time is not input yet
          questionnaireViewItem.setDraftAnswer(editable.toString())
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
