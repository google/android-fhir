/*
 * Copyright 2025-2026 Google LLC
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

import com.google.fhir.model.r4.HumanName
import com.google.fhir.model.r4.Patient
import kotlin.test.Test
import kotlin.test.assertIs

/**
 * Adapted from engine/src/test/java/com/google/android/fhir/sync/AcceptRemoteConflictResolverTest.kt
 *
 * Engine test asserts `Resolved(remoteResource)` — engine-kmp returns
 * `ConflictResolutionResult.AcceptRemote` (simpler sealed class, no resolved resource carried).
 */
class AcceptRemoteConflictResolverTest {

  @Test
  fun resolve_shouldReturnRemoteChange() {
    val localResource =
      Patient(
        id = "patient-id-1",
        name =
          listOf(
            HumanName(
              family = com.google.fhir.model.r4.String(value = "Local"),
              given = listOf(com.google.fhir.model.r4.String(value = "Patient1")),
            ),
          ),
      )

    val remoteResource =
      Patient(
        id = "patient-id-1",
        name =
          listOf(
            HumanName(
              family = com.google.fhir.model.r4.String(value = "Remote"),
              given = listOf(com.google.fhir.model.r4.String(value = "Patient1")),
            ),
          ),
      )

    val result = AcceptRemoteConflictResolver.resolve(localResource, remoteResource)
    assertIs<Resolved>(result)
  }
}
