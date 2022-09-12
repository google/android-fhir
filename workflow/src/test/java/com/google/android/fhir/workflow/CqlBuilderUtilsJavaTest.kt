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
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.workflow.testing.CqlBuilderUtils
import java.io.InputStream
import org.hl7.fhir.instance.model.api.IBaseResource
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.skyscreamer.jsonassert.JSONAssert

@RunWith(RobolectricTestRunner::class)
class CqlBuilderUtilsJavaTest {
  private val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  private fun open(asset: String): InputStream {
    return javaClass.getResourceAsStream(asset)!!
  }

  private fun load(asset: String): String {
    return open(asset).bufferedReader().use { bufferReader -> bufferReader.readText() }
  }

  private fun IBaseResource.toJson(): String {
    return jsonParser.encodeResourceToString(this)
  }

  /**
   * Tests the compilation of CQL expressions into ELM by verifying if the compiled JSONs match.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun shouldCompileImmunityCheckCqlToElm() {
    JSONAssert.assertEquals(
      load("/cql-compiler/ImmunityCheck-1.0.0.elm.json"),
      CqlBuilderUtils.compile(open("/cql-compiler/ImmunityCheck-1.0.0.cql")).toJson(),
      false
    )
  }

  /**
   * Tests the compilation of large CQL expressions into ELM.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun shouldCompileFhirHelpersCqlToElm() {
    JSONAssert.assertEquals(
      load("/cql-compiler/FHIRHelpers-4.0.1.elm.json"),
      CqlBuilderUtils.compile(open("/cql-compiler/FHIRHelpers-4.0.1.cql")).toJson(),
      false
    )
  }

  /**
   * Tests the assembly of a Base64-represented ELM Library inside a FHIR Library.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun shouldCompileAndAssembleImmunityCheck() {
    JSONAssert.assertEquals(
      load("/cql-compiler/ImmunityCheck-1.0.0.fhir.json"),
      CqlBuilderUtils.compileAndBuild(open("/cql-compiler/ImmunityCheck-1.0.0.cql")).toJson(),
      false
    )
  }

  /**
   * Tests the assembly of a Base64-represented ELM Library inside a FHIR Library.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun shouldCompileAndAssembleFhirHelpers() {
    JSONAssert.assertEquals(
      load("/cql-compiler/FHIRHelpers-4.0.1.fhir.json"),
      CqlBuilderUtils.compileAndBuild(open("/cql-compiler/FHIRHelpers-4.0.1.cql")).toJson(),
      false
    )
  }
}
