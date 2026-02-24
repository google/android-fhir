/*
 * Copyright 2023-2026 Google LLC
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
 * ```kotlin
 * override fun getUploadStrategy(): UploadStrategy =
 *   UploadStrategy.forBundleRequest(methodForCreate = HttpCreateMethod.PUT, methodForUpdate = HttpUpdateMethod.PATCH, squash = true, bundleSize = 500)
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
class UploadStrategy
private constructor(
  internal val localChangesFetchMode: LocalChangesFetchMode,
  internal val patchGeneratorMode: PatchGeneratorMode,
  internal val requestGeneratorMode: UploadRequestGeneratorMode,
) {
  companion object {
    /**
     * Creates an [UploadStrategy] for bundling changes into a single request.
     *
     * This strategy fetches all local changes, generates a single patch per resource (squashing
     * multiple changes to the same resource if applicable), and bundles them into a single HTTP
     * request for uploading to the server.
     *
     * Note: Currently, only the `squash = true` scenario is supported. When `squash = false`, the
     * bundle request would need to support chunking to accommodate multiple changes for the same
     * resource. This functionality is not yet implemented.
     *
     * @param methodForCreate The HTTP method to use for creating new resources (PUT or POST).
     * @param methodForUpdate The HTTP method to use for updating existing resources (PUT or PATCH).
     * @param squash Whether to combine multiple changes to the same resource into a single update.
     *   Only `true` is supported currently.
     * @param bundleSize The maximum number of resources to include in a single bundle.
     * @return An [UploadStrategy] configured for bundle requests.
     */
    fun forBundleRequest(
      methodForCreate: HttpCreateMethod,
      methodForUpdate: HttpUpdateMethod,
      squash: Boolean,
      bundleSize: Int,
    ): UploadStrategy {
      if (methodForUpdate == HttpUpdateMethod.PUT) {
        throw NotImplementedError("PUT for UPDATE not supported yet.")
      }
      if (!squash) {
        throw NotImplementedError("No squashing with bundle uploading not supported yet.")
      }
      return UploadStrategy(
        localChangesFetchMode = LocalChangesFetchMode.AllChanges,
        patchGeneratorMode = PatchGeneratorMode.PerResource,
        requestGeneratorMode =
          UploadRequestGeneratorMode.BundleRequest(
            methodForCreate.toBundleHttpVerb(),
            methodForUpdate.toBundleHttpVerb(),
            bundleSize,
          ),
      )
    }

    /**
     * Creates an [UploadStrategy] for sending individual requests for each change.
     *
     * This strategy can either fetch all changes or only the earliest change for each resource,
     * generate patches per resource or per change, and send individual HTTP requests for each
     * change.
     *
     * Note: PUT for update with squash set as false is not supported as that would require storing
     * full resource for each change.
     *
     * @param methodForCreate The HTTP method to use for creating new resources (PUT or POST).
     * @param methodForUpdate The HTTP method to use for updating existing resources (PUT or PATCH).
     * @param squash Whether to squash multiple changes to the same resource into a single update.
     *   If `true`, all changes for a resource are fetched and patches are generated per resource.
     *   If `false`, only the earliest change is fetched and patches are generated per change.
     * @return An [UploadStrategy] configured for individual requests.
     */
    fun forIndividualRequest(
      methodForCreate: HttpCreateMethod,
      methodForUpdate: HttpUpdateMethod,
      squash: Boolean,
    ): UploadStrategy {
      if (methodForUpdate == HttpUpdateMethod.PUT) {
        throw NotImplementedError("PUT for UPDATE not supported yet.")
      }
      require(methodForUpdate != HttpUpdateMethod.PUT || squash) {
        "Http method PUT not supported for UPDATE with squash set as false."
      }
      return UploadStrategy(
        localChangesFetchMode =
          if (squash) LocalChangesFetchMode.PerResource else LocalChangesFetchMode.EarliestChange,
        patchGeneratorMode =
          if (squash) PatchGeneratorMode.PerResource else PatchGeneratorMode.PerChange,
        requestGeneratorMode =
          UploadRequestGeneratorMode.UrlRequest(
            methodForCreate.toHttpVerb(),
            methodForUpdate.toHttpVerb(),
          ),
      )
    }
  }
}

enum class HttpCreateMethod {
  PUT,
  POST,
  ;

  fun toBundleHttpVerb() =
    when (this) {
      PUT -> Bundle.HTTPVerb.PUT
      POST -> Bundle.HTTPVerb.POST
    }

  fun toHttpVerb() =
    when (this) {
      PUT -> HttpVerb.PUT
      POST -> HttpVerb.POST
    }
}

enum class HttpUpdateMethod {
  PUT,
  PATCH,
  ;

  fun toBundleHttpVerb() =
    when (this) {
      PUT -> Bundle.HTTPVerb.PUT
      PATCH -> Bundle.HTTPVerb.PATCH
    }

  fun toHttpVerb() =
    when (this) {
      PUT -> HttpVerb.PUT
      PATCH -> HttpVerb.PATCH
    }
}
