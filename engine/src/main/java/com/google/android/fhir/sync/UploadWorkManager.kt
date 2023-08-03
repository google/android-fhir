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
   * Transforms the [LocalChange]s to a list of lists, containing the changes that need to be
   * uploaded to the server. For example, given a list of changes [a, b, c ,d, e, f], this function
   * could return a list of lists [ [a], [b,c], [d], [e]], or [ [a], [z]], where z is the result of
   * a transformation like squashing
   */
  fun chunkLocalChanges(localChanges: List<LocalChange>): List<List<LocalChange>>

  /** Creates an [UploadRequest] from the list of local changes. */
  fun createNextRequest(localChanges: List<LocalChange>): UploadRequest
}
