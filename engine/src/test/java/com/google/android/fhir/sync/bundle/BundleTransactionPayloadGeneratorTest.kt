/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.sync.bundle

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BundleTransactionPayloadGeneratorTest {

  @Test
  fun `generate() should return empty list if there are no local changes`() = runBlocking {
    val generator =
      BundlePayloadGenerator(
        createRequest = HttpPutForCreateEntryComponent(FhirContext.forR4().newJsonParser()),
        updateRequest = HttpPatchForUpdateEntryComponent(),
        deleteRequest = HttpDeleteEntryComponent(),
        localChangeProvider =
          object : LocalChangeProvider {
            override suspend fun getLocalChanges(): List<List<SquashedLocalChange>> {
              return listOf(listOf(), listOf())
            }
          },
      )

    val result = generator.generate()

    assertThat(result).isEmpty()
  }

  @Test
  fun `generate() should return single Transaction Bundle with 3 entries`() = runBlocking {
    val jsonParser = FhirContext.forR4().newJsonParser()
    val changes =
      listOf(
        SquashedLocalChange(
          LocalChangeToken(listOf(1)),
          LocalChangeEntity(
            id = 1,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.INSERT,
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
              )
          )
        ),
        SquashedLocalChange(
          LocalChangeToken(listOf(2)),
          LocalChangeEntity(
            id = 2,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-002",
            type = LocalChangeEntity.Type.UPDATE,
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
                .toString()
          )
        ),
        SquashedLocalChange(
          LocalChangeToken(listOf(3)),
          LocalChangeEntity(
            id = 3,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-002",
            type = LocalChangeEntity.Type.DELETE,
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
              )
          )
        )
      )
    val generator =
      BundlePayloadGenerator(
        createRequest = HttpPutForCreateEntryComponent(FhirContext.forR4().newJsonParser()),
        updateRequest = HttpPatchForUpdateEntryComponent(),
        deleteRequest = HttpDeleteEntryComponent(),
        localChangeProvider = DefaultLocalChangeProvider(changes)
      )

    val result = generator.generate()

    assertThat(result).hasSize(1)
    val (bundle, _) = result.first()
    assertThat(bundle.type).isEqualTo(Bundle.BundleType.TRANSACTION)
    assertThat(bundle.entry).hasSize(3)
    assertThat(bundle.entry.map { it.request.method })
      .containsExactly(Bundle.HTTPVerb.PUT, Bundle.HTTPVerb.PATCH, Bundle.HTTPVerb.DELETE)
      .inOrder()
  }
}
