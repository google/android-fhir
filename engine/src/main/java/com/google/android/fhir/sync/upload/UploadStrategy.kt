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

import com.google.android.fhir.sync.upload.patch.PatchGeneratorMode
import com.google.android.fhir.sync.upload.request.UploadRequestGeneratorMode
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.codesystems.HttpVerb

/**
 * Strategy to define how to upload the [LocalChange]s to the FHIR server.
 *
 * Each strategy comprises of deciding appropriate modes for [LocalChangeFetcher],
 * [PatchGeneratorMode], [UploadRequestGeneratorMode]. The strategies mentioned here are exhaustive
 * as the different modes for the components mentioned above can only be used together in some
 * specific ways.
 */
sealed class UploadStrategy
private constructor(
  internal val localChangesFetchMode: LocalChangesFetchMode,
  internal val patchGeneratorMode: PatchGeneratorMode,
  internal val requestGeneratorMode: UploadRequestGeneratorMode,
) {

  object AllChangesBundlePut :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.BundleRequest(Bundle.HTTPVerb.PUT, Bundle.HTTPVerb.PATCH),
    )

  object AllChangesSquashedBundlePut :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.BundleRequest(Bundle.HTTPVerb.PUT, Bundle.HTTPVerb.PATCH),
    )

  /**
   * All the [UploadStrategy]s below this line are still in progress and not available as of now. As
   * and when an [UploadStrategy] is implemented, it should be moved above this comment section and
   * made non private.
   */
  private object AllChangesSquashedBundlePost :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.BundleRequest(Bundle.HTTPVerb.POST, Bundle.HTTPVerb.PATCH),
    )

  private object SingleChangePut :
    UploadStrategy(
      LocalChangesFetchMode.EarliestChange,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, HttpVerb.PATCH),
    )

  private object SingleChangePost :
    UploadStrategy(
      LocalChangesFetchMode.EarliestChange,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.POST, HttpVerb.PATCH),
    )

  private object SingleResourcePut :
    UploadStrategy(
      LocalChangesFetchMode.PerResource,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, HttpVerb.PATCH),
    )

  private object SingleResourcePost :
    UploadStrategy(
      LocalChangesFetchMode.PerResource,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.POST, HttpVerb.PATCH),
    )

  private object AllChangesBundlePost :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.BundleRequest(Bundle.HTTPVerb.POST, Bundle.HTTPVerb.PATCH),
    )
}
