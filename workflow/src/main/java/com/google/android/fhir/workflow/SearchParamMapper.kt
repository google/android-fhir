/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.workflow

import ca.uhn.fhir.model.api.IQueryParameterType
import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.QuantityClientParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import ca.uhn.fhir.rest.gclient.UriClientParam
import ca.uhn.fhir.rest.param.DateParam
import ca.uhn.fhir.rest.param.NumberParam
import ca.uhn.fhir.rest.param.QuantityParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.param.UriParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.Search
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType

fun Search.applyFilterParam(name: String, param: IQueryParameterType, type: Operation) =
  when (param) {
    is NumberParam -> {
      this.filter(NumberClientParam(name), { value = param.value }, operation = type)
    }
    is DateParam -> {
      this.filter(
        DateClientParam(name),
        { value = of(DateTimeType(param.value)) },
        operation = type,
      )
    }
    is QuantityParam -> {
      this.filter(
        QuantityClientParam(name),
        {
          value = param.value
          system = param.system
          unit = param.units
        },
        operation = type,
      )
    }
    is StringParam -> {
      this.filter(StringClientParam(name), { value = param.value }, operation = type)
    }
    is TokenParam -> {
      this.filter(
        TokenClientParam(name),
        { value = of(Coding(param.system, param.value, null)) },
      )
    }
    is ReferenceParam -> {
      this.filter(ReferenceClientParam(name), { value = param.value })
    }
    is UriParam -> {
      this.filter(UriClientParam(name), { value = param.value })
    }
    else -> {
      throw UnsupportedOperationException("$param type not supported in FhirEngineDal")
    }
  }
