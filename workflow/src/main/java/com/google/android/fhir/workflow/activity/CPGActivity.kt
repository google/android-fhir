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

package com.google.android.fhir.workflow.activity

import org.hl7.fhir.r4.model.ResourceType

// https://build.fhir.org/ig/HL7/cqf-recommendations/profiles.html#activity-profiles
enum class CPGActivity(
  val code: String,
  val requestResourceType: ResourceType,
  val eventResourceType: ResourceType,
) {
  SEND_MESSAGE("send-message", ResourceType.CommunicationRequest, ResourceType.Communication),
  COLLECT_INFORMATION("collect-information", ResourceType.Task, ResourceType.QuestionnaireResponse),
  ORDER_MEDICATION(
    "order-medication",
    ResourceType.MedicationRequest,
    ResourceType.MedicationDispense,
  ),
  DISPENSE_MEDICATION("dispense-medication", ResourceType.Task, ResourceType.MedicationDispense),
  ADMINISTER_MEDICATION(
    "administer-medication",
    ResourceType.Task,
    ResourceType.MedicationAdministration,
  ),
  DOCUMENT_MEDICATION("document-medication", ResourceType.Task, ResourceType.MedicationStatement),
  RECOMMEND_IMMUNIZATION(
    "recommend-immunization",
    ResourceType.ImmunizationRecommendation,
    ResourceType.Immunization,
  ),
  ORDER_SERVICE("order-service", ResourceType.ServiceRequest, ResourceType.Procedure),
  ENROLLMENT("enrollment", ResourceType.Task, ResourceType.EpisodeOfCare),
  GENERATE_REPORT("generate-report", ResourceType.Task, ResourceType.Composition),
  PROPOSE_DIAGNOSIS("propose-diagnosis", ResourceType.Task, ResourceType.Condition),
  RECORD_DETECTED_ISSUE("record-detected-issue", ResourceType.Task, ResourceType.DetectedIssue),
  RECORD_INFERENCE("record-inference", ResourceType.Task, ResourceType.Observation),
  REPORT_FLAG("report-flag", ResourceType.Task, ResourceType.Flag),
}
