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

package com.google.android.fhir.cqlreference.cql

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.db.impl.DatabaseImpl
import java.util.EnumSet
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import org.cqframework.cql.elm.execution.VersionedIdentifier
import org.opencds.cqf.cql.execution.CqlEngine
import org.opencds.cqf.cql.execution.EvaluationResult

class CqlEngineUtils(private val context: Context) {

  class Factory() {
    fun createCqlEngine(context: Context): CqlEngine {
      val databaseName = "fhirEngine"
      val parser = FhirContext.forR4().newJsonParser()
      val databaseImpl =
        DatabaseImpl(context = context, iParser = parser, databaseName = databaseName)
      val dataProvider = FhirEngineDataProvider.Factory.create(databaseImpl)
      return CqlEngine(
        FhirEngineLibraryLoader(databaseImpl),
        mapOf("http://hl7.org/fhir" to dataProvider),
        FhirEngineTerminologyProvider(),
        EnumSet.noneOf(CqlEngine.Options::class.java)
      )
    }
  }

  /** Returns the result of a CQL evaluation provided with the ID of the library. */
  fun evaluateCql(
    libraryVersionId: String,
    contextString: String,
    expression: String
  ): EvaluationResult? {
    val contextMap: MutableMap<String, Any> = HashMap()
    val contextSplit = contextString.split("/").toTypedArray()
    contextMap[contextSplit[0]] = contextSplit[1]
    val versionedIdentifier = VersionedIdentifier().withId(libraryVersionId)
    val expressions: MutableSet<String> = HashSet()
    expressions.add(expression)
    val map: MutableMap<VersionedIdentifier, Set<String>> = HashMap()
    map[versionedIdentifier] = expressions
    return if (!map.get("system").isNullOrEmpty())
      Factory().createCqlEngine(context).evaluate(contextMap, null, map)
    else null
  }
}
