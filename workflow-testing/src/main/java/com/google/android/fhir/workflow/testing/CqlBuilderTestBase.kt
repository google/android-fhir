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

package com.google.android.fhir.workflow.testing

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.io.InputStream
import org.hl7.fhir.instance.model.api.IBaseResource
import org.skyscreamer.jsonassert.JSONAssert

open class CqlBuilderTestBase {
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

  fun test(
    cqlAssetName: String,
    expectedElmJsonAssetName: String,
    expectedElmXmlAssetName: String,
    expectedFhirAssetName: String
  ) {
    val cqlText = load(cqlAssetName)
    val expectedElmJson = load(expectedElmJsonAssetName)
    val expectedElmXml = load(expectedElmXmlAssetName)

    val translator = CqlBuilderUtils.compile(cqlText)

    // JSONAssert ignores property order and whitespace/tabs
    JSONAssert.assertEquals(expectedElmJson, translator.toJson(), false)

    println(translator.toXml())

    // XmlAssert ignores property order and whitespace/tabs
    XMLAssert.assertEquals(expectedElmXml, translator.toXml())

    // Given the ELM is the same, builds the lib with the expented, not the new ELM to make sure
    // the base 64 representation of the Library matches.
    val library =
      CqlBuilderUtils.assembleFhirLib(
        cqlText,
        expectedElmJson,
        expectedElmXml,
        translator.toELM().identifier.id,
        translator.toELM().identifier.version
      )

    JSONAssert.assertEquals(load(expectedFhirAssetName), library.toJson(), false)
  }
}
