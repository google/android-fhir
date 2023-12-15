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

import com.google.android.fhir.LocalChange
import com.google.android.fhir.sync.upload.patch.Patch
import com.google.android.fhir.sync.upload.patch.PatchMapping
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.codesystems.HttpVerb

/**
 * Generator that generates [UploadRequest]s from the [Patch]es present in the
 * [List<[PatchMapping]>]. Any implementation of this generator is expected to output
 * [List<[UploadRequestMapping]>] which maps [UploadRequest] to the corresponding [LocalChange]s it
 * was generated from.
 */
internal interface UploadRequestGenerator {
  /** Generates a list of [UploadRequestMapping] from the [PatchMapping]s */
  fun generateUploadRequests(
    mappedPatches: List<PatchMapping>,
  ): List<UploadRequestMapping>
}

/** Mode to decide the type of [UploadRequest] that needs to be generated */
internal sealed class UploadRequestGeneratorMode {
  data class UrlRequest(
    val httpVerbToUseForCreate: HttpVerb,
    val httpVerbToUseForUpdate: HttpVerb,
  ) : UploadRequestGeneratorMode()

  data class BundleRequest(
    val httpVerbToUseForCreate: Bundle.HTTPVerb,
    val httpVerbToUseForUpdate: Bundle.HTTPVerb,
  ) : UploadRequestGeneratorMode()
}

internal object UploadRequestGeneratorFactory {
  fun byMode(
    mode: UploadRequestGeneratorMode,
  ): UploadRequestGenerator =
    when (mode) {
      is UploadRequestGeneratorMode.UrlRequest ->
        UrlRequestGenerator.getGenerator(mode.httpVerbToUseForCreate, mode.httpVerbToUseForUpdate)
      is UploadRequestGeneratorMode.BundleRequest ->
        TransactionBundleGenerator.getGenerator(
          mode.httpVerbToUseForCreate,
          mode.httpVerbToUseForUpdate,
        )
    }
}

internal sealed class UploadRequestMapping(
  open val generatedRequest: UploadRequest,
)

internal data class UrlUploadRequestMapping(
  val localChanges: List<LocalChange>,
  override val generatedRequest: UrlUploadRequest,
) : UploadRequestMapping(generatedRequest)

internal data class BundleUploadRequestMapping(
  val splitLocalChanges: List<List<LocalChange>>,
  override val generatedRequest: BundleUploadRequest,
) : UploadRequestMapping(generatedRequest)
