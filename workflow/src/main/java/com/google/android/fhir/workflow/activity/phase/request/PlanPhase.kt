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
import com.google.android.fhir.workflow.activity.phase.equals
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Intent
import com.google.android.fhir.workflow.activity.resource.request.Status
import java.util.UUID
import org.opencds.cqf.fhir.api.Repository

class PlanPhase<R : CPGRequestResource<*>>(repository: Repository, r: R) :
  BaseRequestPhase<R>(repository, r) {
  override fun getPhaseName() = Phase.PhaseName.PLAN

  companion object {

    internal fun <R : CPGRequestResource<*>> draft(inputPhase: Phase): Result<R> = runCatching {
      check(inputPhase is Phase.RequestPhase<*>) {
        "The api can't be called from ${inputPhase.getPhaseName().name}."
      }

      val currentPhase: Phase.RequestPhase<R> = inputPhase as Phase.RequestPhase<R>

      check(currentPhase.getPhaseName() == Phase.PhaseName.PROPOSAL) {
        "A Plan can't be created for a flow in ${currentPhase.getPhaseName()} phase. "
      }

      val inputProposal = (currentPhase as BaseRequestPhase<*>).getRequest()
      check(inputProposal.getIntent() == Intent.PROPOSAL) {
        "A Plan can't be created for a request with ${inputProposal.getIntent().name} intent."
      }

      check(inputProposal.getStatus() == Status.ACTIVE) {
        "Current request is still in ${inputProposal.getStatus()} status."
      }

      val planRequest: CPGRequestResource<*> =
        inputProposal.copy(
          id = UUID.randomUUID().toString(),
          status = Status.DRAFT,
          intent = Intent.PLAN,
        )

      planRequest as R
    }

    internal fun <R : CPGRequestResource<*>> start(
      repository: Repository,
      inputPhase: Phase,
      inputPlan: R,
    ): Result<PlanPhase<R>> = runCatching {
      check(inputPhase is Phase.RequestPhase<*>) {
        "The api can't be called from ${inputPhase.getPhaseName().name}."
      }

      val currentPhase: Phase.RequestPhase<R> = inputPhase as Phase.RequestPhase<R>

      val basedOn = inputPlan.getBasedOn()
      require(basedOn != null) { "${inputPlan.resource.resourceType}.basedOn shouldn't be null" }

      require(equals(basedOn, currentPhase.getRequest().asReference())) {
        "Provided draft is not based on current plan."
      }

      val basedOnProposal =
        repository.read(inputPlan.resource.javaClass, basedOn.idType)?.let {
          CPGRequestResource.of(inputPlan, it)
        }
      require(basedOnProposal != null) { "Couldn't find ${basedOn.reference} in the database." }

      require(basedOnProposal.getIntent() == Intent.PROPOSAL) {
        "Plan request can't be based on request with ${basedOnProposal.getIntent()} intent."
      }

      require(basedOnProposal.getStatus() == Status.ACTIVE) {
        "Plan request can't be based on request with ${basedOnProposal.getStatus()} status."
      }

      require(inputPlan.getIntent() == Intent.PLAN) {
        "Input request has '${inputPlan.getIntent().name}' intent instead of 'plan'."
      }

      require(inputPlan.getStatus() == Status.DRAFT || inputPlan.getStatus() == Status.ACTIVE) {
        "Input request is in ${inputPlan.getStatus().name} status."
      }

      basedOnProposal.setStatus(Status.COMPLETED)

      repository.create(inputPlan.resource)
      repository.update(basedOnProposal.resource)
      PlanPhase(repository, inputPlan)
    }
  }
}
