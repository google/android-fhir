/*
 * Copyright 2022-2024 Google LLC
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

package com.google.android.fhir

import android.os.Build
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.util.*
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreResourcesTest {
  @Test
  fun getResourceType() {
    assertThat(getResourceType(Patient::class.java)).isEqualTo(ResourceType.Patient)
  }

  @Test
  fun `getResourceClass() by name should return resource class`() {
    assertThat(getResourceClass<Resource>("Patient")).isEqualTo(Patient::class.java)
  }

  @Test
  fun `getResourceClass() by resource type should return resource class`() {
    assertThat(getResourceClass<Resource>(ResourceType.Patient)).isEqualTo(Patient::class.java)
  }

  @Test
  fun `updateMeta should update resource meta with given versionId and lastUpdated`() {
    val versionId = "1"
    val instantValue = Instant.now()
    val resource = Patient().apply { id = "patient" }

    resource.updateMeta(versionId, instantValue)

    assertThat(resource.meta.versionId).isEqualTo(versionId)
    assertThat(resource.meta.lastUpdatedElement.value)
      .isEqualTo(InstantType(Date.from(instantValue)).value)
  }

  @Test
  fun `updateMeta should not change existing meta if new values are null`() {
    val versionId = "1"
    val instantValue = InstantType(Date.from(Instant.now()))
    val resource =
      Patient().apply {
        id = "patient"
        meta =
          Meta().apply {
            this.versionId = versionId
            lastUpdatedElement = instantValue
          }
      }

    resource.updateMeta(null, null)

    assertThat(resource.meta.versionId).isEqualTo(versionId)
    assertThat(resource.meta.lastUpdatedElement.value).isEqualTo(instantValue.value)
  }
}
