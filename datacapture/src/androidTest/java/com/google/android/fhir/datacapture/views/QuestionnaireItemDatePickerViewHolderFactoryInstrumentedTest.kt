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
import com.google.android.fhir.datacapture.views.QuestionnaireItemDatePickerViewHolderFactory.NUMBER_OF_MICROSECONDS_PER_SECOND
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemDatePickerViewHolderFactoryInstrumentedTest {
    private lateinit var context: ContextThemeWrapper
    private lateinit var parent: FrameLayout
    private lateinit var viewHolder: QuestionnaireItemViewHolder

    @Before
    fun setUp() {
        context = ContextThemeWrapper(
            InstrumentationRegistry.getInstrumentation().getTargetContext(),
            R.style.Theme_MaterialComponents
        )
        parent = FrameLayout(context)
        viewHolder = QuestionnaireItemDatePickerViewHolderFactory.create(parent)
    }

    @Test
    fun shouldSetTextViewText() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            )
        )

        assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.textInputLayout).hint)
            .isEqualTo("Question?")
    }

    @Test
    @UiThreadTest
    fun shouldSetEmptyDateInput() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            )
        )

        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.textInputEditText).text.toString())
            .isEqualTo("")
    }

    @Test
    @UiThreadTest
    fun shouldSetDateInput() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder().addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setDate(
                                Date.newBuilder()
                                    .setValueUs(
                                        LocalDate
                                            .of(2020, 1, 1)
                                            .atStartOfDay()
                                            .atZone(ZoneId.systemDefault())
                                            .toEpochSecond() * NUMBER_OF_MICROSECONDS_PER_SECOND)
                                    .setPrecision(Date.Precision.DAY)
                                    .setTimezone(ZoneId.systemDefault().id)
                            ).build()
                    }
                )
            )
        )

        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.textInputEditText).text.toString())
            .isEqualTo("2020-01-01")
    }
}
