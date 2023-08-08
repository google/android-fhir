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

package com.google.android.fhir.sync.upload

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.toLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InsertUpdateSquashedChangesUploadWorkManagerTest {
  @Test
  fun `prepareChangesForUpload should return two LocalChanges for same resource locally inserted and deleted`() {
    val insertAndDeleteLocalChanges = insertLocalChanges + deleteLocalChanges

    val localChanges =
      InsertUpdateSquashedChangesUploadWorkManager()
        .prepareChangesForUpload(insertAndDeleteLocalChanges)

    assertThat(localChanges).hasSize(2)
  }

  companion object {
    val insertLocalChanges =
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
          .apply { LocalChangeToken(listOf(1)) }
      )

    val deleteLocalChanges =
      listOf(
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
  }
}
