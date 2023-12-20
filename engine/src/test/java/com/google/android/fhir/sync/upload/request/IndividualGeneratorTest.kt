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

import com.google.android.fhir.sync.upload.patch.PatchMapping
import com.google.android.fhir.sync.upload.request.RequestGeneratorTestUtils.deleteLocalChange
import com.google.android.fhir.sync.upload.request.RequestGeneratorTestUtils.insertionLocalChange
import com.google.android.fhir.sync.upload.request.RequestGeneratorTestUtils.toPatch
import com.google.android.fhir.sync.upload.request.RequestGeneratorTestUtils.updateLocalChange
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.codesystems.HttpVerb
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IndividualGeneratorTest {

  @Test
  fun `should return empty list if there are no local changes`() = runTest {
    val generator = UrlRequestGenerator.getDefault()
    val requests = generator.generateUploadRequests(listOf())
    assertThat(requests).isEmpty()
  }

  @Test
  fun `should create a POST request for insert`() = runTest {
    val generator = UrlRequestGenerator.getGenerator(HttpVerb.POST, HttpVerb.PATCH)
    val patchOutput =
      PatchMapping(
        localChanges = listOf(insertionLocalChange),
        generatedPatch = insertionLocalChange.toPatch(),
      )
    val requests =
      generator.generateUploadRequests(
        listOf(patchOutput),
      )

    with(requests.single()) {
      with(generatedRequest) {
        assertThat(httpVerb).isEqualTo(HttpVerb.POST)
        assertThat(url).isEqualTo("Patient")
      }

      assertThat(localChanges).isEqualTo(patchOutput.localChanges)
    }
  }

  @Test
  fun `should create a PUT request for insert`() = runTest {
    val generator = UrlRequestGenerator.getDefault()
    val patchOutput =
      PatchMapping(
        localChanges = listOf(insertionLocalChange),
        generatedPatch = insertionLocalChange.toPatch(),
      )
    val requests =
      generator.generateUploadRequests(
        listOf(patchOutput),
      )

    with(requests.single()) {
      with(generatedRequest) {
        assertThat(httpVerb).isEqualTo(HttpVerb.PUT)
        assertThat(url).isEqualTo("Patient/Patient-001")
      }
      assertThat(localChanges).isEqualTo(patchOutput.localChanges)
    }
  }

  @Test
  fun `should create a PATCH request for update`() = runTest {
    val patchOutput =
      PatchMapping(
        localChanges = listOf(updateLocalChange),
        generatedPatch = updateLocalChange.toPatch(),
      )
    val generator = UrlRequestGenerator.Factory.getDefault()
    val requests = generator.generateUploadRequests(listOf(patchOutput))
    with(requests.single()) {
      with(generatedRequest) {
        assertThat(requests.size).isEqualTo(1)
        assertThat(httpVerb).isEqualTo(HttpVerb.PATCH)
        assertThat(url).isEqualTo("Patient/Patient-001")
        assertThat((resource as Binary).data.toString(Charsets.UTF_8))
          .isEqualTo(
            "[{\"op\":\"replace\",\"path\":\"\\/name\\/0\\/given\\/0\",\"value\":\"Janet\"}]",
          )
      }
      assertThat(localChanges).isEqualTo(patchOutput.localChanges)
    }
  }

  @Test
  fun `should create a DELETE request for delete`() = runTest {
    val patchOutput =
      PatchMapping(
        localChanges = listOf(deleteLocalChange),
        generatedPatch = deleteLocalChange.toPatch(),
      )
    val generator = UrlRequestGenerator.Factory.getDefault()
    val requests = generator.generateUploadRequests(listOf(patchOutput))
    with(requests.single()) {
      with(generatedRequest) {
        assertThat(httpVerb).isEqualTo(HttpVerb.DELETE)
        assertThat(url).isEqualTo("Patient/Patient-001")
      }
      assertThat(localChanges).isEqualTo(patchOutput.localChanges)
    }
  }

  @Test
  fun `should return multiple requests in order`() = runTest {
    val patchOutputList =
      listOf(insertionLocalChange, updateLocalChange, deleteLocalChange).map {
        PatchMapping(listOf(it), it.toPatch())
      }
    val generator = UrlRequestGenerator.Factory.getDefault()
    val result = generator.generateUploadRequests(patchOutputList)
    assertThat(result).hasSize(3)
    assertThat(result.map { it.generatedRequest.httpVerb })
      .containsExactly(HttpVerb.PUT, HttpVerb.PATCH, HttpVerb.DELETE)
      .inOrder()
  }
}
