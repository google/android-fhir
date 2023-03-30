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

package com.google.android.fhir.sync.upload

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.toLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.sync.UploadResult
import com.google.android.fhir.testing.BundleDataSource
import com.google.common.truth.Truth.assertThat
import java.net.ConnectException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BundleUploaderTest {

  @Test
  fun `upload Bundle transaction should emit Success`() = runBlocking {
    val result =
      BundleUploader(
          BundleDataSource { Bundle().apply { type = Bundle.BundleType.TRANSACTIONRESPONSE } },
          TransactionBundleGenerator.getDefault(),
          LocalChangesPaginator.DEFAULT
        )
        .upload(localChanges)
        .toList()

    assertThat(result).hasSize(2)
    assertThat(result.first()).isInstanceOf(UploadResult.Started::class.java)
    assertThat(result.last()).isInstanceOf(UploadResult.Success::class.java)

    val success = result.last() as UploadResult.Success
    assertThat(success.total).isEqualTo(1)
    assertThat(success.completed).isEqualTo(1)
  }

  @Test
  fun `upload Bundle transaction should emit Started state`() = runBlocking {
    val result =
      BundleUploader(
          BundleDataSource { Bundle() },
          TransactionBundleGenerator.getDefault(),
          LocalChangesPaginator.DEFAULT
        )
        .upload(localChanges)
        .toList()

    assertThat(result.first()).isInstanceOf(UploadResult.Started::class.java)
  }

  @Test
  fun `upload Bundle Transaction server error should emit Failure`() = runBlocking {
    val result =
      BundleUploader(
          BundleDataSource {
            OperationOutcome().apply {
              addIssue(
                OperationOutcome.OperationOutcomeIssueComponent().apply {
                  code = OperationOutcome.IssueType.CONFLICT
                  diagnostics = "The resource has already been updated."
                }
              )
            }
          },
          TransactionBundleGenerator.getDefault(),
          LocalChangesPaginator.DEFAULT
        )
        .upload(localChanges)
        .toList()

    assertThat(result).hasSize(2)
    assertThat(result.last()).isInstanceOf(UploadResult.Failure::class.java)
  }

  @Test
  fun `upload Bundle transaction error during upload should emit Failure`() = runBlocking {
    val result =
      BundleUploader(
          BundleDataSource { throw ConnectException("Failed to connect to server.") },
          TransactionBundleGenerator.getDefault(),
          LocalChangesPaginator.DEFAULT
        )
        .upload(localChanges)
        .toList()

    assertThat(result).hasSize(2)
    assertThat(result.last()).isInstanceOf(UploadResult.Failure::class.java)
  }

  companion object {
    val localChanges =
      listOf(
        LocalChangeEntity(
            id = 1,
            resourceType = ResourceType.Patient.name,
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
                      }
                    )
                  }
                )
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) }
      )
  }
}
