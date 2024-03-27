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
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.Task

abstract class ActivityFlow2<R : Resource, E : Resource>
internal constructor(val fhirEngine: FhirEngine) {

  private val Reference.searchByIdQuery
    get() = reference.split("/").joinToString("?_id=")

  var proposal: R? = null
  var plan: R? = null
  var order: R? = null
  var event: E? = null

  fun init(resource: R) {

    when (Request(resource).intent) {
      Request.Intent.PROPOSAL -> {
        proposal = resource
      }
      Request.Intent.PLAN -> {
        plan = resource
      }
      Request.Intent.ORDER -> {
        order = resource
      }
      else -> {
        throw IllegalArgumentException("Unknown Intent for the resource")
      }
    }
  }

  suspend fun startPlan(init: R.() -> Unit) = apply {
    requireNotNull(proposal)
    proposal?.let {
      it.init()
      fhirEngine.update(it)
      val inputProposal: Request<R> = Request(it)
      check(inputProposal.intent == Request.Intent.PROPOSAL) {
        "Proposal is still in ${inputProposal.intent} state."
      }

      check(inputProposal.status == Request.Status.ACTIVE) {
        "Proposal is still in ${inputProposal.status} status."
      }

      val planRequest: Request<R> =
        inputProposal.copy(
          id = UUID.randomUUID().toString(),
          status = Request.Status.DRAFT,
          intent = Request.Intent.PLAN,
        )

      plan = planRequest.resource
    }
  }

  suspend fun endPlan(init: R.() -> Unit) = apply {
    requireNotNull(plan)
    plan?.let { plan ->
      plan.init()
      val inputPlan = Request(plan)

      val basedOn = inputPlan.getBasedOn()
      check(basedOn != null) { "${plan.resourceType}.basedOn shouldn't be null" }

      val basedOnProposal =
        fhirEngine.search(basedOn.searchByIdQuery).firstOrNull()?.resource?.let { Request(it) }
      check(basedOnProposal != null) { "Couldn't find ${basedOn.searchByIdQuery} in the database." }

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
  }

  suspend fun startOrder(init: R.() -> Unit) = apply {
    requireNotNull(plan)
    plan?.let { request ->
      request.init()
      fhirEngine.update(request)
      val inputRequest = Request(request)
      check(
        inputRequest.intent == Request.Intent.PROPOSAL ||
          inputRequest.intent == Request.Intent.PLAN,
      ) {
        "Plan is still in ${inputRequest.intent} state."
      }

      check(inputRequest.status == Request.Status.ACTIVE) {
        "Plan is still in ${inputRequest.status} status."
      }

      val orderRequest =
        inputRequest.copy(
          UUID.randomUUID().toString(),
          Request.Status.DRAFT,
          Request.Intent.ORDER,
        )

      order = orderRequest.resource
    }
  }

  suspend fun endOrder(init: R.() -> Unit) = apply {
    requireNotNull(order)
    order?.let { order ->
      order.init()
      val inputOrder = Request(order)
      val basedOn = inputOrder.getBasedOn()
      check(basedOn != null) { "${order.resourceType}.basedOn shouldn't be null" }

      val basedOnResource =
        fhirEngine.search(basedOn.searchByIdQuery).firstOrNull()?.resource?.let { Request(it) }
      check(basedOnResource != null) { "Couldn't find $basedOn in the database." }

      check(
        basedOnResource.intent == Request.Intent.PROPOSAL ||
          basedOnResource.intent == Request.Intent.PLAN,
      ) {
        "Proposal is still in ${basedOnResource.intent} state."
      }

      check(basedOnResource.status == Request.Status.ACTIVE) {
        "Proposal is still in ${basedOnResource.status} status."
      }

      check(inputOrder.intent == Request.Intent.ORDER) {
        "Proposal is still in ${basedOnResource.intent} state."
      }

      check(
        inputOrder.status == Request.Status.DRAFT || inputOrder.status == Request.Status.ACTIVE,
      ) {
        "Proposal is still in ${basedOnResource.status} status."
      }

      basedOnResource.setStatus(Request.Status.COMPLETED)

      fhirEngine.create(inputOrder.resource)
      fhirEngine.update(basedOnResource.resource)
    }
  }

  internal suspend inline fun startPerform(init: R.() -> Unit) = apply {
    requireNotNull(order)
    order?.let {
      it.init()
      fhirEngine.update(it)
      val order = Request(it)
      check(order.intent == Request.Intent.ORDER) { "Order is still in ${order.intent} state." }

      check(order.status == Request.Status.ACTIVE) { "Order is still in ${order.status} status." }
      val eventRequest: Event<E> = Event(createEventResource(order.resource))
      eventRequest.setStatus(Event.Status.PREPARATION)
      eventRequest.setBasedOn(order)

      event = eventRequest.resource
    }
  }

  suspend fun endPerform(init: E.() -> Unit) = apply {
    requireNotNull(event)
    event?.let { event ->
      event.init()
      val inputEvent = Event(event)
      val basedOn = inputEvent.getBasedOn()
      check(basedOn != null) { "${event.resourceType}.basedOn shouldn't be null" }

      val basedOnResource =
        fhirEngine.search(basedOn.searchByIdQuery).firstOrNull()?.resource?.let { Request(it) }
      check(basedOnResource != null) { "Couldn't find $basedOn in the database." }

      check(basedOnResource.intent == Request.Intent.ORDER) {
        "Proposal is still in ${basedOnResource.intent} state."
      }

      check(basedOnResource.status == Request.Status.ACTIVE) {
        "Proposal is still in ${basedOnResource.status} status."
      }

      check(
        inputEvent.getStatus() == Event.Status.PREPARATION ||
          inputEvent.getStatus() == Event.Status.INPROGRESS,
      ) {
        "Proposal is still in ${basedOnResource.status} status."
      }

      basedOnResource.setStatus(Request.Status.COMPLETED)

      fhirEngine.create(inputEvent.resource)
      fhirEngine.update(basedOnResource.resource)
    }
  }

  internal abstract fun createEventResource(order: R): E

  companion object {


    fun sendMessage(engine: FhirEngine, resource: CommunicationRequest) =
      CommunicationActivityFlow(engine).apply {
        init(resource)
      }

    fun collectInformation(engine: FhirEngine, resource: Task) =
      CollectInformationFlow(engine).apply {
      init(resource)
    }
  }
}

class CommunicationActivityFlow(engine: FhirEngine) :
  ActivityFlow2<CommunicationRequest, Communication>(engine) {

  override fun createEventResource(order: CommunicationRequest) =
    Communication().apply {
      id = UUID.randomUUID().toString()
      status = Communication.CommunicationStatus.PREPARATION
      category = order.category
      priority = Communication.CommunicationPriority.fromCode(order.priority?.toCode())
      subject = order.subject
      about = order.about
      encounter = order.encounter
      recipient = order.recipient
      order.payload.forEach { addPayload(Communication.CommunicationPayloadComponent(it.content)) }
    }
}


class CollectInformationFlow(engine: FhirEngine): ActivityFlow2<Task, QuestionnaireResponse>(engine) {

  override fun createEventResource(order: Task) =  QuestionnaireResponse().apply {
    id = UUID.randomUUID().toString()
    status = QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS
      subject = order.`for`
      encounter = order.encounter
    }

}