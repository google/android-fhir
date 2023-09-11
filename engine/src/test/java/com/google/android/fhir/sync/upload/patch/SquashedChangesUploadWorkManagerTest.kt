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

package com.google.android.fhir.sync.upload.patch

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.db.impl.dao.toLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SquashedChangesUploadWorkManagerTest {

  private val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  @Test
  fun `prepareChangesForUpload should return no LocalChanges for same resource locally inserted and deleted`() {
    val changes =
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
                ),
            timestamp = Instant.now()
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
        LocalChangeEntity(
            id = 2,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.DELETE,
            payload = "",
            timestamp = Instant.now()
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(2)) }
      )
    val patchToUpload = SquashedChangesUploadWorkManager().prepareChangesForUpload(changes)

    assertThat(patchToUpload).hasSize(1)
    assertThat(patchToUpload.first().type).isEqualTo(LocalChange.Type.NO_OP)
  }

  @Test
  fun `prepareChangesForUpload should squash change to NO_OP if insert is followed by an update, then a delete`() {
    val changes =
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
                ),
            timestamp = Instant.now()
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
        LocalChangeEntity(
            id = 2,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.UPDATE,
            payload =
              LocalChangeUtils.diff(
                  jsonParser,
                  Patient().apply {
                    id = "Patient-001"
                    addName(
                      HumanName().apply {
                        addGiven("Jane")
                        family = "Doe"
                      }
                    )
                  },
                  Patient().apply {
                    id = "Patient-001"
                    addName(
                      HumanName().apply {
                        addGiven("Janet")
                        family = "Doe"
                      }
                    )
                  }
                )
                .toString(),
            timestamp = Instant.now()
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
        LocalChangeEntity(
            id = 3,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.DELETE,
            payload = "",
            timestamp = Instant.now()
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(3)) },
      )
    val patchToUpload = SquashedChangesUploadWorkManager().prepareChangesForUpload(changes)

    assertThat(patchToUpload).hasSize(1)
    assertThat(patchToUpload.first().type).isEqualTo(LocalChange.Type.NO_OP)
  }

  @Test
  fun `prepareChangesForUpload should throw an error if a change is done after a resource is deleted locally`() {
    val changes =
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
                ),
            timestamp = Instant.now()
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
        LocalChangeEntity(
            id = 2,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.DELETE,
            payload = "",
            timestamp = Instant.now()
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(2)) },
        LocalChangeEntity(
            id = 3,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.UPDATE,
            payload = "",
            timestamp = Instant.now()
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(3)) }
      )

    val errorMessage =
      assertThrows(IllegalArgumentException::class.java) {
          SquashedChangesUploadWorkManager().prepareChangesForUpload(changes)
        }
        .localizedMessage

    assertThat(errorMessage).isEqualTo("Cannot merge local changes with type NO_OP and UPDATE.")
  }
}
