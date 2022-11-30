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

import ca.uhn.fhir.rest.gclient.TokenClientParam
import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchDslMarker

/**
 * Represents a criterion for filtering [TokenClientParam]. e.g. filter(Patient.GENDER, { value =
 * of(CodeType("male")) })
 */
@SearchDslMarker
data class TokenParamFilterCriterion(var parameter: TokenClientParam) : FilterCriterion {
  var value: TokenFilterValue? = null

  /** Returns [TokenFilterValue] from [Boolean]. */
  fun of(boolean: Boolean) =
    TokenFilterValue().apply {
      tokenFilters.add(TokenParamFilterValueInstance(code = boolean.toString()))
    }

  /** Returns [TokenFilterValue] from [String]. */
  fun of(string: String) =
    TokenFilterValue().apply { tokenFilters.add(TokenParamFilterValueInstance(code = string)) }

  override fun getConditionalParams() =
    value!!.tokenFilters.map {
      ConditionParam(
        "index_value = ? ${ if (it.uri.isNullOrBlank()) "" else "AND IFNULL(index_system,'') = ?" }",
        listOfNotNull(it.code, it.uri)
      )
    }
}

@SearchDslMarker
class TokenFilterValue {
  val tokenFilters = mutableListOf<TokenParamFilterValueInstance>()
}

/**
 * A structure like [CodeableConcept] may contain multiple [Coding] values each of which will be a
 * filter value. We use [TokenParamFilterValueInstance] to represent individual filter value.
 */
@SearchDslMarker
data class TokenParamFilterValueInstance(var uri: String? = null, var code: String)

internal data class TokenParamFilterCriteria(
  var parameter: TokenClientParam,
  override val filters: List<TokenParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "TokenIndexEntity")
