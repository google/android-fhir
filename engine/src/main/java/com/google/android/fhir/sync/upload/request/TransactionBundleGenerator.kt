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

import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.sync.BundleUploadRequest
import com.google.android.fhir.sync.upload.patch.Patch
import org.hl7.fhir.r4.model.Bundle

/**
 * Generates list of [BundleUploadRequest] with Transaction [Bundle] and [LocalChangeToken]s
 * associated with the resources present in the transaction bundle.
 */
class TransactionBundleGenerator(
  private val generatedBundleSize: Int,
  private val useETagForUpload: Boolean,
  private val getBundleEntryComponentGeneratorForLocalChangeType:
    (type: Patch.Type, useETagForUpload: Boolean) -> BundleEntryComponentGenerator,
) : UploadRequestGenerator {

  override fun generateUploadRequests(patches: List<Patch>): List<BundleUploadRequest> {
    return patches.chunked(generatedBundleSize).map { generateBundleRequest(it) }
  }

  private fun generateBundleRequest(patches: List<Patch>): BundleUploadRequest {
    val bundleRequest =
      Bundle().apply {
        type = Bundle.BundleType.TRANSACTION
        patches.forEach {
          this.addEntry(
            getBundleEntryComponentGeneratorForLocalChangeType(it.type, useETagForUpload)
              .getEntry(it),
          )
        }
      }
    return BundleUploadRequest(
      resource = bundleRequest,
    )
  }

  companion object Factory {

    private val createMapping =
      mapOf(
        Bundle.HTTPVerb.PUT to this::putForCreateBasedBundleComponentMapper,
      )

    private val updateMapping =
      mapOf(
        Bundle.HTTPVerb.PATCH to this::patchForUpdateBasedBundleComponentMapper,
      )

    fun getDefault(useETagForUpload: Boolean = true, bundleSize: Int = 500) =
      getGenerator(Bundle.HTTPVerb.PUT, Bundle.HTTPVerb.PATCH, bundleSize, useETagForUpload)

    /**
     * Returns a [TransactionBundleGenerator] based on the provided [Bundle.HTTPVerb]s for creating
     * and updating resources. The function may throw an [IllegalArgumentException] if the provided
     * [Bundle.HTTPVerb]s are not supported.
     */
    fun getGenerator(
      httpVerbToUseForCreate: Bundle.HTTPVerb,
      httpVerbToUseForUpdate: Bundle.HTTPVerb,
      generatedBundleSize: Int,
      useETagForUpload: Boolean,
    ): TransactionBundleGenerator {
      val createFunction =
        createMapping[httpVerbToUseForCreate]
          ?: throw IllegalArgumentException(
            "Creation using $httpVerbToUseForCreate is not supported.",
          )

      val updateFunction =
        updateMapping[httpVerbToUseForUpdate]
          ?: throw IllegalArgumentException(
            "Update using $httpVerbToUseForUpdate is not supported.",
          )

      return TransactionBundleGenerator(generatedBundleSize, useETagForUpload) { type, useETag ->
        when (type) {
          Patch.Type.INSERT -> createFunction(useETag)
          Patch.Type.UPDATE -> updateFunction(useETag)
          Patch.Type.DELETE -> HttpDeleteEntryComponentGenerator(useETag)
        }
      }
    }

    private fun putForCreateBasedBundleComponentMapper(
      useETagForUpload: Boolean,
    ): BundleEntryComponentGenerator = HttpPutForCreateEntryComponentGenerator(useETagForUpload)

    private fun patchForUpdateBasedBundleComponentMapper(
      useETagForUpload: Boolean,
    ): BundleEntryComponentGenerator = HttpPatchForUpdateEntryComponentGenerator(useETagForUpload)
  }
}
