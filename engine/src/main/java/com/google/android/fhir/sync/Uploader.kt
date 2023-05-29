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
import org.hl7.fhir.r4.model.codesystems.HttpVerb

/** Module for uploading local changes to a [DataSource]. */
internal interface Uploader {

  /**
   * Uploads the local changes to the [DataSource]. Particular implementations should take care of
   * transforming the [SquashedLocalChange]s to particular network operations. If [ProgressCallback]
   * is provided it also reports the intermediate progress
   */
  suspend fun upload(localChanges: List<LocalChange>): Flow<UploadResult>
}

/** A request that needs to be uploaded to a server */
sealed class UploadRequest(open val localChangeToken: LocalChangeToken)

/** A simple HTTP based FHIR request to upload modifications to a server */
sealed class SimpleUploadRequest(
  open val path: String,
  open val body: String,
  val httpVerb: HttpVerb,
  override val localChangeToken: LocalChangeToken
) : UploadRequest(localChangeToken)

/**
 * A simple HTTP PUT FHIR request to update/create a resource/resources on the server
 * https://hl7.org/fhir/R4/http.html#update
 */
data class PutUploadRequest(
  override val path: String,
  override val body: String,
  override val localChangeToken: LocalChangeToken
) : SimpleUploadRequest(path, body, HttpVerb.PUT, localChangeToken)

/**
 * A simple HTTP PATCH FHIR request to update a resource/resources on the server
 * https://hl7.org/fhir/R4/http.html#patch
 */
data class PatchUploadRequest(
  override val path: String,
  override val body: String,
  override val localChangeToken: LocalChangeToken
) : SimpleUploadRequest(path, body, HttpVerb.PATCH, localChangeToken)

/**
 * A simple HTTP DELETE FHIR request to delete resources on the server
 * https://hl7.org/fhir/R4/http.html#delete
 */
data class DeleteUploadRequest(
  override val path: String,
  override val localChangeToken: LocalChangeToken
) : SimpleUploadRequest(path, "", HttpVerb.DELETE, localChangeToken)

/**
 * A [bundle] based FHIR request to upload multiple modification to the server in a batch. e.g.
 * https://hl7.org/fhir/R4/bundle-transaction.json.html
 */
data class BundleUploadRequest(
  val bundle: Bundle,
  override val localChangeToken: LocalChangeToken
) : UploadRequest(localChangeToken)

internal sealed class UploadResult {
  data class Started(val total: Int) : UploadResult()
  data class Success(
    val localChangeToken: LocalChangeToken,
    val resource: Bundle,
    val total: Int,
    val completed: Int
  ) : UploadResult()
  data class Failure(val syncError: ResourceSyncException) : UploadResult()
}
