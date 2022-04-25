/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.sync

import com.google.android.fhir.DownloadedResource
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.logicalId
import kotlinx.coroutines.flow.emptyFlow
import org.hl7.fhir.r4.model.Resource

/**
 * Processes [Resource]s downloaded from the server and resolves conflicts if any with the local
 * changes. This processor depends on provided [ConflictResolver] to make the resolution.
 */
internal class ResourceConflictProcessor(
  private val engine: FhirEngine,
  private val resolver: ConflictResolver
) {

  /**
   * Processes the [downloadedResources] and resolves conflicts if any via provided [resolver].
   * @return Returns a List of [DownloadedResource] in the same order as [Resource]s were provided
   * in the [downloadedResources].
   */
  suspend fun process(downloadedResources: List<Resource>): List<DownloadedResource> {

    val squashedLocalChanges = mutableListOf<SquashedLocalChange>()
    engine.syncUpload {
      squashedLocalChanges.addAll(it)
      emptyFlow()
    }

    val conflictingResourceIds: Set<String> =
      downloadedResources
        .map { it.logicalId }
        .toSet()
        .intersect(squashedLocalChanges.map { it.localChange.resourceId }.toSet())
    return downloadedResources.map { remote ->
      if (conflictingResourceIds.contains(remote.logicalId)) {
        val localResource = engine.get(remote.resourceType, remote.logicalId)
        when (val resolveResult = resolver.resolve(localResource, remote)) {
          is ConflictResolutionResult.Resolved ->
            DownloadedResource.ConflictingWithLocalChange(remote, resolveResult.resolved)
        }
      } else {
        DownloadedResource.NonConflictingWithLocalChange(remote)
      }
    }
  }
}
