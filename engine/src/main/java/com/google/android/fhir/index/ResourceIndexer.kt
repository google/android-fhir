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

package com.google.android.fhir.index

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport
import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import com.google.android.fhir.ConverterException
import com.google.android.fhir.UcumValue
import com.google.android.fhir.UnitConverter
import com.google.android.fhir.epochDay
import com.google.android.fhir.hasLastUpdated
import com.google.android.fhir.hasProfile
import com.google.android.fhir.hasTag
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.DateTimeIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.PositionIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.lastUpdatedElement
import com.google.android.fhir.logicalId
import com.google.android.fhir.resourceType
import com.google.android.fhir.ucumUrl
import java.math.BigDecimal
import java.util.Date
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseDecimalDatatype
import org.hl7.fhir.instance.model.api.IBaseIntegerDatatype
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.ICoding
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.Location
import org.hl7.fhir.r4.model.Money
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.SearchParameter
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.utils.FHIRPathEngine
import org.hl7.fhir.r5.hapi.ctx.HapiWorkerContext

/**
 * Indexes a FHIR resource according to the
 * [search parameters](https://www.hl7.org/fhir/searchparameter-registry.html).
 */
internal object ResourceIndexer {
  // Switched HapiWorkerContext to SimpleWorkerContext as a fix for
  // https://github.com/google/android-fhir/issues/768
  private val fhirPathEngineR4 = FHIRPathEngine(SimpleWorkerContext())
  private val fhirPathEngineR5 =
    org.hl7.fhir.r5.utils.FHIRPathEngine(
      HapiWorkerContext(
        FhirContext.forCached(FhirVersionEnum.R5),
        DefaultProfileValidationSupport(FhirContext.forCached(FhirVersionEnum.R5))
      )
    )

  fun <R : IAnyResource> index(resource: R) = extractIndexValues(resource)

