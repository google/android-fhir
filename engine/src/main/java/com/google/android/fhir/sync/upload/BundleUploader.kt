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
import com.google.android.fhir.ResourceForDatabaseToSave
import com.google.android.fhir.ResourceType
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.ResourceSyncException
import com.google.android.fhir.sync.UploadResult
import com.google.android.fhir.sync.UploadWorkManager
import com.google.android.fhir.sync.Uploader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.instance.model.api.IAnyResource

/** [Uploader] implementation to work with Fhir [Bundle]. */
internal class BundleUploader(
  private val dataSource: DataSource,
  private val bundleGenerator: UploadWorkManager,
  private val functionToGetResources: (IAnyResource) -> ResourceForDatabaseToSave?,
  private val localChangesPaginator: LocalChangesPaginator,
) : Uploader {

  override suspend fun upload(
    localChanges: List<LocalChange>,
  ): Flow<UploadResult> = flow {
    bundleGenerator.generate(localChangesPaginator.page(localChanges)).forEach {
      (bundle, localChangeTokens) ->
      try {
        val response = dataSource.upload(bundle)
        val localChangeToken = bundleGenerator.getUploadResult(response, localChangeTokens)
        emit(UploadResult.Success(localChangeToken, response))
      } catch (e: Exception) {
        emit(UploadResult.Failure(ResourceSyncException(ResourceType.Bundle, e)))
      }
    }
  }

  override suspend fun getResourceTypeToSave(): (IAnyResource) -> ResourceForDatabaseToSave? {
    return functionToGetResources
  }
}
