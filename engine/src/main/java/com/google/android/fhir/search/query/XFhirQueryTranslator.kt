/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.search.query

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.QuantityClientParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import ca.uhn.fhir.rest.gclient.UriClientParam
import com.google.android.fhir.getResourceClass
import com.google.android.fhir.index.SearchParamDefinition
import com.google.android.fhir.index.getSearchParamList
import com.google.android.fhir.isValidDateOnly
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.filter.TokenFilterValue
import com.google.android.fhir.search.filter.TokenParamFilterValueInstance
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Supports translation of x-fhir-query defined in
 * http://build.fhir.org/ig/HL7/sdc/expressions.html#x-fhir-query-enhancements and
 * http://hl7.org/fhir/R4/search.html
 */
object XFhirQueryTranslator {
  private const val XFHIR_QUERY_SORT_PARAM = "_sort"
  private const val XFHIR_QUERY_COUNT_PARAM = "_count"

  /**
   * Translates the basic x-fhir-query string defined in
   * http://build.fhir.org/ig/HL7/sdc/expressions.html#x-fhir-query-enhancements and
   * http://hl7.org/fhir/R4/search.html
   *
   * Example: Patient?active=true&gender=male&_sort=-name,gender&_count=11
   *
   * Complex queries including fhirpath expressions, global common search params, modifiers,
   * prefixes, chained parameters are not supported.
   */
  internal fun translate(xFhirQuery: String): Search {
    val (type, queryStringPairs) =
      xFhirQuery.split("?").let {
        ResourceType.fromCode(it.first()) to it.elementAtOrNull(1)?.split("&")
      }
    val queryParams =
      queryStringPairs?.mapNotNull {
        // skip missing values like active=[missing]
        it
          .split("=")
          .takeIf { it.size > 1 && it[1].isNotBlank() }
          ?.let { it.first() to it.elementAt(1) }
      }

    val sort =
      queryParams
        ?.find { it.first == XFHIR_QUERY_SORT_PARAM }
        ?.second
        ?.takeIf { it.isNotBlank() }
        ?.split(",")
        ?.map {
          // sortParam with prefix '-' means descending order
          it.removePrefix("-") to if (it.startsWith("-")) Order.DESCENDING else Order.ASCENDING
        }
    val count =
      queryParams
        ?.find { it.first == XFHIR_QUERY_COUNT_PARAM }
        ?.second
        ?.takeIf { it.isNotBlank() }
        ?.toInt()
    val searchParams =
      queryParams
        ?.filter {
          listOf(XFHIR_QUERY_COUNT_PARAM, XFHIR_QUERY_SORT_PARAM).contains(it.first).not()
        }
        ?.toMap()

    val querySearchParameters = searchParams?.toSearchParamDefinitionValueMap(type)

    return Search(type, count).apply {
      querySearchParameters?.forEach {
        val (param, filterValue) = it
        this.applyFilterParam(param, filterValue)
      }

      sort?.forEach { sortParam ->
        sortParam.first.toSearchParamDefinition(type).let { sort ->
          this.applySortParam(sort, sortParam.second)
        }
      }
    }
  }

  fun Search.applyFilterParam(param: SearchParamDefinition, filterValue: String) =
    when (param.type) {
      Enumerations.SearchParamType.NUMBER -> {
        this.filter(NumberClientParam(param.name), { value = filterValue.toBigDecimal() })
      }
      Enumerations.SearchParamType.DATE -> {
        if (!isValidDateOnly(filterValue)) {
          this.filter(DateClientParam(param.name), { value = of(DateTimeType(filterValue)) })
        } else {
          this.filter(DateClientParam(param.name), { value = of(DateType(filterValue)) })
        }
      }
      Enumerations.SearchParamType.QUANTITY -> {
        filterValue.toQuantity().let {
          this.filter(
            QuantityClientParam(param.name),
            {
              value = it.value
              system = it.system
              unit = it.unit
            },
          )
        }
      }
      Enumerations.SearchParamType.STRING -> {
        this.filter(StringClientParam(param.name), { value = filterValue })
      }
      Enumerations.SearchParamType.TOKEN -> {
        filterValue.toCoding().let {
          this.filter(
            TokenClientParam(param.name),
            {
              value =
                TokenFilterValue().apply {
                  tokenFilters.add(TokenParamFilterValueInstance(uri = it.system, code = it.code))
                }
            },
          )
        }
      }
      Enumerations.SearchParamType.REFERENCE -> {
        this.filter(ReferenceClientParam(param.name), { value = filterValue })
      }
      Enumerations.SearchParamType.URI -> {
        this.filter(UriClientParam(param.name), { value = filterValue })
      }
      else ->
        throw UnsupportedOperationException("${param.type} type not supported in x-fhir-query")
    }

  internal fun Search.applySortParam(param: SearchParamDefinition, order: Order = Order.ASCENDING) =
    when (param.type) {
      Enumerations.SearchParamType.NUMBER -> {
        this.sort(NumberClientParam(param.name), order)
      }
      Enumerations.SearchParamType.DATE -> {
        this.sort(DateClientParam(param.name), order)
      }
      Enumerations.SearchParamType.STRING -> {
        this.sort(StringClientParam(param.name), order)
      }
      else ->
        throw UnsupportedOperationException("${param.type} sort not supported in x-fhir-query")
    }

  private val ResourceType.resourceSearchParameters
    get() = getSearchParamList(getResourceClass<Resource>(this).newInstance())

  /** Parse string key-val map to SearchParamDefinition-Value map */
  private fun Map<String, String>.toSearchParamDefinitionValueMap(
    type: ResourceType,
  ): List<Pair<SearchParamDefinition, String>> {
    return this.map { (paramKey, paramValue) ->
      val paramDefinition = paramKey.toSearchParamDefinition(type)
      Pair(paramDefinition, paramValue)
    }
  }

  /** Parse param to SearchParamDefinition for given resourceType */
  private fun String.toSearchParamDefinition(resourceType: ResourceType) =
    resourceType.resourceSearchParameters.find { it.name == this }
      ?: throw IllegalArgumentException("$this not found in ${resourceType.name}")

  /**
   * Parse quantity string as defined in specs https://hl7.org/fhir/search.html#quantity The
   *
   * Components: value|system|unit OR value|unit OR value
   *
   * Examples: 5.4|http://unitsofmeasure.org|mg OR 5.4|mg OR 5.4
   */
  private fun String.toQuantity() =
    this.split("|").let { parts ->
      Quantity(parts.first().toDouble()).apply {
        if (parts.size == 3) {
          // system exists at index 1 only if all 3 components are specified
          system = parts.elementAt(1)
        }

        if (parts.size > 1) {
          unit = parts.last() // unit exists as last element only for two or more components
        }
      }
    }

  /**
   * Parse coding string as defined in specs https://hl7.org/fhir/search.html#token
   *
   * Components: system|code OR code
   *
   * Examples: http://snomed.org|112233 OR 112233
   */
  private fun String.toCoding() =
    this.split("|").let { parts ->
      Coding().apply {
        if (parts.size == 2) {
          // system exists as first element only if both components are specified
          system = parts.first()
        }

        code = parts.last() // code would always be specified and would exists as last element
      }
    }
}
