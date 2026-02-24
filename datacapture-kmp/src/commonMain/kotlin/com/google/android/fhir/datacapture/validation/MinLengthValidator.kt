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
import android_fhir.datacapture_kmp.generated.resources.min_length_validation_error_msg
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Integer
import org.jetbrains.compose.resources.getString

internal const val MIN_LENGTH_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/minLength"

/**
 * A validator to check if the answer fulfills the minimum number of permitted characters.
 *
 * <p>Only the following primitive types are subjected to this validation:
 * 1. Boolean
 * 2. Decimal
 * 3. Integer
 * 4. Date
 * 5. Time
 * 6. String
 * 7. Uri
 */
internal object MinLengthValidator :
  AnswerExtensionConstraintValidator(
    url = MIN_LENGTH_EXTENSION_URL,
    predicate = { constraintValue, answer ->
      val minLengthValue = getMinLengthValue(constraintValue)
      answer.value != null &&
        minLengthValue != null &&
        (answer.value!!.asString()?.value?.value ?: "").length < minLengthValue
    },
    messageGenerator = { constraintValue: Any ->
      getString(
        Res.string.min_length_validation_error_msg,
        getMinLengthValue(constraintValue).toString(),
      )
    },
  )

private fun getMinLengthValue(constraintValue: Any): Int? =
  when (constraintValue) {
    is Integer -> constraintValue.value
    is Extension.Value.Integer -> constraintValue.asInteger()?.value?.value
    else -> null
  }
