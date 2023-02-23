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
import android.text.TextWatcher
import android.view.View
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.canonicalizeDatePattern
import com.google.android.fhir.datacapture.format
import com.google.android.fhir.datacapture.getDateSeparator
import com.google.android.fhir.datacapture.parseDate
import com.google.android.fhir.datacapture.tryUnwrapContext
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.MaxValueConstraintValidator.getMaxValue
import com.google.android.fhir.datacapture.validation.MinValueConstraintValidator.getMinValue
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.log10
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object DatePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_date_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: HeaderView
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var textInputEditText: TextInputEditText
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var canonicalizedDatePattern: String
      private lateinit var textWatcher: DatePatternTextWatcher

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        textInputLayout = itemView.findViewById(R.id.text_input_layout)
        textInputEditText = itemView.findViewById(R.id.text_input_edit_text)
        textInputLayout.setEndIconOnClickListener {
          // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
          // and again in TextInputEditText during layout inflation. As a result, it is
          // necessary to access the base context twice to retrieve the application object
          // from the view's context.
          val context = itemView.context.tryUnwrapContext()!!
          createMaterialDatePicker()
            .apply {
              addOnPositiveButtonClickListener { epochMilli ->
                val formattedDate =
                  Instant.ofEpochMilli(epochMilli)
                    .atZone(ZONE_ID_UTC)
                    .toLocalDate()
                    .format(canonicalizedDatePattern)
                textInputEditText.setText(formattedDate)
                questionnaireViewItem.setAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    val localDate =
                      Instant.ofEpochMilli(epochMilli).atZone(ZONE_ID_UTC).toLocalDate()
                    value = localDate.dateType
                  }
                )
                // Clear focus so that the user can refocus to open the dialog
                textInputEditText.clearFocus()
              }
            }
            .show(context.supportFragmentManager, TAG)
        }
      }

      @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        header.bind(questionnaireViewItem.questionnaireItem)
        val localeDatePattern = getLocalizedDateTimePattern()
        // Special character used in date pattern
        val datePatternSeparator = getDateSeparator(localeDatePattern)
        textWatcher = DatePatternTextWatcher(datePatternSeparator)
        canonicalizedDatePattern = canonicalizeDatePattern(localeDatePattern)
        textInputLayout.hint = canonicalizedDatePattern
        textInputEditText.removeTextChangedListener(textWatcher)
        updateTextFieldToDisplayDateValue()
        textInputEditText.addTextChangedListener(textWatcher)
        displayValidationResult(questionnaireViewItem.validationResult)
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        // Since the draft answer is still displayed in the text field, do not erase the error
        // text if the answer is cleared and the validation result is valid.
        if (questionnaireViewItem.answers.isEmpty() && validationResult == Valid) {
          return
        }
        textInputLayout.error =
          when (validationResult) {
            is NotValidated,
            Valid -> null
            is Invalid -> validationResult.getSingleStringValidationMessage()
          }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        textInputEditText.isEnabled = !isReadOnly
        textInputLayout.isEnabled = !isReadOnly
      }

      private fun createMaterialDatePicker(): MaterialDatePicker<Long> {
        val selectedDate =
          questionnaireViewItem.answers
            .singleOrNull()
            ?.valueDateType
            ?.localDate
            ?.atStartOfDay(ZONE_ID_UTC)
            ?.toInstant()
            ?.toEpochMilli()
            ?: MaterialDatePicker.todayInUtcMilliseconds()
        return MaterialDatePicker.Builder.datePicker()
          .setTitleText(R.string.select_date)
          .setSelection(selectedDate)
          .setCalendarConstraints(getCalenderConstraint())
          .build()
      }

      private fun getCalenderConstraint(): CalendarConstraints {
        val min = (getMinValue(questionnaireViewItem.questionnaireItem) as? DateType)?.value?.time
        val max = (getMaxValue(questionnaireViewItem.questionnaireItem) as? DateType)?.value?.time

        if (min != null && max != null && min > max) {
          throw IllegalArgumentException("minValue cannot be greater than maxValue")
        }

        val listValidators = ArrayList<DateValidator>()
        min?.let { listValidators.add(DateValidatorPointForward.from(it)) }
        max?.let { listValidators.add(DateValidatorPointBackward.before(it)) }
        val validators = CompositeDateValidator.allOf(listValidators)

        return CalendarConstraints.Builder().setValidator(validators).build()
      }

      private fun updateAnswer(text: String?) {
        if (text.isNullOrEmpty()) {
          questionnaireViewItem.clearAnswer()
          return
        }
        try {
          val localDate = parseDate(text, canonicalizedDatePattern)
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = localDate.dateType
            }
          )
        } catch (e: ParseException) {
          displayValidationResult(
            Invalid(
              listOf(invalidDateErrorText(textInputEditText.context, canonicalizedDatePattern))
            )
          )
          if (questionnaireViewItem.answers.isNotEmpty()) {
            questionnaireViewItem.clearAnswer()
          }
          questionnaireViewItem.setDraftAnswer(text.toString())
        }
      }

      private fun updateTextFieldToDisplayDateValue() {
        val draftAnswerToDisplay = questionnaireViewItem.draftAnswer as? String
        val answer =
          questionnaireViewItem.answers
            .singleOrNull()
            ?.takeIf { it.hasValue() }
            ?.valueDateType
            ?.localDate
        val textToDisplayInTheTextField =
          answer?.format(canonicalizedDatePattern) ?: draftAnswerToDisplay

        // The same date format style is now used for both
        // accepting user date input and displaying the answer in the text field. For instance, the
        // "MM/dd/yyyy" format is employed to accept and display the date value. As a result, it is
        // possible to simply compare
        // the text field text to the draft or valid answer to determine whether the text field
        // text should be overridden or not.

        if (textInputEditText.text.toString() != textToDisplayInTheTextField) {
          textInputEditText.setText(textToDisplayInTheTextField)
        }

        // Show an error text if draft answer exists
        if (!draftAnswerToDisplay.isNullOrBlank()) {
          displayValidationResult(
            Invalid(
              listOf(invalidDateErrorText(textInputEditText.context, canonicalizedDatePattern))
            )
          )
        } else {
          displayValidationResult(NotValidated)
        }
      }

      /** Automatically appends date separator (e.g. "/") during date input. */
      inner class DatePatternTextWatcher(private val dateFormatSeparator: Char) : TextWatcher {
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
            dateFormatSeparator,
            isDeleting
          )
          updateAnswer(editable.toString())
        }
      }
    }
}

