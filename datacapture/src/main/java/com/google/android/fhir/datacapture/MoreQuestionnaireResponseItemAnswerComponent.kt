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

package com.google.android.fhir.datacapture

import android.content.Context
import com.google.android.fhir.datacapture.utilities.localizedString
import com.google.android.fhir.datacapture.utilities.toLocalizedString
import com.google.android.fhir.datacapture.views.localDate
import com.google.android.fhir.datacapture.views.localTime
import com.google.android.fhir.getLocalizedText
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

internal fun QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent.displayString(
  context: Context
): String {
  return when (value) {
    is Attachment -> valueAttachment.url ?: context.getString(R.string.not_answered)
    is BooleanType -> {
      when (valueBooleanType.value) {
        true -> context.getString(R.string.yes)
        false -> context.getString(R.string.no)
        null -> context.getString(R.string.not_answered)
      }
    }
    is Coding -> {
      val display = valueCoding.displayElement.getLocalizedText() ?: valueCoding.display
      if (display.isNullOrEmpty()) {
        valueCoding.code ?: context.getString(R.string.not_answered)
      } else {
        display
      }
    }
    is DateType -> valueDateType?.localDate?.localizedString
        ?: context.getString(R.string.not_answered)
    is DateTimeType ->
      "${valueDateTimeType.localDate.localizedString} ${valueDateTimeType.localTime.toLocalizedString(context)}"
    is DecimalType -> valueDecimalType.valueAsString ?: context.getString(R.string.not_answered)
    is IntegerType -> valueIntegerType.valueAsString ?: context.getString(R.string.not_answered)
    is Quantity -> valueQuantity.value.toString()
    is StringType -> valueStringType.getLocalizedText()
        ?: valueStringType.valueAsString ?: context.getString(R.string.not_answered)
    is TimeType -> valueTimeType.valueAsString ?: context.getString(R.string.not_answered)
    is UriType -> valueUriType.valueAsString ?: context.getString(R.string.not_answered)
    else -> context.getString(R.string.not_answered)
  }
}

internal fun List<
  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>.hasDifferentAnswerSet(
  answers: List<Type>
) =
  this.size != answers.size ||
    this.map { it.value }.zip(answers).any { (v1, v2) -> v1.equalsDeep(v2).not() }
