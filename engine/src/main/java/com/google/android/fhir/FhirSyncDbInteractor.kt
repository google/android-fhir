/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir

import com.google.android.fhir.db.Database
import com.google.android.fhir.sync.ConflictResolver
import com.google.android.fhir.sync.Resolved
import com.google.android.fhir.sync.download.DownloadState
import com.google.android.fhir.sync.upload.LocalChangeFetcher
import com.google.android.fhir.sync.upload.ResourceConsolidator
import com.google.android.fhir.sync.upload.UploadRequestResult
import org.hl7.fhir.r4.model.Resource

internal interface FhirSyncDbInteractor {
  suspend fun getLocalChanges(): List<LocalChange>

  suspend fun consolidateUploadResult(uploadRequestResult: UploadRequestResult)

  suspend fun getLocalChangesCount(): Int

  suspend fun consolidateDownloadResult(downloadState: DownloadState)
}

internal class FhirSyncDbInteractorImpl(
  private val database: Database,
  private val localChangeFetcher: LocalChangeFetcher,
  private val resourceConsolidator: ResourceConsolidator,
  private val conflictResolver: ConflictResolver,
) : FhirSyncDbInteractor {
  override suspend fun getLocalChanges() = localChangeFetcher.next()

  override suspend fun getLocalChangesCount() = database.getLocalChangesCount()

  override suspend fun consolidateUploadResult(uploadRequestResult: UploadRequestResult) {
    resourceConsolidator.consolidate(uploadRequestResult)
  }

  override suspend fun consolidateDownloadResult(downloadState: DownloadState) {
    when (downloadState) {
      is DownloadState.Started -> {
        /**  */
      }
      is DownloadState.Success -> {
        database.withTransaction {
          val resolved =
            resolveConflictingResources(
              downloadState.resources,
              getConflictingResourceIds(downloadState.resources),
              conflictResolver,
            )
          database.insertSyncedResources(downloadState.resources)
          saveResolvedResourcesToDatabase(resolved)
        }
      }
      is DownloadState.Failure -> {}
    }
  }

  private suspend fun saveResolvedResourcesToDatabase(resolved: List<Resource>?) {
    resolved?.let {
      database.deleteUpdates(it)
      database.update(*it.toTypedArray())
    }
  }

  private suspend fun resolveConflictingResources(
    resources: List<Resource>,
    conflictingResourceIds: Set<String>,
    conflictResolver: ConflictResolver,
  ) =
    resources
      .filter { conflictingResourceIds.contains(it.logicalId) }
      .map { conflictResolver.resolve(database.select(it.resourceType, it.logicalId), it) }
      .filterIsInstance<Resolved>()
      .map { it.resolved }
      .takeIf { it.isNotEmpty() }

  private suspend fun getConflictingResourceIds(resources: List<Resource>) =
    resources
      .map { it.logicalId }
      .toSet()
      .intersect(database.getAllLocalChanges().map { it.resourceId }.toSet())
}
