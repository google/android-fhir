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

import ca.uhn.fhir.rest.gclient.IParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchQuery
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

/** Represents filter for a [IParam] */
internal interface FilterCriterion {

  /** Returns [ConditionalParam]s for the particular [FilterCriterion]. */
  fun getConditionalParams(): List<ConditionParam<out Any>>
}

/**
 * Contains a set of filter criteria sharing the same search parameter. e.g A
 * [StringParamFilterCriteria] may contain a list of [StringParamFilterCriterion] each with
 * different [StringParamFilterCriterion.value] and [StringParamFilterCriterion.modifier] to filter
 * results for a particular [StringClientParam] like [Patient.GIVEN].
 *
 * An api call like filter(Patient.GIVEN,{value = "John"},{value = "Jane"}) will create a
 * [StringParamFilterCriteria] with two [StringParamFilterCriterion] one with
 * [StringParamFilterCriterion.value] as "John" and other as "Jane."
 */
internal sealed class FilterCriteria(
  open val filters: List<FilterCriterion>,
  open val operation: Operation,
  val param: IParam,
  private val entityTableName: String,
) {

  /**
   * Returns a [SearchQuery] for the [FilterCriteria] based on all the [FilterCriterion]. In case a
   * particular FilterCriteria wants to return [SearchQuery] in custom manner, it should override
   * [query] and provide its own implementation. See [DateClientParamFilterCriteria] for reference.
   */
  open fun query(type: ResourceType): SearchQuery {
    val conditionParams = filters.flatMap { it.getConditionalParams() }
    return SearchQuery(
      """
      SELECT resourceUuid FROM $entityTableName
      WHERE resourceType = ? AND index_name = ? AND ${conditionParams.toQueryString(operation)} 
      """,
      listOf(type.name, param.paramName) + conditionParams.flatMap { it.params },
    )
  }

  /**
   * Joins [ConditionParam]s to generate condition string for the SearchQuery.
   *
   * A simple query:
   *
   * SELECT * FROM StringIndexEntity WHERE resourceType = 'Patient' AND index_name = 'name' AND
   * index_value = "X" OR index_value = "Y"
   *
   * behaves like :
   *
   * SELECT * FROM StringIndexEntity WHERE resourceType = 'Patient' AND (index_name = 'name' AND
   * index_value = "X") OR index_value = "Y"
   *
   * instead of the intended:
   *
   * SELECT * FROM StringIndexEntity WHERE resourceType = 'Patient' AND index_name = 'name' AND
   * (index_value = "X" OR index_value = "Y").
   *
   * This function takes care of wrapping the conditions in brackets so that they are evaluated as
   * intended.
   */
  private fun List<ConditionParam<*>>.toQueryString(operation: Operation) =
    this.joinToString(
      separator = " ${operation.logicalOperator} ",
      prefix = if (size > 1) "(" else "",
      postfix = if (size > 1) ")" else "",
    ) {
      if (it.params.size > 1) {
        "(${it.condition})"
      } else {
        it.condition
      }
    }
}
