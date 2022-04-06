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
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.subtitleText
import com.google.android.fhir.datacapture.utilities.localizedString
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemDatePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_date_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var textDateQuestion: TextView
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var questionSubtitleTextView: TextView
      private lateinit var textInputEditText: TextInputEditText
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix_text_view)
        textDateQuestion = itemView.findViewById(R.id.question_text_view)
        textInputLayout = itemView.findViewById(R.id.text_input_layout)
        questionSubtitleTextView = itemView.findViewById(R.id.subtitle_text_view)
        textInputEditText = itemView.findViewById(R.id.text_input_edit_text)

        textInputLayout.setEndIconOnClickListener {
          // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
          // and again in TextInputEditText during layout inflation. As a result, it is
          // necessary to access the base context twice to retrieve the application object
          // from the view's context.
          val context = itemView.context.tryUnwrapContext()!!
          context.supportFragmentManager.setFragmentResultListener(
            DatePickerFragment.RESULT_REQUEST_KEY,
            context
          ) { _, result ->
            // java.time APIs can be used with desugaring
            val year = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_YEAR)
            val month = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_MONTH)
            val dayOfMonth = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_DAY_OF_MONTH)
            // Month values are 1-12 in java.time but 0-11 in
            // DatePickerDialog.
            val localDate = LocalDate.of(year, month + 1, dayOfMonth)
            textInputEditText.setText(localDate?.localizedString)

            val date = DateType(year, month, dayOfMonth)
            questionnaireItemViewItem.singleAnswerOrNull =
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = date
              }
            onAnswerChanged(textInputEditText.context)
          }
          val selectedDate = questionnaireItemViewItem.singleAnswerOrNull?.valueDateType?.localDate
          val datePicker =
            MaterialDatePicker.Builder.datePicker()
              .setTitleText(context.getString(R.string.select_date))
              .setSelection(
                selectedDate?.atStartOfDay(ZONE_ID_UTC)?.toInstant()?.toEpochMilli()
                  ?: MaterialDatePicker.todayInUtcMilliseconds()
              )
              .build()
          datePicker.addOnPositiveButtonClickListener { epochMilli ->
            textInputEditText.setText(
              Instant.ofEpochMilli(epochMilli).atZone(ZONE_ID_UTC).toLocalDate().localizedString
            )
            questionnaireItemViewItem.singleAnswerOrNull =
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                val localDate = Instant.ofEpochMilli(epochMilli).atZone(ZONE_ID_UTC).toLocalDate()
                value = DateType(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth)
              }
            // Clear focus so that the user can refocus to open the dialog
            textInputEditText.clearFocus()
            onAnswerChanged(textInputEditText.context)
          }
          datePicker.show(context.supportFragmentManager, TAG)

          // Clear focus so that the user can refocus to open the dialog
          textDateQuestion.clearFocus()
        }
      }

      @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
        } else {
          prefixTextView.visibility = View.GONE
        }
        textDateQuestion.text = questionnaireItemViewItem.questionnaireItem.localizedTextSpanned
        questionSubtitleTextView.text = questionnaireItemViewItem.questionnaireItem.subtitleText
        textInputEditText.setText(
          questionnaireItemViewItem.singleAnswerOrNull?.valueDateType?.localDate?.localizedString
        )
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        textInputLayout.error =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        textInputEditText.isEnabled = !isReadOnly
        textInputLayout.isEnabled = !isReadOnly
      }
    }

  @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
  val LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE!!
}

const val NUMBER_OF_MICROSECONDS_PER_SECOND = 1000000
const val NUMBER_OF_MICROSECONDS_PER_MILLISECOND = 1000
internal const val TAG = "date-picker"
internal val ZONE_ID_UTC = ZoneId.of("UTC")

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
internal fun Context.tryUnwrapContext(): AppCompatActivity? {
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
