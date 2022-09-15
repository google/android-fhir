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

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.query.XFhirQueryTranslator.applyFilterParam
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Enumerations
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
      if (dataType == null) {
        emptyList()
      } else if (contextPath == "id" && contextValue is String) {
        listOf(fhirEngine.get(ResourceType.fromCode(dataType), contextValue))
      } else {
        val search = Search(ResourceType.fromCode(dataType))

        if (contextPath is String && contextValue != null) {
          // Finds the SearchParamDefinition annotation that matches the incoming contextPath
          val ann = findSearchParamDefinition(dataType, contextPath)

          if (ann != null) {
            // If found, uses the applyFilterParam
            if (ann.type == Enumerations.SearchParamType.REFERENCE) {
              search.applyFilterParam(ann, "$context/$contextValue")
            } else {
              search.applyFilterParam(ann, "$contextValue")
            }
          } else
          // Tries to identify the right param class by type
          when (contextValue) {
              is String -> search.filter(StringClientParam(contextPath), { value = contextValue })
              is DateTimeType ->
                search.filter(DateClientParam(contextPath), { value = of(contextValue) })
              is BigDecimal ->
                search.filter(NumberClientParam(contextPath), { value = contextValue })
              else ->
                throw UnsupportedOperationException(
                  "FhirEngineRetrieveProvider doesn't know " +
                    "how to search for $dataType.$contextPath = $contextValue" +
                    "(${contextValue.javaClass.name}) and get $context"
                )
            }
        }

        fhirEngine.search(search)
      }
    }
  }

  private fun findSearchParamDefinition(dataType: String, path: String) =
    getClass(dataType)
      .fields
      .asSequence()
      .mapNotNull {
        it.getAnnotation(ca.uhn.fhir.model.api.annotation.SearchParamDefinition::class.java)
      }
      .filter { it.path.equals("$dataType.$path") }
      .map {
        com.google.android.fhir.index.SearchParamDefinition(
          it.name,
          Enumerations.SearchParamType.fromCode(it.type),
          it.path
        )
      }
      .firstOrNull()

  private fun getClass(dataType: String): Class<*> {
    return Class.forName("org.hl7.fhir.r4.model.$dataType")
  }
}
