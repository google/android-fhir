/*
 * Copyright 2023-2024 Google LLC
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

import com.google.android.fhir.sync.upload.patch.PatchGeneratorMode
import com.google.android.fhir.sync.upload.request.UploadRequestGeneratorMode
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.codesystems.HttpVerb

/**
 * Defines strategies for uploading FHIR resource
 * [local changes][com.google.android.fhir.LocalChange] to a server during synchronization. It is
 * used by the [FhirSyncWorker][com.google.android.fhir.sync.FhirSyncWorker] to determine the
 * specific upload behavior.
 *
 * To specify an upload strategy, override
 * [getUploadStrategy][com.google.android.fhir.sync.FhirSyncWorker.getUploadStrategy] in your app's
 * [FhirSyncWorker][com.google.android.fhir.sync.FhirSyncWorker], for example:
 * ```
 * override fun getUploadStrategy(): UploadStrategy = UploadStrategy.AllChangesSquashedBundlePut(UpdatePayloadType.PATCH, 500)
 * ```
 *
 * The strategy you select depends on the server's capabilities (for example, support for `PUT` vs
 * `POST` requests), and your business requirements (for example, maintaining the history of every
 * local change).
 *
 * Each strategy specifies three key aspects of the upload process:
 * * **Fetching local changes**: This determines which local changes are included in the upload,
 *   specified by the [localChangesFetchMode] property.
 * * **Generating patches**: This determines how the local changes are represented for upload,
 *   specified by the [patchGeneratorMode] property.
 * * **Creating upload requests**: This determines how the patches are packaged and sent to the
 *   server, specified by the [requestGeneratorMode] property.
 *
 * Note: The strategies listed here represent all currently supported combinations of local change
 * fetching, patch generation, and upload request creation. Not all possible combinations of these
 * modes are valid or supported.
 */
sealed class UploadStrategy
private constructor(
  internal val localChangesFetchMode: LocalChangesFetchMode,
  internal val patchGeneratorMode: PatchGeneratorMode,
  internal val requestGeneratorMode: UploadRequestGeneratorMode,
) {

  /**
   * Fetches all local changes, generates one patch per resource, and uploads them in a single
   * bundled PUT request. This strategy is efficient and minimizes the number of requests sent to
   * the server, but does not maintain individual change history.
   */
  data class AllChangesSquashedBundlePut(
    val updatePayloadType: UpdatePayloadType,
    val bundleSize: Int,
  ) :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.BundleRequest(
        httpVerbToUseForCreate = Bundle.HTTPVerb.PUT,
        httpVerbToUseForUpdate = updatePayloadType.toBundleHttpVerb(),
        bundleSize = bundleSize,
      ),
    )

  /**
   * Fetches all changes for a single resource, generates a patch for that resource, and creates a
   * single POST request with the patch.
   */
  data class SingleResourcePost(val updatePayloadType: UpdatePayloadType) :
    UploadStrategy(
      LocalChangesFetchMode.PerResource,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.BundleRequest(
        httpVerbToUseForCreate = Bundle.HTTPVerb.POST,
        httpVerbToUseForUpdate = updatePayloadType.toBundleHttpVerb(),
      ),
    )

  /*
   * All the [UploadStrategy]s below this line are still in progress and not available as of now. As
   * and when an [UploadStrategy] is implemented, it should be moved above this comment section and
   * made non private.
   */

  /**
   * Not yet implemented - Fetches all local changes, generates one patch per resource, and uploads
   * them in a single bundled POST request.
   */
  private data class AllChangesSquashedBundlePost(
    val updatePayloadType: UpdatePayloadType,
    val bundleSize: Int,
  ) :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.BundleRequest(
        Bundle.HTTPVerb.POST,
        updatePayloadType.toBundleHttpVerb(),
      ),
    )

  /**
   * Not yet implemented - Fetches the earliest local change, generates a patch for that change, and
   * creates a single PUT request with the patch.
   */
  private data class SingleChangePut(val updatePayloadType: UpdatePayloadType) :
    UploadStrategy(
      LocalChangesFetchMode.EarliestChange,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, updatePayloadType.toHttpVerb()),
    )

  /**
   * Not yet implemented - Fetches the earliest local change, generates a patch for that change, and
   * creates a single POST request with the patch.
   */
  private data class SingleChangePost(val updatePayloadType: UpdatePayloadType) :
    UploadStrategy(
      LocalChangesFetchMode.EarliestChange,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, updatePayloadType.toHttpVerb()),
    )

  /**
   * Not yet implemented - Fetches all changes for a single resource, generates a patch for that
   * resource, and creates a single PUT request with the patch.
   */
  private data class SingleResourcePut(val updatePayloadType: UpdatePayloadType) :
    UploadStrategy(
      LocalChangesFetchMode.PerResource,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, updatePayloadType.toHttpVerb()),
    )

  /**
   * Not yet implemented - Fetches all local changes, generates a patch for each individual change,
   * and creates a single bundle POST request containing all the patches.
   */
  private data class AllChangesBundlePut(
    val updatePayloadType: UpdatePayloadType,
    val bundleSize: Int,
  ) :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.BundleRequest(
        Bundle.HTTPVerb.PUT,
        updatePayloadType.toBundleHttpVerb(),
      ),
    )

  /**
   * Not yet implemented - Fetches all local changes, generates a patch for each individual change,
   * and creates a single bundle POST request containing all the patches.
   */
  private data class AllChangesBundlePost(
    val updatePayloadType: UpdatePayloadType,
    val bundleSize: Int,
  ) :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.BundleRequest(
        Bundle.HTTPVerb.POST,
        updatePayloadType.toBundleHttpVerb(),
      ),
    )
}

enum class UpdatePayloadType {
  PATCH,
  FULL,
  ;

  fun toBundleHttpVerb() =
    when (this) {
      PATCH -> Bundle.HTTPVerb.PATCH
      FULL -> Bundle.HTTPVerb.PUT
    }

  fun toHttpVerb() =
    when (this) {
      PATCH -> HttpVerb.PATCH
      FULL -> HttpVerb.PUT
    }
}
