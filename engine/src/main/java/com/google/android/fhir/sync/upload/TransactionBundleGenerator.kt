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

import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChange.Type
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.sync.BundleUploadRequest
import org.hl7.fhir.r4.model.Bundle

/**
 * Generates list of [BundleUploadRequest] with Transaction [Bundle] and [LocalChangeToken]s
 * associated with the resources present in the transaction bundle.
 */
open class TransactionBundleGenerator(
  private val generatedBundleSize: Int,
  private val useETagForUpload: Boolean,
  val getBundleEntryComponentGeneratorForLocalChangeType:
    (type: Type, useETagForUpload: Boolean) -> BundleEntryComponentGenerator
) : UploadRequestGenerator {

  override fun generateUploadRequests(localChanges: List<LocalChange>): List<BundleUploadRequest> {
    return localChanges
      .chunked(generatedBundleSize)
      .filter { it.isNotEmpty() }
      .map { generateBundleRequest(it) }
  }

  private fun generateBundleRequest(localChanges: List<LocalChange>): BundleUploadRequest {
    val bundleRequest =
      Bundle().apply {
        type = Bundle.BundleType.TRANSACTION
        localChanges.forEach {
          this.addEntry(
            getBundleEntryComponentGeneratorForLocalChangeType(it.type, useETagForUpload)
              .getEntry(it)
          )
        }
      }
    return BundleUploadRequest(
      resource = bundleRequest,
      localChangeToken = LocalChangeToken(localChanges.flatMap { it.token.ids })
    )
  }

  companion object Factory {

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
      useETagForUpload: Boolean
    ): TransactionBundleGenerator {

      return if (httpVerbToUseForCreate == Bundle.HTTPVerb.PUT &&
          httpVerbToUseForUpdate == Bundle.HTTPVerb.PATCH
      ) {
        TransactionBundleGenerator(
          generatedBundleSize,
          useETagForUpload,
          this::putForCreateAndPatchForUpdateBasedBundleComponentMapper
        )
      } else {
        throw IllegalArgumentException(
          "Engine currently supports creation using [PUT] and updates using [PATCH]"
        )
      }
    }

    private fun putForCreateAndPatchForUpdateBasedBundleComponentMapper(
      type: Type,
      useETagForUpload: Boolean
    ): BundleEntryComponentGenerator {
      return when (type) {
        Type.INSERT -> HttpPutForCreateEntryComponentGenerator(useETagForUpload)
        Type.UPDATE -> HttpPatchForUpdateEntryComponentGenerator(useETagForUpload)
        Type.DELETE -> HttpDeleteEntryComponentGenerator(useETagForUpload)
      }
    }
  }
}
