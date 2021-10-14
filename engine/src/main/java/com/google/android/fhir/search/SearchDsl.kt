/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.search

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.IParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.QuantityClientParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import com.google.android.fhir.search.filter.DateClientFilter
import com.google.android.fhir.search.filter.Filter
import com.google.android.fhir.search.filter.NumberFilter
import com.google.android.fhir.search.filter.QuantityFilter
import com.google.android.fhir.search.filter.ReferenceFilter
import com.google.android.fhir.search.filter.StringFilter
import com.google.android.fhir.search.filter.TokenClientFilter
import com.google.android.fhir.search.filter.TokenFilter
import org.hl7.fhir.r4.model.ResourceType

@SearchDslMarker
data class Search(val type: ResourceType, var count: Int? = null, var from: Int? = null) {
  internal val stringFilters = mutableListOf<StringFilterCriteria>()
  internal val dateTimeFilter = mutableListOf<DateClientFilterCriteria>()
  internal val numberFilter = mutableListOf<NumberFilterCriteria>()
  internal val referenceFilters = mutableListOf<ReferenceFilterCriteria>()
  internal val tokenFilters = mutableListOf<TokenFilterCriteria>()
  internal val quantityFilters = mutableListOf<QuantityFilterCriteria>()
  internal var sort: IParam? = null
  internal var order: Order? = null
  @PublishedApi internal var nestedSearches = mutableListOf<NestedSearch>()
  var operation = Operation.AND

  fun filter(
    stringParameter: StringClientParam,
    vararg init: StringFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<StringFilter>()
    init.forEach { StringFilter(stringParameter).apply(it).also(filters::add) }
    stringFilters.add(StringFilterCriteria(filters, operation))
  }

  fun filter(
    referenceParameter: ReferenceClientParam,
    vararg init: ReferenceFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<ReferenceFilter>()
    init.forEach { ReferenceFilter(referenceParameter).apply(it).also(filters::add) }
    referenceFilters.add(ReferenceFilterCriteria(filters, operation))
  }

  fun filter(
    dateParameter: DateClientParam,
    vararg init: DateClientFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<DateClientFilter>()
    init.forEach { DateClientFilter(dateParameter).apply(it).also(filters::add) }
    dateTimeFilter.add(DateClientFilterCriteria(filters, operation))
  }

  fun filter(
    parameter: QuantityClientParam,
    vararg init: QuantityFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<QuantityFilter>()
    init.forEach { QuantityFilter(parameter).apply(it).also(filters::add) }
    quantityFilters.add(QuantityFilterCriteria(filters, operation))
  }

  fun filter(
    filter: TokenClientParam,
    vararg init: TokenClientFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<TokenFilter>()
    init.forEach {
      TokenClientFilter()
        .apply(it)
        .value
        ?.tokenFilters
        ?.map { it.copy(parameter = filter) }
        ?.also(filters::addAll)
    }
    tokenFilters.add(TokenFilterCriteria(filters, operation))
  }

  fun filter(
    numberParameter: NumberClientParam,
    vararg init: NumberFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<NumberFilter>()
    init.forEach { NumberFilter(numberParameter).apply(it).also(filters::add) }
    numberFilter.add(NumberFilterCriteria(filters, operation))
  }

  fun sort(parameter: StringClientParam, order: Order) {
    sort = parameter
    this.order = order
  }

  fun sort(parameter: NumberClientParam, order: Order) {
    sort = parameter
    this.order = order
  }
}

enum class Order {
  ASCENDING,
  DESCENDING
}

enum class StringFilterModifier {
  STARTS_WITH,
  MATCHES_EXACTLY,
  CONTAINS
}

/** Logical operator between the filter values or the filters themselves. */
enum class Operation(val resultSetCombiningOperator: String) {
  OR("UNION"),
  AND("INTERSECT"),
}

internal sealed class FilterCriteria(open val filters: List<Filter>, open val operation: Operation)

internal data class StringFilterCriteria(
  override val filters: List<StringFilter>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class DateClientFilterCriteria(
  override val filters: List<DateClientFilter>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class NumberFilterCriteria(
  override val filters: List<NumberFilter>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class ReferenceFilterCriteria(
  override val filters: List<ReferenceFilter>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class TokenFilterCriteria(
  override val filters: List<TokenFilter>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class QuantityFilterCriteria(
  override val filters: List<QuantityFilter>,
  override val operation: Operation
) : FilterCriteria(filters, operation)
