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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentResultListener
import com.google.android.fhir.datacapture.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent

object QuestionnaireItemDatePickerViewHolderFactory : QuestionnaireItemViewHolderFactory(
  R.layout.questionnaire_item_date_picker_view
) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var textView: TextView
      private lateinit var input: TextView
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        textView = itemView.findViewById(R.id.text)
        input = itemView.findViewById(R.id.input)
        val button = itemView.findViewById<TextView>(R.id.button)
        button.setOnClickListener {
          // TODO: find a more robust way to do this as it is not guaranteed that the activity is
          // an AppCompatActivity.
          val context = itemView.context as AppCompatActivity
          DatePickerFragment().show(context.supportFragmentManager, DatePickerFragment.TAG)
          context.supportFragmentManager.setFragmentResultListener(
            DatePickerFragment.RESULT_REQUEST_KEY,
            context,
            object : FragmentResultListener {
              @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
              override fun onFragmentResult(requestKey: String, result: Bundle) {
                val year = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_YEAR)
                val month = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_MONTH)
                val dayOfMonth =
                  result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_DAY_OF_MONTH)
                input.text = LocalDate.of(
                  year,
                  // month values are 1-12 in java.time but 0-11 in DateType (FHIR)
                  month + 1,
                  dayOfMonth
                ).format(LOCAL_DATE_FORMATTER)

                questionnaireItemViewItem.questionnaireResponseItemComponent.answer =
                  listOf(
                    QuestionnaireResponseItemAnswerComponent()
                      .apply {
                        value = DateType(year, month, dayOfMonth)
                      }
                  )
              }
            }
          )
        }
      }

      @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        textView.text = questionnaireItemViewItem.questionnaireItemComponent.text
        questionnaireItemViewItem.questionnaireResponseItemComponent.answer.also {
          if (it.size == 1 && it[0].hasValueDateType()) {
            input.text = it[0].valueDateType.let { date ->
              LocalDate.of(
                date.year,
                // month values are 1-12 in java.time but 0-11 in DateType (FHIR)
                date.month + 1,
                date.day
              )
            }.format(LOCAL_DATE_FORMATTER)
          } else {
            input.text = ""
          }
        }
      }
    }

  @SuppressLint("NewApi") // java.time APIs can be used due to desugaring
  val LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
}
