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

package com.google.android.fhir.datacapture

import android.os.Build
import com.google.android.fhir.datacapture.views.QuestionnaireItemDatePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.*
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.String
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.DateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreAnswerOptionsTest {

    @Test
    fun getString_choiceItemType_answerOptionShouldReturnValueString() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setStringValue(
                        String.newBuilder()
                            .setValue("test")
                    )
            ).build()
        assertThat(answerOption.getString()).isEqualTo("test")
    }

    @Test
    fun getString_choiceItemType_answerOptionShouldReturnValueInteger() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setInteger(
                        Integer.newBuilder()
                            .setValue(0)
                    )
            ).build()
        assertThat(answerOption.getString()).isEqualTo("0")
    }

    @Test
    fun getString_choiceItemType_answerOptionShouldReturnValueCoding() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setCoding(
                        Coding.newBuilder()
                            .setCode(
                                Code.newBuilder()
                                    .setValue("test-code")
                            )
                    )
            ).build()
        assertThat(answerOption.getString()).isEqualTo("test-code")
    }

    @Test
    fun getString_choiceItemType_answerOptionShouldReturnValueDate() {
        val df = DateFormat.getDateTimeInstance()
        df.setTimeZone(TimeZone.getDefault());
        val date = Calendar.getInstance().time
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setDate(
                        Date.newBuilder()
                            .setValueUs(date.time)
                            .setTimezone(TimeZone.getDefault().displayName)
                    )
            ).build()
        assertThat(answerOption.getString()).isEqualTo(
            Instant.ofEpochMilli(date.time /
                QuestionnaireItemDatePickerViewHolderFactory.NUMBER_OF_MICROSECONDS_PER_MILLISECOND)
            .atZone(ZoneId.systemDefault())
            .toString())
    }

    @Test
    fun getString_choiceItemType_answerOptionShouldReturnValueTime() {
        val df = DateFormat.getTimeInstance()
        df.setTimeZone(TimeZone.getDefault());
        val date = Calendar.getInstance().time
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setTime(
                        Time.newBuilder()
                            .setValueUs(date.time)
                    )
            ).build()
        assertThat(answerOption.getString()).isEqualTo(
            df.format(date.time /
            QuestionnaireItemDatePickerViewHolderFactory.NUMBER_OF_MICROSECONDS_PER_MILLISECOND))
    }

    @Test
    fun getResponseAnswerValueX_choiceItemType_ShouldReturnAnswerValueString() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setStringValue(
                        String.newBuilder()
                            .setValue("test")
                    )
            ).build()
        val answerValueX = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
            .setStringValue(
                String.newBuilder()
                    .setValue("test")
            ).build()
        assertThat(answerOption.getResponseAnswerValueX()).isEqualTo( answerValueX )
    }

    @Test
    fun getResponseAnswerValueX_choiceItemType_ShouldReturnAnswerValueInteger() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setInteger(
                        Integer.newBuilder()
                            .setValue(0)
                    )
            ).build()
        val answerValueX = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
            .setInteger(
                Integer.newBuilder()
                    .setValue(0)
            ).build()
        assertThat(answerOption.getResponseAnswerValueX()).isEqualTo( answerValueX )
    }

    @Test
    fun getResponseAnswerValueX_ShouldReturnAnswerValueCoding() {
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setCoding(
                        Coding.newBuilder()
                            .setCode(
                                Code.newBuilder()
                                    .setValue("test-code")
                            )
                    )
            ).build()
        val answerValueX = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
            .setCoding(
                Coding.newBuilder()
                    .setCode(
                        Code.newBuilder()
                            .setValue("test-code")
                    )
            ).build()
        assertThat(answerOption.getResponseAnswerValueX()).isEqualTo( answerValueX )    }

    @Test
    fun getResponseAnswerValueX_ShouldReturnAnswerValueDate() {
        val df = DateFormat.getDateTimeInstance()
        df.setTimeZone(TimeZone.getDefault());
        val date = Calendar.getInstance().time
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setDate(
                        Date.newBuilder()
                            .setValueUs(date.time)
                            .setTimezone(TimeZone.getDefault().displayName)
                    )
            ).build()
        val answerValueX = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
            .setDate(
                Date.newBuilder()
                    .setValueUs(date.time)
                    .setTimezone(TimeZone.getDefault().displayName)
            ).build()
        assertThat(answerOption.getResponseAnswerValueX()).isEqualTo( answerValueX )
    }

    @Test
    fun getResponseAnswerValueX_ShouldReturnAnswerValueTime() {
        val df = DateFormat.getTimeInstance()
        df.setTimeZone(TimeZone.getDefault());
        val date = Calendar.getInstance().time
        val answerOption = Questionnaire.Item.AnswerOption.newBuilder()
            .setValue(
                Questionnaire.Item.AnswerOption.ValueX.newBuilder()
                    .setTime(
                        Time.newBuilder()
                            .setValueUs(date.time)
                    )
            ).build()
        val answerValueX = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
            .setTime(
                Time.newBuilder()
                    .setValueUs(date.time)
            ).build()
        assertThat(answerOption.getResponseAnswerValueX()).isEqualTo( answerValueX )
    }
}
