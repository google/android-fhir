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

package com.google.android.fhir.workflow.activity

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import java.util.UUID
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource

/** Defines various transitions for an activity in a computable Clinical Guideline. */
class ActivityFlow private constructor(private val fhirEngine: FhirEngine) {

  private val Reference.searchByIdQuery
    get() = reference.split("/").joinToString("?_id=")

  /**
   * Given an active proposal, plan the proposal The proposal is accepted or rejected by the user,
   * resulting in a plan to perform (or not perform) the activity.
   *
   * val plan = ActivityFlow.with(fhirEngine).plan("CommunicationRequest/12345")
   */

  suspend fun startPlan(proposalId: String): Resource {
    val searchQuery = proposalId.split("/").joinToString("?_id=")
    val request = fhirEngine.search(searchQuery).first().resource
    return startPlan(request)
  }

  suspend fun startPlan(proposal: Resource): Resource {


//    check inputProposal.intent = proposal
//      check inputProposal.status = active
//    var result = new Request(copy from inputProposal)
//    set result.id = null
//    set result.intent = plan
//      set result.status = draft
//      set result.basedOn = referenceTo(inputProposal)

    val inputProposal = Request(proposal)

    check(inputProposal.intent == Request.Intent.PROPOSAL) {
      "Proposal is still in ${inputProposal.intent} state."
    }

    check(inputProposal.status == Request.Status.ACTIVE) {
      "Proposal is still in ${inputProposal.status} status."
    }

    val plan =
      inputProposal.copy(
        id = UUID.randomUUID().toString(),
        status = Request.Status.DRAFT,
        intent = Request.Intent.PLAN,
      )

    return plan.resource
  }

  suspend fun endPlan(plan: Resource) {
    val inputPlan = Request(plan)
    val basedOn = inputPlan.getBasedOn()
    check(basedOn != null) {
      "${plan.resourceType}.basedOn shouldn't be null"
    }

    val basedOnProposal = fhirEngine.search(basedOn.searchByIdQuery).firstOrNull()?.resource?.let { Request(it) }
    check(basedOnProposal != null) {
      "Couldn't find ${basedOn.searchByIdQuery} in the database."
    }

    check(basedOnProposal.intent == Request.Intent.PROPOSAL) {
      "Proposal is still in ${basedOnProposal.intent} state."
    }

    check(basedOnProposal.status == Request.Status.ACTIVE) {
      "Proposal is still in ${basedOnProposal.status} status."
    }

    check(inputPlan.intent == Request.Intent.PLAN) {
      "Proposal is still in ${basedOnProposal.intent} state."
    }

    check(inputPlan.status == Request.Status.DRAFT || inputPlan.status == Request.Status.ACTIVE) {
      "Proposal is still in ${basedOnProposal.status} status."
    }

    basedOnProposal.setStatus(Request.Status.COMPLETED)

    fhirEngine.create(inputPlan.resource)
    fhirEngine.update(basedOnProposal.resource)
  }

  /**
   * Given an active proposal or plan, order the proposal The plan is authorized by an appropriately
   * qualified user, resulting in an order to perform (or not perform) the activity.
   * ```
   * val order = ActivityFlow.with(fhirEngine).order("CommunicationRequest/6789")
   * ```
   */
  suspend fun startOrder(planId: String): Resource {
    val searchQuery = planId.split("/").joinToString("?_id=")
    val request = fhirEngine.search(searchQuery).first().resource
    return startOrder(request)
  }

  suspend fun startOrder(request: Resource): Resource {

    val inputRequest = Request(request)
    check(inputRequest.intent == Request.Intent.PROPOSAL || inputRequest.intent == Request.Intent.PLAN) {
      "Plan is still in ${inputRequest.intent} state."
    }

    check(inputRequest.status == Request.Status.ACTIVE) { "Plan is still in ${inputRequest.status} status." }

    val order =
      inputRequest.copy(
        UUID.randomUUID().toString(),
        Request.Status.DRAFT,
        Request.Intent.ORDER,
      )
    return order.resource
  }

