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

import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.Reference

class CPGCaseEvent(override val resource: EpisodeOfCare) :
  CPGEventResource<EpisodeOfCare>(resource) {
  override fun setStatus(status: EventStatus, reason: String?) {
    resource.status = EpisodeOfCare.EpisodeOfCareStatus.fromCode(status.code)
  }

  override fun getStatus() = EventStatus.of(resource.status.toCode())

  override fun setBasedOn(reference: Reference) {
    resource.addReferralRequest(reference)
  }

  override fun getBasedOn() = resource.referralRequest.lastOrNull()

  override fun copy() = CPGCaseEvent(resource.copy())
}
