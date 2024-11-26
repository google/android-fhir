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

package com.google.android.fhir.workflow.activity.phase.request

import com.google.android.fhir.workflow.activity.phase.Phase
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Status
import org.opencds.cqf.fhir.api.Repository

/** Encapsulates the state transitions of a [Phase.RequestPhase]. */
@Suppress(
  "UnstableApiUsage", /* Repository is marked @Beta */
)
abstract class BaseRequestPhase<R : CPGRequestResource<*>>(
  /** Implementation of [Repository] to store / retrieve FHIR resources. */
  private val repository: Repository,
  /**
   * Concrete implementation of sealed [CPGRequestResource] class. e.g. `CPGCommunicationRequest`.
   */
  r: R,
  /** PhaseName of the concrete implementation. */
  private val phaseName: Phase.PhaseName,
) : Phase.RequestPhase<R> {
  internal var request: R = r.copy() as R

  // TODO : Maybe this should return a copy of the resource so that if the user does any changes to
  // this resource, it doesn't affect the state of the flow.
  override fun getRequestResource() = request.copy() as R

  override fun getPhaseName() = phaseName

  override fun suspend(reason: String?) =
    runCatching<Unit> {
      check(request.getStatus() == Status.ACTIVE) {
        " Can't suspend an event with status ${request.getStatusCode()} "
      }

      request.setStatus(Status.ONHOLD, reason)
      repository.update(request.resource)
    }

  override fun resume() =
    runCatching<Unit> {
      check(request.getStatus() == Status.ONHOLD) {
        " Can't resume an event with status ${request.getStatusCode()} "
      }

      request.setStatus(Status.ACTIVE)
      repository.update(request.resource)
    }

  override fun update(r: R) =
    runCatching<Unit> {
      // TODO Add some basic checks to make sure e is update event and not a completely different
      // resource.
      require(r.getStatus() in listOf(Status.DRAFT, Status.ACTIVE)) {
        "Status is ${r.getStatusCode()}"
      }
      repository.update(r.resource)
      request = r
    }

  override fun enteredInError(reason: String?) =
    runCatching<Unit> {
      request.setStatus(Status.ENTEREDINERROR, reason)
      repository.update(request.resource)
    }

  override fun reject(reason: String?) =
    runCatching<Unit> {
      check(request.getStatus() == Status.ACTIVE) {
        " Can't reject an event with status ${request.getStatusCode()} "
      }

      request.setStatus(Status.REVOKED, reason)
      repository.update(request.resource)
    }

  companion object {
    val AllowedStatusForPhaseStart = listOf(Status.DRAFT, Status.ACTIVE)
  }
}
