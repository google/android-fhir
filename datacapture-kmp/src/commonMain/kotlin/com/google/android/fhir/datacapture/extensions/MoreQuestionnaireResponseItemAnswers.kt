/*
 * Copyright 2022-2026 Google LLC
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

import com.google.fhir.model.r4.Element
import com.google.fhir.model.r4.QuestionnaireResponse

val QuestionnaireResponse.Item.Answer.elementValue: Element?
  get() =
    this.value?.let {
      when (it) {
        is QuestionnaireResponse.Item.Answer.Value.Attachment ->
          (value as QuestionnaireResponse.Item.Answer.Value.Attachment).value
        is QuestionnaireResponse.Item.Answer.Value.Boolean ->
          (value as QuestionnaireResponse.Item.Answer.Value.Boolean).value
        is QuestionnaireResponse.Item.Answer.Value.Coding ->
          (value as QuestionnaireResponse.Item.Answer.Value.Coding).value
        is QuestionnaireResponse.Item.Answer.Value.Date ->
          (value as QuestionnaireResponse.Item.Answer.Value.Date).value
        is QuestionnaireResponse.Item.Answer.Value.DateTime ->
          (value as QuestionnaireResponse.Item.Answer.Value.DateTime).value
        is QuestionnaireResponse.Item.Answer.Value.Decimal ->
          (value as QuestionnaireResponse.Item.Answer.Value.Decimal).value
        is QuestionnaireResponse.Item.Answer.Value.Integer ->
          (value as QuestionnaireResponse.Item.Answer.Value.Integer).value
        is QuestionnaireResponse.Item.Answer.Value.Quantity ->
          (value as QuestionnaireResponse.Item.Answer.Value.Quantity).value
        is QuestionnaireResponse.Item.Answer.Value.Reference ->
          (value as QuestionnaireResponse.Item.Answer.Value.Reference).value
        is QuestionnaireResponse.Item.Answer.Value.String ->
          (value as QuestionnaireResponse.Item.Answer.Value.String).value
        is QuestionnaireResponse.Item.Answer.Value.Time ->
          (value as QuestionnaireResponse.Item.Answer.Value.Time).value
        is QuestionnaireResponse.Item.Answer.Value.Uri ->
          (value as QuestionnaireResponse.Item.Answer.Value.Uri).value
      }
    }
