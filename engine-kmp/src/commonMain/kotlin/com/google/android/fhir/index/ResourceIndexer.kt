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

package com.google.android.fhir.index

import com.google.android.fhir.UcumValue
import com.google.android.fhir.UnitConverter
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.DateTimeIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.PositionIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.resourceType
import com.google.android.fhir.search.LAST_UPDATED
import com.google.android.fhir.search.LOCAL_LAST_UPDATED
import com.google.android.fhir.ucumUrl
import com.google.fhir.fhirpath.FhirPathEngine
import com.google.fhir.model.r4.Address
import com.google.fhir.model.r4.Canonical
import com.google.fhir.model.r4.CodeableConcept
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.DateTime
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.HumanName
import com.google.fhir.model.r4.Id
import com.google.fhir.model.r4.Identifier
import com.google.fhir.model.r4.Location
import com.google.fhir.model.r4.Money
import com.google.fhir.model.r4.Period
import com.google.fhir.model.r4.Quantity
import com.google.fhir.model.r4.Reference
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.Timing
import com.google.fhir.model.r4.Uri
import com.google.fhir.model.r4.terminologies.ResourceType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

/**
 * Indexes a FHIR resource according to the
 * [search parameters](https://www.hl7.org/fhir/searchparameter-registry.html).
 */
