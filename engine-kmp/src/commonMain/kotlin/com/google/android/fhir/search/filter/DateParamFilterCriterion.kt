/*
 * Copyright 2025-2026 Google LLC
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

import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.DateClientParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.ParamPrefixEnum
import com.google.android.fhir.search.SearchQuery
import com.google.android.fhir.search.getConditionParamPairForDate
import com.google.android.fhir.search.getConditionParamPairForDateTime
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.terminologies.ResourceType

data class DateParamFilterCriterion(
  val parameter: DateClientParam,
  var prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL,
  var value: DateFilterValues? = null,
) : FilterCriterion {

  override fun getConditionalParams(): List<ConditionParam<out Any>> {
    val filterValues = value ?: error("DateFilterValues must not be null")
    return when {
      filterValues.date != null -> listOf(getConditionParamPairForDate(prefix, filterValues.date!!))
      filterValues.dateTime != null ->
        listOf(getConditionParamPairForDateTime(prefix, filterValues.dateTime!!))
      else -> error("Either date or dateTime must be set in DateFilterValues")
    }
  }
}

class DateFilterValues internal constructor() {
  var date: FhirDate? = null
  var dateTime: FhirDateTime? = null

  companion object {
    fun of(date: FhirDate) = DateFilterValues().apply { this.date = date }

    fun of(dateTime: FhirDateTime) = DateFilterValues().apply { this.dateTime = dateTime }
  }
}

/**
 * Splits date filter criteria into separate queries for DateIndexEntity and DateTimeIndexEntity,
 * then combines them with UNION.
 */
internal data class DateClientParamFilterCriteria(
  val parameter: DateClientParam,
  override val filters: List<DateParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "DateIndexEntity") {

  override fun query(type: ResourceType): SearchQuery {
    val dateFilters = filters.filter { it.value?.date != null }
    val dateTimeFilters = filters.filter { it.value?.dateTime != null }

    val queries = mutableListOf<SearchQuery>()

    if (dateFilters.isNotEmpty()) {
      queries.add(
        DateFilterCriteria(parameter, dateFilters, operation).query(type),
      )
    }

    if (dateTimeFilters.isNotEmpty()) {
      queries.add(
        DateTimeFilterCriteria(parameter, dateTimeFilters, operation).query(type),
      )
    }

    if (queries.isEmpty()) {
      return super.query(type)
    }

    if (queries.size == 1) {
      return queries.first()
    }

    val unionOperator =
      if (operation == Operation.OR) "\n UNION \n" else "\n INTERSECT \n"
    return SearchQuery(
      queries.joinToString(unionOperator) { it.query },
      queries.flatMap { it.args },
    )
  }
}

private data class DateFilterCriteria(
  val parameter: DateClientParam,
  override val filters: List<DateParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "DateIndexEntity")

private data class DateTimeFilterCriteria(
  val parameter: DateClientParam,
  override val filters: List<DateParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "DateTimeIndexEntity")
