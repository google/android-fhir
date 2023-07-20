/*
 * Copyright 2023 Google LLC
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

/**
 * A validator to check if the answer (a decimal value) exceeds the maximum number of permitted
 * decimal places.
 *
 * Only decimal types permitted in questionnaires response are subjected to this validation. See
 * https://www.hl7.org/fhir/extension-maxdecimalplaces.html
 */
import android.content.Context
import com.google.android.fhir.datacapture.R
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object MaxDecimalPlacesValidator :
  AnswerExtensionConstraintValidator(
    url = MAX_DECIMAL_URL,
    predicate = {
      extension: Extension,
      answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent ->
      val maxDecimalPlaces = (extension.value as? IntegerType)?.value
      answer.hasValueDecimalType() &&
        maxDecimalPlaces != null &&
        answer.valueDecimalType.valueAsString.substringAfter(".").length > maxDecimalPlaces
    },
    messageGenerator = { extension: Extension, context: Context ->
      context.getString(R.string.max_decimal_validation_error_msg, extension.value.primitiveValue())
    }
  )

private const val MAX_DECIMAL_URL = "http://hl7.org/fhir/StructureDefinition/maxDecimalPlaces"
