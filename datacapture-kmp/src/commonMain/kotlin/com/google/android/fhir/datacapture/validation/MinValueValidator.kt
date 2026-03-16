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
import com.google.android.fhir.datacapture.extensions.compareTo
import com.google.android.fhir.datacapture.extensions.elementDeepValue
import com.google.android.fhir.datacapture.extensions.elementValue
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.QuestionnaireResponse
import org.jetbrains.compose.resources.getString

internal const val MIN_VALUE_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/minValue"

/** A validator to check if the value of an answer is at least the permitted value. */
internal object MinValueValidator :
  AnswerExtensionConstraintValidator(
    url = MIN_VALUE_EXTENSION_URL,
    predicate = { constraintValue: Extension.Value, answer: QuestionnaireResponse.Item.Answer,
      ->
      answer.elementValue!! < constraintValue.elementValue
    },
    messageGenerator = { constraintValue: Extension.Value ->
      getString(
        Res.string.min_value_validation_error_msg,
        constraintValue.elementDeepValue.toString(),
      )
    },
  )
