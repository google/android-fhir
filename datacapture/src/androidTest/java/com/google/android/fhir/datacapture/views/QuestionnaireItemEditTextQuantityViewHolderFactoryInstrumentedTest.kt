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
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputEditText
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemEditTextQuantityViewHolderFactoryInstrumentedTest {

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
        viewHolder = QuestionnaireItemEditTextQuantityViewHolderFactory.create(parent)
    }

    @Test
    fun shouldSetTextViewText() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<TextInputEditText>(R.id.textInputEditText).hint
        ).isEqualTo("Question?")
    }

    @Test
    @UiThreadTest
    fun shouldSetInputText() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.getDefaultInstance(),
                QuestionnaireResponse.Item.newBuilder().addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setQuantity(
                                Quantity.newBuilder()
                                    .setValue(Decimal.newBuilder().setValue("5").build())
                            ).build()
                    }
                )
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<TextInputEditText>(
                R.id.textInputEditText
            ).text.toString()
        ).isEqualTo("5")
    }

    @Test
    @UiThreadTest
    fun shouldSetInputTextToEmpty() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.getDefaultInstance(),
                QuestionnaireResponse.Item.newBuilder().addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setQuantity(
                                Quantity.newBuilder()
                                    .setValue(Decimal.newBuilder().setValue("5").build())
                            ).build()
                    }
                )
            ) {}
        )
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.getDefaultInstance(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<TextInputEditText>(
                R.id.textInputEditText
            ).text.toString()
        ).isEqualTo("")
    }

    @Test
    @UiThreadTest
    fun shouldSetQuestionnaireResponseItemAnswer() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.newBuilder().build(),
            QuestionnaireResponse.Item.newBuilder()
        ) {}
        viewHolder.bind(questionnaireItemViewItem)
        viewHolder.itemView.findViewById<TextInputEditText>(R.id.textInputEditText).setText("10")

        val answer = questionnaireItemViewItem.questionnaireResponseItemBuilder.answerList
        assertThat(answer.size).isEqualTo(1)
        assertThat(answer[0].value?.quantity?.value?.value).isEqualTo("10.0")
    }

    @Test
    @UiThreadTest
    fun shouldSetQuestionnaireResponseItemAnswerOneDecimalPlace() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.newBuilder().build(),
            QuestionnaireResponse.Item.newBuilder()
        ) {}
        viewHolder.bind(questionnaireItemViewItem)
        viewHolder.itemView.findViewById<TextInputEditText>(R.id.textInputEditText).setText("10.1")

        val answer = questionnaireItemViewItem.questionnaireResponseItemBuilder.answerList
        assertThat(answer.size).isEqualTo(1)
        assertThat(answer[0].value?.quantity?.value)
            .isEqualTo(Decimal.newBuilder().setValue("10.1").build())
    }

    @Test
    @UiThreadTest
    fun shouldSetQuestionnaireResponseItemAnswerToEmpty() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.newBuilder().build(),
            QuestionnaireResponse.Item.newBuilder()
        ) {}
        viewHolder.bind(questionnaireItemViewItem)
        viewHolder.itemView.findViewById<TextInputEditText>(R.id.textInputEditText).setText("")

        assertThat(
            questionnaireItemViewItem.questionnaireResponseItemBuilder.answerCount
        ).isEqualTo(0)
    }
}
