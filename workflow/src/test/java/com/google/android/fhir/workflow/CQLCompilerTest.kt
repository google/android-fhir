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
class CQLCompilerTest {

  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val jSONParser = fhirContext.newJsonParser()

  private fun open(asset: String): InputStream {
    return javaClass.getResourceAsStream(asset)!!
  }

  private fun load(assetName: String): String {
    return open(assetName).bufferedReader().use { bufferReader -> bufferReader.readText() }
  }

  private fun parse(jsonAssetName: String): IBaseResource {
    return jSONParser.parseResource(open(jsonAssetName))
  }

  private fun jsonOf(resource: IBaseResource): String {
    return jSONParser.encodeResourceToString(resource)
  }

  /**
   * Tests the compilation of CQL expressions into ELM by verifying if the compiled JSONs match.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun testImmunityCheckCompileCQLToElm() {
    val library = CQLBuilderUtils.compile(open("/cql-compiler/ImmunityCheck-1.0.0.cql"))

    val actual = library.toJxson()
    val expected = load("/cql-compiler/ImmunityCheck-1.0.0.elm.json")
    assertThat(actual).isEqualTo(expected)
  }

  /**
   * Tests the compilation of large CQL expressions into ELM.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun testFhirHelpersCompileCQLToElm() {
    val library = CQLBuilderUtils.compile(open("/cql-compiler/FHIRHelpers-4.0.1.cql"))

    val actual = library.toJxson()
    val expected = load("/cql-compiler/FHIRHelpers-4.0.1.elm.json")
    assertThat(actual).isEqualTo(expected)
  }

  /**
   * Tests the assembly of a Base64-represented JSON-formatted ELM Library inside a FHIR Library.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun testImmunityCheckTranslateElmToFhir() {
    val actual = CQLBuilderUtils.build(open("/cql-compiler/ImmunityCheck-1.0.0.elm.json"))
    val expected = parse("/cql-compiler/ImmunityCheck-1.0.0.fhir.json")
    assertThat(jsonOf(actual)).isEqualTo(jsonOf(expected))
  }

  /**
   * Tests the assembly of a Base64-represented JSON-formatted ELM Library inside a FHIR Library.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun testFhirHelpersTranslateElmToFhir() {
    val actual = CQLBuilderUtils.build(open("/cql-compiler/FHIRHelpers-4.0.1.elm.json"))
    val expected = parse("/cql-compiler/FHIRHelpers-4.0.1.fhir.json")
    assertThat(jsonOf(actual)).isEqualTo(jsonOf(expected))
  }

  /**
   * Tests the compilation of a CQL library and assembly into a FHIR Library
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun testImmunityCheckCompileCQLToFhir() {
    val actual = CQLBuilderUtils.compileAndBuild(open("/cql-compiler/ImmunityCheck-1.0.0.cql"))
    val expected = parse("/cql-compiler/ImmunityCheck-1.0.0.fhir.json")
    assertThat(jsonOf(actual)).isEqualTo(jsonOf(expected))
  }

  /**
   * Tests the bundling of multiple ELM Libraries into a single FHIR Library Bundle
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun testImmunityCheckLibBundle() {
    val immunityCheck = parse("/cql-compiler/ImmunityCheck-1.0.0.fhir.json") as Library
    val fhirHelpers = parse("/cql-compiler/FHIRHelpers-4.0.1.fhir.json") as Library

    val actual =
      Bundle().apply {
        id = "ImmunityCheck-1.0.0-bundle"
        type = Bundle.BundleType.TRANSACTION
        addEntry().resource = immunityCheck
        addEntry().resource = fhirHelpers
      }

    val expected = parse("/cql-compiler/ImmunityCheck-1.0.0.final.bundle.json")
    assertThat(jsonOf(actual)).isEqualTo(jsonOf(expected))
  }
}
