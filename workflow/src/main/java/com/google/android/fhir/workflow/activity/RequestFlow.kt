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
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.Task
import org.opencds.cqf.fhir.api.Repository

private val Reference.idType
  get() = IdType(reference)
private val Reference.`class`
  get() = getResourceClass<Resource>(reference.split("/")[0])

internal interface RequestFlow {

  suspend fun <R : Resource> start(repository: Repository, request: R): Resource

  suspend fun <R : Resource> end(repository: Repository, generated: R)
}

internal object PlanFlow : RequestFlow {

  override suspend fun <R : Resource> start(repository: Repository, proposal: R): Resource {
    val inputProposal: Request<R> = Request(proposal)
    check(inputProposal.intent == Request.Intent.PROPOSAL) {
      "Proposal is still in ${inputProposal.intent} state."
    }

    check(inputProposal.status == Request.Status.ACTIVE) {
      "Proposal is still in ${inputProposal.status} status."
    }

    repository.update(proposal)

    val planRequest: Request<R> =
      inputProposal.copy(
        id = UUID.randomUUID().toString(),
        status = Request.Status.DRAFT,
        intent = Request.Intent.PLAN,
      )
    return planRequest.resource
  }

  override suspend fun <R : Resource> end(repository: Repository, plan: R) {
    val inputPlan = Request(plan)

    val basedOn = inputPlan.getBasedOn()
    check(basedOn != null) { "${plan.resourceType}.basedOn shouldn't be null" }

    val basedOnProposal = repository.read(plan.javaClass, basedOn.idType)?.let { Request(it) }
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

    repository.create(inputPlan.resource)
    repository.update(basedOnProposal.resource)
  }
}

internal object OrderFlow : RequestFlow {
  override suspend fun <R : Resource> start(repository: Repository, planOrProposal: R): Resource {
    val inputRequest = Request(planOrProposal)
    check(
      inputRequest.intent == Request.Intent.PROPOSAL || inputRequest.intent == Request.Intent.PLAN,
    ) {
      "Plan is still in ${inputRequest.intent} state."
    }

    check(inputRequest.status == Request.Status.ACTIVE) {
      "Plan is still in ${inputRequest.status} status."
    }

    repository.update(planOrProposal)

    val orderRequest =
      inputRequest.copy(
        UUID.randomUUID().toString(),
        Request.Status.DRAFT,
        Request.Intent.ORDER,
      )

    return orderRequest.resource
  }

  override suspend fun <R : Resource> end(repository: Repository, order: R) {
    val inputOrder = Request(order)
    val basedOn = inputOrder.getBasedOn()
    check(basedOn != null) { "${order.resourceType}.basedOn shouldn't be null" }

    val basedOnResource = repository.read(order.javaClass, basedOn.idType)?.let { Request(it) }

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

    repository.create(inputOrder.resource)
    repository.update(basedOnResource.resource)
  }
}

internal class PerformFlow<E : Resource> constructor(private val type: Class<E>) : RequestFlow {
  override suspend fun <R : Resource> start(repository: Repository, request: R): Resource {
    val order = Request(request)
    check(order.intent == Request.Intent.ORDER) { "Order is still in ${order.intent} state." }

    check(order.status == Request.Status.ACTIVE) { "Order is still in ${order.status} status." }

    repository.update(request)

    val eventRequest: Event<E> = Event(createEventResource(order.resource))
    eventRequest.setStatus(Event.Status.PREPARATION)
    eventRequest.setBasedOn(order)
    return eventRequest.resource
  }

  override suspend fun <R : Resource> end(repository: Repository, event: R) {
    val inputEvent = Event(event)
    val basedOn = inputEvent.getBasedOn()
    check(basedOn != null) { "${event.resourceType}.basedOn shouldn't be null" }

    val basedOnResource = repository.read(basedOn.`class`, basedOn.idType)?.let { Request(it) }

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

    repository.create(inputEvent.resource)
    repository.update(basedOnResource.resource)
  }

  private fun <R : Resource> createEventResource(order: R): E {
    return when (order) {
      is Task -> order.toEvent(type)
      is MedicationRequest -> order.toEvent(type)
      is ServiceRequest -> order.toEvent(type)
      is CommunicationRequest -> order.toEvent(type)
      else -> {
        throw IllegalArgumentException("Unable to create event for ${order.resourceType}")
      }
    }
  }

