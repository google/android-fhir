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
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentResultListener
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.QuestionnaireResponse
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object QuestionnaireItemDateTimePickerViewHolderFactory : QuestionnaireItemViewHolderFactory(
    R.layout.questionnaire_item_date_time_picker_view
) {
    override fun getQuestionnaireItemViewHolderDelegate() =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var dateInputLayout: TextInputLayout
            private lateinit var dateInputEditText: TextInputEditText
            private lateinit var timeInputLayout: TextInputLayout
            private lateinit var timeInputEditText: TextInputEditText
            private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

            override fun init(itemView: View) {
                dateInputLayout = itemView.findViewById(R.id.dateInputLayout)
                dateInputEditText = itemView.findViewById(R.id.dateInputEditText)
                // Disable direct text input to only allow input from the date picker dialog
                dateInputEditText.keyListener = null
                dateInputEditText.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                    // Do not show the date picker dialog when losing focus.
                    if (!hasFocus) return@setOnFocusChangeListener

                    // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
                    // and again in TextInputEditText during layout inflation. As a result, it is
                    // necessary to access the base context twice to retrieve the application object
                    // from the view's context.
                    val context = itemView.context.tryUnwrapContext()!!
                    context.supportFragmentManager.setFragmentResultListener(
                        DatePickerFragment.RESULT_REQUEST_KEY,
                        context,
                        object : FragmentResultListener {
                            // java.time APIs can be used with desugaring
                            @SuppressLint("NewApi")
                            override fun onFragmentResult(requestKey: String, result: Bundle) {
                                val year = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_YEAR)
                                val month =
                                    result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_MONTH)
                                val dayOfMonth =
                                    result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_DAY_OF_MONTH)
                                val zonedDateTime = LocalDate.of(
                                    year,
                                    // Month values are 1-12 in java.time but 0-11 in
                                    // DatePickerDialog.
                                    month + 1,
                                    dayOfMonth
                                ).atStartOfDay().atZone(ZoneId.systemDefault())
                                updateDateTimeInput(zonedDateTime)
                                updateDateTimeAnswer(zonedDateTime)
                            }
                        }
                    )
                    DatePickerFragment().show(
                        context.supportFragmentManager,
                        DatePickerFragment.TAG
                    )
                    dateInputLayout.clearFocus()
                }

                timeInputLayout = itemView.findViewById(R.id.timeInputLayout)
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
                        context,
                        object : FragmentResultListener {
                            // java.time APIs can be used with desugaring
                            @SuppressLint("NewApi")
                            override fun onFragmentResult(requestKey: String, result: Bundle) {
                                val hour = result.getInt(TimePickerFragment.RESULT_BUNDLE_KEY_HOUR)
                                val minute =
                                    result.getInt(TimePickerFragment.RESULT_BUNDLE_KEY_MINUTE)
                                val zonedDateTime = Instant
                                    .ofEpochMilli(
                                        questionnaireItemViewItem.singleAnswerOrNull!!
                                            .value.dateTime.millis
                                    )
                                    .atZone(ZoneId.systemDefault())
                                    .withHour(hour)
                                    .withMinute(minute)
                                updateDateTimeInput(zonedDateTime)
                                updateDateTimeAnswer(zonedDateTime)
                            }
                        }
                    )
                    TimePickerFragment().show(
                        context.supportFragmentManager,
                        TimePickerFragment.TAG
                    )
                }
                timeInputLayout.clearFocus()
            }

            @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
            override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
                this.questionnaireItemViewItem = questionnaireItemViewItem
                dateInputLayout.hint = questionnaireItemViewItem.questionnaireItem.text.value
                timeInputLayout.hint = questionnaireItemViewItem.questionnaireItem.text.value
                val dateTime = questionnaireItemViewItem.singleAnswerOrNull?.value?.dateTime
                updateDateTimeInput(
                    dateTime?.let {
                        Instant
                            .ofEpochMilli(it.millis)
                            .atZone(ZoneId.systemDefault())
                    }
                )
            }

            /** Update the date and time input fields in the UI. */
            fun updateDateTimeInput(zonedDateTime: ZonedDateTime?) {
                timeInputEditText.isEnabled = zonedDateTime != null
                dateInputEditText.setText(zonedDateTime?.format(LOCAL_DATE_FORMATTER) ?: "")
                timeInputEditText.setText(zonedDateTime?.format(LOCAL_TIME_FORMATTER) ?: "")
            }

            /** Updates the recorded answer. */
            fun updateDateTimeAnswer(zonedDateTime: ZonedDateTime) {
                // Update answer
                val dateTime = DateTime.newBuilder()
                    .setValueUs(
                        zonedDateTime.toEpochSecond() * NUMBER_OF_MICROSECONDS_PER_SECOND
                    )
                    .setTimezone(ZoneId.systemDefault().id)
                    .setPrecision(DateTime.Precision.SECOND)
                    .build()
                questionnaireItemViewItem.singleAnswerOrNull =
                    QuestionnaireResponse.Item.Answer.newBuilder()
                        .apply {
                            value = QuestionnaireResponse.Item.Answer.ValueX
                                .newBuilder()
                                .setDateTime(dateTime)
                                .build()
                        }
            }
        }

    @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
    val LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE!!
    val LOCAL_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME!!
}

private val DateTime.millis
    get() = valueUs / NUMBER_OF_MICROSECONDS_PER_MILLISECOND
