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

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputLayout
import com.google.fhir.r4.core.QuestionnaireResponse
import java.text.DateFormat.getTimeInstance
import java.time.Instant
import java.time.ZoneId

object QuestionnaireItemDropDownViewHolderFactory : QuestionnaireItemViewHolderFactory(
    R.layout.questionnaire_item_drop_down_view
) {
    override fun getQuestionnaireItemViewHolderDelegate() =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var textInputLayout: TextInputLayout
            private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
            private lateinit var context: Context
            private var answerOptionString = arrayListOf<String>()
            private var answerOptionValueX = arrayListOf<QuestionnaireResponse.Item.Answer.ValueX>()
            private val df = getTimeInstance()

            override fun init(itemView: View) {
                textInputLayout = itemView.findViewById(R.id.dropdown_menu)
                context = itemView.context
            }

            override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
                this.questionnaireItemViewItem = questionnaireItemViewItem
                textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.text.value
                getOptionValueXFromQuestionnaire()
                val adapter = ArrayAdapter(
                    context,
                    R.layout.questionnaire_item_drop_down_list,
                    answerOptionString
                )
                (textInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                (textInputLayout.editText as? AutoCompleteTextView)?.onItemClickListener =
                    object : AdapterView.OnItemClickListener {
                        override fun onItemClick(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            questionnaireItemViewItem.singleAnswerOrNull =
                                QuestionnaireResponse.Item.Answer.newBuilder()
                                    .setValue(
                                        answerOptionValueX[position]
                                    )
                        }
                    }
            }
            // Populate list of string to display as options and answerOptionValueX
            private fun getOptionValueXFromQuestionnaire() {
                questionnaireItemViewItem.questionnaireItem.answerOptionList.forEach {
                    if (it.value.hasCoding()) {
                        answerOptionString.add(it.value.coding.code.value)
                        answerOptionValueX.add(
                            QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setCoding(it.value.coding)
                                .build()
                        )
                    } else if (it.value.hasInteger()) {
                        answerOptionString.add(it.value.integer.value.toString())
                        answerOptionValueX.add(
                            QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setInteger(it.value.integer)
                                .build()
                        )
                    } else if (it.value.hasDate()) {
                        it.value.date.let {
                            answerOptionString.add(
                                Instant
                                    .ofEpochMilli(it.valueUs /
                                        QuestionnaireItemDatePickerViewHolderFactory
                                            .NUMBER_OF_MICROSECONDS_PER_MILLISECOND
                                    )
                                    .atZone(
                                        ZoneId.systemDefault()
                                    ).toString()
                            )
                        }
                        answerOptionValueX.add(
                            QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setDate(it.value.date)
                                .build()
                        )
                    } else if (it.value.hasTime()) {
                        it.value.time.let {
                            answerOptionString.add(
                                df.format(it.valueUs /
                                    QuestionnaireItemDatePickerViewHolderFactory
                                        .NUMBER_OF_MICROSECONDS_PER_MILLISECOND
                                )
                            )
                        }
                        answerOptionValueX.add(
                            QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setTime(it.value.time)
                                .build()
                        )
                    } else if (it.value.hasStringValue()) {
                        answerOptionString.add(it.value.stringValue.value.toString())
                        QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setStringValue(it.value.stringValue)
                            .build()
                    }
                }
            }
        }
}
