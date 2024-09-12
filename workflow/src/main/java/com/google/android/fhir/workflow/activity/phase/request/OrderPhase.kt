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

class OrderPhase<R : CPGRequestResource<*>>(repository: Repository, r: R) :
  BaseRequestPhase<R>(repository, r) {

  override fun getPhaseName() = Phase.PhaseName.ORDER

  companion object {

    internal fun <R : CPGRequestResource<*>> draft(inputPhase: Phase): Result<R> = runCatching {
      check(inputPhase is Phase.RequestPhase<*>) {
        "The api can't be called from ${inputPhase.getPhaseName().name}."
      }

      val currentPhase: Phase.RequestPhase<R> = inputPhase as Phase.RequestPhase<R>

      check(
        currentPhase.getPhaseName() in
          listOf(
            Phase.PhaseName.PROPOSAL,
            Phase.PhaseName.PLAN,
          ),
      ) {
        "Order can't be created from ${currentPhase.getPhaseName().name}"
      }

      val inputRequest = (currentPhase as BaseRequestPhase<*>).getRequest()

      check(inputRequest.getStatus() == Status.ACTIVE) {
        "Plan is still in ${inputRequest.getStatus()} status."
      }

      inputRequest.copy(
        UUID.randomUUID().toString(),
        Status.DRAFT,
        Intent.ORDER,
      ) as R
    }

    fun <R : CPGRequestResource<*>> start(
      repository: Repository,
      inputPhase: Phase,
      inputOrder: R,
    ): Result<OrderPhase<R>> = runCatching {
      check(inputPhase is Phase.RequestPhase<*>) {
        "The api can't be called from ${inputPhase.getPhaseName().name}."
      }

      val currentPhase: Phase.RequestPhase<R> = inputPhase as Phase.RequestPhase<R>

      val basedOn = inputOrder.getBasedOn()
      require(basedOn != null) { "${inputOrder.resource.resourceType}.basedOn shouldn't be null" }

      require(
        equals(
          basedOn,
          currentPhase.getRequest().asReference(),
        ),
      ) {
        "Provided draft is not based on current ${currentPhase.getPhaseName().name}."
      }

      val basedOnResource =
        repository.read(inputOrder.resource.javaClass, basedOn.idType)?.let {
          CPGRequestResource.of(inputOrder, it)
        }

      require(basedOnResource != null) { "Couldn't find $basedOn in the database." }

      //      check(
      //        basedOnResource.getIntent() == Intent.PROPOSAL || basedOnResource.getIntent() ==
      // Intent.PLAN,
      //      ) {
      //        "Proposal is still in ${basedOnResource.getIntent()} state."
      //      }

      require(basedOnResource.getStatus() == Status.ACTIVE) {
        "Proposal is still in ${basedOnResource.getStatus()} status."
      }

      require(inputOrder.getIntent() == Intent.ORDER) {
        "Proposal is still in ${inputOrder.getIntent()} state."
      }

      require(
        inputOrder.getStatus() == Status.DRAFT || inputOrder.getStatus() == Status.ACTIVE,
      ) {
        "Proposal is still in ${inputOrder.getStatus()} status."
      }

      basedOnResource.setStatus(Status.COMPLETED)

      repository.create(inputOrder.resource)
      repository.update(basedOnResource.resource)
      OrderPhase(repository, inputOrder)
    }
  }
}
