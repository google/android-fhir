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
import com.google.android.fhir.workflow.activity.phase.event.PerformPhase
import com.google.android.fhir.workflow.activity.phase.request.OrderPhase
import com.google.android.fhir.workflow.activity.phase.request.PlanPhase
import com.google.android.fhir.workflow.activity.phase.request.ProposalPhase
import com.google.android.fhir.workflow.activity.resource.event.CPGCommunicationEvent
import com.google.android.fhir.workflow.activity.resource.event.CPGEventResource
import com.google.android.fhir.workflow.activity.resource.event.CPGEventResourceForOrderMedication
import com.google.android.fhir.workflow.activity.resource.request.CPGCommunicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGMedicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Intent
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.opencds.cqf.fhir.api.Repository

val Reference.idType
  get() = IdType(reference)

val Reference.`class`
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
 * provided by the library to create / resume an activity flow.
 *
 * An activity flow starts with the user creating the flow with an appropriate [CPGRequestResource].
 * The user then may move the activity to the next phase by calling the appropriate start* and end*
 * apis provided by the class.
 *
 * The start* and end* apis take a lambda receiver and the application developer may call the
 * [CPGRequestResource.update] on the passed [CPGRequestResource] request object to put in any
 * updates into the request.
 *
 * Since the perform creates a [CPGEventResource] and the same flow could create different event
 * resources, application developer needs to provide the appropriate event type as a parameter to
 * the [startPerform].
 *
 * Example of a `Send a Message` activity flow.
 *
 *  ```
 *  // Create appropriate CPGRequestResource for the proposal resource.
 *
 *      val cpgCommunicationRequest =
 *       CPGRequestResource.of(
 *         CommunicationRequest().apply {
 *           id = "com-req-01"
 *           status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
 *           subject = Reference("Patient/pat-01")
 *           meta.addProfile("http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest")
 *           addPayload().apply { content = StringType("Hello message for patient") }
 *         }
 *       )
 *
 *     val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
 *     // Create ActivityFlow for the given CPGRequestResource.
 *     val communicationFlow: ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
 *       ActivityFlow.of(repository, "pat-01")
 *         .first() as ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent>
 *     late init var order: CommunicationRequest
 *     communicationFlow.startPlan { // CPGCommunicationRequest
 *       update { // CommunicationRequest
 *         addPayload().apply { content = StringType("Updated proposal with this message ") }
 *       }
 *     }.endPlan { // CPGCommunicationRequest
 *       order = this.resource
 *       update {  // CommunicationRequest
 *         addPayload().apply { content = StringType("Updated newly created plan with this message ") }
 *       }
 *     }
 *
 *     // Find all the flows associated with the patient and resume the particular one
 *     val orderFlow: ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
 *       ActivityFlow.of(repository, "pat-01")
 *         .first() as ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent>
 *
 *     // alternatively if you have access to the order request, create a flow directly.
 *
 *     val orderFlow: ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
 *       ActivityFlow.of(repository, CPGCommunicationRequest(order))
 *
 *     orderFlow.startOrder { // CPGCommunicationRequest
 *       update {  // CommunicationRequest
 *         addPayload().apply { content = StringType("Updated plan with this message ") }
 *       }
 *     }.endOrder { // CPGCommunicationRequest
 *       update {  // CommunicationRequest
 *         addPayload().apply {
 *           content = StringType("Updated newly created order with this message ")
 *         }
 *       }
 *     }
 *     val performFlow: ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
 *       ActivityFlow.of(repository, cpgCommunicationRequest)
 *     performFlow.startPerform(CPGCommunicationEvent::class.java) { // CPGCommunicationRequest
 *       update {  // CommunicationRequest
 *         status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
 *         addPayload().apply { content = StringType("Updated order with this message ") }
 *       }
 *     }.endPerform { // CPGCommunicationEvent
 *       update {  // Communication
 *         addPayload().apply {
 *           content = StringType("Updated newly created event with this message ")
 *         }
 *       }
 *     }
 *     ```
 */
@Suppress(
  "UnstableApiUsage", /* Repository is marked @Beta */
  "UNCHECKED_CAST", /* Cast type erased CPGRequestResource<*> & CPGEventResource<*> to a concrete type classes */
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
          else -> throw IllegalArgumentException("Unknown")
        }
      } else {
        throw IllegalArgumentException("Unknown")
      }
  }

  fun getCurrentPhase(): Phase {
    return currentPhase
  }

  fun draftPlan(): Result<R> {
    return PlanPhase.draft(currentPhase)
  }

  fun startPlan(inputPlan: R) =
    PlanPhase.start(repository, currentPhase, inputPlan).also { it.onSuccess { currentPhase = it } }

  fun draftOrder(): Result<R> {
    return OrderPhase.draft(currentPhase)
  }

  fun startOrder(updatedDraftOrder: R) =
    OrderPhase.start(repository, currentPhase, updatedDraftOrder).also {
      it.onSuccess { currentPhase = it }
    }

  fun <D : E> draftPerform(klass: Class<D>): Result<D> {
    return PerformPhase.draft<R, D>(klass, currentPhase)
  }

  fun <D : E> startPerform(updatedDraftPerform: D) =
    PerformPhase.start<R, D>(repository, currentPhase, updatedDraftPerform).also {
      it.onSuccess { currentPhase = it }
    }

  companion object {
    fun of(
      repository: Repository,
      resource: CPGCommunicationRequest,
    ): ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
      ActivityFlow(repository, resource)

    fun of(
      repository: Repository,
      resource: CPGCommunicationEvent,
    ): ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
      ActivityFlow(repository, null, resource)

    // order medication
    fun of(
      repository: Repository,
      resource: CPGMedicationRequest,
    ): ActivityFlow<CPGMedicationRequest, CPGEventResourceForOrderMedication<*>> =
      ActivityFlow(repository, resource)

    fun of(
      repository: Repository,
      resource: CPGEventResourceForOrderMedication<*>,
    ): ActivityFlow<CPGMedicationRequest, CPGEventResourceForOrderMedication<*>> =
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
