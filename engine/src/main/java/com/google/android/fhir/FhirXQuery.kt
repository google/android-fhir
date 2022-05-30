/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.QuantityClientParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import ca.uhn.fhir.rest.gclient.UriClientParam
import com.google.android.fhir.index.getSearchParamList
import com.google.android.fhir.search.Search
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource

interface FhirXQuery : FhirEngine {
  suspend fun <R : Resource> searchByString(fhirXQueryModel: FhirXQueryModel): List<R> {
    val searchObject = Search(fhirXQueryModel.type, fhirXQueryModel.count, fhirXQueryModel.from)
    val searchParameter =
      getSearchParamList(fhirXQueryModel.resource).filter { it.name == fhirXQueryModel.search }

    // TODO: find a way to add value to the searchObject along with search criteria
    searchParameter.forEach {
      when (it.type) {
        Enumerations.SearchParamType.NUMBER -> {
          searchObject.filter(NumberClientParam(it.name))
        }
        Enumerations.SearchParamType.DATE -> {
          searchObject.filter(DateClientParam(it.name))
        }
        Enumerations.SearchParamType.QUANTITY -> {
          searchObject.filter(QuantityClientParam(it.name))
        }
        Enumerations.SearchParamType.STRING -> {
          searchObject.filter(StringClientParam(it.name))
        }
        Enumerations.SearchParamType.TOKEN -> {
          searchObject.filter(TokenClientParam(it.name))
        }
        Enumerations.SearchParamType.REFERENCE -> {
          searchObject.filter(ReferenceClientParam(it.name))
        }
        Enumerations.SearchParamType.URI -> {
          searchObject.filter(UriClientParam(it.name))
        }
        else -> {}
      }
    }
    return this.search(searchObject)
  }
}
