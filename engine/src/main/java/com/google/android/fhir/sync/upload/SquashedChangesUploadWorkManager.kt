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
import com.google.android.fhir.sync.UploadWorkManager
import java.util.LinkedList

/**
 * [UploadWorkManager] implementation to squash all the changes at a resource level into one change
 * and upload all resource level changes in a [BundleUploadRequest]
 */
class SquashedChangesUploadWorkManager : UploadWorkManager {

  private val bundleUploadRequestGenerator = TransactionBundleGenerator.getDefault()

  private lateinit var changesToUpload: LinkedList<List<LocalChange>>

  /**
   * The implementation is to squash all the changes by resource type so that there is at most one
   * local change to be uploaded per resource
   */
  override fun setChangesToUpload(localChanges: List<LocalChange>) {
    changesToUpload = LinkedList(bundleUploadRequestGenerator.chunkLocalChanges(localChanges))
  }

  override fun getNextRequest(): UploadRequest? {
    val localChanges = changesToUpload.poll() ?: return null
    return bundleUploadRequestGenerator.generateBundleRequest(localChanges)
  }

  /** Simple progress indicator determined by the number of pending requests. */
  override fun getPendingUploadsIndicator(): Int = changesToUpload.size
}
