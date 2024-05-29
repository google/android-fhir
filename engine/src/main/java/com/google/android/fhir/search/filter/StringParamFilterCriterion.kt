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

import ca.uhn.fhir.rest.gclient.StringClientParam
import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchDslMarker
import com.google.android.fhir.search.StringFilterModifier

/**
 * Represents a criterion for filtering [StringClientParam]. e.g. filter(Patient.FAMILY, { value =
 * "Jones" })
 */
@SearchDslMarker
data class StringParamFilterCriterion(
  val parameter: StringClientParam,
  var modifier: StringFilterModifier = StringFilterModifier.STARTS_WITH,
  var value: String? = null,
) : FilterCriterion {

  override fun getConditionalParams() =
    listOf(
      ConditionParam(
        "index_value " +
          when (modifier) {
            StringFilterModifier.STARTS_WITH -> "LIKE ? || '%' COLLATE NOCASE"
            StringFilterModifier.MATCHES_EXACTLY -> "= ?"
            StringFilterModifier.CONTAINS -> "LIKE '%' || ? || '%' COLLATE NOCASE"
          },
        value!!,
      ),
    )
}

internal data class StringParamFilterCriteria(
  val parameter: StringClientParam,
  override val filters: List<StringParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "StringIndexEntity")
