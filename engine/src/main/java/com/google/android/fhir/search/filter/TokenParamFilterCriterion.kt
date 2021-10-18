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

import ca.uhn.fhir.rest.gclient.TokenClientParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchDslMarker
import com.google.android.fhir.search.SearchQuery
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.UriType

/**
 * Represents a criterion for filtering [TokenClientParam]. e.g. filter(Patient.GENDER, { value =
 * of(CodeType("male")) })
 */
@SearchDslMarker
data class TokenParamFilterCriterion internal constructor(var parameter: TokenClientParam) :
  FilterCriterion {
  var value: TokenClientFilterValue? = null

  /** Returns [TokenClientFilterValue] from [Boolean]. */
  fun of(boolean: Boolean) =
    TokenClientFilterValue().apply { tokenFilters.add(TokenParamFilter(code = boolean.toString())) }

  /** Returns [TokenClientFilterValue] from [String]. */
  fun of(string: String) =
    TokenClientFilterValue().apply { tokenFilters.add(TokenParamFilter(code = string)) }

  /** Returns [TokenClientFilterValue] from [UriType]. */
  fun of(uriType: UriType) =
    TokenClientFilterValue().apply { tokenFilters.add(TokenParamFilter(code = uriType.value)) }

  /** Returns [TokenClientFilterValue] from [CodeType]. */
  fun of(codeType: CodeType) =
    TokenClientFilterValue().apply { tokenFilters.add(TokenParamFilter(code = codeType.value)) }

  /** Returns [TokenClientFilterValue] from [Coding]. */
  fun of(coding: Coding) =
    TokenClientFilterValue().apply {
      tokenFilters.add(TokenParamFilter(uri = coding.system, code = coding.code))
    }

  /** Returns [TokenClientFilterValue] from [CodeableConcept]. */
  fun of(codeableConcept: CodeableConcept) =
    TokenClientFilterValue().apply {
      codeableConcept.coding.forEach {
        tokenFilters.add(TokenParamFilter(uri = it.system, code = it.code))
      }
    }

  /** Returns [TokenClientFilterValue] from [Identifier]. */
  fun of(identifier: Identifier) =
    TokenClientFilterValue().apply {
      tokenFilters.add(TokenParamFilter(uri = identifier.system, code = identifier.value))
    }

  /** Returns [TokenClientFilterValue] from [ContactPoint]. */
  fun of(contactPoint: ContactPoint) =
    TokenClientFilterValue().apply {
      tokenFilters.add(
        TokenParamFilter(uri = contactPoint.use?.toCode(), code = contactPoint.value)
      )
    }

  override fun query(type: ResourceType): SearchQuery {
    return value!!.tokenFilters.map { it.query(type, parameter) }.let {
      SearchQuery(
        it.joinToString("\n${Operation.OR.resultSetCombiningOperator}\n") { it.query },
        it.flatMap { it.args }
      )
    }
  }
}

@SearchDslMarker
class TokenClientFilterValue internal constructor() {
  internal val tokenFilters = mutableListOf<TokenParamFilter>()
}
/**
 * A structure like [CodeableConcept] may contain multiple [Coding] values each of which will be a
 * filter value. We use [TokenParamFilter] to represent individual filter value.
 */
@SearchDslMarker
internal data class TokenParamFilter(var uri: String? = null, var code: String) {
  fun query(type: ResourceType, parameter: TokenClientParam): SearchQuery {
    return SearchQuery(
      """
      SELECT resourceId FROM TokenIndexEntity
      WHERE resourceType = ? AND index_name = ? AND index_value = ?
      AND IFNULL(index_system,'') = ? 
      """,
      listOfNotNull(type.name, parameter.paramName, code, uri ?: "")
    )
  }
}
