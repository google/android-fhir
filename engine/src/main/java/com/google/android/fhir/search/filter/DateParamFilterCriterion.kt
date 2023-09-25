/*
 * Copyright 2021-2023 Google LLC
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

package com.google.android.fhir.search.filter

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchDslMarker
import com.google.android.fhir.search.SearchQuery
import com.google.android.fhir.search.getConditionParamPair
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.ResourceType

/**
 * Represents a criterion for filtering [DateClientParam]. e.g. filter(Patient.BIRTHDATE, { value
 * =of(DateType("2013-03-14")) })
 */
@SearchDslMarker
data class DateParamFilterCriterion(
  val parameter: DateClientParam,
  var prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL,
  var value: DateFilterValues? = null,
) : FilterCriterion {
  /** Returns [DateFilterValues] from [DateType]. */
  fun of(date: DateType) = DateFilterValues().apply { this.date = date }

  /** Returns [DateFilterValues] from [DateTimeType]. */
  fun of(dateTime: DateTimeType) = DateFilterValues().apply { this.dateTime = dateTime }

  override fun getConditionalParams(): List<ConditionParam<Long>> {
    checkNotNull(value) { "DateClientParamFilter.value can't be null." }
    return when {
      value!!.date != null -> listOf(getConditionParamPair(prefix, value!!.date!!))
      value!!.dateTime != null -> listOf(getConditionParamPair(prefix, value!!.dateTime!!))
      else -> {
        throw IllegalStateException(
          "DateClientParamFilter.value should have either DateType or DateTimeType.",
        )
      }
    }
  }
}

@SearchDslMarker
class DateFilterValues internal constructor() {
  var date: DateType? = null
  var dateTime: DateTimeType? = null
}

/**
 * It implements its own [query] function as [DateClientParamFilterCriteria] can have both
 * [DateType] and [DateTimeType] criterion in the same filter and both of those values are stored in
 * different entity tables.
 */
internal data class DateClientParamFilterCriteria(
  val parameter: DateClientParam,
  override val filters: List<DateParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "") {

  override fun query(type: ResourceType): SearchQuery {
    val filterCriteria =
      listOf(
        DateFilterCriteria(parameter, filters.filter { it.value!!.date != null }, operation),
        DateTimeFilterCriteria(
          parameter,
          filters.filter { it.value!!.dateTime != null },
          operation,
        ),
      )

    // Join the individual Date and DateTime queries to create a unified DateClientParam query. The
    // user may have provided either a single type or both types of criterion. So filter weeds
    // FilterCriteria with no criterion.
    return filterCriteria
      .filter { it.filters.isNotEmpty() }
      .map { it.query(type) }
      .let { queries ->
        SearchQuery(
          queries.joinToString(separator = " ${operation.logicalOperator} ") {
            if (queries.size > 1) "(${it.query})" else it.query
          },
          queries.flatMap { it.args },
        )
      }
  }

  /** Internal class used to generate query for Date type Criterion */
  private data class DateFilterCriteria(
    val parameter: DateClientParam,
    override val filters: List<DateParamFilterCriterion>,
    override val operation: Operation,
  ) : FilterCriteria(filters, operation, parameter, "DateIndexEntity")

  /** Internal class used to generate query for DateTime type Criterion */
  private data class DateTimeFilterCriteria(
    val parameter: DateClientParam,
    override val filters: List<DateParamFilterCriterion>,
    override val operation: Operation,
  ) : FilterCriteria(filters, operation, parameter, "DateTimeIndexEntity")
}
