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

import java.util.UUID
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.MedicationDispense
import org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.Task
import org.opencds.cqf.fhir.api.Repository

abstract class ActivityFlow2<R : Resource, E : Resource>
internal constructor(val fhirEngine: Repository) {

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
      plan = PlanFlow.start(fhirEngine, it) as R
    }
  }

  suspend fun endPlan(init: R.() -> Unit) = apply {
    requireNotNull(plan)
    plan?.let {
      it.init()
      PlanFlow.end(fhirEngine, it)
    }
  }

  suspend fun startOrder(init: R.() -> Unit) = apply {
    requireNotNull(plan)
    plan?.let {
      it.init()
      fhirEngine.update(it)
      order = OrderFlow.start(fhirEngine, it) as R
    }
  }

  suspend fun endOrder(init: R.() -> Unit) = apply {
    requireNotNull(order)
    order?.let {
      it.init()
      OrderFlow.end(fhirEngine, it)
    }
  }

  internal suspend inline fun <reified TT : Resource> startPerform(init: R.() -> Unit) = apply {
    requireNotNull(order)
    order?.let {
      it.init()
      fhirEngine.update(it)
      event = PerformFlow(TT::class.java).start(fhirEngine, it) as E
    }
  }

  internal suspend fun endPerform(init: E.() -> Unit) = apply {
    requireNotNull(event)
    event?.let { it ->
      it.init()
      PerformFlow(it::class.java).end(fhirEngine, it)
    }
  }

  internal abstract fun createEventResource(order: R): E

  companion object {

    fun `for`(resource: Task) {}

    fun `for`(resource: MedicationRequest) {}

    fun `for`(resource: CommunicationRequest) {}

    fun `for`(resource: ServiceRequest) {}

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
