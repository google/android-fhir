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

import ca.uhn.fhir.model.api.IQueryParameterType
import ca.uhn.fhir.rest.param.ReferenceParam
import com.google.android.fhir.getResourceClass
import com.google.android.fhir.workflow.activity.event.CPGCommunicationEvent
import com.google.android.fhir.workflow.activity.event.CPGEventForOrderService
import com.google.android.fhir.workflow.activity.event.CPGEventResource
import com.google.android.fhir.workflow.activity.event.CPGEventResourceForOrderMedication
import com.google.android.fhir.workflow.activity.event.CPGImmunizationEvent
import com.google.android.fhir.workflow.activity.event.EventStatus
import com.google.android.fhir.workflow.activity.request.CPGCommunicationRequest
import com.google.android.fhir.workflow.activity.request.CPGImmunizationRequest
import com.google.android.fhir.workflow.activity.request.CPGMedicationRequest
import com.google.android.fhir.workflow.activity.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.request.CPGServiceRequest
import com.google.android.fhir.workflow.activity.request.CPGTaskRequest
import com.google.android.fhir.workflow.activity.request.Intent
import com.google.android.fhir.workflow.activity.request.Status
import java.util.UUID
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.Task
import org.opencds.cqf.fhir.api.Repository

private val Reference.idType
  get() = IdType(reference)

