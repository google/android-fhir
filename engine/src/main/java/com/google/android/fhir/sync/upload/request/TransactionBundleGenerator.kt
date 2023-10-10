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
import com.google.android.fhir.LocalChange.Type
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.sync.BundleUploadRequest
import org.hl7.fhir.r4.model.Bundle

/**
 * Generates list of [BundleUploadRequest] with Transaction [Bundle] and [LocalChangeToken]s
 * associated with the resources present in the transaction bundle.
 */
class TransactionBundleGenerator(
  private val generatedBundleSize: Int,
  private val useETagForUpload: Boolean,
  private val getBundleEntryComponentGeneratorForLocalChangeType:
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
        localChanges
          .filterNot { it.type == Type.NO_OP }
          .forEach {
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
      useETagForUpload: Boolean
    ): TransactionBundleGenerator {

      val createFunction =
        createMapping[httpVerbToUseForCreate]
          ?: throw IllegalArgumentException(
            "Creation using $httpVerbToUseForCreate is not supported."
          )

      val updateFunction =
        updateMapping[httpVerbToUseForUpdate]
          ?: throw IllegalArgumentException(
            "Update using $httpVerbToUseForUpdate is not supported."
          )

      return TransactionBundleGenerator(generatedBundleSize, useETagForUpload) { type, useETag ->
        when (type) {
          Type.INSERT -> createFunction(useETag)
          Type.UPDATE -> updateFunction(useETag)
          Type.DELETE -> HttpDeleteEntryComponentGenerator(useETag)
          Type.NO_OP ->
            error("NO_OP type represents a no-operation and is not mapped to an HTTP operation.")
        }
      }
    }

    private fun putForCreateBasedBundleComponentMapper(
      useETagForUpload: Boolean
    ): BundleEntryComponentGenerator = HttpPutForCreateEntryComponentGenerator(useETagForUpload)

    private fun patchForUpdateBasedBundleComponentMapper(
      useETagForUpload: Boolean
    ): BundleEntryComponentGenerator = HttpPatchForUpdateEntryComponentGenerator(useETagForUpload)
  }
}
