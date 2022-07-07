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
import com.google.common.truth.Truth.assertThat
import java.io.InputStream
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Library
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CqlBuilderUtilsTest {
  private val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  private fun open(asset: String): InputStream {
    return javaClass.getResourceAsStream(asset)!!
  }

  private fun load(asset: String): String {
    return open(asset).bufferedReader().use { bufferReader -> bufferReader.readText() }
  }

  private fun loadJsonAsset(asset: String): IBaseResource {
    return jsonParser.parseResource(open(asset))
  }

  fun IBaseResource.toJson(): String {
    return jsonParser.encodeResourceToString(this)
  }

  /**
   * Tests the compilation of CQL expressions into ELM by verifying if the compiled JSONs match.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun `should compile immunity check cql to elm`() {
    val library = CqlBuilderUtils.compile(open("/cql-compiler/ImmunityCheck-1.0.0.cql"))
    assertThat(library.toJxson()).isEqualTo(load("/cql-compiler/ImmunityCheck-1.0.0.elm.json"))
  }

  /**
   * Tests the compilation of large CQL expressions into ELM.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun `should compile fhir helpers cql to elm`() {
    val library = CqlBuilderUtils.compile(open("/cql-compiler/FHIRHelpers-4.0.1.cql"))
    assertThat(library.toJxson()).isEqualTo(load("/cql-compiler/FHIRHelpers-4.0.1.elm.json"))
  }

  /**
   * Tests the assembly of a Base64-represented JSON-formatted ELM Library inside a FHIR Library.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun `should assemble immunity check from elm to fhir`() {
    assertThat(CqlBuilderUtils.build(open("/cql-compiler/ImmunityCheck-1.0.0.elm.json")).toJson())
      .isEqualTo(loadJsonAsset("/cql-compiler/ImmunityCheck-1.0.0.fhir.json").toJson())
  }

  /**
   * Tests the assembly of a Base64-represented JSON-formatted ELM Library inside a FHIR Library.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun `should assemble fhir helpers elm to fhir`() {
    assertThat(CqlBuilderUtils.build(open("/cql-compiler/FHIRHelpers-4.0.1.elm.json")).toJson())
      .isEqualTo(loadJsonAsset("/cql-compiler/FHIRHelpers-4.0.1.fhir.json").toJson())
  }

  /**
   * Tests the compilation of a CQL library and assembly into a FHIR Library
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun `should compile and assemble immunity check cql to fhir`() {
    assertThat(
        CqlBuilderUtils.compileAndBuild(open("/cql-compiler/ImmunityCheck-1.0.0.cql")).toJson()
      )
      .isEqualTo(loadJsonAsset("/cql-compiler/ImmunityCheck-1.0.0.fhir.json").toJson())
  }

  /**
   * Tests the bundling of multiple ELM Libraries into a single FHIR Library Bundle
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun `should assemble immunity check bundle`() {
    val immunityCheck = loadJsonAsset("/cql-compiler/ImmunityCheck-1.0.0.fhir.json") as Library
    val fhirHelpers = loadJsonAsset("/cql-compiler/FHIRHelpers-4.0.1.fhir.json") as Library

    val actual =
      Bundle().apply {
        id = "ImmunityCheck-1.0.0-bundle"
        type = Bundle.BundleType.TRANSACTION
        addEntry().resource = immunityCheck
        addEntry().resource = fhirHelpers
      }

    val expected = loadJsonAsset("/cql-compiler/ImmunityCheck-1.0.0.final.bundle.json")
    assertThat(actual.toJson()).isEqualTo(expected.toJson())
  }
}
