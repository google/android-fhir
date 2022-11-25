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
import com.google.android.fhir.R4Address
import com.google.android.fhir.R4Base
import com.google.android.fhir.R4CanonicalType
import com.google.android.fhir.R4CodeableConcept
import com.google.android.fhir.R4DateTimeType
import com.google.android.fhir.R4DateType
import com.google.android.fhir.R4HumanName
import com.google.android.fhir.R4ICoding
import com.google.android.fhir.R4Identifier
import com.google.android.fhir.R4InstantType
import com.google.android.fhir.R4LocationPositionComponent
import com.google.android.fhir.R4Money
import com.google.android.fhir.R4Period
import com.google.android.fhir.R4Quantity
import com.google.android.fhir.R4Reference
import com.google.android.fhir.R4Resource
import com.google.android.fhir.R4StringType
import com.google.android.fhir.R4Timing
import com.google.android.fhir.R4UriType
import com.google.android.fhir.R5Address
import com.google.android.fhir.R5Base
import com.google.android.fhir.R5CanonicalType
import com.google.android.fhir.R5CodeableConcept
import com.google.android.fhir.R5DateTimeType
import com.google.android.fhir.R5DateType
import com.google.android.fhir.R5HumanName
import com.google.android.fhir.R5ICoding
import com.google.android.fhir.R5Identifier
import com.google.android.fhir.R5InstantType
import com.google.android.fhir.R5LocationPositionComponent
import com.google.android.fhir.R5Money
import com.google.android.fhir.R5Period
import com.google.android.fhir.R5Quantity
import com.google.android.fhir.R5Reference
import com.google.android.fhir.R5Resource
import com.google.android.fhir.R5StringType
import com.google.android.fhir.R5Timing
import com.google.android.fhir.R5UriType
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
import org.hl7.fhir.r4.model.SearchParameter
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
    getSearchParamList(resource.resourceType)
      .map {
        when (resource) {
          is R4Resource -> it to fhirPathEngineR4.evaluate(resource, it.path)
          is R5Resource -> it to fhirPathEngineR5.evaluate(resource, it.path)
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
        is R4DateType -> DateType(value.value, value.precision)
        is R5DateType -> DateType(value.value, value.precision)
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
            is R4DateTimeType -> DateTimeType(value.value, value.precision)
            is R5DateTimeType -> DateTimeType(value.value, value.precision)
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
            is R4InstantType -> InstantType(value.value)
            is R5InstantType -> InstantType(value.value)
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        DateTimeIndex(searchParam.name, searchParam.path, instant.value.time, instant.value.time)
      }
      "Period" -> {
        val period =
          when (value) {
            is R4Period ->
              Period(
                value.hasStart(),
                value.hasEnd(),
                value.start,
                DateTimeType(value.endElement.value, value.endElement.precision),
                value.end
              )
            is R5Period ->
              Period(
                value.hasStart(),
                value.hasEnd(),
                value.start,
                DateTimeType(value.endElement.value, value.endElement.precision),
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
            is R4Timing ->
              Timing(value.hasEvent(), value.event.map { DateTimeType(it.value, it.precision) })
            is R5Timing ->
              Timing(value.hasEvent(), value.event.map { DateTimeType(it.value, it.precision) })
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
              is R4StringType ->
                DateTimeType(
                  R4DateTimeType(value.value).value,
                  R4DateTimeType(value.value).precision
                )
              is R5StringType ->
                DateTimeType(
                  R5DateTimeType(value.value).value,
                  R5DateTimeType(value.value).precision
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
  private fun R4HumanName.asString(separator: CharSequence = " "): String {
    return (prefix.filterNotNull().map { it.value } +
        given.filterNotNull().map { it.value } +
        family +
        suffix.filterNotNull().map { it.value } +
        text)
      .filterNotNull()
      .filter { it.isNotBlank() }
      .joinToString(separator)
  }

  private fun R5HumanName.asString(separator: CharSequence = " "): String {
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
  private fun R4Address.asString(separator: CharSequence = ", "): String {
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

  private fun R5Address.asString(separator: CharSequence = ", "): String {
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
            is R4HumanName -> value.asString()
            is R5HumanName -> value.asString()
            is R4Address -> value.asString()
            is R5Address -> value.asString()
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
            is R4Base -> value.primitiveValue()
            is R5Base -> value.primitiveValue()
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        listOf(TokenIndex(searchParam.name, searchParam.path, system = null, primitiveValue))
      }
      "Identifier" -> {
        val identifier =
          when (value) {
            is R4Identifier -> Identifier(value.system, value.value)
            is R5Identifier -> Identifier(value.system, value.value)
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
            is R4CodeableConcept ->
              CodeableConcept(
                value.coding.map { r4Coding -> Coding(r4Coding.system, r4Coding.code) }
              )
            is R5CodeableConcept ->
              CodeableConcept(
                value.coding.map { r5Coding -> Coding(r5Coding.system, r5Coding.code) }
              )
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        codeableConcept.coding
          .filter { !it.code.isNullOrEmpty() }
          .map { TokenIndex(searchParam.name, searchParam.path, it.system ?: "", it.code!!) }
      }
      "code",
      "Coding" -> {
        val coding =
          when (value) {
            is R4ICoding -> Coding(value.system, value.code)
            is R5ICoding -> Coding(value.system, value.code)
            else -> throw FHIRException("Unsupported FHIR Version")
          }
        listOf(TokenIndex(searchParam.name, searchParam.path, coding.system ?: "", coding.code!!))
      }
      else -> listOf()
    }

  private fun referenceIndex(searchParam: SearchParamDefinition, value: IBase): ReferenceIndex? {
    return when (value) {
      is R4Reference -> value.reference
      is R5Reference -> value.reference
      is R4CanonicalType -> value.value
      is R5CanonicalType -> value.value
      is R4UriType -> value.value
      is R5UriType -> value.value
      else -> throw UnsupportedOperationException("Value $value is not readable by SDK")
    }?.let { ReferenceIndex(searchParam.name, searchParam.path, it) }
  }

  private fun quantityIndex(searchParam: SearchParamDefinition, value: IBase): List<QuantityIndex> =
    when (value.fhirType()) {
      "Money" -> {
        val money =
          when (value) {
            is R4Money -> Money(value.currency, value.value)
            is R5Money -> Money(value.currency, value.value)
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
            is R4Quantity -> Quantity(value.unit, value.code, value.system, value.value)
            is R5Quantity -> Quantity(value.unit, value.code, value.system, value.value)
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
        is R4UriType -> value.value
        is R5UriType -> value.value
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
        is R4LocationPositionComponent ->
          LocationPositionComponent(value.latitude.toDouble(), value.longitude.toDouble())
        is R5LocationPositionComponent ->
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

data class Timing(val hasEvent: Boolean, val event: List<DateTimeType>)

data class Coding(val system: String?, val code: String?)

data class CodeableConcept(val coding: List<Coding>)

data class InstantType(val value: Date)

data class DateType(val value: Date, val precision: TemporalPrecisionEnum)

data class DateTimeType(val value: Date?, val precision: TemporalPrecisionEnum)

data class Period(
  val hasStart: Boolean,
  val hasEnd: Boolean,
  val start: Date?,
  val endElement: DateTimeType,
  val end: Date?
)
