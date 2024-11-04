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

import com.google.android.fhir.workflow.activity.resource.request.CPGMedicationRequest
import org.hl7.fhir.r4.model.Resource

abstract class CPGOrderMedicationEvent<out R : Resource>
internal constructor(override val resource: R, mapper: EventStatusCodeMapper) :
  CPGEventResource<R>(resource, mapper) {

  companion object {

    fun from(request: CPGMedicationRequest, eventClass: Class<*>) =
      when (eventClass) {
        CPGMedicationDispenseEvent::class.java -> CPGMedicationDispenseEvent.from(request)
        else -> throw IllegalArgumentException(" Unknown Event type $eventClass")
      }
  }
}
