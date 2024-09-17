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

import androidx.annotation.WorkerThread
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
import org.opencds.cqf.fhir.api.Repository

/**
 * Manages the workflow of clinical recommendations according to the FHIR Clinical Practice
 * Guidelines (CPG) specification. This class implements an
 * [activity flow](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-lifecycle---request-phases-proposal-plan-order),
 * allowing you to take proposals and guide them through various phases (proposal, plan, order,
 * perform) of a clinical recommendation. You can also resume existing workflows from any phase.
 *
 * **NOTE**
 * * The `prepare` and `initiate` apis of `ActivityFlow` and all apis of `Phase` interface may block
 *   the caller thread and should only be called from a worker thread.
 * * The `ActivityFlow` is not thread safe and concurrent changes to the flow/phase with multiple
 *   threads may produce undesired results.
 *
 * **Creating an ActivityFlow:**
 *
 * Use appropriate `ActivityFlow.of()` factory function to create an instance. You can start a new
 * flow with a `CPGRequestResource` or resume an existing flow from a `CPGRequestResource` or
 * `CPGEventResource` based on the last state of the flow.
 *
 * ``` kotlin
 * val request = CPGMedicationRequest(medicationRequestGeneratedByCarePlan)
 * val flow = ActivityFlow.of(repository,  request)
 * ```
 *
 * **Navigating Phases:**
 *
 * An `ActivityFlow` progresses through a series of phases, represented by the `Phase` class. You
 * can access the current phase using `getCurrentPhase()`.
 *
 * ``` kotlin
 * when (val phase = flow.getCurrentPhase( ) )  {
 * 	is Phase.ProposalPhase -> // Handle proposal phase
 * 	is Phase.PlanPhase -> // Handle plan phase
 * 	is Phase.OrderPhase -> // Handle order phase
 * 	is Phase.PerformPhase -> // Handle perform phase
 * }
 * ```
 *
 * **Transitioning Between Phases:**
 *
 * [ActivityFlow] provides functions to prepare and initiate the next phase.
 * * The prepare api creates a new request or event based on the phase and returns it back to you.
 *   It doesn't make any changes to the current phase request and also doesn't persist anything to
 *   the [repository].
 * * The initiate api creates a new phase based on the current phase and provided request/event. It
 *   does make changes to the current phase request and the provided request and persists them to
 *   the [repository]. For example, to move from the proposal phase to the plan phase:
 * ``` kotlin
 * val preparePlanResult = flow.getCurrentPhase( ).preparePlan()
 * if (preparePlanResult.isFailure) {
 * 	// Handle failure
 * }
 *
 * val preparedPlan = preparePlanResult.getOrThrow()
 * // ... modify preparedPlan
 * val planPhase = flow.getCurrentPhase().initiatePlan(preparedPlan)
 * ```
 *
 * **Note:** The specific `prepare` and `initiate` functions available depend on the current phase.
 *
 * **Transitioning to Perform Phase:**
 *
 * Since the perform creates a [CPGEventResource] and the same flow could create different event
 * resources, you need to provide the appropriate event type as a parameter to the [preparePerform].
 *
 * Example:
 * ``` kotlin
 * // Prepare and initiate the perform phase
 * val preparedPerformEvent = flow.getCurrentPhase().preparePerform(CPGMedicationDispenseEvent::class.java) . getOrThrow( )
 * // update preparedPerformEvent
 * val performPhase = flow.getCurrentPhase( ) . initiatePerform(preparedPerformEvent) . getOrThrow( )
 * ```
 *
 * **Updating states in a phase:**
 *
 * `ProposalPhase`, `PlanPhase` and `OrderPhase` are all a type of `Phase.RequestPhase` and allows
 * you to update state of the request.
 *
 * ``` kotlin
 * val planPhase = flow.getCurrentPhase().initiatePlan(preparedPlan)
 * val medicationRequest = planPhase.getRequestResource()
 * // update medicationRequest
 * planPhase.update(updated medicationRequest)
 * ```
 *
 * `PerformPhase` is a type of `Phase.EventPhase` and allows you to update the state of the event.
 *
 * ``` kotlin
 * val performPhase = ...
 * val medicationDispense = performPhase.getEventResource()
 * // update medicationDispense
 * performPhase.update(updated medicationDispense)
 * performPhase.complete()
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
              "Couldn't create the flow for ${requestResource.getIntent()} intent. Supported intents are 'proposal', 'plan' and 'order'.",
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
   * Prepares a plan resource based on the state of the [currentPhase] and returns it to the caller
   * without persisting any changes into [repository].
   *
   * @return [Result]<[R]> containing plan if the action is successful, error otherwise.
   */
  @WorkerThread
  fun preparePlan(): Result<R> {
    return PlanPhase.prepare(currentPhase)
  }

  /**
   * Initiates a plan phase based on the state of the [currentPhase] and [preparedPlan]. This api
   * will persist the [preparedPlan] into [repository].
   *
   * @return [PlanPhase] if the action is successful, error otherwise.
   */
  @WorkerThread
  fun initiatePlan(preparedPlan: R) =
    PlanPhase.initiate(repository, currentPhase, preparedPlan).also {
      it.onSuccess { currentPhase = it }
    }

  /**
   * Prepares an order resource based on the state of the [currentPhase] and returns it to the
   * caller without persisting any changes into [repository].
   *
   * @return [Result]<[R]> containing order if the action is successful, error otherwise.
   */
  @WorkerThread
  fun prepareOrder(): Result<R> {
    return OrderPhase.prepare(currentPhase)
  }

  /**
   * Initiates an order phase based on the state of the [currentPhase] and [preparePlan]. This api
   * will persist the [preparedOrder] into [repository].
   *
   * @return [OrderPhase] if the action is successful, error otherwise.
   */
  @WorkerThread
  fun initiateOrder(preparedOrder: R) =
    OrderPhase.initiate(repository, currentPhase, preparedOrder).also {
      it.onSuccess { currentPhase = it }
    }

  /**
   * Prepares an event resource based on the state of the [currentPhase] and returns it to the
   * caller without persisting any changes into [repository].
   *
   * @return [Result]<[D]> containing event if the action is successful, error otherwise.
   */
  @WorkerThread
  fun <D : E> preparePerform(klass: Class<in D>): Result<D> {
    return PerformPhase.prepare<R, D>(klass, currentPhase)
  }

  /**
   * Initiate a perform phase based on the state of the [currentPhase] and [preparePlan]. This api
   * will persist the [preparedEvent] into [repository].
   *
   * @return [PerformPhase] if the action is successful, error otherwise.
   */
  @WorkerThread
  fun <D : E> initiatePerform(preparedEvent: D) =
    PerformPhase.initiate<R, D>(repository, currentPhase, preparedEvent).also {
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
  }
}
