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

import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemRadioGroupViewHolderFactoryInstrumentedTest {
    private val parent = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
    private val viewHolder = QuestionnaireItemRadioGroupViewHolderFactory.create(parent)

    @Test
    fun bind_shouldSetHeaderText() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.radio_header).text).isEqualTo(
            "Question?"
        )
    }

    @Test
    fun bind_shouldCreateRadioButtons() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    addAllAnswerOption(mutableListOf(
                        Questionnaire.Item.AnswerOption.newBuilder().apply {
                            value = Questionnaire.Item.AnswerOption.ValueX.newBuilder().apply {
                                coding = Coding.newBuilder().apply {
                                    display = com.google.fhir.r4.core.String.newBuilder()
                                        .setValue("Coding 1")
                                        .build()
                                }.build()
                            }.build()
                        }.build(),
                        Questionnaire.Item.AnswerOption.newBuilder().apply {
                            value = Questionnaire.Item.AnswerOption.ValueX.newBuilder().apply {
                                coding = Coding.newBuilder().apply {
                                    display = com.google.fhir.r4.core.String.newBuilder()
                                        .setValue("Coding 2")
                                        .build()
                                }.build()
                            }.build()
                        }.build()
                    ))
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        val radioGroup = viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group)
        assertThat(radioGroup.childCount).isEqualTo(2)
        val radioButton1 = radioGroup.getChildAt(0) as RadioButton
        assertThat(radioButton1.text).isEqualTo("Coding 1")
        val radioButton2 = radioGroup.getChildAt(1) as RadioButton
        assertThat(radioButton2.text).isEqualTo("Coding 2")
    }

    @Test
    fun bind_noAnswer_shouldLeaveRadioButtonsUnchecked() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    addAllAnswerOption(mutableListOf(
                        Questionnaire.Item.AnswerOption.newBuilder().apply {
                            value = Questionnaire.Item.AnswerOption.ValueX.newBuilder().apply {
                                coding = Coding.newBuilder().apply {
                                    display = com.google.fhir.r4.core.String.newBuilder()
                                        .setValue("Coding 1")
                                        .build()
                                }.build()
                            }.build()
                        }.build()
                    ))
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        val radioButton =
            viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group)
                .getChildAt(0) as RadioButton
        assertThat(radioButton.isChecked).isFalse()
    }

    @Test
    @UiThreadTest
    fun bind_answer_shouldCheckRadioButton() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    addAllAnswerOption(mutableListOf(
                        Questionnaire.Item.AnswerOption.newBuilder().apply {
                            value = Questionnaire.Item.AnswerOption.ValueX.newBuilder().apply {
                                coding = Coding.newBuilder().apply {
                                    display = com.google.fhir.r4.core.String.newBuilder()
                                        .setValue("Coding 1")
                                        .build()
                                }.build()
                            }.build()
                        }.build(),
                        Questionnaire.Item.AnswerOption.newBuilder().apply {
                            value = Questionnaire.Item.AnswerOption.ValueX.newBuilder().apply {
                                coding = Coding.newBuilder().apply {
                                    display = com.google.fhir.r4.core.String.newBuilder()
                                        .setValue("Coding 2")
                                        .build()
                                }.build()
                            }.build()
                        }.build()
                    ))
                }.build(),
                QuestionnaireResponse.Item.newBuilder().apply {
                    addAnswer(QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder().apply {
                            coding = Coding.newBuilder().apply {
                                display = com.google.fhir.r4.core.String.newBuilder()
                                    .setValue("Coding 1")
                                    .build()
                            }.build()
                        }.build()
                    })
                }
            ) {}
        )

        assertThat(
            (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group)
                .getChildAt(0) as RadioButton).isChecked
        ).isTrue()
        assertThat(
            (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group)
                .getChildAt(1) as RadioButton).isChecked
        ).isFalse()
    }

    @Test
    @UiThreadTest
    fun click_shouldSetQuestionnaireResponseItemAnswer() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.newBuilder().apply {
                addAllAnswerOption(mutableListOf(
                    Questionnaire.Item.AnswerOption.newBuilder().apply {
                        value = Questionnaire.Item.AnswerOption.ValueX.newBuilder().apply {
                            coding = Coding.newBuilder().apply {
                                display = com.google.fhir.r4.core.String.newBuilder()
                                    .setValue("Coding 1")
                                    .build()
                            }.build()
                        }.build()
                    }.build()
                ))
            }.build(),
            QuestionnaireResponse.Item.newBuilder()
        ) {}
        viewHolder.bind(questionnaireItemViewItem)
        viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(0)
            .performClick()

        val answer = questionnaireItemViewItem.questionnaireResponseItemBuilder.answerBuilderList
        assertThat(answer.size).isEqualTo(1)
        assertThat(answer[0].value.coding.display.value).isEqualTo("Coding 1")
    }

    @Test
    @UiThreadTest
    fun click_shouldCheckRadioButton() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.newBuilder().apply {
                addAllAnswerOption(mutableListOf(
                    Questionnaire.Item.AnswerOption.newBuilder().apply {
                        value = Questionnaire.Item.AnswerOption.ValueX.newBuilder().apply {
                            coding = Coding.newBuilder().apply {
                                display = com.google.fhir.r4.core.String.newBuilder()
                                    .setValue("Coding 1")
                                    .build()
                            }.build()
                        }.build()
                    }.build(),
                    Questionnaire.Item.AnswerOption.newBuilder().apply {
                        value = Questionnaire.Item.AnswerOption.ValueX.newBuilder().apply {
                            coding = Coding.newBuilder().apply {
                                display = com.google.fhir.r4.core.String.newBuilder()
                                    .setValue("Coding 2")
                                    .build()
                            }.build()
                        }.build()
                    }.build()
                ))
            }.build(),
            QuestionnaireResponse.Item.newBuilder()
        ) {}
        viewHolder.bind(questionnaireItemViewItem)
        viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(0)
            .performClick()

        assertThat(
            (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group)
                .getChildAt(0) as RadioButton).isChecked
        ).isTrue()
        assertThat(
            (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group)
                .getChildAt(1) as RadioButton).isChecked
        ).isFalse()
    }
}
