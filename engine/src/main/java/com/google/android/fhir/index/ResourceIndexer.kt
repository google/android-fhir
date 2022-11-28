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

import com.google.android.fhir.ConverterException
import com.google.android.fhir.FhirConverter
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
import com.google.android.fhir.resourceType
import com.google.android.fhir.ucumUrl
import java.math.BigDecimal
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseDecimalDatatype
import org.hl7.fhir.instance.model.api.IBaseIntegerDatatype

/**
 * Indexes a FHIR resource according to the
 * [search parameters](https://www.hl7.org/fhir/searchparameter-registry.html).
 */
internal object ResourceIndexer {

  private lateinit var fhirConverter: FhirConverter

  fun <R : IAnyResource> index(resource: R, fhirConverter: FhirConverter): ResourceIndices {
    this.fhirConverter = fhirConverter
    return extractIndexValues(resource)
  }

  private fun <R : IAnyResource> extractIndexValues(resource: R): ResourceIndices {
    val indexBuilder = ResourceIndices.Builder(resource.resourceType, resource.logicalId)
    getSearchParamList(resource.resourceType)
      .map { it to fhirConverter.evaluateFunction(resource, it.path) }
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
    if (fhirConverter.hasLastUpdated(resource.meta)) {
      val lastUpdatedElement = fhirConverter.getLastUpdatedElement(resource.meta)
      indexBuilder.addDateTimeIndex(
        DateTimeIndex(
          name = "_lastUpdated",
          path = arrayOf(resource.fhirType(), "meta", "lastUpdated").joinToString(separator = "."),
          from = lastUpdatedElement,
          to = lastUpdatedElement
        )
      )
    }

    if (fhirConverter.hasProfile(resource.meta)) {
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

    if (fhirConverter.hasTag(resource.meta)) {
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
    val date = fhirConverter.createDateType(value)
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
        val dateTime = fhirConverter.createDateTimeType(value)
        DateTimeIndex(
          searchParam.name,
          searchParam.path,
          dateTime.value!!.time,
          dateTime.precision.add(dateTime.value, 1).time - 1
        )
      }
      // No need to add precision because an instant is meant to have zero width
      "instant" -> {
        val instant = fhirConverter.createInstantType(value)
        DateTimeIndex(searchParam.name, searchParam.path, instant.value.time, instant.value.time)
      }
      "Period" -> {
        val period = fhirConverter.createPeriodType(value)
        DateTimeIndex(
          searchParam.name,
          searchParam.path,
          if (period.hasStart) period.start!!.time else 0,
          if (period.hasEnd) period.endElement.precision.add(period.end, 1).time - 1
          else Long.MAX_VALUE
        )
      }
      "Timing" -> {
        val timing = fhirConverter.createTimingType(value)
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
          val dateTime = fhirConverter.createDateTimeType(value)
          DateTimeIndex(
            searchParam.name,
            searchParam.path,
            dateTime.value!!.time,
            dateTime.precision.add(dateTime.value, 1).time - 1
          )
        } catch (e: IllegalArgumentException) {
          null
        }
      }
      else -> null
    }

  private fun stringIndex(searchParam: SearchParamDefinition, value: IBase): StringIndex? =
    if (!value.isEmpty) {
      StringIndex(searchParam.name, searchParam.path, value = fhirConverter.createStringType(value))
    } else {
      null
    }

  private fun tokenIndex(searchParam: SearchParamDefinition, value: IBase): List<TokenIndex> =
    when (value.fhirType()) {
      "boolean" ->
        listOf(
          TokenIndex(
            searchParam.name,
            searchParam.path,
            system = null,
            fhirConverter.getPrimitiveValue(value)
          )
        )
      "Identifier" -> {
        val identifier = fhirConverter.createIdentifierType(value)
        if (identifier.value != null) {
          listOf(
            TokenIndex(searchParam.name, searchParam.path, identifier.system, identifier.value)
          )
        } else {
          listOf()
        }
      }
      "CodeableConcept" -> {
        val codeableConcept = fhirConverter.createCodeableConceptType(value)
        codeableConcept.coding
          .filter { !it.code.isNullOrEmpty() }
          .map { TokenIndex(searchParam.name, searchParam.path, it.system ?: "", it.code!!) }
      }
      "code",
      "Coding", -> {
        val coding = fhirConverter.createCodingType(value)
        listOf(TokenIndex(searchParam.name, searchParam.path, coding.system ?: "", coding.code!!))
      }
      else -> listOf()
    }

  private fun referenceIndex(searchParam: SearchParamDefinition, value: IBase): ReferenceIndex? =
    fhirConverter.createReferenceType(value)?.let {
      ReferenceIndex(searchParam.name, searchParam.path, it)
    }

  private fun quantityIndex(searchParam: SearchParamDefinition, value: IBase): List<QuantityIndex> =
    when (value.fhirType()) {
      "Money" -> {
        val money = fhirConverter.createMoneyType(value)
        listOf(
          QuantityIndex(
            searchParam.name,
            searchParam.path,
            FHIR_CURRENCY_CODE_SYSTEM,
            money.currency,
            money.value!!
          )
        )
      }
      "Quantity" -> {
        val quantity = fhirConverter.createQuantityType(value)
        val quantityIndices = mutableListOf<QuantityIndex>()

        // Add quantity indexing record for the human readable unit
        if (quantity.unit != null) {
          quantityIndices.add(
            QuantityIndex(searchParam.name, searchParam.path, "", quantity.unit, quantity.value!!)
          )
        }

        // Add quantity indexing record for the coded unit
        var canonicalCode = quantity.code
        var canonicalValue = quantity.value
        if (quantity.system == ucumUrl && quantity.code != null) {
          try {
            val ucumUnit =
              UnitConverter.getCanonicalForm(UcumValue(quantity.code, quantity.value!!))
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
            canonicalValue!!
          )
        )
        quantityIndices
      }
      else -> listOf()
    }

  private fun uriIndex(searchParam: SearchParamDefinition, value: IBase?): UriIndex? {
    val uri = value?.let { fhirConverter.createReferenceType(it) }
    return if (uri.isNullOrEmpty()) {
      null
    } else {
      return UriIndex(searchParam.name, searchParam.path, uri)
    }
  }

  private fun specialIndex(value: IBase?): PositionIndex? {
    return when (value?.fhirType()) {
      "Location.position" -> {
        val location = fhirConverter.createLocationPositionComponentType(value)
        PositionIndex(location.latitude, location.longitude)
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
