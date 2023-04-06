/*
 * Copyright 2022 Google LLC
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

import android.content.Context
import com.google.android.fhir.compareTo
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.valueOrCalculateValue
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type

internal const val MIN_VALUE_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/minValue"
/** A validator to check if the value of an answer is at least the permitted value. */
internal object MinValueValidator :
  AnswerExtensionConstraintValidator(
    url = MIN_VALUE_EXTENSION_URL,
    predicate = {
      extension: Extension,
      answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent ->
      answer.value < extension.value?.valueOrCalculateValue()!!
    },
    { extension: Extension, context: Context ->
      context.getString(
        R.string.min_value_validation_error_msg,
        extension.value?.valueOrCalculateValue()?.primitiveValue()
      )
    }
  ) {

  internal fun getMinValue(
    questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent
  ): Type? {
    return questionnaireItemComponent.extension
      .firstOrNull { it.url == MIN_VALUE_EXTENSION_URL }
      ?.let { it.value?.valueOrCalculateValue() }
  }
}