  companion object {

    private fun <E : Resource> Task.toEvent(type: Class<E>): E {
      return when (type::class) {
        QuestionnaireResponse::class -> {
          // collect information
          QuestionnaireResponse().apply {
            id = UUID.randomUUID().toString()
            status = QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS
            subject = this@toEvent.`for`
            //      encounter = order.encounter
          } as E
        }

        //        EnrollPatient
        EpisodeOfCare::class -> {
          EpisodeOfCare().apply { patient = this@toEvent.`for` } as E
        }
        else -> {
          throw IllegalArgumentException(
            IllegalArgumentException("Unable to create Event of type ${type::class} "),
          )
        }
      }
    }

    private fun <E : Resource> MedicationRequest.toEvent(type: Class<E>): E {
      // order medication
      return when (type::class) {
        MedicationDispense::class -> {
          MedicationDispense().apply {
            id = UUID.randomUUID().toString()
            status = MedicationDispense.MedicationDispenseStatus.PREPARATION

            if (this@toEvent.hasCategory() && this@toEvent.category.size == 1) {
              // Only set category if single, otherwise let application fill it in.
              category = this@toEvent.category.first()
            }

            medication = this@toEvent.medication
            subject = this@toEvent.subject
            context = this@toEvent.encounter
            if (this@toEvent.hasSubstitution()) {
              substitution = this@toEvent.substitution.toMedicationDispenseSubstitutionComponent()
            }
            note = this@toEvent.note
            dosageInstruction = this@toEvent.dosageInstruction
            detectedIssue = this@toEvent.detectedIssue
            eventHistory = this@toEvent.eventHistory
          } as E
        }

        // RecommendImmunization
        Immunization::class -> {
          Immunization().apply {
            patient = this@toEvent.subject
            reasonCode = this@toEvent.reasonCode
            reasonReference = this@toEvent.reasonReference
          } as E
        }
        else -> {
          throw IllegalArgumentException(
            IllegalArgumentException("Unable to create Event of type ${type::class} "),
          )
        }
      }
    }

    private fun <E : Resource> CommunicationRequest.toEvent(type: Class<E>): E {
      return when (type.newInstance()) {
        is Communication -> {
          Communication().apply {
            id = UUID.randomUUID().toString()
            status = Communication.CommunicationStatus.PREPARATION
            category = this@toEvent.category
            priority = Communication.CommunicationPriority.fromCode(this@toEvent.priority?.toCode())
            medium = this@toEvent.medium
            subject = this@toEvent.subject
            about = this@toEvent.about
            encounter = this@toEvent.encounter
            recipient = this@toEvent.recipient
            sender = this@toEvent.sender
            reasonCode = this@toEvent.reasonCode
            reasonReference = this@toEvent.reasonReference
            this@toEvent.payload.forEach {
              addPayload(Communication.CommunicationPayloadComponent(it.content))
            }
          } as E
        }
        else -> {
          throw IllegalArgumentException(
            IllegalArgumentException("Unable to create Event of type ${type::class} "),
          )
        }
      }
    }

    private fun <E : Resource> ServiceRequest.toEvent(type: Class<E>): E {
      return when (type.newInstance()) {
        //        OrderService
        Procedure::class -> {
          Procedure().apply {
            code = this@toEvent.code
            subject = this@toEvent.subject
            //    encounter = order.encounter
            reasonCode = this@toEvent.reasonCode
            reasonReference = this@toEvent.reasonReference
            bodySite = this@toEvent.bodySite
          } as E
        }
        else ->
          throw IllegalArgumentException(
            IllegalArgumentException("Unable to create Event of type ${type::class} "),
          )
      }
      //      OrderService
    }

    private fun MedicationRequest.MedicationRequestSubstitutionComponent
      .toMedicationDispenseSubstitutionComponent() =
      MedicationDispense.MedicationDispenseSubstitutionComponent().apply {
        id = this@toMedicationDispenseSubstitutionComponent.id
        extension = this@toMedicationDispenseSubstitutionComponent.extension
        modifierExtension = this@toMedicationDispenseSubstitutionComponent.modifierExtension
        allowed = this@toMedicationDispenseSubstitutionComponent.allowed
        if (this@toMedicationDispenseSubstitutionComponent.hasReason()) {
          addReason(this@toMedicationDispenseSubstitutionComponent.reason)
        }
      }
  }
}
