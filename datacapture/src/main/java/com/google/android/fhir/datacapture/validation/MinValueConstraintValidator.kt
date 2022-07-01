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

package com.google.android.fhir.datacapture.validation

import android.content.Context
import com.google.android.fhir.compareTo
import com.google.android.fhir.datacapture.CQF_CALCULATED_EXPRESSION_URL
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import org.hl7.fhir.r4.model.*

/** A validator to check if the value of an answer is at least the permitted value. */
internal object MinValueConstraintValidator :
  ValueConstraintExtensionValidator(
    url = MIN_VALUE_EXTENSION_URL,
    predicate = {
      extension: Extension,
      answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent ->
      answer.value < getExtensionValue(extension)
    },
    { extension: Extension, context: Context ->
      context.getString(
        R.string.min_value_validation_error_msg,
        getExtensionValue(extension).primitiveValue()
      )
    }
  ) {

  internal fun getMinValue(
    questionnaireItemComponent : Questionnaire.QuestionnaireItemComponent
  ): Type? {
    return questionnaireItemComponent.extension.firstOrNull { it.url == MIN_VALUE_EXTENSION_URL }?.let {
      processCQLExtension(questionnaireItemComponent, it)
    }
  }
  //todo : to move to Util validator
  internal fun processCQLExtension(
    questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent,
    it: Extension
  ): Type? {
    return if (it.value.hasExtension()) {
      it.value.extension.firstOrNull { it.url == CQF_CALCULATED_EXPRESSION_URL }?.let {
        val expression = (it.value as Expression).expression
        ResourceMapper.fhirPathEngine.evaluate(questionnaireItemComponent, expression).firstOrNull()?.let { it as Type }
      }
    } else {
      it.value as Type
    }
  }

  }

internal fun getExtensionValue(extension: Extension): Type {
  var result: Type? = null
  if (extension.value.hasExtension()) {
    extension.value.extension.firstOrNull()?.let {
      ResourceMapper.fhirPathEngine
        .evaluate(extension, (it.value as Expression).expression)
        .firstOrNull()
        ?.let { result = it as Type }
    }
  } else {
    result = extension.value
  }
  return result!!
}

internal const val MIN_VALUE_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/minValue"
