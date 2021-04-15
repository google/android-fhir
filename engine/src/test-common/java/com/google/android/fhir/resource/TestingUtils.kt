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

package com.google.android.fhir.resource

import ca.uhn.fhir.parser.IParser
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.Resource
import org.json.JSONArray
import org.json.JSONObject

/** Utilities for testing. */
class TestingUtils constructor(private val iParser: IParser) {

  /** Asserts that the `expected` and the `actual` FHIR resources are equal. */
  fun assertResourceEquals(expected: Resource?, actual: Resource?) {
    Truth.assertThat(iParser.encodeResourceToString(actual))
      .isEqualTo(iParser.encodeResourceToString(expected))
  }

  fun assertJsonArrayEqualsIgnoringOrder(actual: JSONArray, expected: JSONArray) {
    Truth.assertThat(actual.length()).isEqualTo(expected.length())
    val actuals = mutableListOf<String>()
    val expecteds = mutableListOf<String>()
    for (i in 0 until actual.length()) {
      actuals.add(actual.get(i).toString())
      expecteds.add(expected.get(i).toString())
    }
    actuals.sorted()
    expecteds.sorted()
    Truth.assertThat(actuals).containsExactlyElementsIn(expecteds)
  }

  /** Reads a [Resource] from given file in the `sampledata` dir */
  fun <R : Resource> readFromFile(clazz: Class<R>, filename: String): R {
    val resourceJson = readJsonFromFile(filename)
    return iParser.parseResource(clazz, resourceJson.toString()) as R
  }

  /** Reads a [JSONObject] from given file in the `sampledata` dir */
  fun readJsonFromFile(filename: String): JSONObject {
    val inputStream = javaClass.getResourceAsStream(filename)
    val content = inputStream!!.bufferedReader(Charsets.UTF_8).readText()
    return JSONObject(content)
  }

  /** Reads a [JSONArray] from given file in the `sampledata` dir */
  fun readJsonArrayFromFile(filename: String): JSONArray {
    val inputStream = javaClass.getResourceAsStream(filename)
    val content = inputStream!!.bufferedReader(Charsets.UTF_8).readText()
    return JSONArray(content)
  }
}
