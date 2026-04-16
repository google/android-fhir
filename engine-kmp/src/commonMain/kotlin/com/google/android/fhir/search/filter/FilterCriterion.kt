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

import com.google.android.fhir.search.ClientParam
import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchQuery
import com.google.fhir.model.r4.terminologies.ResourceType

/** Represents filter for a [ClientParam]. */
internal interface FilterCriterion {

  /** Returns [ConditionParam]s for the particular [FilterCriterion]. */
  fun getConditionalParams(): List<ConditionParam<out Any>>
}

/**
 * Contains a set of filter criteria sharing the same search parameter. e.g A
 * [StringParamFilterCriteria] may contain a list of [StringParamFilterCriterion] each with different
 * [StringParamFilterCriterion.value] and [StringParamFilterCriterion.modifier].
 */
internal sealed class FilterCriteria(
  open val filters: List<FilterCriterion>,
  open val operation: Operation,
  val param: ClientParam,
  private val entityTableName: String,
) {

  /**
   * Returns a [SearchQuery] for the [FilterCriteria] based on all the [FilterCriterion]. Subclasses
   * may override to provide custom query generation — see [DateClientParamFilterCriteria].
   */
  open fun query(type: ResourceType): SearchQuery {
    val conditionParams = filters.flatMap { it.getConditionalParams() }
    return SearchQuery(
      """
      SELECT resourceUuid FROM $entityTableName
      WHERE resourceType = ? AND index_name = ?${if (conditionParams.isNotEmpty()) " AND ${conditionParams.toQueryString(operation)}" else ""}
      """,
      listOf(type.name, param.paramName) + conditionParams.flatMap { it.params },
    )
  }

  /**
   * Joins [ConditionParam]s to generate condition string for the SearchQuery. Uses recursive
   * divide-and-conquer to properly bracket conditions with the operation.
   */
  private fun List<ConditionParam<*>>.toQueryString(operation: Operation): String {
    if (this.size == 1) {
      return first().queryString
    }

    val mid = this.size / 2
    val left = this.subList(0, mid).toQueryString(operation)
    val right = this.subList(mid, this.size).toQueryString(operation)

    return "($left ${operation.logicalOperator} $right)"
  }
}
