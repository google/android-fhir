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
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.TokenClientParam

data class TokenParamFilterCriterion(var parameter: TokenClientParam) : FilterCriterion {

  var value: TokenFilterValue? = null

  override fun getConditionalParams(): List<ConditionParam<out Any>> {
    return value!!.tokenFilters.map {
      ConditionParam(
        "index_value = ?${if (it.uri.isNullOrBlank()) "" else " AND IFNULL(index_system,'') = ?"}",
        listOfNotNull(it.code, it.uri),
      )
    }
  }
}

class TokenFilterValue internal constructor() {
  internal val tokenFilters = mutableListOf<TokenParamFilterValueInstance>()

  companion object {
    fun string(code: String) =
      TokenFilterValue().apply { tokenFilters.add(TokenParamFilterValueInstance(code = code)) }

    fun coding(system: String?, code: String) =
      TokenFilterValue().apply {
        tokenFilters.add(TokenParamFilterValueInstance(uri = system, code = code))
      }

    fun boolean(value: Boolean) =
      TokenFilterValue().apply {
        tokenFilters.add(TokenParamFilterValueInstance(code = value.toString()))
      }
  }
}

internal data class TokenParamFilterValueInstance(
  var uri: String? = null,
  var code: String,
)

internal data class TokenParamFilterCriteria(
  var parameter: TokenClientParam,
  override val filters: List<TokenParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "TokenIndexEntity")
