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
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.sync.upload.patch.PatchMapping
import com.google.android.fhir.sync.upload.request.RequestGeneratorTestUtils.deleteLocalChange
import com.google.android.fhir.sync.upload.request.RequestGeneratorTestUtils.insertionLocalChange
import com.google.android.fhir.sync.upload.request.RequestGeneratorTestUtils.toPatch
import com.google.android.fhir.sync.upload.request.RequestGeneratorTestUtils.updateLocalChange
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TransactionBundleGeneratorTest {

  @Test
  fun `generateUploadRequests() should return empty list if there are no local changes`() =
    runBlocking {
      val generator = TransactionBundleGenerator.Factory.getDefault()
      val result = generator.generateUploadRequests(listOf())
      assertThat(result).isEmpty()
    }

  @Test
  fun `generateUploadRequests() should return single Transaction Bundle with 3 entries`() =
    runBlocking {
      val patches =
        listOf(insertionLocalChange, updateLocalChange, deleteLocalChange).map {
          PatchMapping(listOf(it), it.toPatch())
        }
      val generator = TransactionBundleGenerator.Factory.getDefault()
      val result = generator.generateUploadRequests(patches)

      assertThat(result).hasSize(1)
      with(result[0].generatedRequest.resource) {
        assertThat(type).isEqualTo(Bundle.BundleType.TRANSACTION)
        assertThat(entry).hasSize(3)
        assertThat(entry.map { it.request.method })
          .containsExactly(Bundle.HTTPVerb.PUT, Bundle.HTTPVerb.PATCH, Bundle.HTTPVerb.DELETE)
          .inOrder()
      }

      assertThat(result[0].splitLocalChanges).hasSize(3)
      assertThat(result[0].splitLocalChanges.all { it.size == 1 }).isTrue()
    }

  @Test
  fun `generateUploadRequests() should return 3 Transaction Bundle with single entry each`() =
    runBlocking {
      val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
      val patches =
        listOf(insertionLocalChange, updateLocalChange, deleteLocalChange).map {
          PatchMapping(listOf(it), it.toPatch())
        }
      val generator =
        TransactionBundleGenerator.Factory.getGenerator(
          Bundle.HTTPVerb.PUT,
          Bundle.HTTPVerb.PATCH,
          1,
          true,
        )
      with(generator.generateUploadRequests(patches)) {
        // Exactly 3 Requests are generated
        assertThat(this).hasSize(3)
        // Each Request is of type Bundle
        assertThat(all { it.generatedRequest.resource.type == Bundle.BundleType.TRANSACTION })
          .isTrue()
        // Each Bundle has exactly 1 entry
        assertThat(all { it.generatedRequest.resource.entry.size == 1 }).isTrue()
        assertThat(map { it.generatedRequest.resource.entry.first().request.method })
          .containsExactly(Bundle.HTTPVerb.PUT, Bundle.HTTPVerb.PATCH, Bundle.HTTPVerb.DELETE)
          .inOrder()
        assertThat(map { it.generatedRequest.resource.entry.first().request.ifMatch })
          .containsExactly(null, "W/\"v-p002-01\"", "W/\"v-p003-01\"")
          .inOrder()
        // Each Bundle request is mapped to 1 LocalChange
        assertThat(all { it.splitLocalChanges.size == 1 }).isTrue()
        assertThat(all { it.splitLocalChanges[0].size == 1 }).isTrue()
      }
    }

  @Test
  fun `generate() should return Bundle Entry without if-match when useETagForUpload is false`() =
    runBlocking {
      val localChange =
        LocalChange(
          resourceType = ResourceType.Patient.name,
          resourceId = "Patient-002",
          type = LocalChange.Type.UPDATE,
          payload = "[]",
          versionId = "patient-002-version-1",
          timestamp = Instant.now(),
          token = LocalChangeToken(listOf(1L)),
        )
      val patches =
        listOf(
          PatchMapping(
            localChanges = listOf(localChange),
            generatedPatch = localChange.toPatch(),
          ),
        )
      val generator = TransactionBundleGenerator.Factory.getDefault(useETagForUpload = false)
      val result = generator.generateUploadRequests(patches)

      assertThat(result.first().generatedRequest.resource.entry.first().request.ifMatch).isNull()
      assertThat(result.first().splitLocalChanges.size).isEqualTo(1)
      assertThat(result.first().splitLocalChanges[0].size).isEqualTo(1)
    }

  @Test
  fun `generate() should return Bundle Entry with if-match when useETagForUpload is true`() =
    runBlocking {
      val localChange =
        LocalChange(
          resourceType = ResourceType.Patient.name,
          resourceId = "Patient-002",
          type = LocalChange.Type.UPDATE,
          payload = "[]",
          versionId = "patient-002-version-1",
          timestamp = Instant.now(),
          token = LocalChangeToken(listOf(1L)),
        )
      val patches =
        listOf(
          PatchMapping(
            localChanges = listOf(localChange),
            generatedPatch = localChange.toPatch(),
          ),
        )
      val generator = TransactionBundleGenerator.Factory.getDefault(useETagForUpload = true)
      val result = generator.generateUploadRequests(patches)

      assertThat(result.first().generatedRequest.resource.entry.first().request.ifMatch)
        .isEqualTo("W/\"patient-002-version-1\"")
      assertThat(result.first().splitLocalChanges.size).isEqualTo(1)
      assertThat(result.first().splitLocalChanges[0].size).isEqualTo(1)
    }

  @Test
  fun `generate() should return Bundle Entry without if-match when the LocalChangeEntity has no versionId`() =
    runBlocking {
      val localChanges =
        listOf(
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-002",
            type = LocalChange.Type.UPDATE,
            payload = "[]",
            versionId = "",
            timestamp = Instant.now(),
            token = LocalChangeToken(listOf(1L)),
          ),
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-003",
            type = LocalChange.Type.UPDATE,
            payload = "[]",
            versionId = null,
            timestamp = Instant.now(),
            token = LocalChangeToken(listOf(2L)),
          ),
        )
      val patches = localChanges.map { PatchMapping(listOf(it), it.toPatch()) }
      val generator = TransactionBundleGenerator.Factory.getDefault(useETagForUpload = true)
      val result = generator.generateUploadRequests(patches)

      assertThat(result.first().generatedRequest.resource.entry[0].request.ifMatch).isNull()
      assertThat(result.first().generatedRequest.resource.entry[1].request.ifMatch).isNull()
    }

  @Test
  fun `getGenerator() with supported Bundle HTTPVerbs should return TransactionBundleGenerator`() =
    runBlocking {
      val generator =
        TransactionBundleGenerator.Factory.getGenerator(
          Bundle.HTTPVerb.PUT,
          Bundle.HTTPVerb.PATCH,
          generatedBundleSize = 500,
          useETagForUpload = true,
        )

      assertThat(generator).isInstanceOf(TransactionBundleGenerator::class.java)
    }

  @Test
  fun `getGenerator() should through exception for create by DELETE`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        runBlocking {
          TransactionBundleGenerator.Factory.getGenerator(
            Bundle.HTTPVerb.DELETE,
            Bundle.HTTPVerb.PATCH,
            generatedBundleSize = 500,
            useETagForUpload = true,
          )
        }
      }
    assertThat(exception.localizedMessage).isEqualTo("Creation using DELETE is not supported.")
  }

  @Test
  fun `getGenerator() should through exception for create by GET`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        runBlocking {
          TransactionBundleGenerator.Factory.getGenerator(
            Bundle.HTTPVerb.GET,
            Bundle.HTTPVerb.PATCH,
            generatedBundleSize = 500,
            useETagForUpload = true,
          )
        }
      }
    assertThat(exception.localizedMessage).isEqualTo("Creation using GET is not supported.")
  }

  @Test
  fun `getGenerator() should through exception for create by PATCH`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        runBlocking {
          TransactionBundleGenerator.Factory.getGenerator(
            Bundle.HTTPVerb.PATCH,
            Bundle.HTTPVerb.PATCH,
            generatedBundleSize = 500,
            useETagForUpload = true,
          )
        }
      }
    assertThat(exception.localizedMessage).isEqualTo("Creation using PATCH is not supported.")
  }

  @Test
  fun `getGenerator() should through exception for create by POST`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        runBlocking {
          TransactionBundleGenerator.Factory.getGenerator(
            Bundle.HTTPVerb.POST,
            Bundle.HTTPVerb.PATCH,
            generatedBundleSize = 500,
            useETagForUpload = true,
          )
        }
      }
    assertThat(exception.localizedMessage).isEqualTo("Creation using POST is not supported.")
  }

  @Test
  fun `getGenerator() should through exception for update by DELETE`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        runBlocking {
          TransactionBundleGenerator.Factory.getGenerator(
            Bundle.HTTPVerb.PUT,
            Bundle.HTTPVerb.DELETE,
            generatedBundleSize = 500,
            useETagForUpload = true,
          )
        }
      }
    assertThat(exception.localizedMessage).isEqualTo("Update using DELETE is not supported.")
  }

  @Test
  fun `getGenerator() should through exception for update by GET`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        runBlocking {
          TransactionBundleGenerator.Factory.getGenerator(
            Bundle.HTTPVerb.PUT,
            Bundle.HTTPVerb.GET,
            generatedBundleSize = 500,
            useETagForUpload = true,
          )
        }
      }
    assertThat(exception.localizedMessage).isEqualTo("Update using GET is not supported.")
  }

  @Test
  fun `getGenerator() should through exception for update by POST`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        runBlocking {
          TransactionBundleGenerator.Factory.getGenerator(
            Bundle.HTTPVerb.PUT,
            Bundle.HTTPVerb.POST,
            generatedBundleSize = 500,
            useETagForUpload = true,
          )
        }
      }
    assertThat(exception.localizedMessage).isEqualTo("Update using POST is not supported.")
  }

  @Test
  fun `getGenerator() should through exception for update by PUT`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        runBlocking {
          TransactionBundleGenerator.Factory.getGenerator(
            Bundle.HTTPVerb.PUT,
            Bundle.HTTPVerb.PUT,
            generatedBundleSize = 500,
            useETagForUpload = true,
          )
        }
      }
    assertThat(exception.localizedMessage).isEqualTo("Update using PUT is not supported.")
  }
}