internal class ResourceIndexer(
  private val searchParamDefinitionsProvider: SearchParamDefinitionsProvider,
) {
  private val fhirPathEngine = FhirPathEngine.forR4()

  fun <R : Resource> index(resource: R) = extractIndexValues(resource)

  private fun <R : Resource> extractIndexValues(resource: R): ResourceIndices {
    val resourceTypeName = resource.resourceType
    val resourceTypeEnum = ResourceType.fromCode(resourceTypeName)
    val indexBuilder =
      ResourceIndices.Builder(resourceTypeEnum, resource.id ?: error("Resource must have an id"))
    searchParamDefinitionsProvider
      .get(resource)
      .map { it to fhirPathEngine.evaluateExpression(it.path, resource).toList() }
      .flatMap { pair -> pair.second.map { pair.first to it } }
      .forEach { pair ->
        val (searchParam, value) = pair
        when (pair.first.type) {
          SearchParamType.NUMBER ->
            numberIndex(searchParam, value)?.also { indexBuilder.addNumberIndex(it) }
          SearchParamType.DATE ->
            when (value) {
              is com.google.fhir.model.r4.Date ->
                dateIndex(searchParam, value)?.also { indexBuilder.addDateIndex(it) }
              else -> dateTimeIndex(searchParam, value)?.also { indexBuilder.addDateTimeIndex(it) }
            }
          SearchParamType.STRING ->
            stringIndex(searchParam, value)?.also { indexBuilder.addStringIndex(it) }
          SearchParamType.TOKEN ->
            tokenIndex(searchParam, value).forEach { indexBuilder.addTokenIndex(it) }
          SearchParamType.REFERENCE ->
            referenceIndex(searchParam, value)?.also { indexBuilder.addReferenceIndex(it) }
          SearchParamType.QUANTITY ->
            quantityIndex(searchParam, value).forEach { indexBuilder.addQuantityIndex(it) }
          SearchParamType.URI -> uriIndex(searchParam, value)?.also { indexBuilder.addUriIndex(it) }
          SearchParamType.SPECIAL -> specialIndex(value)?.also { indexBuilder.addPositionIndex(it) }
          // TODO: Handle composite type https://github.com/google/android-fhir/issues/292.
          else -> Unit
        }
      }

    return indexBuilder.build()
  }

  companion object {

    private fun numberIndex(searchParam: SearchParamDefinition, value: Any): NumberIndex? =
      when (value) {
        is com.google.fhir.model.r4.Integer ->
          value.value?.let {
            NumberIndex(searchParam.name, searchParam.path, BigDecimal.fromInt(it))
          }
        is com.google.fhir.model.r4.Decimal ->
          value.value?.let { NumberIndex(searchParam.name, searchParam.path, it) }
        else -> null
      }

    private fun dateIndex(
      searchParam: SearchParamDefinition,
      value: com.google.fhir.model.r4.Date,
    ): DateIndex? {
      val fhirDate = value.value ?: return null
      val (fromEpochDay, toEpochDay) = fhirDateToEpochDayRange(fhirDate)
      return DateIndex(searchParam.name, searchParam.path, fromEpochDay, toEpochDay)
    }

    private fun dateTimeIndex(searchParam: SearchParamDefinition, value: Any): DateTimeIndex? {
      return when (value) {
        is DateTime -> {
          val fhirDateTime = value.value ?: return null
          val (fromMs, toMs) = fhirDateTimeToEpochMillisRange(fhirDateTime)
          DateTimeIndex(searchParam.name, searchParam.path, fromMs, toMs)
        }
        // No need to add precision because an instant is meant to have zero width
        is com.google.fhir.model.r4.Instant -> {
          val fhirDateTime = value.value ?: return null
          val ms = fhirDateTimeToEpochMillis(fhirDateTime)
          DateTimeIndex(searchParam.name, searchParam.path, ms, ms)
        }
        is Period -> {
          val startMs = value.start?.value?.let { fhirDateTimeToEpochMillis(it) } ?: 0L
          val endMs = value.end?.value?.let { fhirDateTimeToEndEpochMillis(it) } ?: Long.MAX_VALUE
          DateTimeIndex(searchParam.name, searchParam.path, startMs, endMs)
        }
        is Timing -> {
          if (value.event.isNotEmpty()) {
            val events = value.event.mapNotNull { it.value }
            if (events.isEmpty()) return null
            DateTimeIndex(
              searchParam.name,
              searchParam.path,
              events.minOf { fhirDateTimeToEpochMillis(it) },
              events.maxOf { fhirDateTimeToEndEpochMillis(it) },
            )
          } else {
            null
          }
        }
        is com.google.fhir.model.r4.String -> {
          // e.g. CarePlan may have schedule as a string value
          try {
            val fhirDateTime = FhirDateTime.fromString(value.value)
            if (fhirDateTime != null) {
              val (fromMs, toMs) = fhirDateTimeToEpochMillisRange(fhirDateTime)
              DateTimeIndex(searchParam.name, searchParam.path, fromMs, toMs)
            } else {
              null
            }
          } catch (_: IllegalStateException) {
            null
          }
        }
        else -> null
      }
    }

    /**
     * Extension to express [HumanName] as a separated string using [separator]. See
     * https://www.hl7.org/fhir/patient.html#search
     */
    private fun HumanName.asString(separator: CharSequence = " "): kotlin.String {
      return (prefix.mapNotNull { it.value } +
          given.mapNotNull { it.value } +
          listOfNotNull(family?.value) +
          suffix.mapNotNull { it.value } +
          listOfNotNull(text?.value))
        .filter { it.isNotBlank() }
        .joinToString(separator)
    }

    /**
     * Extension to express [Address] as a string using [separator]. See
     * https://www.hl7.org/fhir/patient.html#search
     */
    private fun Address.asString(separator: CharSequence = ", "): kotlin.String {
      return (line.mapNotNull { it.value } +
          listOfNotNull(
            city?.value,
            district?.value,
            state?.value,
            country?.value,
            postalCode?.value,
            text?.value,
          ))
        .filter { it.isNotBlank() }
        .joinToString(separator)
    }

    private fun stringIndex(searchParam: SearchParamDefinition, value: Any): StringIndex? {
      val stringValue =
        when (value) {
          is HumanName -> value.asString()
          is Address -> value.asString()
          is com.google.fhir.model.r4.String -> value.value
          else -> value.toString()
        }
      return if (!stringValue.isNullOrEmpty()) {
        StringIndex(searchParam.name, searchParam.path, stringValue)
      } else {
        null
      }
    }

    private fun tokenIndex(searchParam: SearchParamDefinition, value: Any): List<TokenIndex> =
      when (value) {
        is com.google.fhir.model.r4.Boolean ->
          value.value?.let {
            listOf(
              TokenIndex(searchParam.name, searchParam.path, system = null, it.toString()),
            )
          }
            ?: emptyList()
        is Identifier -> {
          val identifierValue = value.value?.value
          if (identifierValue != null) {
            listOf(
              TokenIndex(
                searchParam.name,
                searchParam.path,
                value.system?.value,
                identifierValue,
              ),
            )
          } else {
            emptyList()
          }
        }
        is CodeableConcept -> {
          value.coding.mapNotNull { coding ->
            val codeValue = coding.code?.value
            if (codeValue != null && codeValue.isNotEmpty()) {
              TokenIndex(
                searchParam.name,
                searchParam.path,
                coding.system?.value ?: "",
                codeValue,
              )
            } else {
              null
            }
          }
        }
        is Coding -> {
          val code = value.code?.value
          if (code != null) {
            listOf(
              TokenIndex(searchParam.name, searchParam.path, value.system?.value ?: "", code),
            )
          } else {
            emptyList()
          }
        }
        is com.google.fhir.model.r4.Code -> {
          val code = value.value
          if (code != null) {
            listOf(TokenIndex(searchParam.name, searchParam.path, null, code))
          } else {
            emptyList()
          }
        }
        is Id -> {
          val idValue = value.value
          if (idValue != null) {
            listOf(TokenIndex(searchParam.name, searchParam.path, null, idValue))
          } else {
            emptyList()
          }
        }
        else -> emptyList()
      }

    private fun referenceIndex(searchParam: SearchParamDefinition, value: Any): ReferenceIndex? {
      val refValue =
        when (value) {
          is Reference -> value.reference?.value
          is Canonical -> value.value
          is Uri -> value.value
          else -> null
        }
      return refValue?.let { ReferenceIndex(searchParam.name, searchParam.path, it) }
    }

    private fun quantityIndex(
      searchParam: SearchParamDefinition,
      value: Any,
    ): List<QuantityIndex> {
      return when (value) {
        is Money -> {
          val moneyValue = value.value?.value ?: return emptyList()
          // Currencies enum name is the code with first letter capitalized (e.g., "Usd" for "USD")
          val currencyCode = value.currency?.value?.name?.uppercase() ?: return emptyList()
          listOf(
            QuantityIndex(
              searchParam.name,
              searchParam.path,
              FHIR_CURRENCY_CODE_SYSTEM,
              currencyCode,
              moneyValue,
            ),
          )
        }
        is Quantity -> {
          val quantityValue = value.value?.value ?: return emptyList()
          val quantityIndices = mutableListOf<QuantityIndex>()

          // Add quantity indexing record for the human readable unit
          val unit = value.unit?.value
          if (unit != null) {
            quantityIndices.add(
              QuantityIndex(searchParam.name, searchParam.path, "", unit, quantityValue),
            )
          }

          // Add quantity indexing record for the coded unit
          val system = value.system?.value
          val code = value.code?.value
          var canonicalCode = code
          var canonicalValue = quantityValue
          if (system == ucumUrl && code != null) {
            try {
              val ucumUnit =
                UnitConverter.getCanonicalFormOrOriginal(UcumValue(code, quantityValue))
              canonicalCode = ucumUnit.code
              canonicalValue = ucumUnit.value
            } catch (_: Exception) {
              // Fall through with original values
            }
          }
          quantityIndices.add(
            QuantityIndex(
              searchParam.name,
              searchParam.path,
              system ?: "",
              canonicalCode ?: "",
              canonicalValue,
            ),
          )
          quantityIndices
        }
        else -> emptyList()
      }
    }

    private fun uriIndex(searchParam: SearchParamDefinition, value: Any): UriIndex? {
      val uri =
        when (value) {
          is Uri -> value.value
          is com.google.fhir.model.r4.String -> value.value
          else -> null
        }
      return if (!uri.isNullOrEmpty()) {
        UriIndex(searchParam.name, searchParam.path, uri)
      } else {
        null
      }
    }

    private fun specialIndex(value: Any?): PositionIndex? {
      return when (value) {
        is Location.Position -> {
          val lat = value.latitude.value?.doubleValue(false) ?: return null
          val lon = value.longitude.value?.doubleValue(false) ?: return null
          PositionIndex(lat, lon)
        }
        else -> null
      }
    }

    /**
     * The FHIR currency code system. See: https://bit.ly/30YB3ML. See:
     * https://www.hl7.org/fhir/valueset-currencies.html.
     */
    private const val FHIR_CURRENCY_CODE_SYSTEM = "urn:iso:std:iso:4217"

    fun createLastUpdatedIndex(resourceType: ResourceType, epochMillis: Long) =
      DateTimeIndex(
        name = LAST_UPDATED,
        path = arrayOf(resourceType.name, "meta", "lastUpdated").joinToString(separator = "."),
        from = epochMillis,
        to = epochMillis,
      )

    fun createLocalLastUpdatedIndex(resourceType: ResourceType, epochMillis: Long) =
      DateTimeIndex(
        name = LOCAL_LAST_UPDATED,
        path = arrayOf(resourceType.name, "meta", "localLastUpdated").joinToString(separator = "."),
        from = epochMillis,
        to = epochMillis,
      )

    // --- Date utility functions ---

    /**
     * Converts a [FhirDate] to an epoch day range (from, to) accounting for the date's precision.
     */
    private fun fhirDateToEpochDayRange(date: FhirDate): Pair<Long, Long> =
      when (date) {
        is FhirDate.Date -> {
          val epochDay = date.date.toEpochDays().toLong()
          epochDay to epochDay
        }
        is FhirDate.YearMonth -> {
          val firstDay = LocalDate(date.value.year, date.value.month, 1)
          val nextMonth = firstDay.plus(1, DateTimeUnit.MONTH)
          firstDay.toEpochDays().toLong() to (nextMonth.toEpochDays().toLong() - 1)
        }
        is FhirDate.Year -> {
          val firstDay = LocalDate(date.value, 1, 1)
          val nextYear = LocalDate(date.value + 1, 1, 1)
          firstDay.toEpochDays().toLong() to (nextYear.toEpochDays().toLong() - 1)
        }
      }

    /** Converts a [FhirDateTime] to epoch milliseconds (start of the range). */
    private fun fhirDateTimeToEpochMillis(dateTime: FhirDateTime): Long =
      when (dateTime) {
        is FhirDateTime.DateTime -> {
          dateTime.dateTime.toInstant(dateTime.utcOffset).toEpochMilliseconds()
        }
        is FhirDateTime.Date -> {
          dateTime.date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        }
        is FhirDateTime.YearMonth -> {
          val firstDay = LocalDate(dateTime.value.year, dateTime.value.month, 1)
          firstDay.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        }
        is FhirDateTime.Year -> {
          val firstDay = LocalDate(dateTime.value, 1, 1)
          firstDay.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        }
      }

    /** Converts a [FhirDateTime] to epoch milliseconds (end of the range, precision-aware). */
    private fun fhirDateTimeToEndEpochMillis(dateTime: FhirDateTime): Long =
      when (dateTime) {
        is FhirDateTime.DateTime -> {
          dateTime.dateTime.toInstant(dateTime.utcOffset).toEpochMilliseconds()
        }
        is FhirDateTime.Date -> {
          val nextDay = dateTime.date.plus(1, DateTimeUnit.DAY)
          nextDay.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() - 1
        }
        is FhirDateTime.YearMonth -> {
          val firstDay = LocalDate(dateTime.value.year, dateTime.value.month, 1)
          val nextMonth = firstDay.plus(1, DateTimeUnit.MONTH)
          nextMonth.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() - 1
        }
        is FhirDateTime.Year -> {
          val nextYear = LocalDate(dateTime.value + 1, 1, 1)
          nextYear.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() - 1
        }
      }

    /**
     * Converts a [FhirDateTime] to an epoch millisecond range (from, to) accounting for precision.
     */
    private fun fhirDateTimeToEpochMillisRange(dateTime: FhirDateTime): Pair<Long, Long> =
      fhirDateTimeToEpochMillis(dateTime) to fhirDateTimeToEndEpochMillis(dateTime)
  }
}
