package com.google.android.fhir.workflow.activity.phase

import com.google.android.fhir.workflow.activity.resource.event.CPGEventResource
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import org.hl7.fhir.r4.model.Reference

sealed interface Phase {

  enum class PhaseName {
    PROPOSAL,
    PLAN,
    ORDER,
    PERFORM,
  }

  fun getPhaseName(): PhaseName

  interface RequestPhase<R : CPGRequestResource<*>> : Phase {
    fun getRequest(): R

    fun update(r: R): Result<Unit>
    fun suspend(reason: String?): Result<Unit>
    fun resume(): Result<Unit>

    fun enteredInError(reason: String?): Result<Unit>
    fun reject(reason: String?): Result<Unit>
  }

  interface EventPhase<E : CPGEventResource<*>> : Phase {
    fun getEvent(): E

    fun update(r: E): Result<Unit>
    fun suspend(reason: String?): Result<Unit>
    fun resume(): Result<Unit>

    fun enteredInError(reason: String?): Result<Unit>
    fun start(): Result<Unit>
    fun notDone(reason: String?): Result<Unit>
    fun stop(reason: String?): Result<Unit>
    fun complete(): Result<Unit>
  }
}

fun equals(a: Reference, b: Reference) = a.reference == b.reference