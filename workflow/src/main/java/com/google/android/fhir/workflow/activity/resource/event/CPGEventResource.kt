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

package com.google.android.fhir.workflow.activity.resource.event

import com.google.android.fhir.logicalId
import com.google.android.fhir.workflow.activity.resource.event.CPGEventResource.Companion.of
import com.google.android.fhir.workflow.activity.resource.request.CPGCommunicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGMedicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource.Companion.of
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * This abstracts the
 * [CPG Event Resources](https://build.fhir.org/ig/HL7/cqf-recommendations/profiles.html#activity-profiles)
 * used in various activities. The various subclasses of [CPGEventResource] act as a wrapper around
 * the resource they are derived from and helps with the abstracted properties defined for each
 * [CPGEventResource]. e.g. [CPGCommunicationEvent] is a wrapper around the [Communication] and
 * helps with its [EventStatus] and basedOn [Reference]s.
 *
 * Any direct update to the [resource] can be done by using [update] api.
 *
 * The application users may use appropriate [of] static factories to create the required
 * [CPGEventResource]s.
 */
sealed class CPGEventResource<out R>(
  internal open val resource: R,
  internal val mapper: EventStatusCodeMapper,
) where R : Resource {

  val resourceType: ResourceType
    get() = resource.resourceType

  val logicalId: String
    get() = resource.logicalId

  abstract fun setStatus(status: EventStatus, reason: String? = null)

  fun getStatus(): EventStatus = mapper.mapCodeToStatus(getStatusCode())

  abstract fun getStatusCode(): String?

  abstract fun setBasedOn(reference: Reference)

  abstract fun getBasedOn(): Reference?

  abstract fun copy(): CPGEventResource<R>

  companion object {

    fun of(request: CPGRequestResource<*>, eventClass: Class<*>): CPGEventResource<*> {
      return when (request) {
        is CPGCommunicationRequest -> CPGCommunicationEvent.from(request)
        is CPGMedicationRequest -> CPGOrderMedicationEvent.from(request, eventClass)
        else -> {
          throw IllegalArgumentException("Unknown CPG Request type ${request::class}.")
        }
      }
    }
  }
}

sealed interface EventStatus {
  data object PREPARATION : EventStatus

  data object INPROGRESS : EventStatus

  data object NOTDONE : EventStatus

  data object ONHOLD : EventStatus

  data object COMPLETED : EventStatus

  data object ENTEREDINERROR : EventStatus

  data object STOPPED : EventStatus

  data object UNKNOWN : EventStatus

  class OTHER(val code: String?) : EventStatus

  companion object {

    fun of(code: String) =
      when (code) {
        "preparation" -> PREPARATION
        "in-progress" -> INPROGRESS
        "not-done" -> NOTDONE
        "on-hold" -> ONHOLD
        "completed" -> COMPLETED
        "entered-in-error" -> ENTEREDINERROR
        "stopped" -> STOPPED
        else -> OTHER(code)
      }
  }
}
