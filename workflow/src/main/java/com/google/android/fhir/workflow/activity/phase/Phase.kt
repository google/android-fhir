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

import com.google.android.fhir.workflow.activity.resource.event.CPGEventResource
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
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
    fun getRequest(): R

    fun update(r: R): Result<Unit>

    fun suspend(reason: String?): Result<Unit>

    fun resume(): Result<Unit>

    fun enteredInError(reason: String?): Result<Unit>

    fun reject(reason: String?): Result<Unit>
  }

  /** Activity phases for a CPG Event. */
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

fun checkEquals(a: Reference, b: Reference) = a.reference == b.reference
