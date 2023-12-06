/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.sync.upload

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.testing.BundleDataSource
import com.google.android.fhir.toLocalChange
import com.google.common.truth.Truth.assertThat
import java.net.ConnectException
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UploaderTest {

  @Test
  fun `upload should succeed if response is transaction response`() = runTest {
    val result =
      Uploader(
          BundleDataSource { Bundle().apply { type = Bundle.BundleType.TRANSACTIONRESPONSE } },
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Success::class.java)
    with(result as UploadSyncResult.Success) { assertThat(responseResources).hasSize(1) }
  }

  @Test
  fun `upload should fail if response is operation outcome with issue`() = runBlocking {
    val result =
      Uploader(
          BundleDataSource {
            OperationOutcome().apply {
              addIssue(
                OperationOutcome.OperationOutcomeIssueComponent().apply {
                  severity = OperationOutcome.IssueSeverity.WARNING
                  code = OperationOutcome.IssueType.CONFLICT
                  diagnostics = "The resource has already been updated."
                },
              )
            }
          },
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  @Test
  fun `upload should fail if response is empty operation outcome`() = runBlocking {
    val result =
      Uploader(
          BundleDataSource { OperationOutcome() },
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  @Test
  fun `upload should fail if response is neither transaction response nor operation outcome`() =
    runBlocking {
      val result =
        Uploader(
            BundleDataSource { Bundle().apply { type = Bundle.BundleType.SEARCHSET } },
          )
          .upload(localChanges)

      assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
    }

  @Test
  fun `upload should fail if there is connection exception`() = runBlocking {
    val result =
      Uploader(
          BundleDataSource { throw ConnectException("Failed to connect to server.") },
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  companion object {
    val localChanges =
      listOf(
        LocalChangeEntity(
            id = 1,
            resourceType = ResourceType.Patient.name,
            resourceUuid = UUID.randomUUID(),
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(
                  Patient().apply {
                    id = "Patient-001"
                    addName(
                      HumanName().apply {
                        addGiven("John")
                        family = "Doe"
                      },
                    )
                  },
                ),
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
      )
  }
}
