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

package com.google.fhirengine.resource

import ca.uhn.fhir.parser.IParser
import java.io.BufferedReader
import org.hl7.fhir.r4.model.Resource
import org.json.JSONObject
import org.junit.Assert.assertEquals

/** Utilities for testing.  */
class TestingUtils constructor(private val iParser: IParser) {

    /** Asserts that the `expected` and the `actual` FHIR resources are equal.  */
    fun assertResourceEquals(expected: Resource?, actual: Resource?) {
        assertEquals(iParser.encodeResourceToString(expected),
                iParser.encodeResourceToString(actual))
    }

    /** Reads a sample data file from the `sampledata` dir and returns a [Resource] stored under `key` */
    fun <R : Resource> readFromFile(key: String, clazz: Class<R>, filename: String): R {
        val inputStream = javaClass.getResourceAsStream(filename)
        val reader = BufferedReader(inputStream!!.reader())
        val content = StringBuilder()
        reader.use {
            var line = it.readLine()
            while (line != null) {
                content.append(line)
                line = it.readLine()
            }
        }
        val sampleData = JSONObject(content.toString())
        val resourceJson = sampleData.getJSONObject(key)
        return iParser.parseResource(clazz, resourceJson.toString()) as R
    }
}
