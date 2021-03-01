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

import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.responseAnswerValueX
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Code
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import com.google.fhir.r4.core.String
import kotlin.test.assertFailsWith
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemDropDownViewHolderFactoryInstrumentedTest {
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
        viewHolder = QuestionnaireItemDropDownViewHolderFactory.create(parent)
    }

    @Test
    @UiThreadTest
    fun shouldSetTextInputHint() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.dropdown_question_title).text
        ).isEqualTo("Question?")
    }

    @Test
    @UiThreadTest
    fun shouldPopulateDropDown() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setCoding(
                        Coding.newBuilder()
                            .setCode(Code.newBuilder().setValue("test-code"))
                            .setDisplay(String.newBuilder().setValue("Test Code"))
                    )
            ).build()
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    addAnswerOption(answerOption)
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.auto_complete)
            .adapter
            .getItem(0)
            .toString()
        ).isEqualTo("Test Code")
    }

    @Test
    @UiThreadTest
    fun shouldSetDropDownOptionToCodeIfValueCodingDisplayEmpty() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setCoding(
                        Coding.newBuilder().setCode(Code.newBuilder().setValue("test-code"))
                    )
            ).build()
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    addAnswerOption(answerOption)
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.auto_complete)
            .adapter
            .getItem(0)
            .toString()
        ).isEqualTo("test-code")
    }

    @Test
    @UiThreadTest
    fun shouldSetAutoTextViewEmptyIfAnswerNull() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setCoding(
                        Coding.newBuilder()
                            .setCode(Code.newBuilder().setValue("test-code"))
                            .setDisplay(String.newBuilder().setValue("Test Code"))
                    )
            ).build()

        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    addAnswerOption(answerOption)
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.auto_complete)
                .text
                .toString()
        ).isEqualTo("")
    }

    @Test
    @UiThreadTest
    fun shouldAutoCompleteTextViewToDisplayIfAnswerNotNull() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setCoding(
                        Coding.newBuilder()
                            .setCode(Code.newBuilder().setValue("test-code"))
                            .setDisplay(String.newBuilder().setValue("Test Code"))
                    )
            ).build()

        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    addAnswerOption(answerOption)
                }.build(),
                QuestionnaireResponse.Item.newBuilder().addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder()
                        .setValue(
                            answerOption.responseAnswerValueX
                        )

                )
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.auto_complete)
                .text
                .toString()
        ).isEqualTo(answerOption.displayString)
    }

    @Test
    @UiThreadTest
    fun shouldThrowErrorForAnswerOptionWithoutCoding() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setStringValue(
                        String.newBuilder()
                            .setValue("test")))
            .build()

        assertFailsWith<IllegalArgumentException> {
            viewHolder.bind(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder().apply {
                        addAnswerOption(answerOption)
                    }.build(),
                    QuestionnaireResponse.Item.newBuilder().addAnswer(
                        QuestionnaireResponse.Item.Answer.newBuilder()
                            .setValue(
                                answerOption.responseAnswerValueX
                            )

                    )
                ) {}
            )
        }
    }
}
