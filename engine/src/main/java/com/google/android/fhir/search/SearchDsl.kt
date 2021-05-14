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

import ca.uhn.fhir.rest.gclient.IParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import java.math.BigDecimal
import org.hl7.fhir.r4.model.ResourceType

@SearchDslMarker
data class Search(val type: ResourceType, var count: Int? = null, var from: Int? = null) {
  internal val stringFilters = mutableListOf<StringFilter>()
  internal val referenceFilter = mutableListOf<ReferenceFilter>()
  internal val numberFilter = mutableListOf<NumberFilter>()
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
    referenceFilter.add(filter)
  }

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
data class ReferenceFilter(val parameter: ReferenceClientParam?, var value: String? = null)

@SearchDslMarker
data class NumberFilter(
  val parameter: NumberClientParam,
  var prefix: ParamPrefixEnum? = null,
  var value: BigDecimal? = null
)

enum class Order {
  ASCENDING,
  DESCENDING
}

enum class StringFilterModifier {
  STARTS_WITH,
  MATCHES_EXACTLY,
  CONTAINS
}
