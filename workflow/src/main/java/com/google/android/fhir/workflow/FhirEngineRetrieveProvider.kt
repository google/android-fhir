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

package com.google.android.fhir.workflow

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.get
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.search
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Group
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task
import org.opencds.cqf.cql.engine.retrieve.TerminologyAwareRetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

class FhirEngineRetrieveProvider(private val fhirEngine: FhirEngine) :
  TerminologyAwareRetrieveProvider() {
  val fhirContext = FhirContext.forR4Cached()

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
        listOf(fhirEngine.get(ResourceType.fromCode(dataType), contextValue))
      } else {
        val search = Search(ResourceType.fromCode(dataType))

        if (codePath is String)
          codes?.map { Coding(it.system, it.code, it.display) }?.let {
            // codePath is coded property name hence get the search param
            val codeParam =
              fhirContext.getResourceDefinition(dataType).searchParams.find {
                  it.path.endsWith(codePath)
                }!!
                .name
            search.filter(
              TokenClientParam(codeParam),
              { value = of(CodeableConcept().apply { coding = it }) }
            )
          }

        if (contextPath is String && context is String && contextValue is String)
          when {
            // in case of Task ignore the contextPath which is requester and use subject to filter
            // by task beneficiary
            search.type == ResourceType.Task ->
              search.filter(
                ReferenceClientParam(Task.SP_SUBJECT),
                { value = "$context/$contextValue" }
              )
            // in case of Group contextPath is member.entity which might not be intended filter
            search.type != ResourceType.Group ->
              search.filter(ReferenceClientParam(contextPath), { value = "$context/$contextValue" })
          }

        fhirEngine.search(search)
      }
    }
  }

  private fun hasField(dataType: String?, field: String): Boolean {
    if (dataType == null) return false
    return try {
      Class.forName("org.hl7.fhir.r4.model.$dataType").getDeclaredField(field)
      true
    } catch (e: NoSuchFieldException) {
      false
    }
  }
}
