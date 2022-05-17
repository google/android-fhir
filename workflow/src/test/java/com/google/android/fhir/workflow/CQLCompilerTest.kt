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
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Library
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CQLCompilerTest {

  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val jSONParser = fhirContext.newJsonParser()
  private val translator = CQLBuilderUtils()

  private fun open(asset: String): InputStream {
    return javaClass.getResourceAsStream(asset)
  }

  fun load(assetName: String): String {
    return open(assetName)?.bufferedReader().use { bufferReader -> bufferReader?.readText() } ?: ""
  }

  @Test
  fun testImmunityCheckCompileCQLToElm() {
    val libraryStr = translator.translate(open("/cql-compiler/ImmunityCheck-1.0.0.cql"))
    assertThat(libraryStr).isEqualTo(load("/cql-compiler/ImmunityCheck-1.0.0.elm.json"))
  }

  @Test
  @Ignore // Missing dependencies on Android Environment.
  fun testFhirHelpersCompileCQLToElm() {
    val libraryStr = translator.translate(open("/cql-compiler/FHIRHelpers-4.0.0.cql"))
    assertThat(libraryStr).isEqualTo(load("/cql-compiler/FHIRHelpers-4.0.0.elm.json"))
  }

  @Test
  fun testImmunityCheckTranslateElmToFhir() {
    val actual =
      translator.build(load("/cql-compiler/ImmunityCheck-1.0.0.elm.json"), "ImmunityCheck", "1.0.0")
    val expected =
      jSONParser.parseResource(open("/cql-compiler/ImmunityCheck-1.0.0.fhir.json")) as Library
    assertThat(jSONParser.encodeResourceToString(actual))
      .isEqualTo(jSONParser.encodeResourceToString(expected))
  }

  @Test
  fun testFhirHelpersTranslateElmToFhir() {
    val actual =
      translator.build(load("/cql-compiler/FHIRHelpers-4.0.0.elm.json"), "FHIRHelpers", "1.0.0")
    val expected =
      jSONParser.parseResource(open("/cql-compiler/FHIRHelpers-4.0.0.fhir.json")) as Library
    assertThat(jSONParser.encodeResourceToString(actual))
      .isEqualTo(jSONParser.encodeResourceToString(expected))
  }

  @Test
  fun testImmunityCheckCompileCQLToFhir() {
    val actual = translator.build(open("/cql-compiler/ImmunityCheck-1.0.0.cql"))
    val expected =
      jSONParser.parseResource(open("/cql-compiler/ImmunityCheck-1.0.0.fhir.json")) as Library
    assertThat(jSONParser.encodeResourceToString(actual))
      .isEqualTo(jSONParser.encodeResourceToString(expected))
  }

  @Test
  fun testImmunityCheckLibBundle() {
    val covidCheck =
      jSONParser.parseResource(load("/cql-compiler/ImmunityCheck-1.0.0.fhir.json")) as Library
    val fhirHelpers =
      jSONParser.parseResource(load("/cql-compiler/FHIRHelpers-4.0.0.fhir.json")) as Library

    val actual =
      Bundle().apply {
        id = "ImmunityCheck-1.0.0-bundle"
        type = Bundle.BundleType.TRANSACTION
        addEntry().resource = covidCheck
        addEntry().resource = fhirHelpers
      }

    val expected =
      jSONParser.parseResource(open("/cql-compiler/ImmunityCheck-1.0.0.final.bundle.json")) as
        Bundle
    assertThat(jSONParser.encodeResourceToString(actual))
      .isEqualTo(jSONParser.encodeResourceToString(expected))
  }
}
