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
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.sync.UploadRequest
import com.google.android.fhir.sync.UploadWorkManager

/**
 * [UploadWorkManager] implementation to squash all the changes at a resource level into one change
 * and upload all resource level changes in a [BundleUploadRequest]
 */
class SquashedChangesUploadWorkManager : UploadWorkManager {

  private val bundleUploadRequestGenerator = TransactionBundleGenerator.getDefault()

  /**
   * The implementation is to squash all the changes by resource type so that there is at most one
   * local change to be uploaded per resource
   */
  override fun prepareChangesForUpload(localChanges: List<LocalChange>): List<LocalChange> {
    return localChanges
      .groupBy { it.resourceId to it.resourceType }
      .values.map { localResourceChanges -> LocalChangeUtils.squash(localResourceChanges) }
  }

  /**
   * Use the [TransactionBundleGenerator] to bundle the [LocalChange]s into [BundleUploadRequest]s
   */
  override fun createUploadRequestsFromLocalChanges(
    localChanges: List<LocalChange>
  ): List<UploadRequest> {
    return bundleUploadRequestGenerator.generateUploadRequests(localChanges)
  }

  /** Simple progress indicator determined by the number of pending requests. */
  override fun getPendingUploadsIndicator(uploadRequests: List<UploadRequest>): Int =
    uploadRequests.size
}
