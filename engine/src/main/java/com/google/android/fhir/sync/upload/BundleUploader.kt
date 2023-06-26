/*
 * Copyright 2022 Google LLC
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
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.Request
import com.google.android.fhir.sync.ResourceSyncException
import com.google.android.fhir.sync.UploadResult
import com.google.android.fhir.sync.Uploader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

/** [Uploader] implementation to work with Fhir [Bundle]. */
internal class BundleUploader(
  private val dataSource: DataSource,
  private val bundleGenerator: TransactionBundleGenerator,
  private val localChangesPaginator: LocalChangesPaginator
) : Uploader {

  override suspend fun upload(localChanges: List<LocalChange>): Flow<UploadResult> = flow {
    val total = localChanges.size
    var completed = 0

    emit(UploadResult.Started(total))

    bundleGenerator.generate(localChangesPaginator.page(localChanges)).forEach {
      (bundle, localChangeTokens) ->
      try {
        val response = dataSource.upload(Request.of(bundle))

        completed += bundle.entry.size
        emit(getUploadResult(response, localChangeTokens, total, completed))
      } catch (e: Exception) {
        Timber.e(e)
        emit(UploadResult.Failure(ResourceSyncException(ResourceType.Bundle, e)))
      }
    }
  }

  private fun getUploadResult(
    response: Resource,
    localChangeTokens: List<LocalChangeToken>,
    total: Int,
    completed: Int
  ) =
    when {
      response is Bundle && response.type == Bundle.BundleType.TRANSACTIONRESPONSE -> {
        UploadResult.Success(
          LocalChangeToken(localChangeTokens.flatMap { it.ids }),
          response,
          total,
          completed
        )
      }
      response is OperationOutcome && response.issue.isNotEmpty() -> {
        UploadResult.Failure(
          ResourceSyncException(
            ResourceType.Bundle,
            FHIRException(response.issueFirstRep.diagnostics)
          )
        )
      }
      else -> {
        UploadResult.Failure(
          ResourceSyncException(
            ResourceType.Bundle,
            FHIRException("Unknown response for ${response.resourceType}")
          )
        )
      }
    }
}
