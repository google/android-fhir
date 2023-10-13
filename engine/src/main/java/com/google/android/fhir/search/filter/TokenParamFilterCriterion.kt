/*
 * Copyright 2022-2023 Google LLC
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
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.UriType

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

  /** Returns [TokenFilterValue] from [UriType]. */
  fun of(uriType: UriType) =
    TokenFilterValue().apply {
      tokenFilters.add(TokenParamFilterValueInstance(code = uriType.value))
    }

  /** Returns [TokenFilterValue] from [CodeType]. */
  fun of(codeType: CodeType) =
    TokenFilterValue().apply {
      tokenFilters.add(TokenParamFilterValueInstance(code = codeType.value))
    }

  /** Returns [TokenFilterValue] from [Coding]. */
  fun of(coding: Coding) =
    TokenFilterValue().apply {
      tokenFilters.add(TokenParamFilterValueInstance(uri = coding.system, code = coding.code))
    }

  /** Returns [TokenFilterValue] from [CodeableConcept]. */
  fun of(codeableConcept: CodeableConcept) =
    TokenFilterValue().apply {
      codeableConcept.coding.forEach {
        tokenFilters.add(TokenParamFilterValueInstance(uri = it.system, code = it.code))
      }
    }

  /** Returns [TokenFilterValue] from [Identifier]. */
  fun of(identifier: Identifier) =
    TokenFilterValue().apply {
      tokenFilters.add(
        TokenParamFilterValueInstance(uri = identifier.system, code = identifier.value),
      )
    }

  /** Returns [TokenFilterValue] from [ContactPoint]. */
  fun of(contactPoint: ContactPoint) =
    TokenFilterValue().apply {
      tokenFilters.add(
        TokenParamFilterValueInstance(uri = contactPoint.use?.toCode(), code = contactPoint.value),
      )
    }

  override fun getConditionalParams() =
    value!!.tokenFilters.map {
      ConditionParam(
        "index_value = ? ${ if (it.uri.isNullOrBlank()) "" else "AND IFNULL(index_system,'') = ?" }",
        listOfNotNull(it.code, it.uri),
      )
    }
}

@SearchDslMarker
class TokenFilterValue internal constructor() {
  internal val tokenFilters = mutableListOf<TokenParamFilterValueInstance>()
}

/**
 * A structure like [CodeableConcept] may contain multiple [Coding] values each of which will be a
 * filter value. We use [TokenParamFilterValueInstance] to represent individual filter value.
 */
@SearchDslMarker
internal data class TokenParamFilterValueInstance(var uri: String? = null, var code: String)

internal data class TokenParamFilterCriteria(
  var parameter: TokenClientParam,
  override val filters: List<TokenParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "TokenIndexEntity")
