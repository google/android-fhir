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

package com.google.android.fhir.datacapture.extensions

import android.content.Context
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.fhirpath.fhirPathEngine
import com.google.android.fhir.datacapture.views.factories.localDate
import com.google.android.fhir.datacapture.views.factories.localTime
import com.google.android.fhir.getLocalizedText
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.PrimitiveType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

/**
 * Returns the string representation of a [PrimitiveType].
 *
 * <p>If the type isn't a [PrimitiveType], an empty string is returned.
 */
fun Type.asStringValue(): String {
  if (!isPrimitive) return ""
  return (this as PrimitiveType<*>).asStringValue()
}

/**
 * Returns what to display on the UI depending on the [Type]. Used to get the display representation
 * for response item answer options such as
 * [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] or
 * [Questionnaire.QuestionnaireItemAnswerOptionComponent].
 */
fun Type.displayString(context: Context): String =
  getDisplayString(this, context) ?: context.getString(R.string.not_answered)

/** Returns value as string depending on the [Type] of element. */
fun Type.getValueAsString(context: Context): String =
  getValueString(this) ?: context.getString(R.string.not_answered)

/*
 * Returns the unique identifier of a [Type]. Used to differentiate between item answer options that
 * may have similar display strings
 */
fun Type.identifierString(context: Context): String =
  id ?: (this as? Coding)?.code ?: (this as? Reference)?.reference ?: displayString(context)

private fun getDisplayString(type: Type, context: Context): String? =
  when (type) {
    is Coding -> type.displayElement.getLocalizedText() ?: type.display ?: type.code
    is StringType -> type.getLocalizedText() ?: type.asStringValue()
    is DateType -> type.localDate?.format()
    is DateTimeType -> "${type.localDate.format()} ${type.localTime.toLocalizedString(context)}"
    is Reference -> type.display ?: type.reference
    is Attachment -> type.url
    is BooleanType -> {
      when (type.value) {
        true -> context.getString(R.string.yes)
        false -> context.getString(R.string.no)
        null -> null
      }
    }
    is Quantity -> type.value.toString()
    else -> (type as? PrimitiveType<*>)?.valueAsString
  }

private fun getValueString(type: Type): String? =
  when (type) {
    is DateType,
    is DateTimeType,
    is StringType -> type.asStringValue()
    is Quantity -> type.value.toString()
    else -> (type as? PrimitiveType<*>)?.valueAsString
  }

/** Converts StringType to toUriType. */
internal fun StringType.toUriType(): UriType {
  return UriType(value)
}

/** Converts StringType to CodeType. */
internal fun StringType.toCodeType(): CodeType {
  return CodeType(value)
}

/** Converts StringType to IdType. */
internal fun StringType.toIdType(): IdType {
  return IdType(value)
}

/** Converts Coding to CodeType. */
internal fun Coding.toCodeType(): CodeType {
  return CodeType(code)
}

fun Type.valueOrCalculateValue(): Type {
  return if (this.hasExtension()) {
    this.extension
      .firstOrNull { it.url == EXTENSION_CQF_CALCULATED_VALUE_URL }
      ?.let { extension ->
        val expression = (extension.value as Expression).expression
        fhirPathEngine.evaluate(this, expression).singleOrNull()?.let { it as Type }
      }
      ?: this
  } else {
    this
  }
}
