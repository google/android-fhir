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
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

internal const val EXTENSION_OPTION_EXCLUSIVE_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-optionExclusive"

/**
 * Text value for answer option [Questionnaire.QuestionnaireItemAnswerOptionComponent] if answer
 * option is [IntegerType], [StringType], [Coding], or [Reference] type.
 */
internal val Questionnaire.QuestionnaireItemAnswerOptionComponent.displayString: String
  get() = displayString(null, value)

internal fun QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent.displayString(
  context: Context
) = displayString(context, value)

/**
 * Text value for response item answer option
 * [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] or
 * [Questionnaire.QuestionnaireItemAnswerOptionComponent] depending on the type
 */
private fun displayString(context: Context?, value: Type?): String =
  when (value) {
    is Attachment -> value.url
        ?: (context?.getString(R.string.not_answered) ?: value.primitiveValue())
    is BooleanType -> {
      when (value.value) {
        true -> context?.getString(R.string.yes) ?: value.booleanValue().toString()
        false -> context?.getString(R.string.no) ?: value.booleanValue().toString()
        null -> context?.getString(R.string.not_answered) ?: value.booleanValue().toString()
      }
    }
    is Coding -> {
      val display = value.displayElement.getLocalizedText() ?: value.display
      if (display.isNullOrEmpty()) {
        value.code ?: context?.getString(R.string.not_answered) ?: value.primitiveValue()
      } else display
    }
    is DateType -> value.localDate?.localizedString
        ?: context?.getString(R.string.not_answered) ?: value.primitiveValue()
    is DateTimeType ->
      if (context != null) {
        "${value.localDate.localizedString} ${context.let { value.localTime.toLocalizedString(it) }}"
      } else {
        value.primitiveValue()
      }
    is DecimalType -> value.valueAsString
        ?: context?.getString(R.string.not_answered) ?: value.primitiveValue()
    is IntegerType -> value.valueAsString
        ?: context?.getString(R.string.not_answered) ?: value.primitiveValue()
    is Quantity -> value.value.toString()
    is Reference -> {
      val display = value.display
      if (display.isNullOrEmpty()) {
        value.reference ?: context?.getString(R.string.not_answered) ?: value.primitiveValue()
      } else display
    }
    is StringType -> value.getLocalizedText()
        ?: value.valueAsString ?: context?.getString(R.string.not_answered) ?: value.toString()
    is TimeType -> value.valueAsString
        ?: context?.getString(R.string.not_answered) ?: value.primitiveValue()
    is UriType -> value.valueAsString
        ?: context?.getString(R.string.not_answered) ?: value.primitiveValue()
    else -> context?.getString(R.string.not_answered)
        ?: throw IllegalArgumentException("$value is not supported.")
  }

/** Indicates that if this answerOption is selected, no other possible answers may be selected. */
internal val Questionnaire.QuestionnaireItemAnswerOptionComponent.optionExclusive: Boolean
  get() {
    val extension =
      this.extension.singleOrNull { it.url == EXTENSION_OPTION_EXCLUSIVE_URL } ?: return false
    val value = extension.value
    if (value is BooleanType) {
      return value.booleanValue()
    }
    return false
  }
