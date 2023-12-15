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

import com.google.android.fhir.LocalChange

/**
 * Generates a [Patch] for each [LocalChange].
 *
 * Used when all client-side changes to FHIR resources need to be uploaded to the server in order to
 * maintain an audit trail.
 */
internal object PerChangePatchGenerator : PatchGenerator {
  override fun generate(localChanges: List<LocalChange>): List<PatchMapping> =
    localChanges.map {
      PatchMapping(
        localChanges = listOf(it),
        generatedPatch =
          Patch(
            resourceType = it.resourceType,
            resourceId = it.resourceId,
            versionId = it.versionId,
            timestamp = it.timestamp,
            type = it.type.toPatchType(),
            payload = it.payload,
          ),
      )
    }
}
