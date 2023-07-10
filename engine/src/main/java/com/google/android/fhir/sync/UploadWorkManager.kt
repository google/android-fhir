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
import com.google.android.fhir.db.impl.dao.LocalChangeUtils

/**
 * Manager that pre-processes the local FHIR changes and handles how to upload them to the server.
 */
abstract class UploadWorkManager(private val uploadRequestGenerator: UploadRequestGenerator) {

  /**
   * Transform the [localChanges] to the final set of changes that needs to be uploaded to the
   * server. The default implementation is to squash all the changes by resource type so that there
   * is at most one local change to be uploaded per resource
   */
  open fun preprocessLocalChanges(localChanges: List<LocalChange>): List<LocalChange> {
    return localChanges
      .groupBy { it.resourceId to it.resourceType }
      .values.map { localResourceChanges -> LocalChangeUtils.squash(localResourceChanges) }
  }

  /** Generates a list of [UploadRequest] from the [LocalChange] to be sent to the server */
  fun createUploadRequestsFromChanges(localChanges: List<LocalChange>): List<UploadRequest> {
    return uploadRequestGenerator.generateUploadRequests(localChanges)
  }
}
