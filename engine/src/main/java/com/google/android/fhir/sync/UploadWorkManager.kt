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

package com.google.android.fhir.sync

import com.google.android.fhir.LocalChange

/**
 * Manager that pre-processes the local FHIR changes and handles how to upload them to the server.
 */
interface UploadWorkManager {

  /**
   * Transform the [localChanges] to the final set of changes that needs to be uploaded to the
   * server.
   */
  fun preprocessLocalChanges(localChanges: List<LocalChange>): List<LocalChange>

  /** Generates a list of [UploadRequest] from the [LocalChange]s to be uploaded to the server */
  fun createUploadRequestsFromLocalChanges(localChanges: List<LocalChange>): List<UploadRequest>
}
