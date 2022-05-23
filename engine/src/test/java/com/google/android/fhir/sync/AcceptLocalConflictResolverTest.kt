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

package com.google.android.fhir.sync

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.resource.TestingUtils
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AcceptLocalConflictResolverTest {
  private val testingUtils = TestingUtils(FhirContext.forCached(FhirVersionEnum.R4).newJsonParser())

  @Test
  fun resolve_shouldReturnLocalChange() {
    val localResource =
      Patient().apply {
        id = "patient-id-1"
        addName(
          HumanName().apply {
            family = "Local"
            addGiven("Patient1")
          }
        )
      }

    val remoteResource =
      Patient().apply {
        id = "patient-id-1"
        addName(
          HumanName().apply {
            family = "Remote"
            addGiven("Patient1")
          }
        )
      }

    val result = AcceptRemoteConflictResolver.resolve(localResource, remoteResource)
    assertThat(result).isInstanceOf(Resolved::class.java)
    testingUtils.assertResourceEquals(localResource, (result as Resolved).resolved)
  }
}
