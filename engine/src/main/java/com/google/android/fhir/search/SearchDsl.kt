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
import java.util.LinkedList
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
data class Search(val type: ResourceType, var count: Int? = null, var from: Int? = null) :
  IFiltration {
  internal var sort: IParam? = null
  internal var order: Order? = null
  @PublishedApi internal var nestedSearches = mutableListOf<NestedSearch>()
  private val filters = LinkedList<FiltrationImpl>()
  override var operation: Operation = Operation.NONE

  fun filter(init: IFiltration.() -> Unit): IFiltration {
    val filter = FiltrationImpl()
    filter.init()
    if (filters.peekLast()?.operation == Operation.NONE) {
      filters.peekLast()?.operation = Operation.AND
    }
    filters.add(filter)
    return filter
  }

  internal fun filterQuery(): List<Pair<SearchQuery, String>> {
    return filters.map { Pair(it.query(type), it.operation.name) }
  }

  override fun filter(stringParameter: StringClientParam, init: StringFilter.() -> Unit) = filter {
    filter(stringParameter, init)
  }

  override fun filter(referenceParameter: ReferenceClientParam, init: ReferenceFilter.() -> Unit) =
      filter {
    filter(referenceParameter, init)
  }

  override fun filter(dateParameter: DateClientParam, date: DateType, prefix: ParamPrefixEnum) =
      filter {
    filter(dateParameter, date, prefix)
  }

  override fun filter(
    dateParameter: DateClientParam,
    dateTime: DateTimeType,
    prefix: ParamPrefixEnum
  ) = filter { filter(dateParameter, dateTime, prefix) }

  override fun filter(parameter: QuantityClientParam, init: QuantityFilter.() -> Unit) = filter {
    filter(parameter, init)
  }

  override fun filter(filter: TokenClientParam, coding: Coding) = filter { filter(filter, coding) }

  override fun filter(filter: TokenClientParam, codeableConcept: CodeableConcept) = filter {
    filter(filter, codeableConcept)
  }

  override fun filter(filter: TokenClientParam, identifier: Identifier) = filter {
    filter(filter, identifier)
  }

  override fun filter(filter: TokenClientParam, contactPoint: ContactPoint) = filter {
    filter(filter, contactPoint)
  }

  override fun filter(filter: TokenClientParam, codeType: CodeType) = filter {
    filter(filter, codeType)
  }

  override fun filter(filter: TokenClientParam, boolean: Boolean) = filter {
    filter(filter, boolean)
  }

  override fun filter(filter: TokenClientParam, uriType: UriType) = filter {
    filter(filter, uriType)
  }

  override fun filter(filter: TokenClientParam, string: String) = filter { filter(filter, string) }

  override fun filter(
    numberParameter: NumberClientParam,
    init: NumberFilter.() -> Unit
  ): Filterable {
    return FiltrationImpl().apply { filter(numberParameter, init) }.also { filters.add(it) }
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
) : BaseFilter()

@SearchDslMarker
data class DateFilter(
  val parameter: DateClientParam,
  var prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL,
  var value: DateType? = null
) : BaseFilter()

@SearchDslMarker
data class DateTimeFilter(
  val parameter: DateClientParam,
  var prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL,
  var value: DateTimeType? = null
) : BaseFilter()

@SearchDslMarker
data class ReferenceFilter(val parameter: ReferenceClientParam, var value: String? = null) :
  BaseFilter()

@SearchDslMarker
data class NumberFilter(
  val parameter: NumberClientParam,
  var prefix: ParamPrefixEnum? = null,
  var value: BigDecimal? = null
) : BaseFilter()

@SearchDslMarker
data class TokenFilter(val parameter: TokenClientParam, var uri: String? = null, var code: String) :
  BaseFilter()

@SearchDslMarker
data class QuantityFilter(
  val parameter: QuantityClientParam,
  var prefix: ParamPrefixEnum? = null,
  var value: BigDecimal? = null,
  var system: String? = null,
  var unit: String? = null
) : BaseFilter()

enum class Order {
  ASCENDING,
  DESCENDING
}

enum class StringFilterModifier {
  STARTS_WITH,
  MATCHES_EXACTLY,
  CONTAINS
}
