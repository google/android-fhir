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

import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.Composition
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DetectedIssue
import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.Flag
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.MedicationAdministration
import org.hl7.fhir.r4.model.MedicationDispense
import org.hl7.fhir.r4.model.MedicationStatement
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource

/**
 * Logical model for the event resources as per the Clinical Practice Guidelines.
 */
internal class Event<R : Resource>(val resource: R) {

  /**
   * This may not represent all the Event Resource status. For the activity flow, we may ony be
   * interested in a few values and they should be represented here.
   */
  enum class Status(val code: String) {
    PREPARATION("preparation"),
    INPROGRESS("in-progress"),
    CANCELLED("not-done"),
    ONHOLD("on-hold"),
    COMPLETED("completed"),
    ENTEREDINERROR("entered-in-error"),
    STOPPED("stopped"),
    DECLINED("decline"),
    UNKNOWN("unknown"),
    NULL("null");

    companion object {

     fun of (code: String) = when (code) {
       "preparation" -> PREPARATION
         "in-progress" -> INPROGRESS
         "not-done" -> CANCELLED
         "on-hold" ->ONHOLD
         "completed" -> COMPLETED
         "entered-in-error" -> ENTEREDINERROR
         "stopped" -> STOPPED
         "decline" -> DECLINED
         "unknown" -> UNKNOWN
         "null" ->  NULL
         else -> UNKNOWN
     }
    }
  }

  fun setStatus(status: Status) {
    when (resource) {
      // SEND_MESSAGE
      is Communication -> {
        resource.status = Communication.CommunicationStatus.fromCode(status.code)
      }
      // COLLECT_INFORMATION
      is QuestionnaireResponse -> {
        resource.status = QuestionnaireResponse.QuestionnaireResponseStatus.fromCode(status.code)
      }

      //      ORDER_MEDICATION
      //      DISPENSE_MEDICATION
      is MedicationDispense -> {
        resource.status = MedicationDispense.MedicationDispenseStatus.fromCode(status.code)
      }

      //      ADMINISTER_MEDICATION
      is MedicationAdministration -> {
        resource.status =
          MedicationAdministration.MedicationAdministrationStatus.fromCode(status.code)
      }

      //      DOCUMENT_MEDICATION
      is MedicationStatement -> {
        resource.status = MedicationStatement.MedicationStatementStatus.fromCode(status.code)
      }

      //      RECOMMEND_IMMUNIZATION
      is Immunization -> {
        resource.status = Immunization.ImmunizationStatus.fromCode(status.code)
      }

      //      ORDER_SERVICE
      is Procedure -> {
        resource.status = Procedure.ProcedureStatus.fromCode(status.code)
      }
      //      ENROLLMENT
      is EpisodeOfCare -> {
        resource.status = EpisodeOfCare.EpisodeOfCareStatus.fromCode(status.code)
      }
      // GENERATE_REPORT
      is Composition -> {
        resource.status = Composition.CompositionStatus.fromCode(status.code)
      }
      // PROPOSE_DIAGNOSIS
      is Condition -> {
        // TODO : Check as based on activityflow.html, the clinicalStatus and verificationStatus are
        // present but neither map to event status.
        //        resource.status =
      }

      // RECORD_DETECTED_ISSUE
      is DetectedIssue -> {
        resource.status = DetectedIssue.DetectedIssueStatus.fromCode(status.code)
      }

      // RECORD_INFERENCE
      is Observation -> {
        resource.status = Observation.ObservationStatus.fromCode(status.code)
      }

      // REPORT_FLAG
      is Flag -> {
        resource.status = Flag.FlagStatus.fromCode(status.code)
      }
    }
  }

