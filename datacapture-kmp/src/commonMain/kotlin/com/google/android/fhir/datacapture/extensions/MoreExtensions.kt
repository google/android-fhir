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

package com.google.android.fhir.datacapture.extensions

import com.google.fhir.model.r4.Element
import com.google.fhir.model.r4.Expression
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.Quantity
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalTime

internal const val EXTENSION_CQF_CALCULATED_VALUE_URL: String =
  "http://hl7.org/fhir/StructureDefinition/cqf-calculatedValue"

fun Extension.readStringExtension(uri: String): String? {
  val ext = extension.single { it.url == uri }
  return ext.value?.asUri()?.value?.value
    ?: ext.value?.asCanonical()?.value?.value ?: ext.value?.asCode()?.value?.value
      ?: ext.value?.asInteger()?.value?.value?.toString() ?: ext.value?.asMarkdown()?.value?.value
      ?: ext.value?.asString()?.value?.value
}

internal val Extension.Value.elementValue: Element
  get() =
    when (this) {
      is Extension.Value.Id -> this.value
      is Extension.Value.Address -> this.value
      is Extension.Value.Age -> this.value
      is Extension.Value.Annotation -> this.value
      is Extension.Value.Attachment -> this.value
      is Extension.Value.Base64Binary -> this.value
      is Extension.Value.Boolean -> this.value
      is Extension.Value.Canonical -> this.value
      is Extension.Value.Code -> this.value
      is Extension.Value.CodeableConcept -> this.value
      is Extension.Value.Coding -> this.value
      is Extension.Value.ContactDetail -> this.value
      is Extension.Value.ContactPoint -> this.value
      is Extension.Value.Contributor -> this.value
      is Extension.Value.Count -> this.value
      is Extension.Value.DataRequirement -> this.value
      is Extension.Value.Date -> this.value
      is Extension.Value.DateTime -> this.value
      is Extension.Value.Decimal -> this.value
      is Extension.Value.Distance -> this.value
      is Extension.Value.Dosage -> this.value
      is Extension.Value.Duration -> this.value
      is Extension.Value.Expression -> this.value
      is Extension.Value.HumanName -> this.value
      is Extension.Value.Identifier -> this.value
      is Extension.Value.Instant -> this.value
      is Extension.Value.Integer -> this.value
      is Extension.Value.Markdown -> this.value
      is Extension.Value.Meta -> this.value
      is Extension.Value.Money -> this.value
      is Extension.Value.Oid -> this.value
      is Extension.Value.ParameterDefinition -> this.value
      is Extension.Value.Period -> this.value
      is Extension.Value.PositiveInt -> this.value
      is Extension.Value.Quantity -> this.value
      is Extension.Value.Range -> this.value
      is Extension.Value.Ratio -> this.value
      is Extension.Value.Reference -> this.value
      is Extension.Value.RelatedArtifact -> this.value
      is Extension.Value.SampledData -> this.value
      is Extension.Value.Signature -> this.value
      is Extension.Value.String -> this.value
      is Extension.Value.Time -> this.value
      is Extension.Value.Timing -> this.value
      is Extension.Value.TriggerDefinition -> this.value
      is Extension.Value.UnsignedInt -> this.value
      is Extension.Value.Uri -> this.value
      is Extension.Value.Url -> this.value
      is Extension.Value.UsageContext -> this.value
      is Extension.Value.Uuid -> this.value
    }

internal val Extension.Value.elementDeepValue
  get() =
    when (this) {
      is Extension.Value.Date -> this.value.value
      is Extension.Value.DateTime -> this.value.value
      is Extension.Value.Decimal -> this.value.value
      is Extension.Value.PositiveInt -> this.value.value
      is Extension.Value.Quantity -> this.value.value
      is Extension.Value.Integer -> this.value.value
      is Extension.Value.Time -> this.value.value
      else -> elementValue
    }

internal val Extension.Value.cqfCalculatedValueExpression
  get() =
    this.elementValue.extension
      .find { it.url == EXTENSION_CQF_CALCULATED_VALUE_URL }
      ?.value
      ?.asExpression()
      ?.value

internal suspend fun Extension.Value.populateCqfCalculatedValue(
  evaluator: suspend (Expression) -> Any?,
): Extension.Value {
  val result = cqfCalculatedValueExpression?.let { evaluator.invoke(it) }

  return result?.let {
    when (this) {
      is Extension.Value.Date -> {
        this.copy(
          value =
            value.copy(
              value = FhirDate.fromString(result.toString()),
            ),
        )
      }
      is Extension.Value.DateTime ->
        this.copy(
          value =
            value.copy(
              value = FhirDateTime.fromString(result.toString()),
            ),
        )
      is Extension.Value.Decimal ->
        this.copy(
          value =
            value.copy(
              value = result as BigDecimal,
            ),
        )
      is Extension.Value.PositiveInt ->
        this.copy(
          value =
            value.copy(
              value = result as Int,
            ),
        )
      is Extension.Value.Quantity -> this.copy(value = result as Quantity)
      is Extension.Value.Integer -> this.copy(value = FhirR4Integer(value = result as Int))
      is Extension.Value.Time ->
        this.copy(
          value =
            value.copy(
              value = result as LocalTime,
              extension = emptyList(),
            ),
        )
      else -> this
    }
  }
    ?: this
}
