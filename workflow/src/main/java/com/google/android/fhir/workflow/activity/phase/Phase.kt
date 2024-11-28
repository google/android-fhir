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

package com.google.android.fhir.workflow.activity.phase

import androidx.annotation.WorkerThread
import com.google.android.fhir.workflow.activity.resource.event.CPGEventResource
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Intent
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Reference

/** Defines the various phases of a CPG Request. */
sealed interface Phase {

  enum class PhaseName {
    PROPOSAL,
    PLAN,
    ORDER,
    PERFORM,
  }

  fun getPhaseName(): PhaseName

  /** Activity Phases for a CPG Request. */
  interface RequestPhase<R : CPGRequestResource<*>> : Phase {
    fun getRequestResource(): R

    @WorkerThread fun update(r: R): Result<Unit>

    @WorkerThread fun suspend(reason: String?): Result<Unit>

    @WorkerThread fun resume(): Result<Unit>

    @WorkerThread fun enteredInError(reason: String?): Result<Unit>

    @WorkerThread fun reject(reason: String?): Result<Unit>
  }

  /** Activity phases for a CPG Event. */
  interface EventPhase<E : CPGEventResource<*>> : Phase {
    fun getEventResource(): E

    @WorkerThread fun update(e: E): Result<Unit>

    @WorkerThread fun suspend(reason: String?): Result<Unit>

    @WorkerThread fun resume(): Result<Unit>

    @WorkerThread fun enteredInError(reason: String?): Result<Unit>

    @WorkerThread fun start(): Result<Unit>

    @WorkerThread fun notDone(reason: String?): Result<Unit>

    @WorkerThread fun stop(reason: String?): Result<Unit>

    @WorkerThread fun complete(): Result<Unit>
  }
}

/** Checks if two references are equal by equating their value. */
internal fun checkEquals(a: Reference, b: Reference) = a.reference == b.reference

/** Returns an [IdType] of a [Reference]. This is required for [Repository.read] api. */
internal val Reference.idType
  get() = IdType(reference)

/** Iterate through the previous phases of a current activity flow. */
interface PreviousPhaseIterator<R : CPGRequestResource<*>> {
  fun hasPrevious(): Boolean

  fun previous(): ReadOnlyRequestPhase<R>?
}

/** Provides a read-only view of a request phase. */
class ReadOnlyRequestPhase<R : CPGRequestResource<*>>(private val cpgRequestResource: R) {
  /** Returns the [Phase.PhaseName] of this phase. */
  fun getPhase() =
    when (cpgRequestResource.getIntent()) {
      Intent.PROPOSAL -> Phase.PhaseName.PROPOSAL
      Intent.PLAN -> Phase.PhaseName.PLAN
      Intent.ORDER -> Phase.PhaseName.ORDER
      else ->
        throw IllegalArgumentException(
          "Resource intent can't be ${cpgRequestResource.getIntent().code} ",
        )
    }

  /** Returns the underlying [CPGRequestResource] of this phase. */
  fun getRequestResource() = cpgRequestResource
}
