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
import org.hl7.fhir.r4.model.codesystems.HttpVerb

sealed class UploadStrategy(
  internal val localChangesFetchMode: LocalChangesFetchMode,
  internal val patchGeneratorMode: PatchGeneratorMode,
  internal val requestGeneratorMode: UploadRequestGeneratorMode,
) {
  object SingleChangePut :
    UploadStrategy(
      LocalChangesFetchMode.EarliestChange,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, HttpVerb.PATCH),
    )

  object SingleChangePost :
    UploadStrategy(
      LocalChangesFetchMode.EarliestChange,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.POST, HttpVerb.PATCH),
    )

  object SingleResourcePut :
    UploadStrategy(
      LocalChangesFetchMode.PerResource,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, HttpVerb.PATCH),
    )

  object SingleResourcePost :
    UploadStrategy(
      LocalChangesFetchMode.PerResource,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.POST, HttpVerb.PATCH),
    )

  object AllChangesBundlePut :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, HttpVerb.PATCH),
    )

  object AllChangesBundlePost :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerChange,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.POST, HttpVerb.PATCH),
    )

  object AllChangesSquashedBundlePut :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.PUT, HttpVerb.PATCH),
    )

  object AllChangesSquashedBundlePost :
    UploadStrategy(
      LocalChangesFetchMode.AllChanges,
      PatchGeneratorMode.PerResource,
      UploadRequestGeneratorMode.UrlRequest(HttpVerb.POST, HttpVerb.PATCH),
    )
}