/**
 * Format entered date to acceptable date format where 2 digits for day and month, 4 digits for
 * year.
 */
internal fun handleDateFormatAfterTextChange(
  editable: Editable,
  canonicalizedDatePattern: String,
  dateFormatSeparator: Char,
  isDeleting: Boolean
) {
  val editableLength = editable.length
  if (editable.isEmpty()) {
    return
  }
  // restrict date entry upto acceptable date length
  if (editableLength > canonicalizedDatePattern.length) {
    editable.replace(canonicalizedDatePattern.length, editableLength, "")
    return
  }
  // handle delete text and separator
  if (editableLength < canonicalizedDatePattern.length) {
    // Do not add the separator again if the user has just deleted it.
    if (!isDeleting && canonicalizedDatePattern[editableLength] == dateFormatSeparator) {
      // 02 is entered with dd/MM/yyyy so appending / to editable 02/
      editable.append(dateFormatSeparator)
    }
    if (canonicalizedDatePattern[editable.lastIndex] == dateFormatSeparator &&
        editable[editable.lastIndex] != dateFormatSeparator
    ) {
      // Add separator to break different date components, e.g. converting "123" to "12/3"
      editable.insert(editable.lastIndex, dateFormatSeparator.toString())
    }
  }
}

internal const val TAG = "date-picker"
internal val ZONE_ID_UTC = ZoneId.of("UTC")

/**
 * Medium and long format styles use alphabetical month names which are difficult for the user to
 * input. Use short format style which is always numerical.
 */
internal fun getLocalizedDateTimePattern(): String {
  return DateTimeFormatterBuilder.getLocalizedDateTimePattern(
    FormatStyle.SHORT,
    null,
    IsoChronology.INSTANCE,
    Locale.getDefault()
  )
}

internal val DateType.localDate
  get() =
    if (!this.hasValue()) null
    else
      LocalDate.of(
        year,
        month + 1,
        day,
      )

internal val LocalDate.dateType
  get() = DateType(year, monthValue - 1, dayOfMonth)

internal val Date.localDate
  get() = LocalDate.of(year + 1900, month + 1, date)

// Count the number of digits in an Integer
internal fun Int.length() =
  when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
  }

/**
 * Replaces 'dd' with '31', 'MM' with '01' and 'yyyy' with '2023' and returns new string. For
 * example, given a `formatPattern` of dd/MM/yyyy, returns 31/01/2023
 */
internal fun invalidDateErrorText(context: Context, formatPattern: String) =
  context.getString(
    R.string.date_format_validation_error_msg,
    formatPattern,
    formatPattern.replace("dd", "31").replace("MM", "01").replace("yyyy", "2023")
  )
