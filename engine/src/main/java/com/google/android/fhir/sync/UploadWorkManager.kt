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

package com.google.android.fhir.sync

import com.google.android.fhir.LocalChange

/**
 * Manager that pre-processes the local FHIR changes and handles how to upload them to the server.
 */
interface UploadWorkManager {
  /**
   * Transforms the [LocalChange]s to the final set of changes that needs to be uploaded to the
   * server. The transformations can be of various types like squashing the [LocalChange]s by
   * [Resource] e.g. [SquashedChangesUploadWorkManager] or filtering out certain [LocalChange]s or
   * grouping the changes.
   */
  fun prepareChangesForUpload(localChanges: List<LocalChange>): List<LocalChange>

  /** Generates a list of [UploadRequest]s from the [LocalChange]s to be uploaded to the server */
  fun createUploadRequestsFromLocalChanges(localChanges: List<LocalChange>): List<UploadRequest>

  /**
   * Gets the [Int] to indicate the progress in terms of the pending uploads. The indicator could be
   * determined at the resource level (by extracting resource information from the upload requests)
   * etc.
   */
  fun getPendingUploadsIndicator(uploadRequests: List<UploadRequest>): Int
}
