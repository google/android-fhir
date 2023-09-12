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

import com.google.android.fhir.LocalChange
import com.google.android.fhir.sync.UploadRequest
import com.google.android.fhir.sync.upload.patch.Patch

/**
 * Manager that pre-processes the local FHIR changes and handles how to upload them to the server.
 */
internal interface UploadWorkManager {
  /**
   * Transforms the [LocalChange]s to the final set of [Patch]es that need to be uploaded to the
   * server. The transformation can be of various types like grouping the [LocalChange]s by resource
   * or filtering out certain [LocalChange]s.
   */
  fun generatePatches(localChanges: List<LocalChange>): List<Patch>

  /** Generates a list of [UploadRequest]s from the [Patch]es to be uploaded to the server */
  fun generateRequests(patches: List<Patch>): List<UploadRequest>
}
