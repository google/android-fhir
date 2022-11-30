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

package com.google.android.fhir.search.query

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.QuantityClientParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import ca.uhn.fhir.rest.gclient.UriClientParam
import com.google.android.fhir.FhirAdapter
import com.google.android.fhir.index.SearchParamDefinition
import com.google.android.fhir.index.SearchParamType
import com.google.android.fhir.index.getSearchParamList
import com.google.android.fhir.isValidDateOnly
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.filter.DateFilterValues
import com.google.android.fhir.search.filter.TokenFilterValue
import com.google.android.fhir.search.filter.TokenParamFilterValueInstance

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
  internal fun translate(xFhirQuery: String, fhirAdapter: FhirAdapter): Search {
    val (resourceType, queryStringPairs) =
      xFhirQuery.split("?").let { it.first() to it.elementAtOrNull(1)?.split("&") }

    fhirAdapter.validateResourceType(resourceType)
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

    val querySearchParameters = searchParams?.toSearchParamDefinitionValueMap(resourceType)

    return Search(resourceType, count).apply {
      querySearchParameters?.forEach {
        val (param, filterValue) = it
        this.applyFilterParam(param, filterValue, fhirAdapter)
      }

      sort?.forEach { sortParam ->
        sortParam.first.toSearchParamDefinition(resourceType).let { sort ->
          this.applySortParam(sort, sortParam.second)
        }
      }
    }
  }

  fun Search.applyFilterParam(
    param: SearchParamDefinition,
    filterValue: String,
    fhirAdapter: FhirAdapter
  ) =
    when (param.type) {
      SearchParamType.NUMBER -> {
        this.filter(NumberClientParam(param.name), { value = filterValue.toBigDecimal() })
      }
      SearchParamType.DATE -> {
        if (!isValidDateOnly(filterValue))
          this.filter(
            DateClientParam(param.name),
            {
              value =
                DateFilterValues().apply {
                  this.dateTime = fhirAdapter.toDateTime(filterValue)
                  this.getConditionParamPairForDateTimeType =
                    fhirAdapter.toGetConditionParamPairForDateTimeType
                }
            }
          )
        else
          this.filter(
            DateClientParam(param.name),
            {
              value =
                DateFilterValues().apply {
                  this.date = fhirAdapter.toDate(filterValue)
                  this.getConditionParamPairForDateType =
                    fhirAdapter.toGetConditionParamPairForDateType
                }
            }
          )
      }
      SearchParamType.QUANTITY -> {
        val quantity = fhirAdapter.toQuantity(filterValue)
        quantity.let {
          this.filter(
            QuantityClientParam(param.name),
            {
              value = it.first
              system = it.second
              unit = it.third
            }
          )
        }
      }
      SearchParamType.STRING -> {
        this.filter(StringClientParam(param.name), { value = filterValue })
      }
      SearchParamType.TOKEN -> {
        val coding = fhirAdapter.toCoding(filterValue)
        coding.let {
          this.filter(
            TokenClientParam(param.name),
            {
              value =
                TokenFilterValue().apply {
                  tokenFilters.add(TokenParamFilterValueInstance(uri = it.first, code = it.second))
                }
            }
          )
        }
      }
      SearchParamType.REFERENCE -> {
        this.filter(ReferenceClientParam(param.name), { value = filterValue })
      }
      SearchParamType.URI -> {
        this.filter(UriClientParam(param.name), { value = filterValue })
      }
      else ->
        throw UnsupportedOperationException("${param.type} type not supported in x-fhir-query")
    }

  internal fun Search.applySortParam(param: SearchParamDefinition, order: Order = Order.ASCENDING) =
    when (param.type) {
      SearchParamType.NUMBER -> {
        this.sort(NumberClientParam(param.name), order)
      }
      SearchParamType.DATE -> {
        this.sort(DateClientParam(param.name), order)
      }
      SearchParamType.STRING -> {
        this.sort(StringClientParam(param.name), order)
      }
      else ->
        throw UnsupportedOperationException("${param.type} sort not supported in x-fhir-query")
    }

  private val String.resourceSearchParameters
    get() = getSearchParamList(this)

  /** Parse string key-val map to SearchParamDefinition-Value map */
  private fun Map<String, String>.toSearchParamDefinitionValueMap(
    resourceType: String
  ): List<Pair<SearchParamDefinition, String>> {
    return this.map { (paramKey, paramValue) ->
      val paramDefinition = paramKey.toSearchParamDefinition(resourceType)
      Pair(paramDefinition, paramValue)
    }
  }

  /** Parse param to SearchParamDefinition for given resourceType */
  private fun String.toSearchParamDefinition(resourceType: String) =
    resourceType.resourceSearchParameters.find { it.name == this }
      ?: throw IllegalArgumentException("$this not found in $resourceType")
}
