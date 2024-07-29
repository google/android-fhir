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

package com.google.android.fhir.workflow.activity.request

import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Reference

class CPGMedicationRequest(override val resource: MedicationRequest) :
  CPGRequestResource<MedicationRequest>(resource) {
  override fun setIntent(intent: Intent) {
    resource.intent = MedicationRequest.MedicationRequestIntent.fromCode(intent.code)
  }

  override fun getIntent() = Intent.of(resource.intent.toCode())

  override fun setStatus(status: Status) {
    resource.status = MedicationRequest.MedicationRequestStatus.fromCode(status.string)
  }

  override fun getStatus() = Status.of(resource.status.toCode())

  override fun setBasedOn(reference: Reference) {
    resource.addBasedOn(reference)
  }

  override fun getBasedOn() = resource.basedOn.lastOrNull()

  override fun copy(r: MedicationRequest) = CPGMedicationRequest(r)
}