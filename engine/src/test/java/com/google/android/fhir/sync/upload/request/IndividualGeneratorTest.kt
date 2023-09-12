/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.sync.upload.request

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.sync.upload.patch.Patch
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.codesystems.HttpVerb
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IndividualGeneratorTest {

  private val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  @Test
  fun `should return empty list if there are no local changes`() = runTest {
    val generator = IndividualRequestGenerator.getDefault()
    val requests = generator.generateUploadRequests(listOf())
    assertThat(requests).isEmpty()
  }

  @Test
  fun `should create a POST request for insert`() = runTest {
    val generator = IndividualRequestGenerator.getGenerator(HttpVerb.POST, HttpVerb.PATCH)
    val requests =
      generator.generateUploadRequests(
        listOf(
          Patch(
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = Patch.Type.INSERT,
            payload =
              jsonParser.encodeResourceToString(
                Patient().apply {
                  id = "Patient-001"
                  addName(
                    HumanName().apply {
                      addGiven("John")
                      family = "Doe"
                    }
                  )
                }
              ),
            timestamp = Instant.now()
          ),
        )
      )

    with(requests.single()) {
      assertThat(httpVerb).isEqualTo(HttpVerb.POST)
      assertThat(url).isEqualTo("Patient")
    }
  }

  @Test
  fun `should create a PUT request for insert`() = runTest {
    val generator = IndividualRequestGenerator.getDefault()
    val requests =
      generator.generateUploadRequests(
        listOf(
          Patch(
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = Patch.Type.INSERT,
            payload =
              jsonParser.encodeResourceToString(
                Patient().apply {
                  id = "Patient-001"
                  addName(
                    HumanName().apply {
                      addGiven("John")
                      family = "Doe"
                    }
                  )
                }
              ),
            timestamp = Instant.now()
          ),
        )
      )

    with(requests.single()) {
      assertThat(httpVerb).isEqualTo(HttpVerb.PUT)
      assertThat(url).isEqualTo("Patient/Patient-001")
    }
  }

  @Test
  fun `should create a PATCH request for update`() = runTest {
    val patches =
      listOf(
        Patch(
          resourceType = ResourceType.Patient.name,
          resourceId = "Patient-002",
          type = Patch.Type.UPDATE,
          payload =
            "[{\"op\":\"replace\",\"path\":\"\\/name\\/0\\/given\\/0\",\"value\":\"Janet\"}]",
          timestamp = Instant.now()
        ),
      )
    val generator = IndividualRequestGenerator.Factory.getDefault()
    val requests = generator.generateUploadRequests(patches)
    with(requests.single()) {
      assertThat(requests.size).isEqualTo(1)
      assertThat(httpVerb).isEqualTo(HttpVerb.PATCH)
      assertThat(url).isEqualTo("Patient/Patient-002")
      assertThat((resource as Binary).data.toString(Charsets.UTF_8))
        .isEqualTo(
          "[{\"op\":\"replace\",\"path\":\"\\/name\\/0\\/given\\/0\",\"value\":\"Janet\"}]"
        )
    }
  }

  @Test
  fun `should create a DELETE request for delete`() = runTest {
    val patches =
      listOf(
        Patch(
          resourceType = ResourceType.Patient.name,
          resourceId = "Patient-001",
          type = Patch.Type.DELETE,
          payload =
            jsonParser.encodeResourceToString(
              Patient().apply {
                id = "Patient-001"
                addName(
                  HumanName().apply {
                    addGiven("John")
                    family = "Doe"
                  }
                )
              }
            ),
          timestamp = Instant.now()
        ),
      )
    val generator = IndividualRequestGenerator.Factory.getDefault()
    val requests = generator.generateUploadRequests(patches)
    with(requests.single()) {
      assertThat(httpVerb).isEqualTo(HttpVerb.DELETE)
      assertThat(url).isEqualTo("Patient/Patient-001")
    }
  }

  @Test
  fun `should return multiple requests in order`() = runTest {
    val patches =
      listOf(
        Patch(
          resourceType = ResourceType.Patient.name,
          resourceId = "Patient-001",
          type = Patch.Type.INSERT,
          payload =
            jsonParser.encodeResourceToString(
              Patient().apply {
                id = "Patient-001"
                addName(
                  HumanName().apply {
                    addGiven("John")
                    family = "Doe"
                  }
                )
              }
            ),
          timestamp = Instant.now()
        ),
        Patch(
          resourceType = ResourceType.Patient.name,
          resourceId = "Patient-002",
          type = Patch.Type.UPDATE,
          payload =
            "[{\"op\":\"replace\",\"path\":\"\\/name\\/0\\/given\\/0\",\"value\":\"Janet\"}]",
          timestamp = Instant.now()
        ),
        Patch(
          resourceType = ResourceType.Patient.name,
          resourceId = "Patient-003",
          type = Patch.Type.DELETE,
          payload =
            jsonParser.encodeResourceToString(
              Patient().apply {
                id = "Patient-003"
                addName(
                  HumanName().apply {
                    addGiven("John")
                    family = "Roe"
                  }
                )
              }
            ),
          timestamp = Instant.now()
        ),
      )
    val generator = IndividualRequestGenerator.Factory.getDefault()
    val result = generator.generateUploadRequests(patches)
    assertThat(result).hasSize(3)
    assertThat(result.map { it.httpVerb })
      .containsExactly(HttpVerb.PUT, HttpVerb.PATCH, HttpVerb.DELETE)
      .inOrder()
  }
}
