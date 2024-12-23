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

package com.google.android.fhir.workflow.activity.phase.event

import com.google.android.fhir.getResourceClass
import com.google.android.fhir.workflow.activity.phase.Phase
import com.google.android.fhir.workflow.activity.phase.checkEquals
import com.google.android.fhir.workflow.activity.phase.idType
import com.google.android.fhir.workflow.activity.phase.request.BaseRequestPhase
import com.google.android.fhir.workflow.activity.resource.event.CPGEventResource
import com.google.android.fhir.workflow.activity.resource.event.EventStatus
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Intent
import com.google.android.fhir.workflow.activity.resource.request.Status
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.opencds.cqf.fhir.api.Repository

/**
 * Provides implementation of the perform phase of the activity flow. See
 * [general-activity-flow](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#general-activity-flow)
 * for more info.
 */
@Suppress(
  "UnstableApiUsage", /* Repository is marked @Beta */
  "UNCHECKED_CAST", /* Cast type erased CPGRequestResource<*> & CPGEventResource<*> to a concrete type classes */
)
class PerformPhase<E : CPGEventResource<*>>(
  /** Implementation of [Repository] to store / retrieve FHIR resources. */
  private val repository: Repository,
  /** Concrete implementation of sealed [CPGEventResource] class. e.g. `CPGCommunicationEvent`. */
  e: E,
) : Phase.EventPhase<E> {
  private var event: E = e.copy() as E

  override fun getPhaseName() = Phase.PhaseName.PERFORM

  override fun getEventResource() = event.copy() as E

  override fun update(e: E) =
    runCatching<Unit> {
      // TODO Add some basic checks to make sure e is update event and not a completely different
      // resource.
      require(e.getStatus() in listOf(EventStatus.PREPARATION, EventStatus.INPROGRESS)) {
        "Status is ${e.getStatusCode()}"
      }
      repository.update(e.resource)
      event = e
    }

  override fun suspend(reason: String?) =
    runCatching<Unit> {
      check(event.getStatus() == EventStatus.INPROGRESS) {
        " Can't suspend an event with status ${event.getStatusCode()} "
      }

      event.setStatus(EventStatus.ONHOLD, reason)
      repository.update(event.resource)
    }

  override fun resume() =
    runCatching<Unit> {
      check(event.getStatus() == EventStatus.ONHOLD) {
        " Can't resume an event with status ${event.getStatusCode()} "
      }

      event.setStatus(EventStatus.INPROGRESS)
      repository.update(event.resource)
    }

  override fun enteredInError(reason: String?) =
    runCatching<Unit> {
      event.setStatus(EventStatus.ENTEREDINERROR, reason)
      repository.update(event.resource)
    }

  override fun start() =
    runCatching<Unit> {
      check(event.getStatus() == EventStatus.PREPARATION) {
        " Can't start an event with status ${event.getStatusCode()} "
      }

      event.setStatus(EventStatus.INPROGRESS)
      repository.update(event.resource)
    }

  override fun notDone(reason: String?) =
    runCatching<Unit> {
      check(event.getStatus() == EventStatus.PREPARATION) {
        " Can't not-done an event with status ${event.getStatusCode()} "
      }

      event.setStatus(EventStatus.NOTDONE, reason)
      repository.update(event.resource)
    }

  override fun stop(reason: String?) =
    runCatching<Unit> {
      check(event.getStatus() == EventStatus.INPROGRESS) {
        " Can't stop an event with status ${event.getStatusCode()} "
      }

      event.setStatus(EventStatus.STOPPED, reason)
      repository.update(event.resource)
    }

  override fun complete() =
    runCatching<Unit> {
      check(event.getStatus() == EventStatus.INPROGRESS) {
        " Can't complete an event with status ${event.getStatusCode()} "
      }

      event.setStatus(EventStatus.COMPLETED)
      repository.update(event.resource)
    }

  companion object {

    private val AllowedIntents = listOf(Intent.PROPOSAL, Intent.PLAN, Intent.ORDER)
    private val AllowedPhases =
      listOf(Phase.PhaseName.PROPOSAL, Phase.PhaseName.PLAN, Phase.PhaseName.ORDER)
    private val AllowedStatusForPhaseStart = listOf(EventStatus.INPROGRESS, EventStatus.PREPARATION)

    /**
     * Returns the [Resource] class for the resource. e.g. If the Reference is `Patient/1234`, then
     * this would return the `Class` for `org.hl7.fhir.r4.model.Patient`.
     */
    internal val Reference.`class`
      get() = getResourceClass<Resource>(reference.split("/")[0])

    /**
     * Creates a draft event of type [E] based on the state of the provided [inputPhase]. See
     * [beginPerform](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#perform)
     * for more details.
     */
    fun <R : CPGRequestResource<*>, E : CPGEventResource<*>> prepare(
      eventClass: Class<*>,
      inputPhase: Phase,
    ): Result<E> = runCatching {
      check(inputPhase.getPhaseName() in AllowedPhases) {
        "Event can't be created for a flow in ${inputPhase.getPhaseName().name} phase. "
      }

      val inputRequest = (inputPhase as BaseRequestPhase<*>).request

      check(inputRequest.getIntent() in AllowedIntents) {
        "Event can't be created for a request with ${inputRequest.getIntent()} intent."
      }

      check(inputRequest.getStatus() == Status.ACTIVE) {
        "${inputPhase.getPhaseName().name} request is still in ${inputRequest.getStatusCode()} status."
      }

      val eventRequest = CPGEventResource.from(inputRequest, eventClass)
      eventRequest.setStatus(EventStatus.PREPARATION)
      eventRequest.setBasedOn(inputRequest.asReference())
      eventRequest as E
    }

    fun <R : CPGRequestResource<*>, E : CPGEventResource<*>> initiate(
      repository: Repository,
      inputPhase: Phase,
      inputEvent: E,
    ): Result<PerformPhase<E>> = runCatching {
      check(inputPhase.getPhaseName() in AllowedPhases) {
        "A Perform can't be started for a flow in ${inputPhase.getPhaseName().name} phase."
      }

      val currentPhase = inputPhase as BaseRequestPhase<*>

      val basedOn = inputEvent.getBasedOn()
      require(basedOn != null) { "${inputEvent.resource.resourceType}.basedOn can't be null." }

      require(checkEquals(basedOn, currentPhase.request.asReference())) {
        "Provided draft is not based on the request in current phase."
      }

      val basedOnRequest =
        repository.read(basedOn.`class`, basedOn.idType)?.let { CPGRequestResource.of(it) }

      require(basedOnRequest != null) { "Couldn't find ${basedOn.reference} in the database." }

      require(basedOnRequest.getIntent() in AllowedIntents) {
        "Order can't be based on a request with ${basedOnRequest.getIntent()} intent."
      }

      require(basedOnRequest.getStatus() == Status.ACTIVE) {
        "Plan can't be based on a request with ${basedOnRequest.getStatusCode()} status."
      }

      require(inputEvent.getStatus() in AllowedStatusForPhaseStart) {
        "Input event is in ${inputEvent.getStatusCode()} status."
      }

      basedOnRequest.setStatus(Status.COMPLETED)

      repository.create(inputEvent.resource)
      repository.update(basedOnRequest.resource)
      PerformPhase(repository, inputEvent)
    }
  }
}
