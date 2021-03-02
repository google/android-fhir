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
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemDateTimePickerViewHolderFactoryInstrumentedTest {
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
        assertThat(parent).isNotNull()
        viewHolder = QuestionnaireItemDateTimePickerViewHolderFactory.create(parent)
    }

    @Test
    fun shouldSetTextInputLayoutHint() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.date_question).text
        ).isEqualTo("Question?")
        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.time_question).text
        ).isEqualTo("Question?")
    }

    @Test
    @UiThreadTest
    fun shouldSetEmptyDateTimeInput() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.dateInputEditText).text.toString()
        ).isEqualTo("")
        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.timeInputEditText).text.toString()
        ).isEqualTo("")
    }

    @Test
    @UiThreadTest
    fun shouldSetDateTimeInput() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder().addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setDateTime(
                                DateTime.newBuilder()
                                    .setValueUs(
                                        LocalDate
                                            .of(2020, 1, 5)
                                            .atTime(LocalTime.of(1, 30))
                                            .atZone(ZoneId.systemDefault())
                                            .toEpochSecond() * NUMBER_OF_MICROSECONDS_PER_SECOND)
                                    .setPrecision(DateTime.Precision.SECOND)
                                    .setTimezone(ZoneId.systemDefault().id)
                            ).build()
                    }
                )
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.dateInputEditText).text.toString()
        ).isEqualTo("2020-01-05")
        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.timeInputEditText).text.toString()
        ).isEqualTo("01:30:00")
    }
}
