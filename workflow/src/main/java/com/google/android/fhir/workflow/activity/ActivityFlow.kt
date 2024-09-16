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
import com.google.android.fhir.workflow.activity.phase.Phase
import com.google.android.fhir.workflow.activity.phase.Phase.PhaseName
import com.google.android.fhir.workflow.activity.phase.Phase.PhaseName.ORDER
import com.google.android.fhir.workflow.activity.phase.Phase.PhaseName.PERFORM
import com.google.android.fhir.workflow.activity.phase.Phase.PhaseName.PLAN
import com.google.android.fhir.workflow.activity.phase.Phase.PhaseName.PROPOSAL
import com.google.android.fhir.workflow.activity.phase.event.PerformPhase
import com.google.android.fhir.workflow.activity.phase.request.OrderPhase
import com.google.android.fhir.workflow.activity.phase.request.PlanPhase
import com.google.android.fhir.workflow.activity.phase.request.ProposalPhase
import com.google.android.fhir.workflow.activity.resource.event.CPGCommunicationEvent
import com.google.android.fhir.workflow.activity.resource.event.CPGEventResource
import com.google.android.fhir.workflow.activity.resource.event.CPGOrderMedicationEvent
import com.google.android.fhir.workflow.activity.resource.request.CPGCommunicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGMedicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Intent
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.opencds.cqf.fhir.api.Repository

internal val Reference.idType
  get() = IdType(reference)

internal val Reference.`class`
  get() = getResourceClass<Resource>(reference.split("/")[0])

