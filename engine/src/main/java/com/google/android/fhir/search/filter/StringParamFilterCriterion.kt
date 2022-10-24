/*
 * Copyright 2022 Google LLC
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

import ca.uhn.fhir.rest.gclient.StringClientParam
import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchDslMarker
import com.google.android.fhir.search.SearchQuery
import com.google.android.fhir.search.StringFilterModifier
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

/**
 * Represents a criterion for filtering [StringClientParam]. e.g. filter(Patient.FAMILY, { value =
 * "Jones" })
 */
@SearchDslMarker
data class StringParamFilterCriterion(
  val parameter: StringClientParam,
  var modifier: StringFilterModifier = StringFilterModifier.STARTS_WITH,
  var value: String? = null
) : FilterCriterion {

  override fun getConditionalParams() =
    listOf(
      ConditionParam(
        if(modifier == StringFilterModifier.MATCHES_FTS) "'*' || ? || '*'" else "index_value " +
          when (modifier) {
            StringFilterModifier.STARTS_WITH -> "LIKE ? || '%' COLLATE NOCASE"
            StringFilterModifier.MATCHES_EXACTLY -> "= ?"
            StringFilterModifier.MATCHES_FTS -> "'*' || ? || '*'"
            StringFilterModifier.CONTAINS -> "LIKE '%' || ? || '%' COLLATE NOCASE"
          },
        value!!
      )
    )
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
 *
 * For MATCH_FTS [StringFilterModifier], it returns a query doing JOIN with FullTextStringIndexEntity
 * Table
 */
internal data class StringParamFilterCriteria(
  val parameter: StringClientParam,
  override val filters: List<StringParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "StringIndexEntity") {

  override fun query(type: ResourceType): SearchQuery {
    val conditionParams = filters.flatMap { it.getConditionalParams() }

    return if (filters.first().modifier == StringFilterModifier.MATCHES_FTS)
      SearchQuery(
        """
      SELECT resourceUuid FROM StringIndexEntity c JOIN FullTextStringIndexEntity d ON c.id = d.docid
      WHERE resourceType = ? AND d.index_name = ? AND d.${conditionParams.toQueryString(operation, true)} 
      """,
        listOf(type.name, param.paramName) + conditionParams.flatMap { it.params }
      )
    else
      SearchQuery(
        """
      SELECT resourceUuid FROM StringIndexEntity
      WHERE resourceType = ? AND index_name = ? AND ${conditionParams.toQueryString(operation)} 
      """,
        listOf(type.name, param.paramName) + conditionParams.flatMap { it.params }
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
  private fun List<ConditionParam<*>>.toQueryString(operation: Operation, isMatchesFts: Boolean = false): String {

   return if(isMatchesFts) {

      this.joinToString(
        separator = if(operation == Operation.OR) " ${operation.logicalOperator} " else " ",
        prefix = "index_value MATCH "
      ) {
          it.condition
      }
    } else {
      this.joinToString(
        separator = " ${operation.logicalOperator} ",
        prefix = if (size > 1) "(" else "",
        postfix = if (size > 1) ")" else ""
      ) {
        if (it.params.size > 1) {
          "(${it.condition})"
        } else {
          it.condition
        }
      }
    }
  }

}
