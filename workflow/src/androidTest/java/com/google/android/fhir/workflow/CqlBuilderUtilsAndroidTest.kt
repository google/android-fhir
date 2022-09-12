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

import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.workflow.testing.CqlBuilderUtils
import java.io.InputStream
import org.hl7.fhir.instance.model.api.IBaseResource
import org.junit.Test
import org.junit.runner.RunWith
import org.skyscreamer.jsonassert.JSONAssert

@RunWith(AndroidJUnit4::class)
class CqlBuilderUtilsAndroidTest {
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

  fun testCompilerAssembler(
    cqlAssetName: String,
    expectedElmAssetName: String,
    expectedFhirAssetName: String
  ) {
    val cqlText = load(cqlAssetName)
    val expectedElm = load(expectedElmAssetName)

    val translator = CqlBuilderUtils.compile(cqlText)

    // Uses JSONAssert because Android and Java export the JSON properties in a different order.
    JSONAssert.assertEquals(expectedElm, translator.toJson(), false)

    println(translator.toXml())

    // Given the ELM is the same, builds the lib with the expented, not the new ELM to make sure
    // the base 64 representation of the Library matches.
    val library =
      CqlBuilderUtils.assembleFhirLib(
        cqlText,
        expectedElm,
        translator.toXml(),
        translator.toELM().identifier.id,
        translator.toELM().identifier.version
      )

    JSONAssert.assertEquals(load(expectedFhirAssetName), library.toJson(), false)
  }

  /**
   * Tests the compilation of CQL expressions into ELM by verifying if the compiled JSONs match.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun shouldCompileAndAssembleImmunityCheck() {
    testCompilerAssembler(
      "/cql-compiler/ImmunityCheck-1.0.0.cql",
      "/cql-compiler/ImmunityCheck-1.0.0.elm.json",
      "/cql-compiler/ImmunityCheck-1.0.0.fhir.json"
    )
  }

  /**
   * Tests the compilation of large CQL expressions into ELM.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun shouldCompileAndAssembleFhirHelpers() {
    testCompilerAssembler(
      "/cql-compiler/FHIRHelpers-4.0.1.cql",
      "/cql-compiler/FHIRHelpers-4.0.1.elm.json",
      "/cql-compiler/FHIRHelpers-4.0.1.fhir.json"
    )
  }
}
