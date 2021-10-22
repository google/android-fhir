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

import ca.uhn.fhir.rest.gclient.StringClientParam
import com.google.android.fhir.search.SearchDslMarker
import com.google.android.fhir.search.SearchQuery
import com.google.android.fhir.search.StringFilterModifier
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
  override fun query(type: ResourceType): SearchQuery {
    val condition =
      when (modifier) {
        StringFilterModifier.STARTS_WITH -> "LIKE ? || '%' COLLATE NOCASE"
        StringFilterModifier.MATCHES_EXACTLY -> "= ?"
        StringFilterModifier.CONTAINS -> "LIKE '%' || ? || '%' COLLATE NOCASE"
      }
    return SearchQuery(
      """
      SELECT resourceId FROM StringIndexEntity
      WHERE resourceType = ? AND index_name = ? AND index_value $condition 
      """,
      listOf(type.name, parameter.paramName, value!!)
    )
  }
}
