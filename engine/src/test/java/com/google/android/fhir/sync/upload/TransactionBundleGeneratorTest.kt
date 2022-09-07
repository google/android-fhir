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
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.db.impl.dao.toLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.LocalChangeEntity.Type
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
class TransactionBundleGeneratorTest {

  @Test
  fun `generate() should return empty list if there are no local changes`() = runBlocking {
    val generator = TransactionBundleGenerator.Factory.getDefault()
    val result = generator.generate(listOf(listOf(), listOf()))
    assertThat(result).isEmpty()
  }

  @Test
  fun `generate() should return single Transaction Bundle with 3 entries`() = runBlocking {
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
              )
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
                .toString()
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
              )
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(3)) }
      )
    val generator = TransactionBundleGenerator.Factory.getDefault()
    val result = generator.generate(listOf(changes))

    assertThat(result).hasSize(1)
    val (bundle, _) = result.first()
    assertThat(bundle.type).isEqualTo(Bundle.BundleType.TRANSACTION)
    assertThat(bundle.entry).hasSize(3)
    assertThat(bundle.entry.map { it.request.method })
      .containsExactly(Bundle.HTTPVerb.PUT, Bundle.HTTPVerb.PATCH, Bundle.HTTPVerb.DELETE)
      .inOrder()
  }

  @Test
  fun `generate() should return 3 Transaction Bundle with single entry each`() = runBlocking {
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
              )
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
                .toString()
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
              )
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(3)) }
      )
    val generator = TransactionBundleGenerator.Factory.getDefault()
    val result = generator.generate(changes.chunked(1))

    // Exactly 3 Bundles are generated
    assertThat(result).hasSize(3)
    // Each Bundle is of type transaction
    assertThat(result.map { it.first.type }.filter { it == Bundle.BundleType.TRANSACTION }.size)
      .isEqualTo(3)
    // Each Bundle has exactly 1 entry
    assertThat(result.map { it.first.entry.size }.filter { it == 1 }.size).isEqualTo(3)
    assertThat(result.map { it.first.entry.first().request.method })
      .containsExactly(Bundle.HTTPVerb.PUT, Bundle.HTTPVerb.PATCH, Bundle.HTTPVerb.DELETE)
      .inOrder()
  }
}
