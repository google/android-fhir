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

import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireItemTypeCode
import com.google.fhir.r4.core.QuestionnaireResponse
import com.google.fhir.shaded.protobuf.Message

/**
 * Returns the value of the [Questionnaire.Item.EnableWhen.AnswerX] for the [type].
 *
 * Used to retrieve the value to set the field in the extracted FHIR resource.
 *
 * @throws IllegalArgumentException if [type] is not supported (for example, questions of type
 * [QuestionnaireItemTypeCode.Value.URL] do not have an explicit EnableWhen answer).
 */
fun Questionnaire.Item.EnableWhen.AnswerX.getValueForType(
    type: Questionnaire.Item.TypeCode
): Message = when (val value = type.value) {
    QuestionnaireItemTypeCode.Value.DATE -> this.date
    QuestionnaireItemTypeCode.Value.BOOLEAN -> this.boolean
    QuestionnaireItemTypeCode.Value.DECIMAL -> this.decimal
    QuestionnaireItemTypeCode.Value.INTEGER -> this.integer
    QuestionnaireItemTypeCode.Value.DATE_TIME -> this.dateTime
    QuestionnaireItemTypeCode.Value.TIME -> this.time
    QuestionnaireItemTypeCode.Value.STRING, QuestionnaireItemTypeCode.Value.TEXT ->
        this.stringValue
    else -> throw IllegalArgumentException("Unsupported value type $value")
}

/**
 * Returns the value of the [QuestionnaireResponse.Item.Answer] for the [type].
 *
 * Used to retrieve the value to set the field in the extracted FHIR resource.
 *
 * @throws IllegalArgumentException if [type] is not supported (for example, questions of type
 * [QuestionnaireItemTypeCode.Value.GROUP] do not collect any answer).
 */
fun QuestionnaireResponse.Item.Answer.getValueForType(
    type: Questionnaire.Item.TypeCode
): Message = when (val value = type.value) {
    QuestionnaireItemTypeCode.Value.DATE -> this.value.date
    QuestionnaireItemTypeCode.Value.BOOLEAN -> this.value.boolean
    QuestionnaireItemTypeCode.Value.DECIMAL -> this.value.decimal
    QuestionnaireItemTypeCode.Value.INTEGER -> this.value.integer
    QuestionnaireItemTypeCode.Value.DATE_TIME -> this.value.dateTime
    QuestionnaireItemTypeCode.Value.TIME -> this.value.time
    QuestionnaireItemTypeCode.Value.STRING, QuestionnaireItemTypeCode.Value.TEXT ->
        this.value.stringValue
    QuestionnaireItemTypeCode.Value.URL -> this.value.uri
    else -> throw IllegalArgumentException("Unsupported value type $value")
}