  suspend fun endOrder(order: Resource) {
//    check inputOrder.basedOn is not null
//    var basedOn = engine.get(inputOrder.basedOn)
//    check basedOn.intent in { proposal | plan }
//    check basedOn.status = active
//      check inputOrder.status in { draft | active }
//    check inputOrder.intent = order
//      set basedOn.status = completed
//      try
//        engine.save(inputOrder)
//        engine.save(basedOn)
//        commit


    val inputOrder = Request(order)
    val basedOn = inputOrder.getBasedOn()
    check(basedOn != null) {
      "${order.resourceType}.basedOn shouldn't be null"
    }

    val basedOnResource = fhirEngine.search(basedOn.searchByIdQuery).firstOrNull()?.resource?.let { Request(it) }
    check(basedOnResource != null) {
      "Couldn't find $basedOn in the database."
    }


    check(basedOnResource.intent == Request.Intent.PROPOSAL || basedOnResource.intent == Request.Intent.PLAN) {
      "Proposal is still in ${basedOnResource.intent} state."
    }

    check(basedOnResource.status == Request.Status.ACTIVE) {
      "Proposal is still in ${basedOnResource.status} status."
    }

    check(inputOrder.intent == Request.Intent.ORDER) {
      "Proposal is still in ${basedOnResource.intent} state."
    }

    check(inputOrder.status == Request.Status.DRAFT || inputOrder.status == Request.Status.ACTIVE) {
      "Proposal is still in ${basedOnResource.status} status."
    }

    basedOnResource.setStatus(Request.Status.COMPLETED)

    fhirEngine.create(inputOrder.resource)
    fhirEngine.update(basedOnResource.resource)
  }

  /**
   * Given an active order, perform the event. The order is fulfilled through actually performing
   * the activity.
   *
   * val communicationEvent = ActivityFlow.with(fhirEngine).perform("CommunicationRequest/6789")
   */
  suspend fun startPerform(requestId: String): Resource {
    // TODO : End the order by marking completed.
    val searchQuery = requestId.split("/").joinToString("?_id=")
    val request = fhirEngine.search(searchQuery).first().resource
    val order = Request(request)
    check(order.intent == Request.Intent.ORDER) { "Order is still in ${order.intent} state." }

    check(order.status == Request.Status.ACTIVE) { "Order is still in ${order.status} status." }
    val event = order.createEventResource(fhirEngine)
    event.setStatus(Event.Status.PREPARATION)
    event.setBasedOn(order)
    return event.resource
  }

  suspend fun endPerform(event: Resource) {

    val inputEvent = Event(event)
    val basedOn = inputEvent.getBasedOn()
    check(basedOn != null) {
      "${event.resourceType}.basedOn shouldn't be null"
    }

    val basedOnResource = fhirEngine.search(basedOn.searchByIdQuery).firstOrNull()?.resource?.let { Request(it) }
    check(basedOnResource != null) {
      "Couldn't find $basedOn in the database."
    }

    check(basedOnResource.intent == Request.Intent.ORDER) {
      "Proposal is still in ${basedOnResource.intent} state."
    }

    check(basedOnResource.status == Request.Status.ACTIVE) {
      "Proposal is still in ${basedOnResource.status} status."
    }

    check(inputEvent.getStatus() == Event.Status.PREPARATION || inputEvent.getStatus() == Event.Status.INPROGRESS) {
      "Proposal is still in ${basedOnResource.status} status."
    }

    basedOnResource.setStatus(Request.Status.COMPLETED)

    fhirEngine.create(inputEvent.resource)
    fhirEngine.update(basedOnResource.resource)

  }

  companion object {
    fun with(fhirEngine: FhirEngine): ActivityFlow = ActivityFlow(fhirEngine)
  }
}
