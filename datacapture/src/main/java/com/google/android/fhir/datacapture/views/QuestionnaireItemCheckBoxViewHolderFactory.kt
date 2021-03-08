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

import android.view.View
import android.widget.CheckBox
import com.google.android.fhir.datacapture.R
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.QuestionnaireResponse

internal object QuestionnaireItemCheckBoxViewHolderFactory : QuestionnaireItemViewHolderFactory(
    R.layout.questionnaire_item_check_box_view
) {
    override fun getQuestionnaireItemViewHolderDelegate() =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var checkBox: CheckBox
            private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

            override fun init(itemView: View) {
                checkBox = itemView.findViewById(R.id.check_box)
                checkBox.setOnClickListener {
                    // if-else block to prevent over-writing of "items" nested within "answer"
                    if (questionnaireItemViewItem.singleAnswerOrNull != null) {
                        questionnaireItemViewItem.singleAnswerOrNull?.value =
                            questionnaireItemViewItem.singleAnswerOrNull?.valueBuilder
                                ?.setBoolean(
                                    Boolean.newBuilder().setValue(checkBox.isChecked).build()
                                )?.build()
                    } else {
                        questionnaireItemViewItem.singleAnswerOrNull =
                            QuestionnaireResponse.Item.Answer.newBuilder().apply {
                                value =
                                    QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                        .setBoolean(
                                            Boolean.newBuilder()
                                                .setValue(checkBox.isChecked).build()
                                    ).build()
                            }
                    }

                    questionnaireItemViewItem.questionnaireResponseItemChangedCallback()
                }
            }

            override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
                this.questionnaireItemViewItem = questionnaireItemViewItem
                checkBox.text = questionnaireItemViewItem.questionnaireItem.text.value
                checkBox.isChecked =
                    questionnaireItemViewItem.singleAnswerOrNull?.value?.boolean?.value ?: false
            }
        }
}
