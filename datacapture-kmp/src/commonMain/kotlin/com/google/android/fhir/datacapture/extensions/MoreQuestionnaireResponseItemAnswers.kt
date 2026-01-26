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
        is QuestionnaireResponse.Item.Answer.Value.Attachment -> it.value
        is QuestionnaireResponse.Item.Answer.Value.Boolean -> it.value
        is QuestionnaireResponse.Item.Answer.Value.Coding -> it.value
        is QuestionnaireResponse.Item.Answer.Value.Date -> it.value
        is QuestionnaireResponse.Item.Answer.Value.DateTime -> it.value
        is QuestionnaireResponse.Item.Answer.Value.Decimal -> it.value
        is QuestionnaireResponse.Item.Answer.Value.Integer -> it.value
        is QuestionnaireResponse.Item.Answer.Value.Quantity -> it.value
        is QuestionnaireResponse.Item.Answer.Value.Reference -> it.value
        is QuestionnaireResponse.Item.Answer.Value.String -> it.value
        is QuestionnaireResponse.Item.Answer.Value.Time -> it.value
        is QuestionnaireResponse.Item.Answer.Value.Uri -> it.value
      }
    }
