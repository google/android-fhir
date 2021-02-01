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

import com.google.android.fhir.datacapture.views.QuestionnaireItemDatePickerViewHolderFactory
import com.google.fhir.r4.core.Questionnaire.Item.AnswerOption
import com.google.fhir.r4.core.QuestionnaireResponse.Item.Answer
import java.security.InvalidKeyException
import java.text.DateFormat

private val df_time = DateFormat.getTimeInstance()
val df_date = DateFormat.getDateInstance()

fun AnswerOption.getString(): String {
    if (this.value.hasCoding()) {
        return this.value.coding.code.value
    } else if (this.value.hasInteger()) {
        return this.value.integer.value.toString()
    } else if (this.value.hasDate()) {
        return df_date.format(this.value.date.valueUs /
            QuestionnaireItemDatePickerViewHolderFactory.NUMBER_OF_MICROSECONDS_PER_MILLISECOND)
    } else if (this.value.hasTime()) {
        return df_time.format(this.value.time.valueUs /
            QuestionnaireItemDatePickerViewHolderFactory.NUMBER_OF_MICROSECONDS_PER_MILLISECOND)
    } else if (this.value.hasStringValue()) {
        return this.value.stringValue.value.toString()
    } else {
        throw InvalidKeyException("$this is incorrect")
    }
}

fun AnswerOption.getResponseAnswerValueX(): Answer.ValueX {
    if (this.value.hasCoding()) {
        return Answer.ValueX.newBuilder()
            .setCoding(this.value.coding)
            .build()
    } else if (this.value.hasInteger()) {
        return Answer.ValueX.newBuilder()
            .setInteger(this.value.integer)
            .build()
    } else if (this.value.hasDate()) {
        return Answer.ValueX.newBuilder()
            .setDate(this.value.date)
            .build()
    } else if (this.value.hasTime()) {
        return Answer.ValueX.newBuilder()
            .setTime(this.value.time)
            .build()
    } else if (this.value.hasStringValue()) {
        return Answer.ValueX.newBuilder()
            .setStringValue(this.value.stringValue)
            .build()
    } else {
        throw InvalidKeyException("$this is incorrect")
    }
}
