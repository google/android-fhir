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

package com.google.android.fhir

import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.DateTimeIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.PositionIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.filter.TokenFilterValue
import com.google.android.fhir.search.filter.TokenParamFilterValueInstance
import com.google.android.fhir.search.getApproximateDateRange
import com.google.android.fhir.search.rangeEpochDays
import com.google.android.fhir.search.rangeEpochMillis
import java.math.BigDecimal
import java.util.Date
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.ICoding
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Location
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Money
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.utils.FHIRPathEngine

val r4FhirAdapter =
  FhirAdapter(
    fhirVersionEnum = FhirVersionEnum.R4,
    evaluateFunction = { resource, path ->
      FHIRPathEngine(SimpleWorkerContext()).evaluate(resource as Base, path)
    },
    hasLastUpdated = { (it as Meta).hasLastUpdated() },
    toLastUpdatedElement = { (it as Meta).lastUpdatedElement.value },
    hasProfile = { (it as Meta).hasProfile() },
    hasTag = { (it as Meta).hasTag() },
    toNumberIndex = { searchParam, value ->
      when (value.fhirType()) {
        "integer" ->
          NumberIndex(searchParam.name, searchParam.path, BigDecimal((value as IntegerType).value))
        "decimal" -> NumberIndex(searchParam.name, searchParam.path, (value as DecimalType).value)
        else -> null
      }
    },
    toDateIndex = { searchParam, value ->
      val date = value as DateType
      DateIndex(
        searchParam.name,
        searchParam.path,
        date.value.epochDay,
        date.precision.add(date.value, 1).epochDay - 1
      )
    },
    toDateTimeIndex = { searchParam, value ->
      when (value.fhirType()) {
        "dateTime" -> {
          val dateTime = value as DateTimeType
          DateTimeIndex(
            searchParam.name,
            searchParam.path,
            dateTime.value.time,
            dateTime.precision.add(dateTime.value, 1).time - 1
          )
        }
        // No need to add precision because an instant is meant to have zero width
        "instant" -> {
          val instant = value as InstantType
          DateTimeIndex(searchParam.name, searchParam.path, instant.value.time, instant.value.time)
        }
        "Period" -> {
          val period = value as Period
          DateTimeIndex(
            searchParam.name,
            searchParam.path,
            if (period.hasStart()) period.start.time else 0,
            if (period.hasEnd()) period.endElement.precision.add(period.end, 1).time - 1
            else Long.MAX_VALUE
          )
        }
        "Timing" -> {
          val timing = value as Timing
          // Skip for now if its is repeating.
          if (timing.hasEvent()) {
            DateTimeIndex(
              searchParam.name,
              searchParam.path,
              timing.event.minOf { it.value.time },
              timing.event.maxOf { it.precision.add(it.value, 1).time } - 1
            )
          } else null
        }
        "string" -> {
          // e.g. CarePlan may have schedule as a string value 2011-06-27T09:30:10+01:00 (see
          // https://www.hl7.org/fhir/careplan-example-f001-heart.json.html)
          // OR 'daily' (see https://www.hl7.org/fhir/careplan-example-f201-renal.json.html)
          try {
            val dateTime = DateTimeType((value as StringType).value)
            DateTimeIndex(
              searchParam.name,
              searchParam.path,
              dateTime.value.time,
              dateTime.precision.add(dateTime.value, 1).time - 1
            )
          } catch (e: IllegalArgumentException) {
            null
          }
        }
        else -> null
      }
    },
    toStringIndex = { searchParam, value ->
      if (!value.isEmpty) {
        StringIndex(
          searchParam.name,
          searchParam.path,
          value =
            when (value) {
              is HumanName -> value.asString()
              is Address -> value.asString()
              else -> value.toString()
            }
        )
      } else {
        null
      }
    },
    toTokenIndexList = { searchParam, value ->
      when (value.fhirType()) {
        "boolean" ->
          listOf(
            TokenIndex(
              searchParam.name,
              searchParam.path,
              system = null,
              (value as Base).primitiveValue()
            )
          )
        "Identifier" -> {
          val identifier = value as Identifier
          if (identifier.value != null) {
            listOf(
              TokenIndex(searchParam.name, searchParam.path, identifier.system, identifier.value)
            )
          } else {
            listOf()
          }
        }
        "CodeableConcept" -> {
          val codeableConcept = value as CodeableConcept
          codeableConcept.coding
            .filter { it.code != null && it.code!!.isNotEmpty() }
            .map { TokenIndex(searchParam.name, searchParam.path, it.system ?: "", it.code) }
        }
        "code",
        "Coding" -> {
          val coding = value as ICoding
          listOf(TokenIndex(searchParam.name, searchParam.path, coding.system ?: "", coding.code))
        }
        else -> listOf()
      }
    },
    toReferenceIndex = { searchParam, value ->
      when (value) {
        is Reference -> value.reference
        is CanonicalType -> value.value
        is UriType -> value.value
        else -> throw UnsupportedOperationException("Value $value is not readable by SDK")
      }?.let { ReferenceIndex(searchParam.name, searchParam.path, it) }
    },
    toQuantityIndexList = { searchParam, value ->
      when (value.fhirType()) {
        "Money" -> {
          val money = value as Money
          listOf(
            QuantityIndex(
              searchParam.name,
              searchParam.path,
              FHIR_CURRENCY_CODE_SYSTEM,
              money.currency,
              money.value
            )
          )
        }
        "Quantity" -> {
          val quantity = value as Quantity
          val quantityIndices = mutableListOf<QuantityIndex>()

          // Add quantity indexing record for the human readable unit
          if (quantity.unit != null) {
            quantityIndices.add(
              QuantityIndex(searchParam.name, searchParam.path, "", quantity.unit, quantity.value)
            )
          }

          // Add quantity indexing record for the coded unit
          var canonicalCode = quantity.code
          var canonicalValue = quantity.value
          if (quantity.system == ucumUrl && quantity.code != null) {
            try {
              val ucumUnit =
                UnitConverter.getCanonicalForm(UcumValue(quantity.code, quantity.value))
              canonicalCode = ucumUnit.code
              canonicalValue = ucumUnit.value
            } catch (exception: ConverterException) {
              exception.printStackTrace()
            }
          }
          quantityIndices.add(
            QuantityIndex(
              searchParam.name,
              searchParam.path,
              quantity.system ?: "",
              canonicalCode ?: "",
              canonicalValue
            )
          )
          quantityIndices
        }
        else -> listOf()
      }
    },
    toUriIndex = { searchParam, value ->
      val uri = (value as UriType).value
      if (uri.isNotEmpty()) {
        UriIndex(searchParam.name, searchParam.path, uri)
      } else {
        null
      }
    },
    toPositionIndex = { value ->
      when (value?.fhirType()) {
        "Location.position" -> {
          val location = (value as Location.LocationPositionComponent)
          PositionIndex(location.latitude.toDouble(), location.longitude.toDouble())
        }
        else -> null
      }
    },
    toTokenFilterValue = { value ->
      when (value) {
        is ICoding ->
          TokenFilterValue().apply {
            tokenFilters.add(TokenParamFilterValueInstance(uri = value.system, code = value.code!!))
          }
        is CodeableConcept ->
          TokenFilterValue().apply {
            value.coding.forEach {
              tokenFilters.add(TokenParamFilterValueInstance(uri = it.system, code = it.code!!))
            }
          }
        is Identifier ->
          TokenFilterValue().apply {
            tokenFilters.add(
              TokenParamFilterValueInstance(uri = value.system, code = value.value!!)
            )
          }
        is ContactPoint ->
          TokenFilterValue().apply {
            tokenFilters.add(
              TokenParamFilterValueInstance(uri = value.use?.toCode(), code = value.value)
            )
          }
        is UriType ->
          TokenFilterValue().apply {
            tokenFilters.add(TokenParamFilterValueInstance(code = value.value))
          }
        else -> throw UnsupportedOperationException("Value $value is not readable by SDK")
      }
    },
    toGetConditionParamPairForDateType = { prefix, value ->
      val start = (value as DateType).rangeEpochDays.first
      val end = value.rangeEpochDays.last
      when (prefix) {
        // see https://www.hl7.org/fhir/search.html#prefix
        ParamPrefixEnum.APPROXIMATE -> {
          val currentDateType = DateType(Date.from(DateProvider().instant()), value.precision)
          val (diffStart, diffEnd) =
            getApproximateDateRange(value.rangeEpochDays, currentDateType.rangeEpochDays)

          ConditionParam(
            "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
            diffStart,
            diffEnd,
            diffStart,
            diffEnd
          )
        }
        ParamPrefixEnum.STARTS_AFTER -> ConditionParam("index_from > ?", end)
        ParamPrefixEnum.ENDS_BEFORE -> ConditionParam("index_to < ?", start)
        ParamPrefixEnum.NOT_EQUAL ->
          ConditionParam(
            "index_from NOT BETWEEN ? AND ? OR index_to NOT BETWEEN ? AND ?",
            start,
            end,
            start,
            end
          )
        ParamPrefixEnum.EQUAL ->
          ConditionParam(
            "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
            start,
            end,
            start,
            end
          )
        ParamPrefixEnum.GREATERTHAN -> ConditionParam("index_to > ?", end)
        ParamPrefixEnum.GREATERTHAN_OR_EQUALS -> ConditionParam("index_to >= ?", start)
        ParamPrefixEnum.LESSTHAN -> ConditionParam("index_from < ?", start)
        ParamPrefixEnum.LESSTHAN_OR_EQUALS -> ConditionParam("index_from <= ?", end)
      }
    },
    toGetConditionParamPairForDateTimeType = { prefix, value ->
      val start = (value as DateTimeType).rangeEpochMillis.first
      val end = value.rangeEpochMillis.last
      when (prefix) {
        // see https://www.hl7.org/fhir/search.html#prefix
        ParamPrefixEnum.APPROXIMATE -> {
          val currentDateTime = DateTimeType(Date.from(DateProvider().instant()), value.precision)
          val (diffStart, diffEnd) =
            getApproximateDateRange(value.rangeEpochMillis, currentDateTime.rangeEpochMillis)

          ConditionParam(
            "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
            diffStart,
            diffEnd,
            diffStart,
            diffEnd
          )
        }
        ParamPrefixEnum.STARTS_AFTER -> ConditionParam("index_from > ?", end)
        ParamPrefixEnum.ENDS_BEFORE -> ConditionParam("index_to < ?", start)
        ParamPrefixEnum.NOT_EQUAL ->
          ConditionParam(
            "index_from NOT BETWEEN ? AND ? OR index_to NOT BETWEEN ? AND ?",
            start,
            end,
            start,
            end
          )
        ParamPrefixEnum.EQUAL ->
          ConditionParam(
            "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
            start,
            end,
            start,
            end
          )
        ParamPrefixEnum.GREATERTHAN -> ConditionParam("index_to > ?", end)
        ParamPrefixEnum.GREATERTHAN_OR_EQUALS -> ConditionParam("index_to >= ?", start)
        ParamPrefixEnum.LESSTHAN -> ConditionParam("index_from < ?", start)
        ParamPrefixEnum.LESSTHAN_OR_EQUALS -> ConditionParam("index_from <= ?", end)
      }
    },
    toDate = { value -> DateType(value) },
    toDateTime = { value -> DateTimeType(value) },
    toQuantity = { value ->
      val quantity =
        value.split("|").let { parts ->
          Quantity(parts.first().toDouble()).apply {
            if (parts.size == 3) // system exists at index 1 only if all 3 components are specified
             system = parts.elementAt(1)

            if (parts.size > 1)
              unit = parts.last() // unit exists as last element only for two or more components
          }
        }
      Triple(quantity.value, quantity.system, quantity.unit)
    },
    toCoding = { value ->
      val coding =
        value.split("|").let { parts ->
          Coding().apply {
            if (parts.size == 2
            ) // system exists as first element only if both components are specified
             system = parts.first()

            code = parts.last() // code would always be specified and would exists as last element
          }
        }
      Pair(coding.system, coding.code)
    },
    validateResourceType = { ResourceType.fromCode(it) }
  )

/**
 * Extension to expresses [HumanName] as a separated string using [separator]. See
 * https://www.hl7.org/fhir/patient.html#search
 */
private fun HumanName.asString(separator: CharSequence = " "): String {
  return (prefix.filterNotNull().map { it.value } +
      given.filterNotNull().map { it.value } +
      family +
      suffix.filterNotNull().map { it.value } +
      text)
    .filterNotNull()
    .filter { it.isNotBlank() }
    .joinToString(separator)
}

/**
 * Extension to expresses [Address] as a string using [separator]. See
 * https://www.hl7.org/fhir/patient.html#search
 */
private fun Address.asString(separator: CharSequence = ", "): String {
  return (line.filterNotNull().map { it.value } +
      city +
      district +
      state +
      country +
      postalCode +
      text)
    .filterNotNull()
    .filter { it.isNotBlank() }
    .joinToString(separator)
}

/**
 * The FHIR currency code system. See: https://bit.ly/30YB3ML. See:
 * https://www.hl7.org/fhir/valueset-currencies.html.
 */
private const val FHIR_CURRENCY_CODE_SYSTEM = "urn:iso:std:iso:4217"
