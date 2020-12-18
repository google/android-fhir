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

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentResultListener
import com.google.android.fhir.datacapture.R
import java.util.Calendar
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.QuestionnaireResponse

object QuestionnaireItemDatePickerViewHolderFactory : QuestionnaireItemViewHolderFactory {
    override fun create(parent: ViewGroup): QuestionnaireItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questionnaire_item_date_picker_view, parent, false)
        return QuestionnaireItemDatePickerViewHolder(view)
    }
}

private class QuestionnaireItemDatePickerViewHolder(
  itemView: View
) : QuestionnaireItemViewHolder(itemView) {
    private val textView = itemView.findViewById<TextView>(R.id.text)
    private val input = itemView.findViewById<TextView>(R.id.input)
    private val button = itemView.findViewById<TextView>(R.id.button)
    init {
        button.setOnClickListener {
            // TODO: find a more robust way to do this.
            val context = itemView.context as AppCompatActivity
            DatePickerFragment().show(context.supportFragmentManager, DatePickerFragment.TAG)
            context.supportFragmentManager.setFragmentResultListener(
                DatePickerFragment.RESULT_REQUEST_KEY,
                context,
                object : FragmentResultListener {
                    override fun onFragmentResult(requestKey: String, result: Bundle) {
                        val year = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_YEAR)
                        val month = result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_MONTH)
                        val dayOfMonth =
                            result.getInt(DatePickerFragment.RESULT_BUNDLE_KEY_DAY_OF_MONTH)
                        val date = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }.time

                        // Prefer the ICU library on Android N or above.
                        input.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            android.icu.text.DateFormat.getDateInstance().format(date)
                        } else {
                            java.text.DateFormat.getDateInstance().format(date)
                        }
                        questionnaireItemViewItem.questionnaireResponseItemComponent.answer =
                            listOf(
                                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                                    .apply {
                                        value = DateType(year, month, dayOfMonth)
                                    }
                            )
                    }
                }
            )
        }
    }

    private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

    override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        textView.text = questionnaireItemViewItem.questionnaireItemComponent.text
    }
}
