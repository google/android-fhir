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

import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchDslMarker
import com.google.android.fhir.search.SearchQuery
import org.hl7.fhir.r4.model.ResourceType

/**
 * Represents a criterion for filtering [ReferenceClientParam]. e.g. filter(Observation.SUBJECT, {
 * value = "Patient/001" })
 */
@SearchDslMarker
data class ReferenceParamFilterCriterion(
  val parameter: ReferenceClientParam,
  var value: String? = null
) : FilterCriterion

internal data class ReferenceParamFilterCriteria(
  override val filters: List<ReferenceParamFilterCriterion>,
  override val operation: Operation
) : FilterCriteria(filters, operation) {

  override fun query(type: ResourceType): SearchQuery {
    val condition =
      filters.map { "index_value = ?" }.joinToQueryString(
          separator = " ${operation.conditionOperator} ",
          prePost = PrePost.NONE
        ) { it }

    return SearchQuery(
      """
      SELECT resourceId FROM ReferenceIndexEntity
      WHERE resourceType = ? AND index_name = ? AND $condition 
      """,
      listOf(type.name, filters.first().parameter.paramName) + filters.map { it.value!! }
    )
  }
}
