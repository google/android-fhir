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

package com.google.android.fhir.sync.upload.request

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.codesystems.HttpVerb

/** Mode to decide the type of [UploadRequest] that needs to be generated */
sealed class UploadRequestGeneratorMode {
  data class UrlRequest(
    val httpVerbToUseForCreate: HttpVerb,
    val httpVerbToUseForUpdate: HttpVerb,
  ) : UploadRequestGeneratorMode()

  data class BundleRequest(
    val httpVerbToUseForCreate: Bundle.HTTPVerb,
    val httpVerbToUseForUpdate: Bundle.HTTPVerb,
  ) : UploadRequestGeneratorMode()
}
