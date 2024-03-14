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
import org.hl7.fhir.r4.model.Resource

/** Defines various transitions for an activity in a computable Clinical Guideline. */
class ActivityFlow private constructor(private val fhirEngine: FhirEngine) {

  //  private val requestTypes =
  //    setOf(ResourceType.MedicationRequest, ResourceType.Task, ResourceType.ServiceRequest)

  //  init {
  //    require(request.resourceType in requestTypes ) {
  //      "${request.resourceType} is not supported. Supported types are
  // ${requestTypes.joinToString()}"
  //    }
  //
  //  }

  // Defined http://hl7.org/fhir/uv/cpg/STU1/CodeSystem-cpg-activity-type.html
  // Request
  // https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-lifecycle---request-phases-proposal-plan-order
  // Event
  // https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-lifecycle---event-phase
  // This would come from ActivityDefinition.code e.g
  //  "code" : {
  //    "coding" : [
  //    {
  //      "system" : "http://hl7.org/fhir/uv/cpg/CodeSystem/cpg-activity-type",
  //      "code" : "administer-medication",
  //      "display" : "Administer a medication"
  //    }
  //    ]
  //  }

  /**
   * Given an active proposal, plan the proposal The proposal is accepted or rejected by the user,
   * resulting in a plan to perform (or not perform) the activity.
   *
   * val plan = ActivityFlow.with(fhirEngine).plan("CommunicationRequest/12345")
   */
  suspend fun plan(proposalId: String): Resource {
    // TODO : End the proposal by marking completed.
    val request = fhirEngine.search(proposalId).first().resource
    val proposal = Request(request)

    check(proposal.intent == Request.Intent.PROPOSAL) {
      "Proposal is still in ${proposal.intent} state."
    }

    check(proposal.status == Request.Status.ACTIVE) {
      "Proposal is still in ${proposal.status} status."
    }

    val plan =
      proposal.copy(
        UUID.randomUUID().toString(),
        Request.Status.DRAFT,
        Request.Intent.PLAN,
      )

    fhirEngine.create(plan.request)
    return plan.request
  }

  /**
   * Given an active proposal or plan, order the proposal The plan is authorized by an appropriately
   * qualified user, resulting in an order to perform (or not perform) the activity val order =
   * ActivityFlow.with(fhirEngine).order("CommunicationRequest/6789")
   */
  suspend fun order(planId: String): Resource {
    // TODO : End the plan/proposal by marking completed.
    val request = fhirEngine.search(planId).first().resource
    val plan = Request(request)

    check(plan.intent == Request.Intent.PROPOSAL || plan.intent == Request.Intent.PLAN) {
      "Plan is still in ${plan.intent} state."
    }

    check(plan.status == Request.Status.ACTIVE) { "Plan is still in ${plan.status} status." }

    val order =
      plan.copy(
        UUID.randomUUID().toString(),
        Request.Status.DRAFT,
        Request.Intent.ORDER,
      )
    fhirEngine.create(order.request)
    return order.request
  }

  /**
   * Given an active order, perform the event. The order is fulfilled through actually performing
   * the activity.
   *
   * val communicationEvent = ActivityFlow.with(fhirEngine).perform("CommunicationRequest/6789")
   */
  suspend fun perform(requestId: String): Resource {
    // TODO : End the order by marking completed.
    val request = fhirEngine.search(requestId).first().resource
    val order = Request(request)
    check(order.intent == Request.Intent.ORDER) { "Order is still in ${order.intent} state." }

    check(order.status == Request.Status.ACTIVE) { "Order is still in ${order.status} status." }
    val event = order.createEventResource(fhirEngine)
    event.setStatus(Event.Status.PREPARATION)
    event.setBasedOn(order)
    fhirEngine.create(event.resource)
    return event.resource
  }

  companion object {
    fun with(fhirEngine: FhirEngine): ActivityFlow = ActivityFlow(fhirEngine)
  }
}
