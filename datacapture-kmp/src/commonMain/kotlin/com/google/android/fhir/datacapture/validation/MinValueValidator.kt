/*
 * Copyright 2023-2026 Google LLC
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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.min_value_validation_error_msg
import com.google.android.fhir.datacapture.enablement.compareFhirValue
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.QuestionnaireResponse
import org.jetbrains.compose.resources.getString

internal const val MIN_VALUE_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/minValue"

/** A validator to check if the value of an answer is at least the permitted value. */
internal object MinValueValidator :
  AnswerExtensionConstraintValidator(
    url = MIN_VALUE_EXTENSION_URL,
    predicate = { constraintValue: Any, answer: QuestionnaireResponse.Item.Answer,
      ->
      answer.value compareFhirValue constraintValue < 0
    },
    messageGenerator = { constraintValue: Any ->
      getString(
        Res.string.min_value_validation_error_msg,
        getValue(constraintValue).toString(),
      )
    },
  )

private fun getValue(constraintValue: Any): Int? =
  when (constraintValue) {
    is Integer -> constraintValue.value
    is Extension.Value.Integer -> constraintValue.asInteger()?.value?.value
    else -> null
  }
