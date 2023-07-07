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
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource

/** Module for uploading local changes to a [DataSource]. */
internal interface Uploader {

  /**
   * Uploads the local changes to the [DataSource]. Particular implementations should take care of
   * transforming the [LocalChange]s to particular network operations. If [ProgressCallback] is
   * provided it also reports the intermediate progress
   */
  suspend fun upload(localChanges: List<LocalChange>): Flow<UploadResult>
}

sealed class UploadRequest(
  open val headers: Map<String, String>,
  open val localChangeToken: LocalChangeToken
)

/** A FHIR [Bundle] based request for uploads */
data class BundleUploadRequest(
  val bundle: Bundle,
  override val localChangeToken: LocalChangeToken,
  override val headers: Map<String, String> = emptyMap()
) : UploadRequest(headers, localChangeToken)

/**
 * A FHIR PATCH based request for updates to a resource based on
 * https://www.hl7.org/fhir/http.html#patch
 */
data class PatchUploadRequest(
  val patchBody: String,
  val resourceType: String,
  val resourceId: String,
  override val localChangeToken: LocalChangeToken,
  override val headers: Map<String, String> = emptyMap()
) : UploadRequest(headers, localChangeToken)

/**
 * A FHIR PUT based request for deletions of a resource based on
 * https://www.hl7.org/fhir/http.html#delete Deletes will always be on a per resource basis as the
 * current way of [LocalChange] supports that
 */
data class DeleteUploadRequest(
  val resourceType: String,
  val resourceId: String,
  override val localChangeToken: LocalChangeToken,
  override val headers: Map<String, String> = emptyMap()
) : UploadRequest(headers, localChangeToken)

/**
 * A FHIR PUT based request for updates/creation of a resource based on
 * https://www.hl7.org/fhir/http.html#update
 */
data class PutUploadRequest(
  val resource: Resource,
  val resourceType: String,
  val resourceId: String,
  override val localChangeToken: LocalChangeToken,
  override val headers: Map<String, String> = emptyMap()
) : UploadRequest(headers, localChangeToken)

/**
 * A FHIR POST based request for creation of a resource based on
 * https://www.hl7.org/fhir/http.html#update
 */
data class PostUploadRequest(
  val resource: Resource,
  val resourceType: String,
  override val localChangeToken: LocalChangeToken,
  override val headers: Map<String, String> = emptyMap()
) : UploadRequest(headers, localChangeToken)

internal sealed class UploadResult {
  data class Started(val total: Int) : UploadResult()
  data class Success(
    val localChangeToken: LocalChangeToken,
    val resource: Resource,
    val total: Int,
    val completed: Int
  ) : UploadResult()
  data class Failure(val syncError: ResourceSyncException) : UploadResult()
}
