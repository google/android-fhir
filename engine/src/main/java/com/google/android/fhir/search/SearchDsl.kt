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
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import java.math.BigDecimal
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.UriType

@SearchDslMarker
data class Search(val type: ResourceType, var count: Int? = null, var from: Int? = null) {
  internal val stringFilters = mutableListOf<Pair<List<StringFilter>, Operation>>()
  internal val dateTimeFilter = mutableListOf<Pair<List<DateClientParamFilter>, Operation>>()
  internal val numberFilter = mutableListOf<Pair<List<NumberFilter>, Operation>>()
  internal val referenceFilters = mutableListOf<Pair<List<ReferenceFilter>, Operation>>()
  internal val tokenFilters = mutableListOf<Pair<List<TokenFilter>, Operation>>()
  internal val quantityFilters = mutableListOf<Pair<List<QuantityFilter>, Operation>>()
  internal var sort: IParam? = null
  internal var order: Order? = null
  @PublishedApi internal var nestedSearches = mutableListOf<NestedSearch>()
  var operation = Operation.AND

  fun filter(
    stringParameter: StringClientParam,
    vararg init: StringFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) =
    init.map { StringFilter(stringParameter).apply(it) }.also {
      stringFilters.add(Pair(it, operation))
    }

  fun filter(
    referenceParameter: ReferenceClientParam,
    vararg init: ReferenceFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) =
    init.map { ReferenceFilter(referenceParameter).apply(it) }.also {
      referenceFilters.add(Pair(it, operation))
    }

  fun filter(
    dateParameter: DateClientParam,
    vararg init: DateClientParamFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) =
    init.map { DateClientParamFilter(dateParameter).apply(it) }.also {
      dateTimeFilter.add(Pair(it, operation))
    }

  fun filter(
    parameter: QuantityClientParam,
    vararg init: QuantityFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) =
    init.map { QuantityFilter(parameter).apply(it) }.also {
      quantityFilters.add(Pair(it, operation))
    }

  fun filter(
    filter: TokenClientParam,
    vararg init: TokenClientFilterValues.() -> Unit,
    operation: Operation = Operation.OR
  ) =
    init
      .flatMap { TokenClientFilterValues().apply(it).tokenFilters }
      .map { it.copy(parameter = filter) }
      .also { tokenFilters.add(Pair(it, operation)) }

  fun filter(
    numberParameter: NumberClientParam,
    vararg init: NumberFilter.() -> Unit,
    operation: Operation = Operation.OR
  ) =
    init.map { NumberFilter(numberParameter).apply(it) }.also {
      numberFilter.add(Pair(it, operation))
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

@SearchDslMarker
data class StringFilter(
  val parameter: StringClientParam,
  var modifier: StringFilterModifier = StringFilterModifier.STARTS_WITH,
  var value: String? = null
) : Filter

@SearchDslMarker
data class DateFilter(
  val parameter: DateClientParam,
  var prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL,
  var value: DateType? = null
) : Filter

@SearchDslMarker
data class DateTimeFilter(
  val parameter: DateClientParam,
  var prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL,
  var value: DateTimeType? = null
) : Filter

@SearchDslMarker
data class DateClientParamFilter(
  val parameter: DateClientParam,
  var prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL,
  var value: DateClientFilterValues? = null
) : Filter {
  /** Returns [DateClientFilterValues] from [DateType]. */
  fun of(date: DateType) = DateClientFilterValues().apply { this.date = date }

  /** Returns [DateClientFilterValues] from [DateTimeType]. */
  fun of(dateTime: DateTimeType) = DateClientFilterValues().apply { this.dateTime = dateTime }
}

@SearchDslMarker
class DateClientFilterValues internal constructor() {
  var date: DateType? = null
  var dateTime: DateTimeType? = null
}

@SearchDslMarker
class TokenClientFilterValues internal constructor() {
  internal val tokenFilters = mutableListOf<TokenFilter>()
  lateinit var value: TokenClientFilterValues

  /** Returns [TokenClientFilterValues] from [Boolean]. */
  fun of(boolean: Boolean) = apply {
    tokenFilters.clear()
    tokenFilters.add(TokenFilter(code = boolean.toString()))
  }

  /** Returns [TokenClientFilterValues] from [String]. */
  fun of(string: String) = apply {
    tokenFilters.clear()
    tokenFilters.add(TokenFilter(code = string))
  }

  /** Returns [TokenClientFilterValues] from [UriType]. */
  fun of(uriType: UriType) = apply {
    tokenFilters.clear()
    tokenFilters.add(TokenFilter(code = uriType.value))
  }

  /** Returns [TokenClientFilterValues] from [CodeType]. */
  fun of(codeType: CodeType) = apply {
    tokenFilters.clear()
    tokenFilters.add(TokenFilter(code = codeType.value))
  }

  /** Returns [TokenClientFilterValues] from [Coding]. */
  fun of(coding: Coding) = apply {
    tokenFilters.clear()
    tokenFilters.add(TokenFilter(uri = coding.system, code = coding.code))
  }

  /** Returns [TokenClientFilterValues] from [CodeableConcept]. */
  fun of(codeableConcept: CodeableConcept) = apply {
    tokenFilters.clear()
    codeableConcept.coding.forEach {
      tokenFilters.add(TokenFilter(uri = it.system, code = it.code))
    }
  }

  /** Returns [TokenClientFilterValues] from [Identifier]. */
  fun of(identifier: Identifier) = apply {
    tokenFilters.clear()
    tokenFilters.add(TokenFilter(uri = identifier.system, code = identifier.value))
  }

  /** Returns [TokenClientFilterValues] from [ContactPoint]. */
  fun of(contactPoint: ContactPoint) = apply {
    tokenFilters.clear()
    tokenFilters.add(TokenFilter(uri = contactPoint.use?.toCode(), code = contactPoint.value))
  }
}

@SearchDslMarker
data class ReferenceFilter(val parameter: ReferenceClientParam?, var value: String? = null) :
  Filter

@SearchDslMarker
data class NumberFilter(
  val parameter: NumberClientParam,
  var prefix: ParamPrefixEnum? = null,
  var value: BigDecimal? = null
) : Filter

@SearchDslMarker
data class TokenFilter(
  var parameter: TokenClientParam? = null,
  var uri: String? = null,
  var code: String
) : Filter

@SearchDslMarker
data class QuantityFilter(
  val parameter: QuantityClientParam,
  var prefix: ParamPrefixEnum? = null,
  var value: BigDecimal? = null,
  var system: String? = null,
  var unit: String? = null
) : Filter

enum class Order {
  ASCENDING,
  DESCENDING
}

enum class StringFilterModifier {
  STARTS_WITH,
  MATCHES_EXACTLY,
  CONTAINS
}

/** Marker interface */
interface Filter

enum class Operation(val value: String) {
  OR("UNION"),
  AND("INTERSECT"),
}
