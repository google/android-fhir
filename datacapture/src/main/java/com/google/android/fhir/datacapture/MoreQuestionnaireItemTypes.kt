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

import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Returns the value of the [Questionnaire.Item.EnableWhen.AnswerX] for the [type].
 *
 * @throws IllegalArgumentException if [type] is not supported (for example, questions of type
 * [QuestionnaireItemTypeCode.Value.URL] do not have an explicit EnableWhen answer).
 */
fun Questionnaire.QuestionnaireItemEnableWhenComponent.getValueForType(
  type: Questionnaire.QuestionnaireItemType
): Base =
  when (type) {
    Questionnaire.QuestionnaireItemType.DATE -> answerDateType
    Questionnaire.QuestionnaireItemType.BOOLEAN -> answerBooleanType
    Questionnaire.QuestionnaireItemType.DECIMAL -> answerDecimalType
    Questionnaire.QuestionnaireItemType.INTEGER -> answerIntegerType
    Questionnaire.QuestionnaireItemType.DATETIME -> answerDateTimeType
    Questionnaire.QuestionnaireItemType.TIME -> answerTimeType
    Questionnaire.QuestionnaireItemType.STRING, Questionnaire.QuestionnaireItemType.TEXT ->
      answerStringType
    else -> throw IllegalArgumentException("Unsupported value type $type")
  }

/**
 * Returns the value of the [QuestionnaireResponse.Item.Answer] for the [type].
 *
 * @throws IllegalArgumentException if [type] is not supported (for example, questions of type
 * [QuestionnaireItemTypeCode.Value.GROUP] do not collect any answer).
 */
fun QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent.getValueForType(
  type: Questionnaire.QuestionnaireItemType
): Base =
  when (type) {
    Questionnaire.QuestionnaireItemType.DATE -> valueDateType
    Questionnaire.QuestionnaireItemType.BOOLEAN -> valueBooleanType
    Questionnaire.QuestionnaireItemType.DECIMAL -> valueDecimalType
    Questionnaire.QuestionnaireItemType.INTEGER -> valueIntegerType
    Questionnaire.QuestionnaireItemType.DATETIME -> valueDateTimeType
    Questionnaire.QuestionnaireItemType.TIME -> valueTimeType
    Questionnaire.QuestionnaireItemType.STRING, Questionnaire.QuestionnaireItemType.TEXT ->
      this.valueStringType
    Questionnaire.QuestionnaireItemType.URL -> this.valueUriType
    else -> throw IllegalArgumentException("Unsupported value type $value")
  }
