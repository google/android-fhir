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
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.ZoneId

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemDatePickerViewHolderFactoryInstrumentedTest: BaseRobot() {
     lateinit var context2: ContextThemeWrapper
    private lateinit var parent: FrameLayout
    private lateinit var viewHolder: QuestionnaireItemViewHolder

    @Before
    fun setUp() {
        context2 = ContextThemeWrapper(
            InstrumentationRegistry.getInstrumentation().targetContext,
            R.style.Theme_MaterialComponents
        )
        parent = FrameLayout(context2)
        viewHolder = QuestionnaireItemDatePickerViewHolderFactory.create(parent)
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
            viewHolder.itemView.findViewById<TextView>(R.id.question).text
        ).isEqualTo("Question?")
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
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.textInputEditText).text.toString()
        ).isEqualTo("")
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
            ) {}
        )

        assertThat(
            viewHolder.itemView.findViewById<TextView>(R.id.textInputEditText).text.toString()
        ).isEqualTo("2020-01-01")
    }

    @Test
    @UiThreadTest
    fun isTimePickerDisplayed() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )
        viewHolder.itemView.findViewById<TextView>(R.id.textInputEditText).performClick()
        onView(withId(R.id.textInputEditText)).perform(ViewActions.click())
//        isDisplayed(withClassName(endsWith("android.widget.DatePicker")))
            .check(
                ViewAssertions.matches(withClassName(containsString("android.widget.DatePicker"))))
    }


}
