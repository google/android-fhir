/*
 * Copyright 2025 Google LLC
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

import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Compares a QuestionnaireResponse answer value with an EnableWhen answer value. Handles the sealed
 * interfaces used in Kotlin FHIR models.
 */
infix fun QuestionnaireResponse.Item.Answer.Value?.equalsFhirValue(
  other: Questionnaire.Item.EnableWhen.Answer?,
): Boolean {
  return when (this) {
    is QuestionnaireResponse.Item.Answer.Value.Boolean ->
      other is Questionnaire.Item.EnableWhen.Answer.Boolean && this.value.value == other.value.value
    is QuestionnaireResponse.Item.Answer.Value.Decimal ->
      other is Questionnaire.Item.EnableWhen.Answer.Decimal && this.value.value == other.value.value
    is QuestionnaireResponse.Item.Answer.Value.Integer ->
      other is Questionnaire.Item.EnableWhen.Answer.Integer && this.value.value == other.value.value
    is QuestionnaireResponse.Item.Answer.Value.Date ->
      other is Questionnaire.Item.EnableWhen.Answer.Date && this.value.value == other.value.value
    is QuestionnaireResponse.Item.Answer.Value.DateTime ->
      other is Questionnaire.Item.EnableWhen.Answer.DateTime &&
        this.value.value == other.value.value
    is QuestionnaireResponse.Item.Answer.Value.Time ->
      other is Questionnaire.Item.EnableWhen.Answer.Time && this.value.value == other.value.value
    is QuestionnaireResponse.Item.Answer.Value.String ->
      other is Questionnaire.Item.EnableWhen.Answer.String && this.value.value == other.value.value
    is QuestionnaireResponse.Item.Answer.Value.Uri ->
      throw IllegalStateException("EnableWhen.Answer.Uri is not supported")
    is QuestionnaireResponse.Item.Answer.Value.Coding ->
      other is Questionnaire.Item.EnableWhen.Answer.Coding && this.value == other.value
    is QuestionnaireResponse.Item.Answer.Value.Quantity ->
      other is Questionnaire.Item.EnableWhen.Answer.Quantity && this.value == other.value
    else -> false
  }
}

/**
 * Compares a QuestionnaireResponse answer value with an EnableWhen answer value for ordering.
 * Returns negative if this < other, 0 if equal, positive if this > other.
 */
infix fun QuestionnaireResponse.Item.Answer.Value?.compareFhirValue(
  other: Questionnaire.Item.EnableWhen.Answer?,
): Int {
  return when (this) {
    is QuestionnaireResponse.Item.Answer.Value.Decimal -> {
      if (other !is Questionnaire.Item.EnableWhen.Answer.Decimal) {
        throw IllegalStateException("The other value is not of type decimal")
      }
      val thisVal = this.value.value ?: BigInteger.fromInt(0)
      val otherVal = other.value.value ?: BigInteger.fromInt(0)
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Integer -> {
      if (other !is Questionnaire.Item.EnableWhen.Answer.Integer) {
        throw IllegalStateException("The other value is not of type integer")
      }
      val thisVal = this.value.value ?: 0
      val otherVal = other.value.value ?: 0
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Date -> {
      if (other !is Questionnaire.Item.EnableWhen.Answer.Date) {
        throw IllegalStateException("The other value is not of type date")
      }
      val thisVal = this.value.value?.toString() ?: ""
      val otherVal = other.value.value?.toString() ?: ""
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.DateTime -> {
      if (other !is Questionnaire.Item.EnableWhen.Answer.DateTime) {
        throw IllegalStateException("The other value is not of type datetime")
      }
      val thisVal = this.value.value?.toString() ?: ""
      val otherVal = other.value.value?.toString() ?: ""
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Time -> {
      if (other !is Questionnaire.Item.EnableWhen.Answer.Time) {
        throw IllegalStateException("The other value is not of type time")
      }
      val thisVal = this.value.value?.toString() ?: ""
      val otherVal = other.value.value?.toString() ?: ""
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.String -> {
      if (other !is Questionnaire.Item.EnableWhen.Answer.String) {
        throw IllegalStateException("The other value is not of type string")
      }
      val thisVal = this.value.value ?: ""
      val otherVal = other.value.value ?: ""
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Quantity -> {
      if (other !is Questionnaire.Item.EnableWhen.Answer.Quantity) {
        throw IllegalStateException("The other value is not of type quantity")
      }
      val thisVal = this.value.value?.value ?: BigInteger.fromInt(0)
      val otherVal = other.value.value?.value ?: BigInteger.fromInt(0)
      thisVal.compareTo(otherVal)
    }
    is QuestionnaireResponse.Item.Answer.Value.Attachment ->
      throw IllegalStateException("EnableWhen.Answer.Attachment comparison is not supported")
    is QuestionnaireResponse.Item.Answer.Value.Boolean ->
      throw IllegalStateException("EnableWhen.Answer.Boolean comparison is not supported")
    is QuestionnaireResponse.Item.Answer.Value.Coding ->
      throw IllegalStateException("EnableWhen.Answer.Coding comparison is not supported")
    is QuestionnaireResponse.Item.Answer.Value.Reference ->
      throw IllegalStateException("EnableWhen.Answer.Reference comparison is not supported")
    is QuestionnaireResponse.Item.Answer.Value.Uri ->
      throw IllegalStateException("EnableWhen.Answer.Uri comparison is not supported")
    null -> throw IllegalStateException("EnableWhen.Answer.Value cannot be null")
  }
}
