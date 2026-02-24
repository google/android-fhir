/*
 * Copyright 2024-2026 Google LLC
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

package com.google.android.fhir.workflow.demo.ui.main

import com.google.android.fhir.workflow.activity.ActivityFlow
import com.google.android.fhir.workflow.activity.phase.Phase
import com.google.android.fhir.workflow.activity.resource.event.CPGEventResource
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Status

class ActivityHandler(
  val activityFlow: ActivityFlow<CPGRequestResource<*>, CPGEventResource<*>>,
) {

  suspend fun prepareAndInitiatePlan(): Result<Boolean> {
    // Get the current phase.
    val currentPhase = activityFlow.getCurrentPhase() as Phase.RequestPhase<CPGRequestResource<*>>
    // ActivityFlow returns a copy of the resource it has, so after updating the returned
    // resource, give it back to the ActivityFlow by calling update api on the phase.
    currentPhase.update(
      currentPhase.getRequestResource().apply { setStatus(Status.ACTIVE) },
    )

    val preparedPlan = activityFlow.preparePlan()
    if (preparedPlan.isFailure) return Result.failure(preparedPlan.exceptionOrNull()!!)

    val initiatedPlan = activityFlow.initiatePlan(preparedPlan.getOrThrow())

    return if (initiatedPlan.isFailure) {
      Result.failure(initiatedPlan.exceptionOrNull()!!)
    } else {
      Result.success(true)
    }
  }

  suspend fun prepareAndInitiateOrder(): Result<Boolean> {
    // Get the current phase.
    val currentPhase = activityFlow.getCurrentPhase() as Phase.RequestPhase<CPGRequestResource<*>>
    // ActivityFlow returns a copy of the resource it has, so after updating the returned
    // resource, give it back to the ActivityFlow by calling update api on the phase.
    currentPhase.update(
      currentPhase.getRequestResource().apply { setStatus(Status.ACTIVE) },
    )

    val preparedOrder = activityFlow.prepareOrder()
    if (preparedOrder.isFailure) return Result.failure(preparedOrder.exceptionOrNull()!!)

    val initiatedOrder = activityFlow.initiateOrder(preparedOrder.getOrThrow())

    return if (initiatedOrder.isFailure) {
      Result.failure(initiatedOrder.exceptionOrNull()!!)
    } else {
      Result.success(true)
    }
  }

  suspend fun prepareAndInitiatePerform(clazz: Class<CPGEventResource<*>>): Result<Boolean> {
    val currentPhase = activityFlow.getCurrentPhase() as Phase.RequestPhase<CPGRequestResource<*>>
    // ActivityFlow returns a copy of the resource it has, so after updating the returned
    // resource, give it back to the ActivityFlow by calling update api on the phase.
    currentPhase.update(
      currentPhase.getRequestResource().apply { setStatus(Status.ACTIVE) },
    )

    val preparedEvent = activityFlow.preparePerform(clazz)
    if (preparedEvent.isFailure) return Result.failure(preparedEvent.exceptionOrNull()!!)

    val initiatedEvent = activityFlow.initiatePerform(preparedEvent.getOrThrow())

    return if (initiatedEvent.isFailure) {
      Result.failure(initiatedEvent.exceptionOrNull()!!)
    } else {
      Result.success(true)
    }
  }
}
