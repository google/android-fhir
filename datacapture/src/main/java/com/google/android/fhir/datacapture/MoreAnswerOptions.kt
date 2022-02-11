/*
 * Copyright 2021 Google LLC
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

import com.google.android.fhir.getLocalizedText
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

val Questionnaire.QuestionnaireItemAnswerOptionComponent.displayString: String
  get() {
    if (!hasValueCoding()) {
      throw IllegalArgumentException("Answer option does not having coding.")
    }
    val display = valueCoding.displayElement.getLocalizedText() ?: valueCoding.display
    return if (display.isNullOrEmpty()) {
      valueCoding.code
    } else {
      display
    }
  }

val QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent.displayString: String?
  get() {
    return if (hasValueCoding()) {
      val display = valueCoding.display
      if (display.isNullOrEmpty()) {
        valueCoding.code
      } else {
        display
      }
    } else {
      null
    }
  }
