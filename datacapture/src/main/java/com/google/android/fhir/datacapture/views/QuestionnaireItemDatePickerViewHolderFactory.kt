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
import android.icu.text.DateFormat
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.utilities.isAndroidIcuSupported
import com.google.android.fhir.datacapture.utilities.localizedString
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.MaxValueConstraintValidator.getMaxValue
import com.google.android.fhir.datacapture.validation.MinValueConstraintValidator.getMinValue
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
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

internal object QuestionnaireItemDatePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_date_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var textInputEditText: TextInputEditText
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      private var textWatcher: TextWatcher? = null

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
                textInputEditText.setText(
                  Instant.ofEpochMilli(epochMilli).atZone(ZONE_ID_UTC).toLocalDate().localizedString
                )
                questionnaireItemViewItem.setAnswer(
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
      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        header.bind(questionnaireItemViewItem.questionnaireItem)
        textInputLayout.hint = localeDatePattern
        textInputEditText.removeTextChangedListener(textWatcher)
        if (isTextUpdateRequired(
            textInputEditText.context,
            questionnaireItemViewItem.answers.singleOrNull()?.valueDateType,
            textInputEditText.text.toString()
          )
        ) {
          textInputEditText.setText(
            questionnaireItemViewItem.answers
              .singleOrNull()
              ?.valueDateType
              ?.localDate
              ?.localizedString
          )
          displayValidationResult(Valid)
        }
        textWatcher = textInputEditText.doAfterTextChanged { text -> updateAnswer(text.toString()) }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
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
          questionnaireItemViewItem.answers
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
        val min =
          (getMinValue(questionnaireItemViewItem.questionnaireItem) as? DateType)?.value?.time
        val max =
          (getMaxValue(questionnaireItemViewItem.questionnaireItem) as? DateType)?.value?.time

        if (min != null && max != null && min > max) {
          throw IllegalArgumentException("minValue cannot be greater than maxValue")
        }

        val listValidators = ArrayList<DateValidator>()
        min?.let { listValidators.add(DateValidatorPointForward.from(it)) }
        max?.let { listValidators.add(DateValidatorPointBackward.before(it)) }
        val validators = CompositeDateValidator.allOf(listValidators)

        return CalendarConstraints.Builder().setValidator(validators).build()
      }

      private fun updateAnswer(text: CharSequence?) {
        try {
          val localDate = parseDate(text, textInputEditText.context.applicationContext)
          questionnaireItemViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = localDate.dateType
            }
          )
          displayValidationResult(Valid)
        } catch (e: ParseException) {
          if (text.isNullOrEmpty()) {
            if (questionnaireItemViewItem.questionnaireItem.required) {
              displayValidationResult(
                Invalid(
                  listOf(
                    textInputEditText.context.getString(
                      R.string.required_constraint_validation_error_msg
                    )
                  )
                )
              )
            } else {
              displayValidationResult(Valid)
            }
          } else {
            displayValidationResult(
              Invalid(
                listOf(
                  textInputEditText.context.getString(
                    R.string.date_format_validation_error_msg,
                    localeDatePattern
                  )
                )
              )
            )
          }
          if (questionnaireItemViewItem.answers.isNotEmpty()) {
            questionnaireItemViewItem.clearAnswer()
          }
        }
      }
    }

  private fun isTextUpdateRequired(
    context: Context,
    answer: DateType?,
    inputText: String?
  ): Boolean {
    val inputDate =
      try {
        parseDate(inputText, context)
      } catch (e: Exception) {
        null
      }
    if (inputDate == null || answer == null) {
      return true
    }
    return answer?.localDate != inputDate
  }
}

internal const val TAG = "date-picker"
internal val ZONE_ID_UTC = ZoneId.of("UTC")

/**
 * Medium and long format styles use alphabetical month names which are difficult for the user to
 * input. Use short format style which is always numerical.
 */
internal val localeDatePattern =
  DateTimeFormatterBuilder.getLocalizedDateTimePattern(
    FormatStyle.SHORT,
    null,
    IsoChronology.INSTANCE,
    Locale.getDefault()
  )

/**
 * Returns the [AppCompatActivity] if there exists one wrapped inside [ContextThemeWrapper] s, or
 * `null` otherwise.
 *
 * This function is inspired by the function with the same name in `AppCompateDelegateImpl`. See
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:appcompat/appcompat/src/main/java/androidx/appcompat/app/AppCompatDelegateImpl.java;l=1615
 *
 * TODO: find a more robust way to do this as it is not guaranteed that the activity is an
 * AppCompatActivity.
 */
fun Context.tryUnwrapContext(): AppCompatActivity? {
  var context = this
  while (true) {
    when (context) {
      is AppCompatActivity -> return context
      is ContextThemeWrapper -> context = context.baseContext
      else -> return null
    }
  }
}

internal val DateType.localDate
  get() =
    LocalDate.of(
      year,
      month + 1,
      day,
    )

internal val LocalDate.dateType
  get() = DateType(year, monthValue - 1, dayOfMonth)

internal val Date.localDate
  get() = LocalDate.of(year + 1900, month + 1, date)

internal fun parseDate(text: CharSequence?, context: Context): LocalDate {
  val localDate =
    if (isAndroidIcuSupported()) {
        DateFormat.getDateInstance(DateFormat.SHORT).parse(text.toString())
      } else {
        android.text.format.DateFormat.getDateFormat(context).parse(text.toString())
      }
      .localDate
  // date/localDate with year more than 4 digit throws data format exception if deep copy
  // operation get performed on QuestionnaireResponse,
  // QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent in org.hl7.fhir.r4.model
  // e.g ca.uhn.fhir.parser.DataFormatException: Invalid date/time format: "19843-12-21":
  // Expected character '-' at index 4 but found 3
  if (localDate.year.length() > 4) {
    throw ParseException("Year has more than 4 digits.", 4)
  }
  return localDate
}

// https://stackoverflow.com/questions/42950812/count-number-of-digits-in-kotlin
internal fun Int.length() =
  when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
  }
