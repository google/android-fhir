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

import com.google.android.fhir.workflow.activity.resource.event.EventStatus.COMPLETED
import com.google.android.fhir.workflow.activity.resource.event.EventStatus.ENTEREDINERROR
import com.google.android.fhir.workflow.activity.resource.event.EventStatus.INPROGRESS
import com.google.android.fhir.workflow.activity.resource.event.EventStatus.NOTDONE
import com.google.android.fhir.workflow.activity.resource.event.EventStatus.ONHOLD
import com.google.android.fhir.workflow.activity.resource.event.EventStatus.OTHER
import com.google.android.fhir.workflow.activity.resource.event.EventStatus.PREPARATION
import com.google.android.fhir.workflow.activity.resource.event.EventStatus.STOPPED
import com.google.android.fhir.workflow.activity.resource.event.EventStatus.UNKNOWN

/**
 * Since event resources may have different code for same status, each [CPGEventResource] should
 * provide its own mapper. See
 * [columns next to status](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-lifecycle---event-phase)
 */
internal interface EventStatusCodeMapper {
  fun mapCodeToStatus(code: String?): EventStatus

  fun mapStatusToCode(status: EventStatus): String?
}

/** A base implementation where the status and code map each other. */
internal open class EventStatusCodeMapperImpl : EventStatusCodeMapper {
  override fun mapCodeToStatus(code: String?): EventStatus {
    return when (code) {
      "preparation" -> PREPARATION
      "in-progress" -> INPROGRESS
      "not-done" -> NOTDONE
      "on-hold" -> ONHOLD
      "completed" -> COMPLETED
      "entered-in-error" -> ENTEREDINERROR
      "stopped" -> STOPPED
      "unknown" -> UNKNOWN
      else -> OTHER(code)
    }
  }

  override fun mapStatusToCode(status: EventStatus): String? {
    return when (status) {
      PREPARATION -> "preparation"
      INPROGRESS -> "in-progress"
      NOTDONE -> "not-done"
      ONHOLD -> "on-hold"
      COMPLETED -> "completed"
      ENTEREDINERROR -> "entered-in-error"
      STOPPED -> "stopped"
      UNKNOWN -> "unknown"
      is OTHER -> status.code
    }
  }
}