  private fun <R : IAnyResource> extractIndexValues(resource: R): ResourceIndices {
    val indexBuilder = ResourceIndices.Builder(resource.resourceType, resource.logicalId)
    getSearchParamList(resource)
      .map {
        when (resource) {
          is Resource -> it to fhirPathEngineR4.evaluate(resource, it.path)
          is org.hl7.fhir.r5.model.Resource -> it to fhirPathEngineR5.evaluate(resource, it.path)
          else -> {
            return indexBuilder.build()
          }
        }
      }
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
            quantityIndex(searchParam, value)?.forEach { indexBuilder.addQuantityIndex(it) }
          SearchParamType.URI -> uriIndex(searchParam, value)?.also { indexBuilder.addUriIndex(it) }
          SearchParamType.SPECIAL -> specialIndex(value)?.also { indexBuilder.addPositionIndex(it) }
          // TODO: Handle composite type https://github.com/google/android-fhir/issues/292.
          else -> Unit
        }
      }

    addIndexesFromResourceClass(resource, indexBuilder)
    return indexBuilder.build()
  }

  /**
   * Manually add indexes for [SearchParameter]s defined in [Resource] class. This is because:
   * 1. There is no clear way defined in the search parameter definitions to figure out the class
   * hierarchy of the model classes in codegen.
   * 2. Common [SearchParameter]'s paths are defined for [Resource] class e.g even for the [Patient]
   * model, the [SearchParameter] expression for id would be `Resource.id` and
   * [FHIRPathEngine.evaluate] doesn't return anything when [Patient] is passed to the function.
   */
  private fun <R : IAnyResource> addIndexesFromResourceClass(
    resource: R,
    indexBuilder: ResourceIndices.Builder,
  ) {
    indexBuilder.addTokenIndex(
      TokenIndex(
        "_id",
        arrayOf(resource.fhirType(), "id").joinToString(separator = "."),
        null,
        resource.logicalId
      )
    )
    // Add 'lastUpdated' index to all resources.
    if (resource.meta.hasLastUpdated()) {
      val lastUpdatedElement = resource.meta.lastUpdatedElement
      indexBuilder.addDateTimeIndex(
        DateTimeIndex(
          name = "_lastUpdated",
          path = arrayOf(resource.fhirType(), "meta", "lastUpdated").joinToString(separator = "."),
          from = lastUpdatedElement,
          to = lastUpdatedElement
        )
      )
    }

    if (resource.meta.hasProfile()) {
      resource.meta.profile
        .filter { it.value != null && it.value.isNotEmpty() }
        .forEach {
          indexBuilder.addReferenceIndex(
            ReferenceIndex(
              "_profile",
              arrayOf(resource.fhirType(), "meta", "profile").joinToString(separator = "."),
              it.value
            )
          )
        }
    }

    if (resource.meta.hasTag()) {
      resource.meta.tag
        .filter { it.code != null && it.code!!.isNotEmpty() }
        .forEach {
          indexBuilder.addTokenIndex(
            TokenIndex(
              "_tag",
              arrayOf(resource.fhirType(), "meta", "tag").joinToString(separator = "."),
              it.system ?: "",
              it.code
            )
          )
        }
    }
  }

  private fun numberIndex(searchParam: SearchParamDefinition, value: IBase): NumberIndex? =
    when (value.fhirType()) {
      "integer" ->
        NumberIndex(
          searchParam.name,
          searchParam.path,
          BigDecimal((value as IBaseIntegerDatatype).value)
        )
      "decimal" ->
        NumberIndex(searchParam.name, searchParam.path, (value as IBaseDecimalDatatype).value)
      else -> null
    }

  private fun dateIndex(searchParam: SearchParamDefinition, value: IBase): DateIndex {
    val date =
      when (value) {
        is DateType -> com.google.android.fhir.index.DateType(value.value, value.precision)
        is org.hl7.fhir.r5.model.DateType ->
          com.google.android.fhir.index.DateType(value.value, value.precision)
        else -> throw FHIRException("Unsupported FHIR Version")
      }
    return DateIndex(
      searchParam.name,
      searchParam.path,
      date.value.epochDay,
      date.precision.add(date.value, 1).epochDay - 1
    )
  }

  private fun dateTimeIndex(searchParam: SearchParamDefinition, value: IBase): DateTimeIndex? =
    when (value.fhirType()) {
      "dateTime" -> {
        val dateTime =
          when (value) {
            is DateTimeType ->
              com.google.android.fhir.index.DateTimeType(value.value, value.precision)
            is org.hl7.fhir.r5.model.DateTimeType ->
              com.google.android.fhir.index.DateTimeType(value.value, value.precision)
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        dateTime.value?.let {
          DateTimeIndex(
            searchParam.name,
            searchParam.path,
            it.time,
            dateTime.precision.add(dateTime.value, 1).time - 1
          )
        }
      }
      // No need to add precision because an instant is meant to have zero width
      "instant" -> {
        val instant =
          when (value) {
            is InstantType -> com.google.android.fhir.index.InstantType(value.value)
            is org.hl7.fhir.r5.model.InstantType ->
              com.google.android.fhir.index.InstantType(value.value)
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        DateTimeIndex(searchParam.name, searchParam.path, instant.value.time, instant.value.time)
      }
      "Period" -> {
        val period =
          when (value) {
            is Period ->
              Period(
                value.hasStart(),
                value.hasEnd(),
                value.start,
                com.google.android.fhir.index.DateTimeType(
                  value.endElement.value,
                  value.endElement.precision
                ),
                value.end
              )
            is org.hl7.fhir.r5.model.Period ->
              Period(
                value.hasStart(),
                value.hasEnd(),
                value.start,
                com.google.android.fhir.index.DateTimeType(
                  value.endElement.value,
                  value.endElement.precision
                ),
                value.end
              )
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        DateTimeIndex(
          searchParam.name,
          searchParam.path,
          if (period.hasStart) period.start!!.time else 0,
          if (period.hasEnd) period.endElement.precision.add(period.end, 1).time - 1
          else Long.MAX_VALUE
        )
      }
      "Timing" -> {
        val timing =
          when (value) {
            is Timing ->
              Timing(
                value.hasEvent(),
                value.event.map {
                  com.google.android.fhir.index.DateTimeType(it.value, it.precision)
                }
              )
            is org.hl7.fhir.r5.model.Timing ->
              Timing(
                value.hasEvent(),
                value.event.map {
                  com.google.android.fhir.index.DateTimeType(it.value, it.precision)
                }
              )
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        // Skip for now if its is repeating.
        if (timing.hasEvent) {
          DateTimeIndex(
            searchParam.name,
            searchParam.path,
            timing.event.minOf { it.value!!.time },
            timing.event.maxOf { it.precision.add(it.value, 1).time } - 1
          )
        } else null
      }
      "string" -> {
        // e.g. CarePlan may have schedule as a string value 2011-06-27T09:30:10+01:00 (see
        // https://www.hl7.org/fhir/careplan-example-f001-heart.json.html)
        // OR 'daily' (see https://www.hl7.org/fhir/careplan-example-f201-renal.json.html)
        try {
          val dateTime =
            when (value) {
              is StringType ->
                com.google.android.fhir.index.DateTimeType(
                  DateTimeType(value.value).value,
                  DateTimeType(value.value).precision
                )
              is org.hl7.fhir.r5.model.StringType ->
                com.google.android.fhir.index.DateTimeType(
                  org.hl7.fhir.r5.model.DateTimeType(value.value).value,
                  org.hl7.fhir.r5.model.DateTimeType(value.value).precision
                )
              else -> throw FHIRException("Unsupported FHIR Version")
            }
          dateTime.value?.let {
            DateTimeIndex(
              searchParam.name,
              searchParam.path,
              it.time,
              dateTime.precision.add(dateTime.value, 1).time - 1
            )
          }
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

  private fun org.hl7.fhir.r5.model.HumanName.asString(separator: CharSequence = " "): String {
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

  private fun org.hl7.fhir.r5.model.Address.asString(separator: CharSequence = ", "): String {
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
  private fun stringIndex(searchParam: SearchParamDefinition, value: IBase): StringIndex? =
    if (!value.isEmpty) {
      StringIndex(
        searchParam.name,
        searchParam.path,
        value =
          when (value) {
            is HumanName -> value.asString()
            is org.hl7.fhir.r5.model.HumanName -> value.asString()
            is Address -> value.asString()
            is org.hl7.fhir.r5.model.Address -> value.asString()
            else -> value.toString()
          }
      )
    } else {
      null
    }

  private fun tokenIndex(searchParam: SearchParamDefinition, value: IBase): List<TokenIndex> =
    when (value.fhirType()) {
      "boolean" -> {
        val primitiveValue =
          when (value) {
            is Base -> value.primitiveValue()
            is org.hl7.fhir.r5.model.Base -> value.primitiveValue()
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        listOf(TokenIndex(searchParam.name, searchParam.path, system = null, primitiveValue))
      }
      "Identifier" -> {
        val identifier =
          when (value) {
            is Identifier -> Identifier(value.system, value.value)
            is org.hl7.fhir.r5.model.Identifier -> Identifier(value.system, value.value)
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        if (identifier.value != null) {
          listOf(
            TokenIndex(searchParam.name, searchParam.path, identifier.system, identifier.value)
          )
        } else {
          listOf()
        }
      }
      "CodeableConcept" -> {
        val codeableConcept =
          when (value) {
            is CodeableConcept ->
              CodeableConcept(
                value.coding.map { r4Coding -> Coding(r4Coding.system, r4Coding.code) }
              )
            is org.hl7.fhir.r5.model.CodeableConcept ->
              CodeableConcept(
                value.coding.map { r5Coding -> Coding(r5Coding.system, r5Coding.code) }
              )
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        codeableConcept.coding
          .filter { it.code != null && it.code.isNotEmpty() }
          .map { TokenIndex(searchParam.name, searchParam.path, it.system ?: "", it.code!!) }
      }
      "code",
      "Coding" -> {
        val coding =
          when (value) {
            is ICoding -> Coding(value.system, value.code)
            is org.hl7.fhir.r5.model.ICoding -> Coding(value.system, value.code)
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        listOf(TokenIndex(searchParam.name, searchParam.path, coding.system ?: "", coding.code!!))
      }
      else -> listOf()
    }

  private fun referenceIndex(searchParam: SearchParamDefinition, value: IBase): ReferenceIndex? {
    return when (value) {
      is Reference -> value.reference
      is org.hl7.fhir.r5.model.Reference -> value.reference
      is CanonicalType -> value.value
      is org.hl7.fhir.r5.model.CanonicalType -> value.value
      is UriType -> value.value
      is org.hl7.fhir.r5.model.UriType -> value.value
      else -> throw UnsupportedOperationException("Value $value is not readable by SDK")
    }?.let { ReferenceIndex(searchParam.name, searchParam.path, it) }
  }

  private fun quantityIndex(searchParam: SearchParamDefinition, value: IBase): List<QuantityIndex> =
    when (value.fhirType()) {
      "Money" -> {
        val money =
          when (value) {
            is Money -> Money(value.currency, value.value)
            is org.hl7.fhir.r5.model.Money -> Money(value.currency, value.value)
            else -> throw FHIRException("Unsupported FHIR Version")
          }
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
        val quantity =
          when (value) {
            is Quantity -> Quantity(value.unit, value.code, value.system, value.value)
            is org.hl7.fhir.r5.model.Quantity ->
              Quantity(value.unit, value.code, value.system, value.value)
            else -> throw FHIRException("Unsupported FHIR Version")
          }
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
            val ucumUnit = UnitConverter.getCanonicalForm(UcumValue(quantity.code, quantity.value))
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

  private fun uriIndex(searchParam: SearchParamDefinition, value: IBase?): UriIndex? {
    val url =
      when (value) {
        is UriType -> value.value
        is org.hl7.fhir.r5.model.UriType -> value.value
        else -> throw FHIRException("Unsupported FHIR Version")
      }
    return if (url.isNotEmpty()) {
      UriIndex(searchParam.name, searchParam.path, url)
    } else {
      null
    }
  }

  private fun specialIndex(value: IBase?): PositionIndex? {
    val locationPositionComponent =
      when (value) {
        is Location.LocationPositionComponent ->
          LocationPositionComponent(value.latitude.toDouble(), value.longitude.toDouble())
        is org.hl7.fhir.r5.model.Location.LocationPositionComponent ->
          LocationPositionComponent(value.latitude.toDouble(), value.longitude.toDouble())
        else -> null
      }

    return when (value?.fhirType()) {
      "Location.position" -> {
        PositionIndex(locationPositionComponent!!.latitude, locationPositionComponent.longitude)
      }
      else -> null
    }
  }

  /**
   * The FHIR currency code system. See: https://bit.ly/30YB3ML. See:
   * https://www.hl7.org/fhir/valueset-currencies.html.
   */
  private const val FHIR_CURRENCY_CODE_SYSTEM = "urn:iso:std:iso:4217"
}

data class SearchParamDefinition(val name: String, val type: SearchParamType, val path: String)

data class LocationPositionComponent(val latitude: Double, val longitude: Double)

data class Money(val currency: String, val value: BigDecimal)

data class Identifier(val system: String?, val value: String?)

data class Quantity(
  val unit: String?,
  val code: String?,
  val system: String?,
  val value: BigDecimal
)

data class Timing(
  val hasEvent: Boolean,
  val event: List<com.google.android.fhir.index.DateTimeType>
)

data class Coding(val system: String?, val code: String?)

data class CodeableConcept(val coding: List<Coding>)

data class InstantType(val value: Date)

data class DateType(val value: Date, val precision: TemporalPrecisionEnum)

data class DateTimeType(val value: Date?, val precision: TemporalPrecisionEnum)

data class Period(
  val hasStart: Boolean,
  val hasEnd: Boolean,
  val start: Date?,
  val endElement: com.google.android.fhir.index.DateTimeType,
  val end: Date?
)
