/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir

import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.HumanName
import com.google.fhir.model.r4.Patient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

/**
 * Adapted from engine module's use of FhirContext.forR4Cached().newJsonParser() for JSON
 * serialization/deserialization. Engine-kmp uses kotlin-fhir's FhirR4Json instead of HAPI's
 * IParser.
 */
class SerializationTest {

  private val parser = FhirR4Json()

  @Test
  fun serializeAndDeserialize_patientFromJson() {
    val json =
      """
      {
        "resourceType": "Patient",
        "id": "test-1",
        "name": [
          {
            "family": "Doe",
            "given": ["John"]
          }
        ]
      }
      """
        .trimIndent()

    val resource = parser.decodeFromString(json)
    assertIs<Patient>(resource)

    assertEquals("test-1", resource.id)
    assertEquals(1, resource.name.size)
    assertEquals("Doe", resource.name.first().family?.value)
    assertEquals("John", resource.name.first().given.first().value)

    // Round-trip
    val serialized = parser.encodeToString(resource)
    val deserialized = parser.decodeFromString(serialized) as Patient
    assertEquals("test-1", deserialized.id)
    assertEquals("Doe", deserialized.name.first().family?.value)
  }

  @Test
  fun serializeAndDeserialize_patientFromConstructor() {
    val patient =
      Patient(
        id = "test-2",
        name =
          listOf(
            HumanName(
              family = com.google.fhir.model.r4.String("Smith"),
              given = listOf(com.google.fhir.model.r4.String("Jane")),
            ),
          ),
      )

    val serialized = parser.encodeToString(patient)

    val deserialized = parser.decodeFromString(serialized) as Patient
    assertEquals("test-2", deserialized.id)
    assertEquals(1, deserialized.name.size)
    assertNotNull(deserialized.name.first().family)
  }
}
