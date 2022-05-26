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

package com.google.android.fhir.workflow

import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Search
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.opencds.cqf.cql.engine.retrieve.TerminologyAwareRetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

class FhirEngineRetrieveProvider(private val fhirEngine: FhirEngine) :
  TerminologyAwareRetrieveProvider() {
  override fun retrieve(
    context: String?,
    contextPath: String?,
    contextValue: Any?,
    dataType: String?,
    templateId: String?,
    codePath: String?,
    codes: MutableIterable<Code>?,
    valueSet: String?,
    datePath: String?,
    dateLowPath: String?,
    dateHighPath: String?,
    dateRange: Interval?
  ): Iterable<Any> {
    return runBlocking {
      if (contextPath == "id" && contextValue is String) {
        mutableListOf(fhirEngine.get(ResourceType.fromCode(dataType), contextValue))
      } else if (contextPath is String && context is String && contextValue is String) {
        val search = Search(ResourceType.fromCode(dataType))
        search.filter(ReferenceClientParam(contextPath), { value = "$context/$contextValue" })
        fhirEngine.search<Resource>(search).toMutableList()
      } else {
        val search = Search(ResourceType.fromCode(dataType))
        if (hasField(dataType, "active")) {
          // TODO: I am not sure why the default search is only for active entities
          search.filter(TokenClientParam("active"), { value = of(true) })
        }
        fhirEngine.search<Resource>(search).toMutableList()
      }
    }
  }

  fun hasField(dataType: String?, field: String): Boolean {
    if (dataType == null) return false
    return try {
      Class.forName("org.hl7.fhir.r4.model.$dataType").getDeclaredField(field)
      true
    } catch (e: NoSuchFieldException) {
      false
    }
  }
}
