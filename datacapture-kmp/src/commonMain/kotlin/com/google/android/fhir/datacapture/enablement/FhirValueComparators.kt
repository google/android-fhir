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

  return when (this) {
    is QuestionnaireResponse.Item.Answer.Value.Boolean ->
      other is FhirR4Boolean && this.value.value == other.value
    is QuestionnaireResponse.Item.Answer.Value.Decimal ->
      other is FhirR4Decimal && this.value.value == other.value
    is QuestionnaireResponse.Item.Answer.Value.Integer ->
      other is Integer && this.value.value == other.value
    is QuestionnaireResponse.Item.Answer.Value.Date ->
      other is Date && this.value.value == other.value
    is QuestionnaireResponse.Item.Answer.Value.DateTime ->
      other is DateTime && this.value.value == other.value
    is QuestionnaireResponse.Item.Answer.Value.Time ->
      other is Time && this.value.value == other.value
    is QuestionnaireResponse.Item.Answer.Value.String ->
      other is FhirR4String && this.value.value == other.value
    is QuestionnaireResponse.Item.Answer.Value.Coding ->
      other is Coding &&
        this.value.system?.value == other.system?.value &&
        this.value.code?.value == other.code?.value
    is QuestionnaireResponse.Item.Answer.Value.Quantity ->
      other is Quantity && this.value.value?.value == other.value?.value
    is QuestionnaireResponse.Item.Answer.Value.Uri ->
      other is Uri && this.value.value == other.value
    is QuestionnaireResponse.Item.Answer.Value.Attachment ->
      other is Attachment && this.value == other
    is QuestionnaireResponse.Item.Answer.Value.Reference ->
      other is Reference && this.value.reference?.value == other.reference?.value
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

  return when (this) {
    is QuestionnaireResponse.Item.Answer.Value.Decimal -> {
      if (other !is Decimal) {
        throw IllegalStateException("The other value is not of type decimal")
      }
      val thisVal = this.value.value!!
      val otherVal = other.value!!
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Integer -> {
      if (other !is Integer) {
        throw IllegalStateException("The other value is not of type integer")
      }
      val thisVal = this.value.value!!
      val otherVal = other.value!!
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Date -> {
      if (other !is Date) {
        throw IllegalStateException("The other value is not of type date")
      }
      val thisVal = this.value.value!!.toString()
      val otherVal = other.value!!.toString()
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.DateTime -> {
      if (other !is DateTime) {
        throw IllegalStateException("The other value is not of type datetime")
      }
      val thisVal = this.value.value!!.toString()
      val otherVal = other.value!!.toString()
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Time -> {
      if (other !is Time) {
        throw IllegalStateException("The other value is not of type time")
      }
      val thisVal = this.value.value!!
      val otherVal = other.value!!
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.String -> {
      if (other !is FhirR4String) {
        throw IllegalStateException("The other value is not of type string")
      }
      val thisVal = this.value.value!!
      val otherVal = other.value!!
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Quantity -> {
      if (other !is Quantity) {
        throw IllegalStateException("The other value is not of type quantity")
      }
      val thisVal = this.value.value!!.value!!
      val otherVal = other.value!!.value!!
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
