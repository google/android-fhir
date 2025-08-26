/*
 * Copyright 2024-2025 Google LLC
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
import android.text.InputType
import android.text.format.DateFormat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.toLocalizedString
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.TimeType

object TimePickerViewHolderFactory :
  QuestionnaireItemAndroidViewHolderFactory(R.layout.time_picker_view) {

  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemAndroidViewHolderDelegate {
      private val TAG = "time-picker"
      private lateinit var context: AppCompatActivity
      private lateinit var header: HeaderView
      private lateinit var timeInputLayout: TextInputLayout
      private lateinit var timeInputEditText: TextInputEditText
      override lateinit var questionnaireViewItem: QuestionnaireViewItem

      override fun init(itemView: View) {
        context = itemView.context.tryUnwrapContext()!!
        header = itemView.findViewById(R.id.header)
        timeInputLayout = itemView.findViewById(R.id.text_input_layout)
        timeInputEditText = itemView.findViewById(R.id.text_input_edit_text)
        timeInputEditText.inputType = InputType.TYPE_NULL
        timeInputEditText.hint = itemView.context.getString(R.string.time)

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
      }

      @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        clearPreviousState()
        header.bind(questionnaireViewItem)
        timeInputLayout.helperText = getRequiredOrOptionalText(questionnaireViewItem, context)

        val questionnaireItemViewItemDateTimeAnswer =
          questionnaireViewItem.answers.singleOrNull()?.valueTimeType?.localTime

        // If there is no set answer in the QuestionnaireItemViewItem, make the time field empty.
        timeInputEditText.setText(
          questionnaireItemViewItemDateTimeAnswer?.toLocalizedString(timeInputEditText.context)
            ?: "",
        )
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        // The system outside this delegate should only be able to mark it read only. Otherwise, it
        // will change the state set by this delegate in bindView().
        if (isReadOnly) {
          timeInputEditText.isEnabled = false
          timeInputLayout.isEnabled = false
        }
      }

      private fun buildMaterialTimePicker(context: Context, inputMode: Int) {
        val selectedTime =
          questionnaireViewItem.answers.singleOrNull()?.valueTimeType?.localTime ?: LocalTime.now()
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
                setQuestionnaireItemViewItemAnswer(this)
                timeInputEditText.clearFocus()
              }
            }
          }
          .show(context.tryUnwrapContext()!!.supportFragmentManager, TAG)
      }

      /** Set the answer in the [QuestionnaireResponse]. */
      private fun setQuestionnaireItemViewItemAnswer(localDateTime: LocalTime) =
        context.lifecycleScope.launch {
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(TimeType(localDateTime.format(DateTimeFormatter.ISO_TIME))),
          )
        }

      private fun clearPreviousState() {
        timeInputEditText.isEnabled = true
        timeInputLayout.isEnabled = true
      }
    }

  private val TimeType.localTime
    get() =
      LocalTime.of(
        hour,
        minute,
        second.toInt(),
      )
}
