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
import android_fhir.datacapture_kmp.generated.resources.regex_validation_error_msg
import co.touchlab.kermit.Logger
import com.google.fhir.model.r4.QuestionnaireResponse
import org.jetbrains.compose.resources.getString

internal const val REGEX_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/regex"

/**
 * A validator to check if the answer matches a given regular expression.
 *
 * <p>Only primitive types permitted in questionnaires response are subjected to this validation.
 * See https://www.hl7.org/fhir/valueset-item-type.html#expansion
 */
internal object RegexValidator :
  AnswerExtensionConstraintValidator(
    url = REGEX_EXTENSION_URL,
    predicate =
      predicate@{ constraintValue: Any, answer: QuestionnaireResponse.Item.Answer,
        ->
        if (constraintValue !is String || answer.value == null) {
          return@predicate false
        }
        try {
          !constraintValue.toRegex().matches(answer.value!!.asString()?.value.toString())
        } catch (e: IllegalArgumentException) {
          Logger.w("Can't parse regex: $constraintValue", e)
          false
        }
      },
    messageGenerator = { constraintValue: Any ->
      getString(Res.string.regex_validation_error_msg, constraintValue.toString())
    },
  )
