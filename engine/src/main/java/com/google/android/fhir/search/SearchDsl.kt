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
import com.google.android.fhir.search.filter.DateParamFilterCriterion
import com.google.android.fhir.search.filter.FilterCriterion
import com.google.android.fhir.search.filter.NumberParamFilterCriterion
import com.google.android.fhir.search.filter.QuantityParamFilterCriterion
import com.google.android.fhir.search.filter.ReferenceParamFilterCriterion
import com.google.android.fhir.search.filter.StringParamFilterCriterion
import com.google.android.fhir.search.filter.TokenParamFilterCriterion
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

@SearchDslMarker
data class Search(val type: ResourceType, var count: Int? = null, var from: Int? = null) {
  internal val p = Patient()
  internal val stringFilterCriteria = mutableListOf<StringParamFilterCriteria>()
  internal val dateTimeFilterCriteria = mutableListOf<DateClientParamFilterCriteria>()
  internal val numberFilterCriteria = mutableListOf<NumberParamFilterCriteria>()
  internal val referenceFilterCriteria = mutableListOf<ReferenceParamFilterCriteria>()
  internal val tokenFilterCriteria = mutableListOf<TokenParamFilterCriteria>()
  internal val quantityFilterCriteria = mutableListOf<QuantityParamFilterCriteria>()
  internal var sort: IParam? = null
  internal var order: Order? = null
  @PublishedApi internal var nestedSearches = mutableListOf<NestedSearch>()
  var operation = Operation.AND

  fun filter(
    stringParameter: StringClientParam,
    vararg init: StringParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<StringParamFilterCriterion>()
    init.forEach { StringParamFilterCriterion(stringParameter).apply(it).also(filters::add) }
    stringFilterCriteria.add(StringParamFilterCriteria(filters, operation))
  }

  fun filter(
    referenceParameter: ReferenceClientParam,
    vararg init: ReferenceParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<ReferenceParamFilterCriterion>()
    init.forEach { ReferenceParamFilterCriterion(referenceParameter).apply(it).also(filters::add) }
    referenceFilterCriteria.add(ReferenceParamFilterCriteria(filters, operation))
  }

  fun filter(
    dateParameter: DateClientParam,
    vararg init: DateParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<DateParamFilterCriterion>()
    init.forEach { DateParamFilterCriterion(dateParameter).apply(it).also(filters::add) }
    dateTimeFilterCriteria.add(DateClientParamFilterCriteria(filters, operation))
  }

  fun filter(
    parameter: QuantityClientParam,
    vararg init: QuantityParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<QuantityParamFilterCriterion>()
    init.forEach { QuantityParamFilterCriterion(parameter).apply(it).also(filters::add) }
    quantityFilterCriteria.add(QuantityParamFilterCriteria(filters, operation))
  }

  fun filter(
    filter: TokenClientParam,
    vararg init: TokenParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<TokenParamFilterCriterion>()
    init.forEach { TokenParamFilterCriterion(filter).apply(it).also(filters::add) }
    tokenFilterCriteria.add(TokenParamFilterCriteria(filters, operation))
  }

  fun filter(
    numberParameter: NumberClientParam,
    vararg init: NumberParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<NumberParamFilterCriterion>()
    init.forEach { NumberParamFilterCriterion(numberParameter).apply(it).also(filters::add) }
    numberFilterCriteria.add(NumberParamFilterCriteria(filters, operation))
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

/**
 * Contains a set of filter criteria sharing the same search parameter. e.g A
 * [StringParamFilterCriteria] may contain a list of [StringParamFilterCriterion] each with
 * different [StringParamFilterCriterion.value] and [StringParamFilterCriterion.modifier] to filter
 * results for a particular [StringClientParam] like [Patient.GIVEN].
 *
 * An api call like filter(Patient.GIVEN,{value = "John"},{value = "Jane"}) will create a
 * [StringParamFilterCriteria] with two [StringParamFilterCriterion] one with
 * [StringParamFilterCriterion.value] as "John" and other as "Jane."
 */
internal sealed class FilterCriteria(
  open val filters: List<FilterCriterion>,
  open val operation: Operation
)

internal data class StringParamFilterCriteria(
  override val filters: List<StringParamFilterCriterion>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class DateClientParamFilterCriteria(
  override val filters: List<DateParamFilterCriterion>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class NumberParamFilterCriteria(
  override val filters: List<NumberParamFilterCriterion>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class ReferenceParamFilterCriteria(
  override val filters: List<ReferenceParamFilterCriterion>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class TokenParamFilterCriteria(
  override val filters: List<TokenParamFilterCriterion>,
  override val operation: Operation
) : FilterCriteria(filters, operation)

internal data class QuantityParamFilterCriteria(
  override val filters: List<QuantityParamFilterCriterion>,
  override val operation: Operation
) : FilterCriteria(filters, operation)