/**
 * This abstracts the flow of various
 * [CPG activities](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-lifecycle---request-phases-proposal-plan-order)
 * throughout their various stages.
 *
 * The application developer may create the flow of a new proposal and move it through the various
 * stages or they may resume the workflow of a request already in a later stage (plan,order).
 *
 * The application developers should use the appropriate static factory [ActivityFlow.of] apis
 * provided by the library to create or resume an activity flow.
 *
 * An activity flow starts with the user creating the flow with an appropriate [CPGRequestResource].
 * The user may do valid state transitions on the current phase by calling appropriate apis on
 * current phase. The user may call [getCurrentPhase] as appropriately cast it to [PlanPhase],
 * [OrderPhase] or [PerformPhase] by either checking type or value of [Phase.getPhaseName].
 *
 * The user may then move the activity to the next phase by creating a draft resource by calling
 * appropriate draft api([draftPlan], [draftOrder] or [draftPerform]). The user may review and
 * update the draft resource and then call appropriate start api([startPlan], [startOrder] or
 * [startPerform]) to create a new activity phase.
 *
 * Since the perform creates a [CPGEventResource] and the same flow could create different event
 * resources, application developer needs to provide the appropriate event type as a parameter to
 * the [draftPerform].
 *
 * Example of a `Order a Medication to dispense` where user completes only one [Phase] at a time and
 * has to reload / resume the [ActivityFlow] to move to the next [Phase].
 *
 * ```
 *   val cpgMedicationRequest =
 *     CPGMedicationRequest(
 *       MedicationRequest().apply {
 *         id = "med-req-01"
 *         subject = Reference("Patient/pat-01")
 *         intent = MedicationRequest.MedicationRequestIntent.PROPOSAL
 *         meta.addProfile("http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-medicationrequest")
 *         status = MedicationRequest.MedicationRequestStatus.ACTIVE
 *         addNote(Annotation(MarkdownType("Proposal looks OK.")))
 *       },
 *     )
 *
 *   val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
 *   repository.create(cpgMedicationRequest.resource)
 *
 *   val flow = ActivityFlow.of(repository, cpgMedicationRequest)
 *
 *   var cachedRequestId = ""
 *
 *   flow
 *   .draftPlan()
 *   .onSuccess { draftPlan ->
 *     draftPlan.update { addNote(Annotation(MarkdownType("Draft plan looks OK."))) }
 *
 *     flow.startPlan(draftPlan).onSuccess { planPhase ->
 *       val updatedPlan =
 *         planPhase.getRequest().copy().apply {
 *           setStatus(Status.ACTIVE)
 *           update { addNote(Annotation(MarkdownType("Plan looks OK."))) }
 *         }
 *
 *       planPhase
 *         .update(updatedPlan)
 *         .onSuccess { cachedRequestId = updatedPlan.logicalId }
 *         .onFailure { fail("Should have succeeded", it) }
 *     }
 *   }
 *   .onFailure { fail("Unexpected", it) }
 *
 *   val planToResume =
 *     repository
 *       .read(MedicationRequest::class.java, IdType("MedicationRequest", cachedRequestId))
 *       .let { CPGMedicationRequest(it) }
 *   val resumedPlanFlow = ActivityFlow.of(repository, planToResume)
 *
 *   check(resumedPlanFlow.getCurrentPhase() is PlanPhase<*>) {
 *     "Flow is in ${resumedPlanFlow.getCurrentPhase().getPhaseName()} "
 *   }
 *   resumedPlanFlow
 *   .draftOrder()
 *   .onSuccess { draftOrder ->
 *     draftOrder.update { addNote(Annotation(MarkdownType("Draft order looks OK."))) }
 *
 *     resumedPlanFlow.startOrder(draftOrder).onSuccess { orderPhase ->
 *       val updatedOrder =
 *         orderPhase.getRequest().copy().apply {
 *           setStatus(Status.ACTIVE)
 *           update { addNote(Annotation(MarkdownType("Order looks OK."))) }
 *         }
 *
 *       orderPhase
 *         .update(updatedOrder)
 *         .onSuccess { cachedRequestId = updatedOrder.logicalId }
 *         .onFailure { fail("Should have succeeded", it) }
 *     }
 *   }
 *   .onFailure { fail("Unexpected", it) }
 *
 *   val orderToResume =
 *     repository
 *       .read(MedicationRequest::class.java, IdType("MedicationRequest", cachedRequestId))
 *       .let { CPGMedicationRequest(it) }
 *
 *   val resumedOrderFlow = ActivityFlow.of(repository, orderToResume)
 *
 *   check(resumedOrderFlow.getCurrentPhase() is OrderPhase<*>) {
 *     "Flow is in ${resumedOrderFlow.getCurrentPhase().getPhaseName()} "
 *   }
 *
 *   resumedOrderFlow
 *   .draftPerform(CPGMedicationDispenseEvent::class.java)
 *   .onSuccess { draftEvent ->
 *     draftEvent.update { addNote(Annotation(MarkdownType("Draft event looks OK."))) }
 *
 *     resumedOrderFlow.startPerform(draftEvent).onSuccess { performPhase ->
 *       val updatedEvent =
 *         performPhase.getEvent().copy().apply {
 *           setStatus(EventStatus.INPROGRESS)
 *           update { addNote(Annotation(MarkdownType("Event looks OK."))) }
 *         }
 *
 *       performPhase
 *         .update(updatedEvent)
 *         .onSuccess { cachedRequestId = updatedEvent.logicalId }
 *         .onFailure { fail("Should have succeeded", it) }
 *     }
 *   }
 *   .onFailure { fail("Unexpected", it) }
 *
 *   val eventToResume =
 *     repository
 *       .read(MedicationDispense::class.java, IdType("MedicationDispense", cachedRequestId))
 *       .let { CPGMedicationDispenseEvent(it) }
 *
 *   val resumedPerformFlow = ActivityFlow.of(repository, eventToResume)
 *
 *   check(resumedPerformFlow.getCurrentPhase() is Phase.EventPhase<*>) {
 *     "Flow is in ${resumedPerformFlow.getCurrentPhase().getPhaseName()} "
 *   }
 *
 *   (resumedPerformFlow.getCurrentPhase() as Phase.EventPhase<*>).complete()
 * ```
 */
