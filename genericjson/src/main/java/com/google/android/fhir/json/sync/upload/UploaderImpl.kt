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

package com.google.android.fhir.json.sync.upload

import com.google.android.fhir.json.LocalChange
import com.google.android.fhir.json.db.impl.dao.LocalChangeToken
import com.google.android.fhir.json.sync.DataSource
import com.google.android.fhir.json.sync.JsonResource
import com.google.android.fhir.json.sync.ResourceSyncException
import com.google.android.fhir.json.sync.UploadResult
import com.google.android.fhir.json.sync.Uploader
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

/** [Uploader] implementation to work with Fhir [Bundle]. */
internal class UploaderImpl(private val dataSource: DataSource) : Uploader {

  val resourceTypeToUpload = "Patient"

  override suspend fun upload(
    localChanges: List<LocalChange>,
  ): Flow<UploadResult> = flow {
    generate(localChanges).forEach { (jsonResource, localChangeTokens) ->
      try {
        val response =
          dataSource.upload(resourceTypeToUpload, jsonResource.id, jsonResource.payload)
        emit(getUploadResult(response, localChangeTokens))
      } catch (e: Exception) {
        emit(UploadResult.Failure(ResourceSyncException(e)))
      }
    }
  }

  private fun getUploadResult(
    response: JSONObject,
    localChangeTokens: List<LocalChangeToken>
  ): UploadResult {
    return UploadResult.Failure(ResourceSyncException(Exception("No imp")))
  }

  private fun generate(
    listOfLocalChanges: List<LocalChange>
  ): List<Pair<JsonResource, List<LocalChangeToken>>> {
    return listOfLocalChanges.map {
      UploadJsonResource(
        it.resourceId,
        it.resourceType,
        it.versionId,
        null,
        JSONObject(it.payload)
      ) to listOf(it.token)
    }
  }
}

data class UploadJsonResource(
  override val id: String,
  override val resourceType: String,
  override val versionId: String?,
  override val lastUpdated: Date?,
  override val payload: JSONObject
) : JsonResource
