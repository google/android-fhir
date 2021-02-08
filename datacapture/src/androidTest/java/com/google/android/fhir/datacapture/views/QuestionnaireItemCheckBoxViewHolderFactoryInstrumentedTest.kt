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

import android.widget.CheckBox
import android.widget.FrameLayout
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemCheckBoxViewHolderFactoryInstrumentedTest {
    private val parent = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
    private val viewHolder = QuestionnaireItemCheckBoxViewHolderFactory.create(parent)

    @Test
    fun shouldSetCheckBoxText() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).text).isEqualTo(
            "Question?"
        )
    }

    @Test
    fun noAnswer_shouldSetCheckBoxUnchecked() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).isChecked).isFalse()
    }

    @Test
    @UiThreadTest
    fun answerTrue_shouldSetCheckBoxChecked() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder().addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setBoolean(Boolean.newBuilder().setValue(true)).build()
                    }
                )
            ) {}
        )

        assertThat(viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).isChecked).isTrue()
    }

    @Test
    fun answerFalse_shouldSetCheckBoxUnchecked() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder().addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setBoolean(Boolean.newBuilder().setValue(false)).build()
                    }
                )
            ) {}
        )

        assertThat(viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).isChecked).isFalse()
    }

    @Test
    @UiThreadTest
    fun shouldSetQuestionnaireResponseItemComponentAnswer() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.newBuilder().apply {
                text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
            }.build(),
            QuestionnaireResponse.Item.newBuilder()
        ) {}
        viewHolder.bind(questionnaireItemViewItem)
        viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).performClick()

        val answer = questionnaireItemViewItem.questionnaireResponseItemBuilder.answerBuilderList
        assertThat(answer.size).isEqualTo(1)
        assertThat(answer[0].value.boolean.value).isTrue()
    }
}
