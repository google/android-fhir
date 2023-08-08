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
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.db.impl.dao.toLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.LocalChangeEntity.Type
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IndividualGeneratorTest {

  @Test
  fun `generateUploadRequests() should return 3 requests`() = runTest {
    val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
    val changes =
      listOf(
        LocalChangeEntity(
            id = 1,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = Type.INSERT,
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
          )
          .toLocalChange()
          .apply { token = LocalChangeToken(listOf(1)) },
        LocalChangeEntity(
            id = 2,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-002",
            type = Type.UPDATE,
            payload =
              LocalChangeUtils.diff(
                  jsonParser,
                  Patient().apply {
                    id = "Patient-002"
                    addName(
                      HumanName().apply {
                        addGiven("Jane")
                        family = "Doe"
                      }
                    )
                  },
                  Patient().apply {
                    id = "Patient-002"
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
          .apply { LocalChangeToken(listOf(2)) },
        LocalChangeEntity(
            id = 3,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-003",
            type = Type.DELETE,
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
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(3)) }
      )
    val generator = IndividualRequestGenerator.Factory.getDefault()
    val result = generator.generateUploadRequests(changes)
    assertThat(result).hasSize(3)
  }
}
