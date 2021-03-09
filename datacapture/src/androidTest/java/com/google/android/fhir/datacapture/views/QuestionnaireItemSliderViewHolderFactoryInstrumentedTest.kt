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
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.material.slider.Slider
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemSliderViewHolderFactoryInstrumentedTest {
    private lateinit var context: ContextThemeWrapper
    private lateinit var parent: FrameLayout
    private lateinit var viewHolder: QuestionnaireItemViewHolder

    @Before
    fun setUp() {
        context = ContextThemeWrapper(
            InstrumentationRegistry.getInstrumentation().targetContext,
            R.style.Theme_MaterialComponents
        )
        parent = FrameLayout(context)
        viewHolder = QuestionnaireItemSliderViewHolderFactory.create(parent)
    }

    @Test
    fun shouldSetHeaderTextViewText() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.slider_header).text)
            .isEqualTo("Question?")
    }

    @Test
    fun singleAnswerOrNull_noAnswer_shouldReturnNull() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.getDefaultInstance(),
            QuestionnaireResponse.Item.newBuilder()
        ) {}

        assertThat(questionnaireItemViewItem.singleAnswerOrNull).isNull()
    }

    @Test
    fun shouldSetSliderValue() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.getDefaultInstance(),
                QuestionnaireResponse.Item.newBuilder().apply {
                    addAnswer(
                        QuestionnaireResponse.Item.Answer.newBuilder().apply {
                            value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setInteger(Integer.newBuilder().setValue(10).build())
                                .build()
                        }
                    )
                }
            ) {}
        )

        assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).value).isEqualTo(10)
    }

    @Test
    fun shouldSetQuestionnaireResponseSliderAnswer() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.getDefaultInstance(),
            QuestionnaireResponse.Item.newBuilder()
        ) {}

        viewHolder.bind(questionnaireItemViewItem)
        viewHolder.itemView.findViewById<Slider>(R.id.slider)
            .value = 10.0F

        val answer = questionnaireItemViewItem.questionnaireResponseItemBuilder.answerBuilderList
        assertThat(answer.size).isEqualTo(1)
        assertThat(answer[0].value.integer.value).isEqualTo(10)
    }

    @Test
    fun shouldSetAnswerToNull() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.getDefaultInstance(),
            QuestionnaireResponse.Item.newBuilder().apply {
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setInteger(Integer.newBuilder().setValue(10).build())
                            .build()
                    }
                )
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setInteger(Integer.newBuilder().setValue(10).build())
                            .build()
                    }
                )
            }
        ) {}

        assertThat(questionnaireItemViewItem.singleAnswerOrNull).isNull()
    }

    @Test
    fun shouldSetSliderValueToDefault() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.getDefaultInstance(),
                QuestionnaireResponse.Item.newBuilder().apply {
                    addAnswer(
                        QuestionnaireResponse.Item.Answer.newBuilder().apply {
                            value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setInteger(Integer.newBuilder().setValue(10).build())
                                .build()
                        }
                    )
                    addAnswer(
                        QuestionnaireResponse.Item.Answer.newBuilder().apply {
                            value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setInteger(Integer.newBuilder().setValue(10).build())
                                .build()
                        }
                    )
                }
            ) {}
        )

        assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).value).isEqualTo(0.0F)
    }
}
