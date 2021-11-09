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

package com.google.android.fhir.search.filter

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
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
  var value: DateFilterValues? = null
) : FilterCriterion {
  /** Returns [DateFilterValues] from [DateType]. */
  fun of(date: DateType) = DateFilterValues().apply { this.date = date }

  /** Returns [DateFilterValues] from [DateTimeType]. */
  fun of(dateTime: DateTimeType) = DateFilterValues().apply { this.dateTime = dateTime }

  override fun query(type: ResourceType): SearchQuery {
    return when {
      value?.date != null -> {
        val conditionParamPair = getConditionParamPair(prefix, value?.date!!)
        SearchQuery(
          """
          SELECT resourceId FROM DateIndexEntity 
          WHERE resourceType = ? AND index_name = ? AND ${conditionParamPair.condition}
          """,
          listOf(type.name, parameter.paramName) + conditionParamPair.params
        )
      }
      value?.dateTime != null -> {
        val conditionParamPair = getConditionParamPair(prefix, value?.dateTime!!)
        SearchQuery(
          """
          SELECT resourceId FROM DateTimeIndexEntity 
          WHERE resourceType = ? AND index_name = ? AND ${conditionParamPair.condition}
          """,
          listOf(type.name, parameter.paramName) + conditionParamPair.params
        )
      }
      else -> throw IllegalArgumentException("DateClientParamFilter.value can't be null.")
    }
  }
}

@SearchDslMarker
class DateFilterValues internal constructor() {
  var date: DateType? = null
  var dateTime: DateTimeType? = null
}
