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

import com.google.android.fhir.datacapture.utilities.localizedString
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem.Companion.NOT_ANSWERED
import com.google.android.fhir.datacapture.views.localDate
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
import org.hl7.fhir.r4.model.UriType

internal val QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent.displayString: String
  get() {
    return when (value) {
      is Attachment -> valueAttachment.url ?: NOT_ANSWERED
      is BooleanType -> valueBooleanType.valueAsString ?: NOT_ANSWERED
      is Coding -> {
        val display = valueCoding.displayElement.getLocalizedText() ?: valueCoding.display
        if (display.isNullOrEmpty()) {
          valueCoding.code
        } else {
          display
        }
      }
      is DateType -> valueDateType?.localDate?.localizedString ?: NOT_ANSWERED
      is DateTimeType -> valueDateTimeType.valueAsString ?: NOT_ANSWERED
      is DecimalType -> valueDecimalType.valueAsString ?: NOT_ANSWERED
      is IntegerType -> valueIntegerType.valueAsString ?: NOT_ANSWERED
      is Quantity -> valueQuantity.value.toString()
      is StringType -> valueStringType.getLocalizedText()
          ?: valueStringType.valueAsString ?: NOT_ANSWERED
      is TimeType -> valueTimeType.valueAsString ?: NOT_ANSWERED
      is UriType -> valueUriType.valueAsString ?: NOT_ANSWERED
      else -> NOT_ANSWERED
    }
  }
