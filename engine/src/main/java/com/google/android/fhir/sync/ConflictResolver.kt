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

package com.google.android.fhir.sync

import org.hl7.fhir.r4.model.Resource

/** Resolves conflicts between the client and remote changes in a Resource. */
fun interface ConflictResolver {
  /**
   * @param local The modified resource on the client.
   * @param remote The latest version of the resource downloaded from the remote server.
   */
  fun resolve(local: Resource, remote: Resource): ConflictResolutionResult
}

/**
 * Contains the result of the conflict resolution. For now, [Resolved] is the only acceptable result
 * and the expectation is that the client will resolve each and every conflict in-flight that may
 * arise during the sync process. There is no way for the client application to abort or defer the
 * conflict resolution to a later time.
 */
sealed class ConflictResolutionResult

data class Resolved(val resolved: Resource) : ConflictResolutionResult()

/** Accepts the local change and rejects the remote change. */
val AcceptLocalConflictResolver = ConflictResolver { local, _ -> Resolved(local) }

/** Accepts the remote change and rejects the local change. */
val AcceptRemoteConflictResolver = ConflictResolver { _, remote -> Resolved(remote) }
