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

import com.google.android.fhir.FhirEngine
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
import org.hl7.fhir.r4.model.Bundle.HTTPVerb
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

internal class IndividualRequestUploader(val dataSource: DataSource, val engine: FhirEngine) :
  Uploader {

  override suspend fun upload(localChanges: List<LocalChange>): Flow<UploadResult> = flow {
    localChanges.forEach { localChange ->
      val http =
        when (localChange.type) {
          LocalChange.Type.INSERT -> HTTPVerb.POST
          LocalChange.Type.UPDATE -> HTTPVerb.PUT
          else -> null
        }

      http?.let {
        val resourceToUpload =
          engine.get(ResourceType.valueOf(localChange.resourceType), localChange.resourceId)
        try {
          emit(
            getUploadResult(
              dataSource.upload(Request.of(resourceToUpload, http = http)),
              resourceToUpload.resourceType,
              LocalChangeToken(localChange.token.ids),
              1,
              1
            )
          )
        } catch (e: Exception) {
          Timber.e(e)
          emit(
            UploadResult.Failure(
              ResourceSyncException(ResourceType.valueOf(localChange.resourceType), e)
            )
          )
        }
      }
    }
  }

  private fun getUploadResult(
    response: Resource,
    resourceType: ResourceType,
    localChangeTokens: LocalChangeToken,
    total: Int,
    completed: Int
  ) =
    when {
      response.resourceType == resourceType -> {
        UploadResult.Success(localChangeTokens, response, total, completed)
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
