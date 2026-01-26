/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.datacapture.enablement

import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4Decimal
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Date
import com.google.fhir.model.r4.DateTime
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Quantity
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Reference
import com.google.fhir.model.r4.Time
import com.google.fhir.model.r4.Uri

/**
 * Compares a [QuestionnaireResponse.Item.Answer.Value] with [Any] value for equality check. Returns
 * false if both values are null.
 */
infix fun QuestionnaireResponse.Item.Answer.Value?.equalsFhirValue(
  other: Any?,
): Boolean {
  if (this == null || other == null) return false

  val otherValue =
    if (other is Extension.Value) {
      when (other) {
        is Extension.Value.Boolean -> other.asBoolean()?.value
        is Extension.Value.Decimal -> other.asDecimal()?.value
        is Extension.Value.Integer -> other.asInteger()?.value
        is Extension.Value.Date -> other.asDate()?.value
        is Extension.Value.DateTime -> other.asDateTime()?.value
        is Extension.Value.Time -> other.asTime()?.value
        is Extension.Value.String -> other.asString()?.value
        is Extension.Value.Uri -> other.asUri()?.value
        is Extension.Value.Coding -> other.asCoding()?.value
        is Extension.Value.Quantity -> other.asQuantity()?.value
        is Extension.Value.Reference -> other.asReference()?.value
        else -> null
      }
    } else {
      other
    }

  if (otherValue == null) return false

  return when (this) {
    is QuestionnaireResponse.Item.Answer.Value.Boolean ->
      otherValue is FhirR4Boolean && this.value.value == otherValue.value
    is QuestionnaireResponse.Item.Answer.Value.Decimal ->
      otherValue is FhirR4Decimal && this.value.value == otherValue.value
    is QuestionnaireResponse.Item.Answer.Value.Integer ->
      otherValue is Integer && this.value.value == otherValue.value
    is QuestionnaireResponse.Item.Answer.Value.Date ->
      otherValue is Date && this.value.value == otherValue.value
    is QuestionnaireResponse.Item.Answer.Value.DateTime ->
      otherValue is DateTime && this.value.value == otherValue.value
    is QuestionnaireResponse.Item.Answer.Value.Time ->
      otherValue is Time && this.value.value == otherValue.value
    is QuestionnaireResponse.Item.Answer.Value.String ->
      otherValue is FhirR4String && this.value.value == otherValue.value
    is QuestionnaireResponse.Item.Answer.Value.Coding ->
      otherValue is Coding &&
        this.value.system?.value == otherValue.system?.value &&
        this.value.code?.value == otherValue.code?.value
    is QuestionnaireResponse.Item.Answer.Value.Quantity ->
      otherValue is Quantity && this.value.value?.value == otherValue.value?.value
    is QuestionnaireResponse.Item.Answer.Value.Uri ->
      otherValue is Uri && this.value.value == otherValue.value
    is QuestionnaireResponse.Item.Answer.Value.Attachment ->
      otherValue is Attachment && this.value == otherValue
    is QuestionnaireResponse.Item.Answer.Value.Reference ->
      otherValue is Reference && this.value.reference?.value == otherValue.reference?.value
  }
}

/**
 * Compares a [QuestionnaireResponse.Item.Answer.Value] with [Any] value for ordering. Returns
 * negative if this < other, 0 if equal, positive if this > other.
 */
infix fun QuestionnaireResponse.Item.Answer.Value?.compareFhirValue(
  other: Any?,
): Int {
  if (this == null || other == null) return 0

  val otherValue =
    if (other is Extension.Value) {
      when (other) {
        is Extension.Value.Decimal -> other.asDecimal()?.value
        is Extension.Value.Integer -> other.asInteger()?.value
        is Extension.Value.Date -> other.asDate()?.value
        is Extension.Value.DateTime -> other.asDateTime()?.value
        is Extension.Value.Time -> other.asTime()?.value
        is Extension.Value.String -> other.asString()?.value
        is Extension.Value.Quantity -> other.asQuantity()?.value
        else -> null
      }
    } else {
      other
    }

  if (otherValue == null) return 0

  return when (this) {
    is QuestionnaireResponse.Item.Answer.Value.Decimal -> {
      if (otherValue !is Decimal) {
        throw IllegalStateException("The other value is not of type decimal")
      }
      val thisVal = this.value.value!!
      val otherVal = otherValue.value!!
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Integer -> {
      if (otherValue !is Integer) {
        throw IllegalStateException(
          "The other value is not of type integer. Actual type: ${otherValue::class}",
        )
      }
      val thisVal = this.value.value!!
      val otherVal = otherValue.value!!
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Date -> {
      if (otherValue !is Date) {
        throw IllegalStateException("The other value is not of type date")
      }
      val thisVal = this.value.value!!.toString()
      val otherVal = otherValue.value!!.toString()
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.DateTime -> {
      if (otherValue !is DateTime) {
        throw IllegalStateException("The other value is not of type datetime")
      }
      val thisVal = this.value.value!!.toString()
      val otherVal = otherValue.value!!.toString()
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Time -> {
      if (otherValue !is Time) {
        throw IllegalStateException("The other value is not of type time")
      }
      val thisVal = this.value.value!!
      val otherVal = otherValue.value!!
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.String -> {
      if (otherValue !is FhirR4String) {
        throw IllegalStateException("The other value is not of type string")
      }
      val thisVal = this.value.value!!
      val otherVal = otherValue.value!!
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Quantity -> {
      if (otherValue !is Quantity) {
        throw IllegalStateException("The other value is not of type quantity")
      }
      val thisVal = this.value.value!!.value!!
      val otherVal = otherValue.value!!.value!!
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Attachment,
    is QuestionnaireResponse.Item.Answer.Value.Boolean,
    is QuestionnaireResponse.Item.Answer.Value.Coding,
    is QuestionnaireResponse.Item.Answer.Value.Reference,
    is QuestionnaireResponse.Item.Answer.Value.Uri, ->
      throw IllegalStateException("Comparison not supported for type :$this")
  }
}
