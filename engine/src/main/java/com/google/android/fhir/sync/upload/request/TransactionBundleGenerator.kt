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

/** Generates list of [BundleUploadRequest] of type Transaction [Bundle] from the [Patch]es */
internal class TransactionBundleGenerator(
  private val generatedBundleSize: Int,
  private val useETagForUpload: Boolean,
  private val getBundleEntryComponentGeneratorForPatch:
    (patch: Patch, useETagForUpload: Boolean) -> BundleEntryComponentGenerator,
) : UploadRequestGenerator {

  override fun generateUploadRequests(
    mappedPatches: List<PatchMapping>,
  ): List<BundleUploadRequestMapping> {
    return mappedPatches.chunked(generatedBundleSize).map { patchList ->
      generateBundleRequest(patchList).let { mappedBundleRequest ->
        BundleUploadRequestMapping(
          splitLocalChanges = mappedBundleRequest.first,
          generatedRequest = mappedBundleRequest.second,
        )
      }
    }
  }

  private fun generateBundleRequest(
    patches: List<PatchMapping>,
  ): Pair<List<List<LocalChange>>, BundleUploadRequest> {
    val splitLocalChanges = mutableListOf<List<LocalChange>>()
    val bundleRequest =
      Bundle().apply {
        type = Bundle.BundleType.TRANSACTION
        patches.forEach {
          splitLocalChanges.add(it.localChanges)
          this.addEntry(
            getBundleEntryComponentGeneratorForPatch(it.generatedPatch, useETagForUpload)
              .getEntry(it.generatedPatch),
          )
        }
      }
    return splitLocalChanges to
      BundleUploadRequest(
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
      generatedBundleSize: Int = 500,
      useETagForUpload: Boolean = true,
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

      return TransactionBundleGenerator(generatedBundleSize, useETagForUpload) { patch, useETag ->
        when (patch.type) {
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
