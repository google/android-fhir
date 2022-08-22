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

package com.google.android.fhir.db.impl.dao

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocalChangeUtilsTest : TestCase() {

  @Test
  fun `toLocalChange() should return LocalChange`() = runBlocking {
    val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
    val localChangeEntity =
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

    val localChange = localChangeEntity.toLocalChange()
    assertThat(localChangeEntity.id).isEqualTo(localChange.token.ids.first())
    assertThat(localChangeEntity.resourceType).isEqualTo(localChange.resourceType)
    assertThat(localChangeEntity.resourceId).isEqualTo(localChange.resourceId)
    assertThat(localChangeEntity.timestamp).isEqualTo(localChange.timestamp)
    assertThat(LocalChange.Type.from(localChangeEntity.type.value)).isEqualTo(localChange.type)
    assertThat(localChangeEntity.payload).isEqualTo(localChange.payload)
    assertThat(localChangeEntity.versionId).isEqualTo(localChange.versionId)
  }
}
