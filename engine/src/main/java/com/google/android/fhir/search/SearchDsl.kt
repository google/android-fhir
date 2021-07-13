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
  internal val stringFilters = mutableListOf<StringFilter>()
  internal val dateFilter = mutableListOf<DateFilter>()
  internal val dateTimeFilter = mutableListOf<DateTimeFilter>()
  internal val numberFilter = mutableListOf<NumberFilter>()
  internal val referenceFilters = mutableListOf<ReferenceFilter>()
  internal val tokenFilters = mutableListOf<TokenFilter>()
  internal var sort: IParam? = null
  internal var order: Order? = null

  fun filter(stringParameter: StringClientParam, init: StringFilter.() -> Unit) {
    val filter = StringFilter(stringParameter)
    filter.init()
    stringFilters.add(filter)
  }

  fun filter(referenceParameter: ReferenceClientParam, init: ReferenceFilter.() -> Unit) {
    val filter = ReferenceFilter(referenceParameter)
    filter.init()
    referenceFilters.add(filter)
  }

  fun filter(
    dateParameter: DateClientParam,
    date: DateType,
    prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL
  ) {
    dateFilter.add(DateFilter(dateParameter, prefix, date))
  }

  fun filter(
    dateParameter: DateClientParam,
    dateTime: DateTimeType,
    prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL
  ) {
    dateTimeFilter.add(DateTimeFilter(dateParameter, prefix, dateTime))
  }

  fun filter(filter: TokenClientParam, coding: Coding) =
    tokenFilters.add(TokenFilter(parameter = filter, uri = coding.system, code = coding.code))

  fun filter(filter: TokenClientParam, codeableConcept: CodeableConcept) =
    codeableConcept.coding.forEach {
      tokenFilters.add(TokenFilter(parameter = filter, uri = it.system, code = it.code))
    }

  fun filter(filter: TokenClientParam, identifier: Identifier) =
    tokenFilters.add(
      TokenFilter(parameter = filter, uri = identifier.system, code = identifier.value)
    )

  fun filter(filter: TokenClientParam, contactPoint: ContactPoint) =
    tokenFilters.add(
      TokenFilter(parameter = filter, uri = contactPoint.use?.toCode(), code = contactPoint.value)
    )

  fun filter(filter: TokenClientParam, codeType: CodeType) =
    tokenFilters.add(TokenFilter(parameter = filter, code = codeType.value))

  fun filter(filter: TokenClientParam, boolean: Boolean) =
    tokenFilters.add(TokenFilter(parameter = filter, code = boolean.toString()))

  fun filter(filter: TokenClientParam, uriType: UriType) =
    tokenFilters.add(TokenFilter(parameter = filter, code = uriType.value))

  fun filter(filter: TokenClientParam, string: String) =
    tokenFilters.add(TokenFilter(parameter = filter, code = string))

  fun filter(numberParameter: NumberClientParam, init: NumberFilter.() -> Unit) {
    val filter = NumberFilter(numberParameter)
    filter.init()
    numberFilter.add(filter)
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
)

@SearchDslMarker
data class DateFilter(
  val parameter: DateClientParam,
  var prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL,
  var value: DateType? = null
)

@SearchDslMarker
data class DateTimeFilter(
  val parameter: DateClientParam,
  var prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL,
  var value: DateTimeType? = null
)

@SearchDslMarker
data class ReferenceFilter(val parameter: ReferenceClientParam?, var value: String? = null)

@SearchDslMarker
data class NumberFilter(
  val parameter: NumberClientParam,
  var prefix: ParamPrefixEnum? = null,
  var value: BigDecimal? = null
)

@SearchDslMarker
data class TokenFilter(val parameter: TokenClientParam?, var uri: String? = null, var code: String)

enum class Order {
  ASCENDING,
  DESCENDING
}

enum class StringFilterModifier {
  STARTS_WITH,
  MATCHES_EXACTLY,
  CONTAINS
}
