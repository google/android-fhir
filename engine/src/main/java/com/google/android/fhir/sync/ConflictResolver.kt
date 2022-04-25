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

import org.hl7.fhir.r4.model.Resource

/**
 * Defines the strategy to resolve conflicts between the client and remote changes in a Resource.
 */
interface ConflictResolver {
  /**
   * @param local
   * - The modified resource on the client.
   * @param remote
   * - The latest downloaded version of the remote resource on the client.
   *
   * @return Resolved resource.
   */
  suspend fun resolve(local: Resource, remote: Resource): ConflictResolutionResult
}

/**
 * Contains the result of the conflict resolution. For now, [Resolved] is the only acceptable result
 * and the expectation is that the client will resolve each and every conflict in-flight that may
 * arise during the sync process. There is no way for the client application to abort or defer the
 * conflict resolution to a later time.
 */
sealed class ConflictResolutionResult {
  data class Resolved(val resolved: Resource) : ConflictResolutionResult()
}

/**
 * Implementation of [ConflictResolver] where the [local] change in the resource is accepted as the
 * final result for the resolution and the [remote] resource downloaded from the server is simply
 * discarded.
 */
object AcceptOursStrategyBasedConflictResolver : ConflictResolver {
  override suspend fun resolve(local: Resource, remote: Resource): ConflictResolutionResult {
    return ConflictResolutionResult.Resolved(local)
  }
}

/**
 * Implementation of [ConflictResolver] where the [remote] resource downloaded from the server is
 * accepted as the final result for the resolution and the [local] change is simply discarded.
 */
object AcceptTheirsStrategyBasedConflictResolver : ConflictResolver {
  override suspend fun resolve(local: Resource, remote: Resource): ConflictResolutionResult {
    return ConflictResolutionResult.Resolved(remote)
  }
}
