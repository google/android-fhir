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
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.UriType

interface Filterable {
  var operation: Operation

  infix fun or(a: Filterable): Filterable {
    operation = Operation.OR
    return a
  }

  infix fun and(a: Filterable): Filterable {
    operation = Operation.OR
    return a
  }
}

enum class Operation(val value: String) {
  OR("Union"),
  AND("Intersect"),
  NONE("")
}

interface IFiltration : Filterable {
  fun filter(stringParameter: StringClientParam, init: StringFilter.() -> Unit): Filterable
  fun filter(referenceParameter: ReferenceClientParam, init: ReferenceFilter.() -> Unit): Filterable
  fun filter(
    dateParameter: DateClientParam,
    date: DateType,
    prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL
  ): Filterable
  fun filter(
    dateParameter: DateClientParam,
    dateTime: DateTimeType,
    prefix: ParamPrefixEnum = ParamPrefixEnum.EQUAL
  ): Filterable
  fun filter(parameter: QuantityClientParam, init: QuantityFilter.() -> Unit): Filterable
  fun filter(filter: TokenClientParam, coding: Coding): Filterable
  fun filter(filter: TokenClientParam, codeableConcept: CodeableConcept): Filterable
  fun filter(filter: TokenClientParam, identifier: Identifier): Filterable
  fun filter(filter: TokenClientParam, contactPoint: ContactPoint): Filterable
  fun filter(filter: TokenClientParam, codeType: CodeType): Filterable
  fun filter(filter: TokenClientParam, boolean: Boolean): Filterable
  fun filter(filter: TokenClientParam, uriType: UriType): Filterable
  fun filter(filter: TokenClientParam, string: String): Filterable
  fun filter(numberParameter: NumberClientParam, init: NumberFilter.() -> Unit): Filterable
}