private val Reference.`class`
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
internal constructor(
  private val repository: Repository,
  val requestResource: R,
  private val eventResource: E? = null,
) :
  StartPlan<R, E>,
  EndPlan<R, E>,
  StartOrder<R, E>,
  EndOrder<R, E>,
  StartPerform<R, E>,
  EndPerform<E> {

  fun intent() = requestResource.getIntent()

  fun status() = requestResource.getStatus()

  override suspend fun startPlan(init: R.() -> Unit): EndPlan<R, E> {
    requestResource.init()
    return ActivityFlow(repository, startPlan(requestResource), null)
  }

  override suspend fun endPlan(init: R.() -> Unit): StartOrder<R, E> {
    requestResource.init()
    endPlan(requestResource)
    return this
  }

  override suspend fun startOrder(init: R.() -> Unit): EndOrder<R, E> {
    requestResource.init()
    return ActivityFlow(repository, startOrder(requestResource), null)
  }

  override suspend fun endOrder(init: R.() -> Unit): StartPerform<R, E> {
    requestResource.init()
    endOrder(requestResource)
    return this
  }

  //  The flow maybe such that the event type can only be confirmed after a certain stage, lets say
  // at endPlan or endOrder.
  override suspend fun <D : E> startPerform(klass: Class<D>, init: R.() -> Unit): EndPerform<D> {
    // The caller could still call with any event resource, but we can fail fast by checking the
    // compatibility of request and event types.
    requestResource.init()
    return ActivityFlow(repository, requestResource, startPerform(requestResource, klass) as D)
  }

  override suspend fun endPerform(init: E.() -> Unit) {
    checkNotNull(eventResource) { "No event generated." }
    eventResource.init()
    endPerform(eventResource)
  }

  private fun startPlan(inputProposal: R): R {
    check(inputProposal.getIntent() == Intent.PROPOSAL) {
      "Proposal is still in ${inputProposal.getIntent()} state."
    }

    check(inputProposal.getStatus() == Status.ACTIVE) {
      "Proposal is still in ${inputProposal.getStatus()} status."
    }

    try {
      repository.create(inputProposal.resource)
    } catch (e: Exception) {
      repository.update(inputProposal.resource)
    }

    val planRequest: CPGRequestResource<*> =
      inputProposal.copy(
        id = UUID.randomUUID().toString(),
        status = Status.DRAFT,
        intent = Intent.PLAN,
      )
    return planRequest as R
  }

  private fun endPlan(inputPlan: R) {
    val basedOn = inputPlan.getBasedOn()
    check(basedOn != null) { "${inputPlan.resource.resourceType}.basedOn shouldn't be null" }

    val basedOnProposal =
      repository.read(inputPlan.resource.javaClass, basedOn.idType)?.let {
        CPGRequestResource.of(inputPlan, it)
      }
    check(basedOnProposal != null) { "Couldn't find ${basedOn.reference} in the database." }

    check(basedOnProposal.getIntent() == Intent.PROPOSAL) {
      "Proposal is still in ${basedOnProposal.getIntent()} state."
    }

    check(basedOnProposal.getStatus() == Status.ACTIVE) {
      "Proposal is still in ${basedOnProposal.getStatus()} status."
    }

    check(inputPlan.getIntent() == Intent.PLAN) {
      "Proposal is still in ${basedOnProposal.getIntent()} state."
    }

    check(inputPlan.getStatus() == Status.DRAFT || inputPlan.getStatus() == Status.ACTIVE) {
      "Proposal is still in ${basedOnProposal.getStatus()} status."
    }

    basedOnProposal.setStatus(Status.COMPLETED)

    repository.create(inputPlan.resource)
    repository.update(basedOnProposal.resource)
  }

  private fun startOrder(inputRequest: R): R {
    check(
      inputRequest.getIntent() == Intent.PROPOSAL || inputRequest.getIntent() == Intent.PLAN,
    ) {
      "Plan is still in ${inputRequest.getIntent()} state."
    }

    check(inputRequest.getStatus() == Status.ACTIVE) {
      "Plan is still in ${inputRequest.getStatus()} status."
    }

    repository.update(inputRequest.resource)

    return inputRequest.copy(
      UUID.randomUUID().toString(),
      Status.DRAFT,
      Intent.ORDER,
    ) as R
  }

  private fun endOrder(inputOrder: R) {
    val basedOn = inputOrder.getBasedOn()
    check(basedOn != null) { "${inputOrder.resource.resourceType}.basedOn shouldn't be null" }

    val basedOnResource =
      repository.read(inputOrder.resource.javaClass, basedOn.idType)?.let {
        CPGRequestResource.of(inputOrder, it)
      }

    check(basedOnResource != null) { "Couldn't find $basedOn in the database." }

    check(
      basedOnResource.getIntent() == Intent.PROPOSAL || basedOnResource.getIntent() == Intent.PLAN,
    ) {
      "Proposal is still in ${basedOnResource.getIntent()} state."
    }

    check(basedOnResource.getStatus() == Status.ACTIVE) {
      "Proposal is still in ${basedOnResource.getStatus()} status."
    }

    check(inputOrder.getIntent() == Intent.ORDER) {
      "Proposal is still in ${basedOnResource.getIntent()} state."
    }

    check(
      inputOrder.getStatus() == Status.DRAFT || inputOrder.getStatus() == Status.ACTIVE,
    ) {
      "Proposal is still in ${basedOnResource.getStatus()} status."
    }

    basedOnResource.setStatus(Status.COMPLETED)

    repository.create(inputOrder.resource)
    repository.update(basedOnResource.resource)
  }

  private fun startPerform(inputOrder: R, eventClass: Class<*>): E {
    check(inputOrder.getIntent() == Intent.ORDER) {
      "Order is still in ${inputOrder.getIntent()} state."
    }

    check(inputOrder.getStatus() == Status.ACTIVE) {
      "Order is still in ${inputOrder.getStatus()} status."
    }

    repository.update(inputOrder.resource)

    val eventRequest = CPGEventResource.of(inputOrder, eventClass)
    eventRequest.setStatus(EventStatus.PREPARATION)
    eventRequest.setBasedOn(inputOrder.asReference())
    return eventRequest as E
  }

  private fun endPerform(inputEvent: CPGEventResource<*>) {
    val basedOn = inputEvent.getBasedOn()
    check(basedOn != null) { "${inputEvent.resourceType}.basedOn shouldn't be null" }

    val basedOnResource =
      repository.read(basedOn.`class`, basedOn.idType)?.let { CPGRequestResource.of(it) }

    check(basedOnResource != null) { "Couldn't find $basedOn in the database." }

    check(basedOnResource.getIntent() == Intent.ORDER) {
      "Proposal is still in ${basedOnResource.getIntent()} state."
    }

    check(basedOnResource.getStatus() == Status.ACTIVE) {
      "Proposal is still in ${basedOnResource.getStatus()} status."
    }

    check(
      inputEvent.getStatus() == EventStatus.PREPARATION ||
        inputEvent.getStatus() == EventStatus.INPROGRESS,
    ) {
      "Proposal is still in ${basedOnResource.getStatus()} status."
    }

    basedOnResource.setStatus(Status.COMPLETED)

    repository.create(inputEvent.resource)
    repository.update(basedOnResource.resource)
  }

  companion object {

    // Send a message
    fun of(
      repository: Repository,
      resource: CPGCommunicationRequest,
    ): CPGCommunicationActivity = ActivityFlow(repository, resource)

    // Collect information
    fun of(repository: Repository, resource: CPGTaskRequest) =
      ActivityFlow(repository, resource, null)

    // order medication
    fun of(
      repository: Repository,
      resource: CPGMedicationRequest,
    ): CPGMedicationRequestActivity = ActivityFlow(repository, resource)

    // Order a service
    fun of(
      repository: Repository,
      resource: CPGServiceRequest,
    ): ActivityFlow<CPGServiceRequest, CPGEventForOrderService<*>> =
      ActivityFlow(repository, resource)

    fun of(
      repository: Repository,
      resource: CPGImmunizationRequest,
    ): CPGRecommendImmunizationActivity = ActivityFlow(repository, resource)

    /**
     * This returns a list of flows and because of type erasure, its not possible to do a
     * filterInstance with specific CPG resource types. Instead, use list.filter and check the cpg
     * type of the [ActivityFlow.requestResource] instead.
     */
    fun of(
      repository: Repository,
      patientId: String,
    ): List<ActivityFlow<CPGRequestResource<*>, CPGEventResource<*>>> {
      val tasks =
        repository
          .search(
            Bundle::class.java,
            Task::class.java,
            mutableMapOf<String, MutableList<IQueryParameterType>>(
              Task.SUBJECT.paramName to mutableListOf(ReferenceParam("Patient/$patientId")),
            ),
            null,
          )
          .entry
          .map { it.resource }

      val medicationRequests =
        repository
          .search(
            Bundle::class.java,
            MedicationRequest::class.java,
            mutableMapOf<String, MutableList<IQueryParameterType>>(
              MedicationRequest.SUBJECT.paramName to
                mutableListOf(ReferenceParam("Patient/$patientId")),
            ),
            null,
          )
          .entry
          .map { it.resource }

      val communicationRequests =
        repository
          .search(
            Bundle::class.java,
            CommunicationRequest::class.java,
            mutableMapOf<String, MutableList<IQueryParameterType>>(
              CommunicationRequest.SUBJECT.paramName to
                mutableListOf(ReferenceParam("Patient/$patientId")),
            ),
            null,
          )
          .entry
          .map { it.resource }

      val serviceRequests =
        repository
          .search(
            Bundle::class.java,
            ServiceRequest::class.java,
            mutableMapOf<String, MutableList<IQueryParameterType>>(
              ServiceRequest.SUBJECT.paramName to
                mutableListOf(ReferenceParam("Patient/$patientId")),
            ),
            null,
          )
          .entry
          .map { it.resource }

      val cache: MutableMap<String, CPGRequestResource<*>> =
        sequenceOf(tasks, communicationRequests, serviceRequests, medicationRequests)
          .flatten()
          .map { CPGRequestResource.of(it) }
          .associateByTo(LinkedHashMap()) { "${it.resourceType}/${it.logicalId}" }

      fun addBasedOn(
        request: RequestChain<CPGRequestResource<*>>,
      ): RequestChain<CPGRequestResource<*>>? {
        request.request.getBasedOn()?.let { reference ->
          cache[reference.reference]?.let {
            cache.remove(reference.reference)
            request.basedOn = RequestChain(it, addBasedOn(RequestChain(it, null)))
            request.basedOn
          }
        }
        return null
      }

      val requestChain =
        cache.values
          .filter {
            it.getIntent() == Intent.ORDER ||
              it.getIntent() == Intent.PLAN ||
              it.getIntent() == Intent.PROPOSAL
          }
          .sortedByDescending { it.getIntent().ordinal }
          .mapNotNull {
            if (cache.containsKey("${it.resourceType}/${it.logicalId}")) {
              RequestChain(it, addBasedOn(RequestChain(it, null)))
            } else {
              null
            }
          }

      return requestChain.map { ActivityFlow(repository, it.request) }
    }
  }
}

internal data class RequestChain<R : CPGRequestResource<*>>(
  val request: CPGRequestResource<*>,
  var basedOn: RequestChain<R>?,
)

// Send a message
typealias CPGCommunicationActivity = ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent>

// Order a service
typealias CPGServiceRequestActivity = ActivityFlow<CPGServiceRequest, CPGEventForOrderService<*>>

// Order a medication
typealias CPGMedicationRequestActivity =
  ActivityFlow<CPGMedicationRequest, CPGEventResourceForOrderMedication<*>>

// Recommend an immunization
typealias CPGRecommendImmunizationActivity =
  ActivityFlow<CPGImmunizationRequest, CPGImmunizationEvent>
