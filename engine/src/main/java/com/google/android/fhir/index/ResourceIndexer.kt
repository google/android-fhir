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

package com.google.android.fhir.index

import com.google.android.fhir.ConverterException
import com.google.android.fhir.UcumValue
import com.google.android.fhir.UnitConverter
import com.google.android.fhir.epochDay
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.DateTimeIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.PositionIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.LAST_UPDATED
import com.google.android.fhir.search.LOCAL_LAST_UPDATED
import com.google.android.fhir.ucumUrl
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumerations.SearchParamType
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.ICoding
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Location
import org.hl7.fhir.r4.model.Money
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.utils.FHIRPathEngine

/**
 * Indexes a FHIR resource according to the
 * [search parameters](https://www.hl7.org/fhir/searchparameter-registry.html).
 */
internal class ResourceIndexer(
  private val searchParamDefinitionsProvider: SearchParamDefinitionsProvider,
) {
  private val fhirPathEngine = FHIRPathEngine(DualHapiWorkerContext())

  fun <R : Resource> index(resource: R) = extractIndexValues(resource)

  private fun <R : Resource> extractIndexValues(resource: R): ResourceIndices {
    val indexBuilder = ResourceIndices.Builder(resource.resourceType, resource.logicalId)
    searchParamDefinitionsProvider
      .get(resource)
      .map { it to fhirPathEngine.evaluate(resource, it.path) }
      .flatMap { pair -> pair.second.map { pair.first to it } }
      .forEach { pair ->
        val (searchParam, value) = pair
        when (pair.first.type) {
          SearchParamType.NUMBER ->
            numberIndex(searchParam, value)?.also { indexBuilder.addNumberIndex(it) }
          SearchParamType.DATE ->
            if (value.fhirType() == "date") {
              dateIndex(searchParam, value).also { indexBuilder.addDateIndex(it) }
            } else {
              dateTimeIndex(searchParam, value)?.also { indexBuilder.addDateTimeIndex(it) }
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

    private fun numberIndex(searchParam: SearchParamDefinition, value: Base): NumberIndex? =
      when (value.fhirType()) {
        "integer" ->
          NumberIndex(searchParam.name, searchParam.path, BigDecimal((value as IntegerType).value))
        "decimal" -> NumberIndex(searchParam.name, searchParam.path, (value as DecimalType).value)
        else -> null
      }

    private fun dateIndex(searchParam: SearchParamDefinition, value: Base): DateIndex {
      val date = value as DateType
      return DateIndex(
        searchParam.name,
        searchParam.path,
        date.value.epochDay,
        date.precision.add(date.value, 1).epochDay - 1,
      )
    }

    private fun dateTimeIndex(searchParam: SearchParamDefinition, value: Base): DateTimeIndex? =
      when (value.fhirType()) {
        "dateTime" -> {
          val dateTime = value as DateTimeType
          DateTimeIndex(
            searchParam.name,
            searchParam.path,
            dateTime.value.time,
            dateTime.precision.add(dateTime.value, 1).time - 1,
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
            if (period.hasEnd()) {
              period.endElement.precision.add(period.end, 1).time - 1
            } else {
              Long.MAX_VALUE
            },
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
              timing.event.maxOf { it.precision.add(it.value, 1).time } - 1,
            )
          } else {
            null
          }
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
              dateTime.precision.add(dateTime.value, 1).time - 1,
            )
          } catch (e: IllegalArgumentException) {
            null
          }
        }
        else -> null
      }

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

    private fun stringIndex(searchParam: SearchParamDefinition, value: Base): StringIndex? =
      if (!value.isEmpty) {
        StringIndex(
          searchParam.name,
          searchParam.path,
          value =
            when (value) {
              is HumanName -> value.asString()
              is Address -> value.asString()
              else -> value.toString()
            },
        )
      } else {
        null
      }

    private fun tokenIndex(searchParam: SearchParamDefinition, value: Base): List<TokenIndex> =
      when (value.fhirType()) {
        "boolean" ->
          listOf(
            TokenIndex(searchParam.name, searchParam.path, system = null, value.primitiveValue()),
          )
        "Identifier" -> {
          val identifier = value as Identifier
          if (identifier.value != null) {
            listOf(
              TokenIndex(searchParam.name, searchParam.path, identifier.system, identifier.value),
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
        "Coding", -> {
          val coding = value as ICoding
          if (coding.code != null) {
            listOf(TokenIndex(searchParam.name, searchParam.path, coding.system ?: "", coding.code))
          } else {
            listOf()
          }
        }
        "id" -> {
          val id = value as IdType
          if (id.value != null) {
            listOf(TokenIndex(searchParam.name, searchParam.path, null, id.idPart ?: id.value))
          } else {
            listOf()
          }
        }
        else -> listOf()
      }

    private fun referenceIndex(searchParam: SearchParamDefinition, value: Base): ReferenceIndex? {
      return if (!value.isEmpty) {
        when (value) {
          is Reference -> value.reference
          is CanonicalType -> value.value
          is UriType -> value.value
          else -> throw UnsupportedOperationException("Value $value is not readable by SDK")
        }?.let { ReferenceIndex(searchParam.name, searchParam.path, it) }
      } else {
        null
      }
    }

    private fun quantityIndex(
      searchParam: SearchParamDefinition,
      value: Base,
    ): List<QuantityIndex> =
      when (value.fhirType()) {
        "Money" -> {
          val money = value as Money
          listOf(
            QuantityIndex(
              searchParam.name,
              searchParam.path,
              FHIR_CURRENCY_CODE_SYSTEM,
              money.currency,
              money.value,
            ),
          )
        }
        "Quantity" -> {
          val quantity = value as Quantity
          val quantityIndices = mutableListOf<QuantityIndex>()

          // Add quantity indexing record for the human readable unit
          if (quantity.unit != null) {
            quantityIndices.add(
              QuantityIndex(searchParam.name, searchParam.path, "", quantity.unit, quantity.value),
            )
          }

          // Add quantity indexing record for the coded unit
          var canonicalCode = quantity.code
          var canonicalValue = quantity.value
          if (quantity.system == ucumUrl && quantity.code != null) {
            try {
              val ucumUnit =
                UnitConverter.getCanonicalFormOrOriginal(UcumValue(quantity.code, quantity.value))
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
              canonicalValue,
            ),
          )
          quantityIndices
        }
        else -> listOf()
      }

    private fun uriIndex(searchParam: SearchParamDefinition, value: Base?): UriIndex? {
      val uri = (value as UriType).value
      return if (uri.isNotEmpty()) {
        UriIndex(searchParam.name, searchParam.path, uri)
      } else {
        null
      }
    }

    private fun specialIndex(value: Base?): PositionIndex? {
      return when (value?.fhirType()) {
        "Location.position" -> {
          val location = (value as Location.LocationPositionComponent)
          PositionIndex(location.latitude.toDouble(), location.longitude.toDouble())
        }
        else -> null
      }
    }

    /**
     * The FHIR currency code system. See: https://bit.ly/30YB3ML. See:
     * https://www.hl7.org/fhir/valueset-currencies.html.
     */
    private const val FHIR_CURRENCY_CODE_SYSTEM = "urn:iso:std:iso:4217"

    fun createLastUpdatedIndex(resourceType: ResourceType, instant: InstantType) =
      DateTimeIndex(
        name = LAST_UPDATED,
        path = arrayOf(resourceType.name, "meta", "lastUpdated").joinToString(separator = "."),
        from = instant.value.time,
        to = instant.value.time,
      )

    fun createLocalLastUpdatedIndex(resourceType: ResourceType, instant: InstantType) =
      DateTimeIndex(
        name = LOCAL_LAST_UPDATED,
        path = arrayOf(resourceType.name, "meta", "localLastUpdated").joinToString(separator = "."),
        from = instant.value.time,
        to = instant.value.time,
      )
  }
}

data class SearchParamDefinition(val name: String, val type: SearchParamType, val path: String)
