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
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.logicalId
import com.google.android.fhir.sync.upload.patch.PatchGeneratorFactory
import com.google.android.fhir.sync.upload.patch.PatchGeneratorMode
import com.google.android.fhir.sync.upload.request.UploadRequestGeneratorFactory
import com.google.android.fhir.sync.upload.request.UploadRequestGeneratorMode
import com.google.android.fhir.testing.BundleDataSource
import com.google.android.fhir.testing.UrlRequestDataSource
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
import org.hl7.fhir.r4.model.codesystems.HttpVerb
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UploaderTest {

  @Test
  fun `bundle upload for per resource patch should output responses mapped correctly to the local changes`() =
    runTest {
      val patient1Id = "Patient-001"
      val patient2Id = "Patient-002"
      val patient2 = Patient().apply { id = patient2Id }
      val databaseLocalChanges =
        listOf(
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient1Id,
            type = LocalChange.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(patient),
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(1)),
          ),
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient2Id,
            type = LocalChange.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(patient2),
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(2)),
          ),
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient1Id,
            type = LocalChange.Type.UPDATE,
            payload = "[{\"op\":\"replace\",\"path\":\"/name/0/family\",\"value\":\"Nucleus\"}]",
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(3)),
          ),
        )

      val updatedPatient1 =
        patient.copy().apply {
          addName(
            HumanName().apply {
              addGiven("John")
              family = "Nucleus"
            },
          )
        }
      val result =
        Uploader(
            BundleDataSource {
              Bundle().apply {
                type = Bundle.BundleType.TRANSACTIONRESPONSE
                addEntry(
                  Bundle.BundleEntryComponent().apply { resource = updatedPatient1 },
                )
                addEntry(
                  Bundle.BundleEntryComponent().apply { resource = patient2 },
                )
              }
            },
            perResourcePatchGenerator,
            bundleUploadRequestGenerator,
          )
          .upload(databaseLocalChanges)

      assertThat(result).isInstanceOf(UploadSyncResult.Success::class.java)
      with(result as UploadSyncResult.Success) { assertThat(uploadResponses).hasSize(2) }
      with(result.uploadResponses[0]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(2)
        assertThat(localChanges.all { it.resourceId == patient1Id }).isTrue()
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient1Id)
      }

      with(result.uploadResponses[1]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(1)
        assertThat(localChanges.all { it.resourceId == patient2Id }).isTrue()
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient2Id)
      }
    }

  @Test
  fun `bundle upload for per change patch should output responses mapped correctly to the local changes`() =
    runTest {
      val patient1Id = "Patient-001"
      val patient2Id = "Patient-002"
      val patient2 = Patient().apply { id = patient2Id }
      val databaseLocalChanges =
        listOf(
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient1Id,
            type = LocalChange.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(patient),
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(1)),
          ),
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient2Id,
            type = LocalChange.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(patient2),
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(2)),
          ),
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient1Id,
            type = LocalChange.Type.UPDATE,
            payload = "[{\"op\":\"replace\",\"path\":\"/name/0/family\",\"value\":\"Nucleus\"}]",
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(3)),
          ),
        )

      val updatedPatient1 =
        patient.copy().apply {
          addName(
            HumanName().apply {
              addGiven("John")
              family = "Nucleus"
            },
          )
        }
      val result =
        Uploader(
            BundleDataSource {
              Bundle().apply {
                type = Bundle.BundleType.TRANSACTIONRESPONSE
                addEntry(
                  Bundle.BundleEntryComponent().apply { resource = patient },
                )
                addEntry(
                  Bundle.BundleEntryComponent().apply { resource = patient2 },
                )
                addEntry(
                  Bundle.BundleEntryComponent().apply { resource = updatedPatient1 },
                )
              }
            },
            perChangePatchGenerator,
            bundleUploadRequestGenerator,
          )
          .upload(databaseLocalChanges)

      assertThat(result).isInstanceOf(UploadSyncResult.Success::class.java)
      with(result as UploadSyncResult.Success) { assertThat(uploadResponses).hasSize(3) }
      with(result.uploadResponses[0]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(1)
        assertThat(localChanges[0].resourceId).isEqualTo(patient1Id)
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient1Id)
      }

      with(result.uploadResponses[1]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(1)
        assertThat(localChanges[0].resourceId).isEqualTo(patient2Id)
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient2Id)
      }

      with(result.uploadResponses[2]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(1)
        assertThat(localChanges[0].resourceId).isEqualTo(patient1Id)
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient1Id)
      }
    }

  @Test
  fun `bundle upload should fail if bundle response has incorrect size`() = runTest {
    val result =
      Uploader(
          BundleDataSource { Bundle().apply { type = Bundle.BundleType.TRANSACTIONRESPONSE } },
          perResourcePatchGenerator,
          bundleUploadRequestGenerator,
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  @Test
  fun `bundle upload should fail if response is operation outcome with issue`() = runBlocking {
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
          perResourcePatchGenerator,
          bundleUploadRequestGenerator,
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  @Test
  fun `bundle upload should fail if response is empty operation outcome`() = runBlocking {
    val result =
      Uploader(
          BundleDataSource { OperationOutcome() },
          perResourcePatchGenerator,
          bundleUploadRequestGenerator,
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  @Test
  fun `bundle upload should fail if response is neither transaction response nor operation outcome`() =
    runBlocking {
      val result =
        Uploader(
            BundleDataSource { Bundle().apply { type = Bundle.BundleType.SEARCHSET } },
            perResourcePatchGenerator,
            bundleUploadRequestGenerator,
          )
          .upload(localChanges)

      assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
    }

  @Test
  fun `bundle upload should fail if there is connection exception`() = runBlocking {
    val result =
      Uploader(
          BundleDataSource { throw ConnectException("Failed to connect to server.") },
          perResourcePatchGenerator,
          bundleUploadRequestGenerator,
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  @Test
  fun `url upload for per resource patch should output responses mapped correctly to the local changes`() =
    runTest {
      val patient1Id = "Patient-001"
      val patient2Id = "Patient-002"
      val patient2 = Patient().apply { id = patient2Id }
      val databaseLocalChanges =
        listOf(
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient1Id,
            type = LocalChange.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(patient),
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(1)),
          ),
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient2Id,
            type = LocalChange.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(patient2),
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(2)),
          ),
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient1Id,
            type = LocalChange.Type.UPDATE,
            payload = "[{\"op\":\"replace\",\"path\":\"/name/0/family\",\"value\":\"Nucleus\"}]",
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(3)),
          ),
        )

      val updatedPatient1 =
        patient.copy().apply {
          addName(
            HumanName().apply {
              addGiven("John")
              family = "Nucleus"
            },
          )
        }
      val result =
        Uploader(
            UrlRequestDataSource {
              when (it.resource.logicalId) {
                patient1Id -> updatedPatient1
                patient2Id -> patient2
                else -> throw IllegalArgumentException("Unknown patient ID")
              }
            },
            perResourcePatchGenerator,
            urlUploadRequestGenerator,
          )
          .upload(databaseLocalChanges)

      assertThat(result).isInstanceOf(UploadSyncResult.Success::class.java)
      with(result as UploadSyncResult.Success) { assertThat(uploadResponses).hasSize(2) }
      with(result.uploadResponses[0]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(2)
        assertThat(localChanges.all { it.resourceId == patient1Id }).isTrue()
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient1Id)
      }

      with(result.uploadResponses[1]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(1)
        assertThat(localChanges.all { it.resourceId == patient2Id }).isTrue()
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient2Id)
      }
    }

  @Test
  fun `url upload for per change patch should output responses mapped correctly to the local changes`() =
    runTest {
      val patient1Id = "Patient-001"
      val patient2Id = "Patient-002"
      val patient2 = Patient().apply { id = patient2Id }
      val databaseLocalChanges =
        listOf(
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient1Id,
            type = LocalChange.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(patient),
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(1)),
          ),
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient2Id,
            type = LocalChange.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(patient2),
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(2)),
          ),
          LocalChange(
            resourceType = ResourceType.Patient.name,
            resourceId = patient1Id,
            type = LocalChange.Type.UPDATE,
            payload = "[{\"op\":\"replace\",\"path\":\"/name/0/family\",\"value\":\"Nucleus\"}]",
            timestamp = Instant.now(),
            versionId = null,
            token = LocalChangeToken(listOf(3)),
          ),
        )

      val updatedPatient1 =
        patient.copy().apply {
          addName(
            HumanName().apply {
              addGiven("John")
              family = "Nucleus"
            },
          )
        }
      val result =
        Uploader(
            UrlRequestDataSource {
              when (it.httpVerb) {
                HttpVerb.PUT -> {
                  when (it.resource.logicalId) {
                    patient1Id -> updatedPatient1
                    patient2Id -> patient2
                    else -> throw IllegalArgumentException("Unknown patient ID")
                  }
                }
                HttpVerb.PATCH -> updatedPatient1
                else -> throw IllegalArgumentException("Unknown patient ID")
              }
            },
            perChangePatchGenerator,
            urlUploadRequestGenerator,
          )
          .upload(databaseLocalChanges)

      assertThat(result).isInstanceOf(UploadSyncResult.Success::class.java)
      with(result as UploadSyncResult.Success) { assertThat(uploadResponses).hasSize(3) }
      with(result.uploadResponses[0]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(1)
        assertThat(localChanges[0].resourceId).isEqualTo(patient1Id)
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient1Id)
      }

      with(result.uploadResponses[1]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(1)
        assertThat(localChanges[0].resourceId).isEqualTo(patient2Id)
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient2Id)
      }

      with(result.uploadResponses[2]) {
        assertThat(this).isInstanceOf(ResourceUploadResponseMapping::class.java)
        assertThat(localChanges).hasSize(1)
        assertThat(localChanges[0].resourceId).isEqualTo(patient1Id)
        assertThat(output).isInstanceOf(Patient::class.java)
        assertThat((output as Patient).id).isEqualTo(patient1Id)
      }
    }

  @Test
  fun `url upload should fail if response has incorrect resource type`() = runTest {
    val result =
      Uploader(
          UrlRequestDataSource { Bundle().apply { type = Bundle.BundleType.SEARCHSET } },
          perResourcePatchGenerator,
          urlUploadRequestGenerator,
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  @Test
  fun `url upload should fail if response is operation outcome with issue`() = runBlocking {
    val result =
      Uploader(
          UrlRequestDataSource {
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
          perResourcePatchGenerator,
          urlUploadRequestGenerator,
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  @Test
  fun `url upload should fail if response is empty operation outcome`() = runBlocking {
    val result =
      Uploader(
          UrlRequestDataSource { OperationOutcome() },
          perResourcePatchGenerator,
          urlUploadRequestGenerator,
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  @Test
  fun `url upload should fail if there is connection exception`() = runBlocking {
    val result =
      Uploader(
          UrlRequestDataSource { throw ConnectException("Failed to connect to server.") },
          perResourcePatchGenerator,
          urlUploadRequestGenerator,
        )
        .upload(localChanges)

    assertThat(result).isInstanceOf(UploadSyncResult.Failure::class.java)
  }

  companion object {
    private val perResourcePatchGenerator =
      PatchGeneratorFactory.byMode(PatchGeneratorMode.PerResource)
    private val perChangePatchGenerator = PatchGeneratorFactory.byMode(PatchGeneratorMode.PerChange)
    private val urlUploadRequestGenerator =
      UploadRequestGeneratorFactory.byMode(
        UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, HttpVerb.PATCH),
      )
    private val bundleUploadRequestGenerator =
      UploadRequestGeneratorFactory.byMode(
        UploadRequestGeneratorMode.BundleRequest(Bundle.HTTPVerb.PUT, Bundle.HTTPVerb.PATCH),
      )
    val patient =
      Patient().apply {
        id = "Patient-001"
        addName(
          HumanName().apply {
            addGiven("John")
            family = "Doe"
          },
        )
      }
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
                .encodeResourceToString(patient),
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
      )
  }
}