@Suppress(
  "UnstableApiUsage", /* Repository is marked @Beta */
)
class ActivityFlow<R : CPGRequestResource<*>, E : CPGEventResource<*>>
private constructor(
  private val repository: Repository,
  requestResource: R? = null,
  eventResource: E? = null,
) {

  private var currentPhase: Phase

  init {
    currentPhase =
      if (eventResource != null) {
        PerformPhase(repository, eventResource)
      } else if (requestResource != null) {
        when (requestResource.getIntent()) {
          Intent.PROPOSAL -> ProposalPhase(repository, requestResource)
          Intent.PLAN -> PlanPhase(repository, requestResource)
          Intent.ORDER -> OrderPhase(repository, requestResource)
          else ->
            throw IllegalArgumentException(
              "Couldn't create the flow for ${requestResource.getIntent().code} intent. Supported intents are 'proposal', 'plan' and 'order'.",
            )
        }
      } else {
        throw IllegalArgumentException(
          "Either Request or Event is required to create a flow. Both can't be null.",
        )
      }
  }

  /**
   * Returns the current phase of the flow. The users may check the type of flow by calling
   * [Phase.getPhaseName] on the [getCurrentPhase] and then cast it to appropriate classes.
   *
   * The table below shows the mapping between the [PhaseName] and [Phase] implementations.
   *
   * | [PhaseName] | [Class]         |
   * |-------------|-----------------|
   * | [PROPOSAL]  | [ProposalPhase] |
   * | [PLAN]      | [PlanPhase]     |
   * | [ORDER]     | [OrderPhase]    |
   * | [PERFORM]   | [PerformPhase]  |
   */
  fun getCurrentPhase(): Phase {
    return currentPhase
  }

  /**
   * Creates a draft plan resource based on the state of the [currentPhase].
   *
   * @return [R] if the action is successful, error otherwise.
   */
  fun draftPlan(): Result<R> {
    return PlanPhase.draft(currentPhase)
  }

  /**
   * Starts a plan phase based on the state of the [currentPhase] and [draftPlan].
   *
   * @return [PlanPhase] if the action is successful, error otherwise.
   */
  fun startPlan(draftPlan: R) =
    PlanPhase.start(repository, currentPhase, draftPlan).also { it.onSuccess { currentPhase = it } }

  /**
   * Creates a draft order resource based on the state of the [currentPhase].
   *
   * @return [R] if the action is successful, error otherwise.
   */
  fun draftOrder(): Result<R> {
    return OrderPhase.draft(currentPhase)
  }

  /**
   * Starts an order phase based on the state of the [currentPhase] and [draftPlan].
   *
   * @return [OrderPhase] if the action is successful, error otherwise.
   */
  fun startOrder(updatedDraftOrder: R) =
    OrderPhase.start(repository, currentPhase, updatedDraftOrder).also {
      it.onSuccess { currentPhase = it }
    }

  /**
   * Creates a draft event resource based on the state of the [currentPhase].
   *
   * @return [D] if the action is successful, error otherwise.
   */
  fun <D : E> draftPerform(klass: Class<in D>): Result<D> {
    return PerformPhase.draft<R, D>(klass, currentPhase)
  }

  /**
   * Starts a perform phase based on the state of the [currentPhase] and [draftPlan].
   *
   * @return [PerformPhase] if the action is successful, error otherwise.
   */
  fun <D : E> startPerform(updatedDraftPerform: D) =
    PerformPhase.start<R, D>(repository, currentPhase, updatedDraftPerform).also {
      it.onSuccess { currentPhase = it }
    }

  companion object {

    /**
     * Create flow for the
     * [Send Message](https://build.fhir.org/ig/HL7/cqf-recommendations/examples-activities.html#send-a-message)
     * activity with the [CPGCommunicationRequest].
     *
     * @return ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent>
     */
    fun of(
      repository: Repository,
      resource: CPGCommunicationRequest,
    ): ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
      ActivityFlow(repository, resource)

    /**
     * Create flow for the
     * [Send Message](https://build.fhir.org/ig/HL7/cqf-recommendations/examples-activities.html#send-a-message)
     * activity with the [CPGCommunicationEvent].
     *
     * @return ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent>
     */
    fun of(
      repository: Repository,
      resource: CPGCommunicationEvent,
    ): ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
      ActivityFlow(repository, null, resource)

    /**
     * Create flow for the
     * [Order a medication](https://build.fhir.org/ig/HL7/cqf-recommendations/examples-activities.html#order-a-medication)
     * activity with the [CPGMedicationRequest].
     *
     * @return ActivityFlow<CPGMedicationRequest, CPGOrderMedicationEvent<*>>
     */
    fun of(
      repository: Repository,
      resource: CPGMedicationRequest,
    ): ActivityFlow<CPGMedicationRequest, CPGOrderMedicationEvent<*>> =
      ActivityFlow(repository, resource)

    /**
     * Create flow for the
     * [Order a medication](https://build.fhir.org/ig/HL7/cqf-recommendations/examples-activities.html#order-a-medication)
     * activity with the [CPGOrderMedicationEvent].
     *
     * @return ActivityFlow<CPGMedicationRequest, CPGOrderMedicationEvent<*>>
     */
    fun of(
      repository: Repository,
      resource: CPGOrderMedicationEvent<*>,
    ): ActivityFlow<CPGMedicationRequest, CPGOrderMedicationEvent<*>> =
      ActivityFlow(repository, null, resource)

    // Collect information
    //    fun of(repository: Repository, resource: CPGTaskRequest) =
    //      ActivityFlow(repository, resource, null)

    //    // Order a service
    //    fun of(
    //      repository: Repository,
    //      resource: CPGServiceRequest,
    //    ): ActivityFlow<CPGServiceRequest, CPGEventForOrderService<*>> =
    //      ActivityFlow(repository, resource)
    //
    //    fun of(
    //      repository: Repository,
    //      resource: CPGImmunizationRequest,
    //    ): ActivityFlow<CPGImmunizationRequest, CPGImmunizationEvent> = ActivityFlow(repository,
    // resource)
  }
}
