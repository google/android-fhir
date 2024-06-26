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

import com.google.android.fhir.getResourceClass
import java.util.UUID
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.MedicationDispense
import org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.Task
import org.opencds.cqf.fhir.api.Repository

abstract class ActivityFlow2<R : Resource, E : Resource>
internal constructor(val fhirEngine: Repository) {

  private val Reference.`class`
    get() = getResourceClass<Resource>(reference.split("/")[0])

  private val Reference.idType
    get() = IdType(reference)

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

      val basedOnProposal = fhirEngine.read(plan.javaClass, basedOn.idType)?.let { Request(it) }
      check(basedOnProposal != null) { "Couldn't find ${basedOn.reference} in the database." }

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

      val basedOnResource = fhirEngine.read(order.javaClass, basedOn.idType)?.let { Request(it) }

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

      val basedOnResource = fhirEngine.read(basedOn.`class`, basedOn.idType)?.let { Request(it) }

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

    fun sendMessage(engine: Repository, resource: CommunicationRequest) =
      CommunicationActivityFlow(engine).apply { init(resource) }

    fun collectInformation(engine: Repository, resource: Task) =
      CollectInformationFlow(engine).apply { init(resource) }

    fun orderMedication(engine: Repository, resource: MedicationRequest) =
      OrderMedicationFlow(engine).apply { init(resource) }

    fun recommendImmunization(engine: Repository, resource: MedicationRequest) =
      RecommendImmunization(engine).apply { init(resource) }

    fun orderService(engine: Repository, resource: ServiceRequest) =
      OrderService(engine).apply { init(resource) }

    fun enrollPatient(engine: Repository, resource: Task) =
      EnrollPatient(engine).apply { init(resource) }

    fun generateReport(engine: Repository, resource: Task) =
      NotImplementedError("Not implemented yet.")

    fun proposeDiagnosis(engine: Repository, resource: Task) =
      NotImplementedError("Not implemented yet.")

    fun recordDetectedIssue(engine: Repository, resource: Task) =
      NotImplementedError("Not implemented yet.")

    fun recordInference(engine: Repository, resource: Task) =
      NotImplementedError("Not implemented yet.")

    fun reportFlag(engine: Repository, resource: Task) = NotImplementedError("Not implemented yet.")
  }
}

class CommunicationActivityFlow(engine: Repository) :
  ActivityFlow2<CommunicationRequest, Communication>(engine) {

  override fun createEventResource(order: CommunicationRequest) =
    Communication().apply {
      id = UUID.randomUUID().toString()
      status = Communication.CommunicationStatus.PREPARATION
      category = order.category
      priority = Communication.CommunicationPriority.fromCode(order.priority?.toCode())
      medium = order.medium
      subject = order.subject
      about = order.about
      encounter = order.encounter
      recipient = order.recipient
      sender = order.sender
      reasonCode = order.reasonCode
      reasonReference = order.reasonReference
      order.payload.forEach { addPayload(Communication.CommunicationPayloadComponent(it.content)) }
    }
}

class CollectInformationFlow(engine: Repository) :
  ActivityFlow2<Task, QuestionnaireResponse>(engine) {

  override fun createEventResource(order: Task) =
    QuestionnaireResponse().apply {
      id = UUID.randomUUID().toString()
      status = QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS
      subject = order.`for`
      //      encounter = order.encounter
    }
}

class OrderMedicationFlow(engine: Repository) :
  ActivityFlow2<MedicationRequest, MedicationDispense>(engine) {
  override fun createEventResource(order: MedicationRequest) =
    MedicationDispense().apply {
      id = UUID.randomUUID().toString()
      status = MedicationDispense.MedicationDispenseStatus.PREPARATION

      if (order.hasCategory() && order.category.size == 1) {
        // Only set category if single, otherwise let application fill it in.
        category = order.category.first()
      }

      medication = order.medication
      subject = order.subject
      context = order.encounter
      if (order.hasSubstitution()) {
        substitution = order.substitution.toMedicationDispenseSubstitutionComponent()
      }
      note = order.note
      dosageInstruction = order.dosageInstruction
      detectedIssue = order.detectedIssue
      eventHistory = order.eventHistory
    }

  private fun MedicationRequestSubstitutionComponent.toMedicationDispenseSubstitutionComponent() =
    MedicationDispenseSubstitutionComponent().apply {
      id = this@toMedicationDispenseSubstitutionComponent.id
      extension = this@toMedicationDispenseSubstitutionComponent.extension
      modifierExtension = this@toMedicationDispenseSubstitutionComponent.modifierExtension
      allowed = this@toMedicationDispenseSubstitutionComponent.allowed
      if (this@toMedicationDispenseSubstitutionComponent.hasReason()) {
        addReason(this@toMedicationDispenseSubstitutionComponent.reason)
      }
    }
}

class RecommendImmunization(engine: Repository) :
  ActivityFlow2<MedicationRequest, Immunization>(engine) {
  override fun createEventResource(order: MedicationRequest) =
    Immunization().apply {
      patient = order.subject
      reasonCode = order.reasonCode
      reasonReference = order.reasonReference
    }
}

class OrderService(engine: Repository) : ActivityFlow2<ServiceRequest, Procedure>(engine) {
  override fun createEventResource(order: ServiceRequest) =
    Procedure().apply {
      code = order.code
      subject = order.subject
      //    encounter = order.encounter
      reasonCode = order.reasonCode
      reasonReference = order.reasonReference
      bodySite = order.bodySite
    }
}

class EnrollPatient(engine: Repository) : ActivityFlow2<Task, EpisodeOfCare>(engine) {

  override fun createEventResource(order: Task) = EpisodeOfCare().apply { patient = order.`for` }
}
