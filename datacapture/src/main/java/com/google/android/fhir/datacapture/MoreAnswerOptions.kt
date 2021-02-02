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

import com.google.fhir.r4.core.Questionnaire.Item.AnswerOption
import com.google.fhir.r4.core.QuestionnaireResponse.Item.Answer

// Helper functions defined for Questionnaire.Item.AnswerOption .
// "choice" type question only expects valueCoding in answerOptions

fun AnswerOption.getDisplayString(): String {
    if (this.value.hasCoding()) {
        return this.value.coding.display.value
    } else {
        throw NotImplementedError("$this is not supported")
    }
}

fun AnswerOption.getResponseAnswerValueX(): Answer.ValueX {
    if (this.value.hasCoding()) {
        return Answer.ValueX.newBuilder()
            .setCoding(this.value.coding)
            .build()
    } else {
        throw NotImplementedError("$this is not supported")
    }
}
