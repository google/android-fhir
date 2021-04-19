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

package com.google.android.fhir.datacapture.validation

import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object MaxValueConstraintValidator :
  ValueConstraintValidator(
    url = MAX_VALUE_EXTENSION_URL,
    predicate = {
      extension: Extension,
      answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent ->
      extension.value > answer.value
    },
    { extension: Extension, answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ->
      when {
        extension.value.fhirType().equals("integer") && answer.hasValueIntegerType() -> {
          "Maximum value allowed is:" + extension.value.primitiveValue()
        }
        extension.value.fhirType().equals("decimal") && answer.hasValueDecimalType() -> {
          "Maximum value allowed is:" + extension.value.primitiveValue()
        }
        extension.value.fhirType().equals("date") && answer.hasValueDateType() -> {
          "Maximum date allowed is:" + extension.value.primitiveValue()
        }
        extension.value.fhirType().equals("dateTime") && answer.hasValueDateTimeType() -> {
          "Maximum date & time allowed is:" + extension.value.primitiveValue()
        }
        else -> ""
      }
    }
  )

internal const val MAX_VALUE_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/maxValue"
