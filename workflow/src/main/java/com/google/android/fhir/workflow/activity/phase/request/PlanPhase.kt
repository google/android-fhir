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

import com.google.android.fhir.workflow.activity.idType
import com.google.android.fhir.workflow.activity.phase.Phase
import com.google.android.fhir.workflow.activity.phase.checkEquals
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Intent
import com.google.android.fhir.workflow.activity.resource.request.Status
import java.util.UUID
import org.opencds.cqf.fhir.api.Repository

/**
 * Provides implementation of the plan phase of the activity flow. See
 * [general-activity-flow](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#general-activity-flow)
 * for more info.
 */
@Suppress(
  "UnstableApiUsage", /* Repository is marked @Beta */
  "UNCHECKED_CAST", /* Cast type erased CPGRequestResource<*> & CPGEventResource<*> to a concrete type classes */
)
class PlanPhase<R : CPGRequestResource<*>>(repository: Repository, r: R) :
  BaseRequestPhase<R>(repository, r) {
  override fun getPhaseName() = Phase.PhaseName.PLAN

  companion object {

    /**
     * Creates a draft plan of type [R] based on the state of the provided [inputPhase]. See
     * [beginPlan](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#plan) for
     * more details.
     */
    internal fun <R : CPGRequestResource<*>> draft(inputPhase: Phase): Result<R> = runCatching {
      check(inputPhase.getPhaseName() == Phase.PhaseName.PROPOSAL) {
        "A Plan can't be created for a flow in ${inputPhase.getPhaseName().name} phase."
      }

      val inputRequest = (inputPhase as Phase.RequestPhase<*>).getRequest()
      check(inputRequest.getIntent() == Intent.PROPOSAL) {
        "Plan can't be created for a request with ${inputRequest.getIntent().code} intent."
      }

      check(inputRequest.getStatus() == Status.ACTIVE) {
        "${inputPhase.getPhaseName().name} request is still in ${inputRequest.getStatus()} status."
      }

      val planRequest: CPGRequestResource<*> =
        inputRequest.copy(
          id = UUID.randomUUID().toString(),
          status = Status.DRAFT,
          intent = Intent.PLAN,
        )

      planRequest as R
    }

    /**
     * Creates a [PlanPhase] of request type [R] based on the [inputPhase] and [draftPlan]. See
     * [endPlan](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#plan) for more
     * details.
     */
    internal fun <R : CPGRequestResource<*>> start(
      repository: Repository,
      inputPhase: Phase,
      draftPlan: R,
    ): Result<PlanPhase<R>> = runCatching {
      check(inputPhase.getPhaseName() == Phase.PhaseName.PROPOSAL) {
        "A Plan can't be started for a flow in ${inputPhase.getPhaseName().name} phase."
      }

      val currentPhase = inputPhase as Phase.RequestPhase<*>

      val basedOn = draftPlan.getBasedOn()
      require(basedOn != null) { "${draftPlan.resource.resourceType}.basedOn can't be null." }

      require(checkEquals(basedOn, currentPhase.getRequest().asReference())) {
        "Provided draft is not based on the request in current phase."
      }

      val basedOnRequest =
        repository.read(draftPlan.resource.javaClass, basedOn.idType)?.let {
          CPGRequestResource.of(draftPlan, it)
        }
      require(basedOnRequest != null) { "Couldn't find ${basedOn.reference} in the database." }

      require(basedOnRequest.getIntent() == Intent.PROPOSAL) {
        "Plan can't be based on a request with ${basedOnRequest.getIntent()} intent."
      }

      require(basedOnRequest.getStatus() == Status.ACTIVE) {
        "Plan can't be based on a request with ${basedOnRequest.getStatus()} status."
      }

      require(draftPlan.getIntent() == Intent.PLAN) {
        "Input request has '${draftPlan.getIntent().code}' intent."
      }

      require(draftPlan.getStatus() in AllowedStatusForPhaseStart) {
        "Input request is in ${draftPlan.getStatus().name} status."
      }

      basedOnRequest.setStatus(Status.COMPLETED)

      repository.create(draftPlan.resource)
      repository.update(basedOnRequest.resource)
      PlanPhase(repository, draftPlan)
    }
  }
}