  fun getStatus(): Status {
    return when (resource) {
      // SEND_MESSAGE
      is Communication -> {
        Status.of(resource.status.toCode())

      }
      // COLLECT_INFORMATION
      is QuestionnaireResponse -> {
        Status.of(resource.status.toCode())
      }

      //      ORDER_MEDICATION
      //      DISPENSE_MEDICATION
      is MedicationDispense -> {
        Status.of(resource.status.toCode())
      }

      //      ADMINISTER_MEDICATION
      is MedicationAdministration -> {
        Status.of(resource.status.toCode())
      }

      //      DOCUMENT_MEDICATION
      is MedicationStatement -> {
        Status.of(resource.status.toCode())
      }

      //      RECOMMEND_IMMUNIZATION
      is Immunization -> {
        Status.of(resource.status.toCode())
      }

      //      ORDER_SERVICE
      is Procedure -> {
        Status.of(resource.status.toCode())
      }
      //      ENROLLMENT
      is EpisodeOfCare -> {
        Status.of(resource.status.toCode())
      }
      // GENERATE_REPORT
      is Composition -> {
        Status.of(resource.status.toCode())
      }
      // PROPOSE_DIAGNOSIS
      is Condition -> {
        // TODO : Check as based on activityflow.html, the clinicalStatus and verificationStatus are
        // present but neither map to event status.
        //        resource.status =
        Status.UNKNOWN
      }

      // RECORD_DETECTED_ISSUE
      is DetectedIssue -> {
        Status.of(resource.status.toCode())
      }

      // RECORD_INFERENCE
      is Observation -> {
        Status.of(resource.status.toCode())
      }

      // REPORT_FLAG
      is Flag -> {
        Status.of(resource.status.toCode())
      }
      else -> {Status.NULL}
    }
  }


  fun getBasedOn() : Reference? {
    return when (resource) {
      // SEND_MESSAGE
      is Communication -> {
        resource.basedOn.firstOrNull()
      }
      // COLLECT_INFORMATION
      is QuestionnaireResponse -> {
        resource.basedOn.firstOrNull()
      }

      //      ORDER_MEDICATION
      //      DISPENSE_MEDICATION
      is MedicationDispense -> {
        resource.authorizingPrescription.firstOrNull()
      }

      //      ADMINISTER_MEDICATION
      is MedicationAdministration -> {
        resource.request
      }

      //      DOCUMENT_MEDICATION
      is MedicationStatement -> {
        resource.basedOn.firstOrNull()
      }

      //      RECOMMEND_IMMUNIZATION
      is Immunization -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
        null
      }

      //      ORDER_SERVICE
      is Procedure -> {
        resource.basedOn.firstOrNull()
      }
      //      ENROLLMENT
      is EpisodeOfCare -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
        null
      }
      // GENERATE_REPORT
      is Composition -> {
        resource
          .getExtensionByUrl(" http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-summaryFor")
          .value as Reference
      }
      // PROPOSE_DIAGNOSIS
      is Condition -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
        null
      }

      // RECORD_DETECTED_ISSUE
      is DetectedIssue -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
        null
      }

      // RECORD_INFERENCE
      is Observation -> {
        resource.basedOn.firstOrNull()
      }

      // REPORT_FLAG
      is Flag -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
        null
      }

      else -> { null}
    }
  }

  fun <R : Resource> setBasedOn(basedOn: Request<R>) {
    when (resource) {
      // SEND_MESSAGE
      is Communication -> {
        resource.addBasedOn(basedOn.asReference())
      }
      // COLLECT_INFORMATION
      is QuestionnaireResponse -> {
        resource.addBasedOn(basedOn.asReference())
      }

      //      ORDER_MEDICATION
      //      DISPENSE_MEDICATION
      is MedicationDispense -> {
        resource.addAuthorizingPrescription(basedOn.asReference())
      }

      //      ADMINISTER_MEDICATION
      is MedicationAdministration -> {
        resource.request = basedOn.asReference()
      }

      //      DOCUMENT_MEDICATION
      is MedicationStatement -> {
        resource.addBasedOn(basedOn.asReference())
      }

      //      RECOMMEND_IMMUNIZATION
      is Immunization -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
      }

      //      ORDER_SERVICE
      is Procedure -> {
        resource.addBasedOn(basedOn.asReference())
      }
      //      ENROLLMENT
      is EpisodeOfCare -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
      }
      // GENERATE_REPORT
      is Composition -> {
        if (resource.hasExtension(" http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-summaryFor")) {
          resource
            .getExtensionByUrl(" http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-summaryFor")
            .setValue(basedOn.asReference())
        } else {
          resource
            .addExtension(" http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-summaryFor", basedOn.asReference())
        }

      }
      // PROPOSE_DIAGNOSIS
      is Condition -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
      }

      // RECORD_DETECTED_ISSUE
      is DetectedIssue -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
      }

      // RECORD_INFERENCE
      is Observation -> {
        resource.addBasedOn(basedOn.asReference())
      }

      // REPORT_FLAG
      is Flag -> {
        // TODO: Not present
        //        resource.addBasedOn(Reference(basedOn.request))
      }
    }
  }
}
