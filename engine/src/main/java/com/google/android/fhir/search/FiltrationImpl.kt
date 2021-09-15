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

package com.google.android.fhir.search

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.QuantityClientParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import java.util.LinkedList
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.UriType

internal class FiltrationImpl : IFiltration {
  override var operation: Operation = Operation.NONE
  private val filters = LinkedList<BaseFilter>()

  fun query(type: ResourceType): SearchQuery {
    val pairs = filters.map { Pair(it.getQuery(type), it.operation) }
    return SearchQuery(
      pairs.joinToString("") { it.first.query + " " + it.second.value },
      pairs.flatMap { it.first.args }
    )
  }

  override fun filter(stringParameter: StringClientParam, init: StringFilter.() -> Unit) =
    StringFilter(stringParameter).apply(init).also { filter(it) }

  override fun filter(referenceParameter: ReferenceClientParam, init: ReferenceFilter.() -> Unit) =
    ReferenceFilter(referenceParameter).apply(init).also { filter(it) }

  override fun filter(dateParameter: DateClientParam, date: DateType, prefix: ParamPrefixEnum) =
    DateFilter(dateParameter, prefix, date).also { filter(it) }

  override fun filter(
    dateParameter: DateClientParam,
    dateTime: DateTimeType,
    prefix: ParamPrefixEnum
  ) = DateTimeFilter(dateParameter, prefix, dateTime).also { filter(it) }

  override fun filter(parameter: QuantityClientParam, init: QuantityFilter.() -> Unit) =
    QuantityFilter(parameter).apply(init).also { filter(it) }

  override fun filter(filter: TokenClientParam, coding: Coding) =
    TokenFilter(parameter = filter, uri = coding.system, code = coding.code).also { filter(it) }

  override fun filter(filter: TokenClientParam, codeableConcept: CodeableConcept): TokenFilter {
    codeableConcept.coding
      .map {
        TokenFilter(parameter = filter, uri = it.system, code = it.code).apply {
          operation = Operation.AND
        }
      }
      .also {
        it.last().operation = Operation.NONE
        filters.addAll(it)
        return it.last()
      }
  }

  override fun filter(filter: TokenClientParam, identifier: Identifier) =
    TokenFilter(parameter = filter, uri = identifier.system, code = identifier.value).also {
      filter(it)
    }

  override fun filter(filter: TokenClientParam, contactPoint: ContactPoint) =
    TokenFilter(parameter = filter, uri = contactPoint.use?.toCode(), code = contactPoint.value)
      .also { filter(it) }

  override fun filter(filter: TokenClientParam, codeType: CodeType) =
    TokenFilter(parameter = filter, code = codeType.value).also { filter(it) }

  override fun filter(filter: TokenClientParam, boolean: Boolean) =
    TokenFilter(parameter = filter, code = boolean.toString()).also { filter(it) }

  override fun filter(filter: TokenClientParam, uriType: UriType) =
    TokenFilter(parameter = filter, code = uriType.value).also { filter(it) }

  override fun filter(filter: TokenClientParam, string: String) =
    TokenFilter(parameter = filter, code = string).also { filter(it) }

  override fun filter(numberParameter: NumberClientParam, init: NumberFilter.() -> Unit) =
    NumberFilter(numberParameter).apply(init).also { filter(it) }

  private fun filter(filter: BaseFilter) {
    if (filters.peekLast()?.operation == Operation.NONE) {
      filters.peekLast()?.operation = Operation.AND
    }
    filters.add(filter)
  }
}
