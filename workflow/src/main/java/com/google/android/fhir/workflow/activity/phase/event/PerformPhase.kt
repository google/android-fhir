package com.google.android.fhir.workflow.activity.phase.event

import com.google.android.fhir.workflow.activity.`class`
import com.google.android.fhir.workflow.activity.idType
import com.google.android.fhir.workflow.activity.phase.Phase
import com.google.android.fhir.workflow.activity.phase.request.BaseRequestPhase
import com.google.android.fhir.workflow.activity.resource.event.CPGEventResource
import com.google.android.fhir.workflow.activity.resource.event.EventStatus
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Intent
import com.google.android.fhir.workflow.activity.resource.request.Status
import org.opencds.cqf.fhir.api.Repository

class PerformPhase<E : CPGEventResource<*>>(val repository: Repository, e: E) :
  Phase.EventPhase<E> {
  private var event: E = e

  override fun getPhaseName() = Phase.PhaseName.PERFORM

  override fun getEvent() = event


  override fun update(e: E) = runCatching<Unit> {
    //TODO Add some basic checks to make sure e is update event and not a completely different resource.
    require(e.getStatus() in listOf(EventStatus.PREPARATION, EventStatus.INPROGRESS)) {
      "Status is ${e.getStatus()}"
    }
    repository.update(e.resource)
    event = e
  }

  override fun suspend(reason: String?) = runCatching<Unit> {
    check(event.getStatus() == EventStatus.INPROGRESS) {
      " Can't suspend an event with status ${event.getStatus()} "
    }

    event.setStatus(EventStatus.ONHOLD, reason)
    repository.update(event.resource)
  }

  override fun resume() = runCatching<Unit> {
    check(event.getStatus() == EventStatus.ONHOLD) {
      " Can't resume an event with status ${event.getStatus()} "
    }

    event.setStatus(EventStatus.INPROGRESS)
    repository.update(event.resource)
  }

  override fun enteredInError(reason: String?) = runCatching<Unit> {

    event.setStatus(EventStatus.ENTEREDINERROR, reason)
    repository.update(event.resource)
  }

  override fun start() = runCatching<Unit> {
    check(event.getStatus() == EventStatus.PREPARATION) {
      " Can't start an event with status ${event.getStatus()} "
    }

    event.setStatus(EventStatus.INPROGRESS)
    repository.update(event.resource)
  }

  override fun notDone(reason: String?) = runCatching<Unit> {
    check(event.getStatus() == EventStatus.PREPARATION) {
      " Can't not-done an event with status ${event.getStatus()} "
    }

    event.setStatus(EventStatus.NOTDONE, reason)
    repository.update(event.resource)
  }

  override fun stop(reason: String?) = runCatching<Unit> {
    check(event.getStatus() == EventStatus.INPROGRESS) {
      " Can't stop an event with status ${event.getStatus()} "
    }

    event.setStatus(EventStatus.STOPPED, reason)
    repository.update(event.resource)
  }

  override fun complete() = runCatching<Unit> {
    check(event.getStatus() == EventStatus.INPROGRESS) {
      " Can't complete an event with status ${event.getStatus()} "
    }

    event.setStatus(EventStatus.COMPLETED)
    repository.update(event.resource)
  }

  companion object {

    fun <R : CPGRequestResource<*>, E : CPGEventResource<*>> draft(
      eventClass: Class<*>,
      inputPhase: Phase
    ): Result<E> = runCatching {

      check(inputPhase is Phase.RequestPhase<*>) {
        "The api can't be called from ${inputPhase.getPhaseName().name}."
      }

      val currentPhase: Phase.RequestPhase<R> = inputPhase as Phase.RequestPhase<R>


      val acceptPhases = listOf(
        Phase.PhaseName.PROPOSAL,
        Phase.PhaseName.PLAN,
        Phase.PhaseName.ORDER
      )
      check(acceptPhases.contains(currentPhase.getPhaseName())) {
        "Order can't be created from ${currentPhase.getPhaseName().name}"
      }

      val inputOrder = (currentPhase as BaseRequestPhase<*>).getRequest()

//      check(inputOrder.getIntent() == Intent.ORDER) {
//        "Order is still in ${inputOrder.getIntent()} state."
//      }

      check(inputOrder.getStatus() == Status.ACTIVE) {
        "Order is still in ${inputOrder.getStatus()} status."
      }

      val eventRequest = CPGEventResource.of(inputOrder, eventClass)
      eventRequest.setStatus(EventStatus.PREPARATION)
      eventRequest.setBasedOn(inputOrder.asReference())
      eventRequest as E
    }

    fun <R : CPGRequestResource<*>, E : CPGEventResource<*>> start(
      repository: Repository,
      inputPhase: Phase,
      inputEvent: E
    ): Result<PerformPhase<E>> = runCatching {

      check(inputPhase is Phase.RequestPhase<*>) {
        "The api can't be called from ${inputPhase.getPhaseName().name}."
      }

      val currentPhase: Phase.RequestPhase<R> = inputPhase as Phase.RequestPhase<R>


      val basedOn = inputEvent.getBasedOn()
      require(basedOn != null) { "${inputEvent.resourceType}.basedOn shouldn't be null" }

      require(
        com.google.android.fhir.workflow.activity.phase.equals(
          basedOn,
          currentPhase.getRequest().asReference()
        )
      ) {
        "Provided draft is not based on current ${currentPhase.getPhaseName().name}."
      }

      val basedOnResource =
        repository.read(basedOn.`class`, basedOn.idType)?.let { CPGRequestResource.of(it) }

      require(basedOnResource != null) { "Couldn't find $basedOn in the database." }

//      require(basedOnResource.getIntent() == Intent.ORDER) {
//        "Proposal is still in ${basedOnResource.getIntent()} state."
//      }

      require(basedOnResource.getStatus() == Status.ACTIVE) {
        "Proposal is still in ${basedOnResource.getStatus()} status."
      }

      require(
        inputEvent.getStatus() == EventStatus.PREPARATION ||
          inputEvent.getStatus() == EventStatus.INPROGRESS,
      ) {
        "Proposal is still in ${inputEvent.getStatus()} status."
      }

      basedOnResource.setStatus(Status.COMPLETED)

      repository.create(inputEvent.resource)
      repository.update(basedOnResource.resource)
      PerformPhase(repository, inputEvent)
    }
  }
}