/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.workflow.demo.helper

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.workflow.testing.CqlBuilder
import java.io.InputStream
import kotlin.reflect.KSuspendFunction1
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

class ResourceLoader(
  private val fhirContext: FhirContext = FhirContext.forR4(),
  private val androidContext: Context,
) {
  private val jsonParser = fhirContext.newJsonParser()

  suspend fun loadFile(
    path: String,
    importFunction: KSuspendFunction1<Resource, Unit>,
  ) {
    val resource =
      if (path.endsWith(".json")) {
        jsonParser.parseResource(open(path)) as Resource
      } else if (path.endsWith(".cql")) {
        toFhirLibrary(open(path))
      } else {
        throw IllegalArgumentException("Only json and cql files are supported")
      }
    loadResource(resource, importFunction)
  }

  private suspend fun loadResource(
    resource: Resource,
    importFunction: KSuspendFunction1<Resource, Unit>,
  ) {
    when (resource.resourceType) {
      ResourceType.Bundle -> loadBundle(resource as Bundle, importFunction)
      else -> importFunction(resource)
    }
  }

  private suspend fun loadBundle(
    bundle: Bundle,
    importFunction: KSuspendFunction1<Resource, Unit>,
  ) {
    for (entry in bundle.entry) {
      val resource = entry.resource
      loadResource(resource, importFunction)
    }
  }

  private fun toFhirLibrary(cql: InputStream): Library {
    return CqlBuilder.compileAndBuild(cql)
  }

  private fun open(path: String) = androidContext.assets.open(path)
}
