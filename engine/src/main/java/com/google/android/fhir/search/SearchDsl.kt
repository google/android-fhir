/*
 * Copyright 2021 Google LLC
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
import ca.uhn.fhir.rest.gclient.UriClientParam
import com.google.android.fhir.search.filter.DateClientParamFilterCriteria
import com.google.android.fhir.search.filter.DateParamFilterCriterion
import com.google.android.fhir.search.filter.NumberParamFilterCriteria
import com.google.android.fhir.search.filter.NumberParamFilterCriterion
import com.google.android.fhir.search.filter.QuantityParamFilterCriteria
import com.google.android.fhir.search.filter.QuantityParamFilterCriterion
import com.google.android.fhir.search.filter.ReferenceParamFilterCriteria
import com.google.android.fhir.search.filter.ReferenceParamFilterCriterion
import com.google.android.fhir.search.filter.StringParamFilterCriteria
import com.google.android.fhir.search.filter.StringParamFilterCriterion
import com.google.android.fhir.search.filter.TokenParamFilterCriteria
import com.google.android.fhir.search.filter.TokenParamFilterCriterion
import com.google.android.fhir.search.filter.UriFilterCriteria
import com.google.android.fhir.search.filter.UriParamFilterCriterion
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
  internal val uriFilterCriteria = mutableListOf<UriFilterCriteria>()
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
    stringFilterCriteria.add(StringParamFilterCriteria(stringParameter, filters, operation))
  }

  fun filter(
    referenceParameter: ReferenceClientParam,
    vararg init: ReferenceParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<ReferenceParamFilterCriterion>()
    init.forEach { ReferenceParamFilterCriterion(referenceParameter).apply(it).also(filters::add) }
    referenceFilterCriteria.add(
      ReferenceParamFilterCriteria(referenceParameter, filters, operation)
    )
  }

  fun filter(
    dateParameter: DateClientParam,
    vararg init: DateParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<DateParamFilterCriterion>()
    init.forEach { DateParamFilterCriterion(dateParameter).apply(it).also(filters::add) }
    dateTimeFilterCriteria.add(DateClientParamFilterCriteria(dateParameter, filters, operation))
  }

  fun filter(
    quantityParameter: QuantityClientParam,
    vararg init: QuantityParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<QuantityParamFilterCriterion>()
    init.forEach { QuantityParamFilterCriterion(quantityParameter).apply(it).also(filters::add) }
    quantityFilterCriteria.add(QuantityParamFilterCriteria(quantityParameter, filters, operation))
  }

  fun filter(
    tokenParameter: TokenClientParam,
    vararg init: TokenParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<TokenParamFilterCriterion>()
    init.forEach { TokenParamFilterCriterion(tokenParameter).apply(it).also(filters::add) }
    tokenFilterCriteria.add(TokenParamFilterCriteria(tokenParameter, filters, operation))
  }

  fun filter(
    numberParameter: NumberClientParam,
    vararg init: NumberParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<NumberParamFilterCriterion>()
    init.forEach { NumberParamFilterCriterion(numberParameter).apply(it).also(filters::add) }
    numberFilterCriteria.add(NumberParamFilterCriteria(numberParameter, filters, operation))
  }

  fun filter(
    uriParam: UriClientParam,
    vararg init: UriParamFilterCriterion.() -> Unit,
    operation: Operation = Operation.OR
  ) {
    val filters = mutableListOf<UriParamFilterCriterion>()
    init.forEach { UriParamFilterCriterion(uriParam).apply(it).also(filters::add) }
    uriFilterCriteria.add(UriFilterCriteria(uriParam, filters, operation))
  }

  fun sort(parameter: StringClientParam, order: Order) {
    sort = parameter
    this.order = order
  }

  fun sort(parameter: NumberClientParam, order: Order) {
    sort = parameter
    this.order = order
  }

  fun sort(parameter: DateClientParam, order: Order) {
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
enum class Operation(val logicalOperator: String) {
  OR("OR"),
  AND("AND"),
}
