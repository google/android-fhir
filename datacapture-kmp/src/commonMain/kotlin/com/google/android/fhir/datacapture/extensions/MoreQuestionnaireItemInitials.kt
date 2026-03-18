/*
 * Copyright 2026 Google LLC
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

package com.google.android.fhir.datacapture.extensions

import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse

fun Questionnaire.Item.Initial.toQuestionnaireResponseItemAnswer():
  QuestionnaireResponse.Item.Answer =
  QuestionnaireResponse.Item.Answer(
    value =
      value.let {
        when (it) {
          is Questionnaire.Item.Initial.Value.Attachment ->
            QuestionnaireResponse.Item.Answer.Value.Attachment(
              value = it.value,
            )
          is Questionnaire.Item.Initial.Value.Boolean ->
            QuestionnaireResponse.Item.Answer.Value.Boolean(value = it.value)
          is Questionnaire.Item.Initial.Value.Coding ->
            QuestionnaireResponse.Item.Answer.Value.Coding(value = it.value)
          is Questionnaire.Item.Initial.Value.Date ->
            QuestionnaireResponse.Item.Answer.Value.Date(value = it.value)
          is Questionnaire.Item.Initial.Value.DateTime ->
            QuestionnaireResponse.Item.Answer.Value.DateTime(value = it.value)
          is Questionnaire.Item.Initial.Value.Decimal ->
            QuestionnaireResponse.Item.Answer.Value.Decimal(value = it.value)
          is Questionnaire.Item.Initial.Value.Integer ->
            QuestionnaireResponse.Item.Answer.Value.Integer(value = it.value)
          is Questionnaire.Item.Initial.Value.Quantity ->
            QuestionnaireResponse.Item.Answer.Value.Quantity(value = it.value)
          is Questionnaire.Item.Initial.Value.Reference ->
            QuestionnaireResponse.Item.Answer.Value.Reference(value = it.value)
          is Questionnaire.Item.Initial.Value.String ->
            QuestionnaireResponse.Item.Answer.Value.String(value = it.value)
          is Questionnaire.Item.Initial.Value.Time ->
            QuestionnaireResponse.Item.Answer.Value.Time(value = it.value)
          is Questionnaire.Item.Initial.Value.Uri ->
            QuestionnaireResponse.Item.Answer.Value.Uri(value = it.value)
        }
      },
  )
