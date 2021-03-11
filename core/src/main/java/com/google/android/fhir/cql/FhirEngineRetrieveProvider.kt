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

package com.google.android.fhir.cql

import com.google.android.fhir.db.Database
import com.google.android.fhir.resource.getResourceClass
import org.opencds.cqf.cql.retrieve.RetrieveProvider
import org.opencds.cqf.cql.runtime.Code
import org.opencds.cqf.cql.runtime.Interval

/**
 * FHIR Engine's implementation of a [org.opencds.cqf.cql.retrieve.RetrieveProvider] which provides
 * the [org.opencds.cqf.cql.execution.CqlEngine] required FHIR resources to complete CQL evaluation.
 *
 * Note: must be used in conjunction with a [org.opencds.cqf.cql.model.ModelResolver] for HAPI FHIR
 * resources.
 */
internal class FhirEngineRetrieveProvider(private val database: Database) : RetrieveProvider {
  override fun retrieve(
    context: String,
    contextPath: String,
    contextValue: Any,
    dataType: String,
    templateId: String,
    codePath: String,
    codes: Iterable<Code>,
    valueSet: String,
    datePath: String,
    dateLowPath: String,
    dateHighPath: String,
    dateRange: Interval
  ): Iterable<Any> {
    val codeList = codes.toList()
    return when (codeList.size) {
      0 ->
        database.searchByReference(
          clazz = getResourceClass(dataType),
          reference = "$dataType.$contextPath",
          value = if ((contextValue as String).isEmpty()) "" else "$context/$contextValue"
        )
      1 -> {
        val code = codeList[0]
        database.searchByReferenceAndCode(
          clazz = getResourceClass(dataType),
          reference = "$dataType.$contextPath",
          referenceValue =
            if ((contextValue as String).isEmpty()) {
              ""
            } else {
              "$context/$contextValue"
            },
          code = "$dataType.$codePath",
          codeSystem = code.system,
          codeValue = code.code
        )
      }
      else -> emptyList()
    }
  }
}
