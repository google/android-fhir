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

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.search
import java.util.UUID
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.Communication.CommunicationPriority
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.Composition
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DetectedIssue
import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.Flag
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.ImmunizationRecommendation
import org.hl7.fhir.r4.model.MedicationAdministration
import org.hl7.fhir.r4.model.MedicationDispense
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.MedicationStatement
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Task


// Known issues:
// 1. RECOMMEND_IMMUNIZATION CGPActivity activity says its based on
//  [MedicationRequest](https://build.fhir.org/ig/HL7/cqf-recommendations/activityflow.html#activity-lifecycle---request-phases-proposal-plan-order)
// but the examples have
//[ImmunizationRecommendation](https://hl7.org/fhir/uv/cpg/ActivityDefinition-activity-example-recommendimmunization.json.html).


/**
 * Logical model for the Request resources as per the Clinical Practice Guidelines.
 */
internal open class Request<R : Resource>(val resource: R) {

  val intent: Intent
    get() {
      return when (resource) {

        // SEND_MESSAGE
        is CommunicationRequest -> {
          // TODO: CommunicationRequest has no intent field

//          RequestIntent
          resource.getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/request-intent")?.let {
            Intent.of((it.value.primitiveValue()))
          } ?:
          Intent.PROPOSAL
          // Intent.of(request.intent.toCode())
        }

        // COLLECT_INFORMATION
        // DISPENSE_MEDICATION
        // ADMINISTER_MEDICATION
        // DOCUMENT_MEDICATION
        // ENROLLMENT
        // GENERATE_REPORT
        // PROPOSE_DIAGNOSIS
        // RECORD_DETECTED_ISSUE
        // RECORD_INFERENCE
        // REPORT_FLAG

        is Task -> {
          Intent.of(resource.intent.toCode())
        }
        // ORDER_MEDICATION
        // RECOMMEND_IMMUNIZATION
        is MedicationRequest -> {
          Intent.of(resource.intent.toCode())
        }

        // ORDER_SERVICE
        is ServiceRequest -> {
          Intent.of(resource.intent.toCode())
        }
        else -> Intent.NULL
      }
    }

  val status: Status
    get() {
      return when (resource) {

        // SEND_MESSAGE
        is CommunicationRequest -> {
          Status.of(resource.status.toCode())
        }

        // COLLECT_INFORMATION
        // DISPENSE_MEDICATION
        // ADMINISTER_MEDICATION
        // DOCUMENT_MEDICATION
        // ENROLLMENT
        // GENERATE_REPORT
        // PROPOSE_DIAGNOSIS
        // RECORD_DETECTED_ISSUE
        // RECORD_INFERENCE
        // REPORT_FLAG

        is Task -> {
          Status.of(resource.status.toCode())
        }
        // ORDER_MEDICATION
        // RECOMMEND_IMMUNIZATION
        is MedicationRequest -> {
          Status.of(resource.status.toCode())
        }

        // ORDER_SERVICE
        is ServiceRequest -> {
          Status.of(resource.status.toCode())
        }
        else -> Status.NULL
      }
    }

  enum class Intent(val code: String) {
    PROPOSAL("proposal"),
    PLAN("plan"),
    DIRECTIVE("directive"),
    ORDER("order"),
    ORIGINALORDER("original-order"),
    REFLEXORDER("reflex-order"),
    FILLERORDER("filler-order"),
    INSTANCEORDER("instance-order"),
    OPTION("option"),
    NULL("null"),
    ;

    companion object {
      fun of(code: String): Intent {
        return when (code) {
          "proposal" -> PROPOSAL
          "plan" -> PLAN
          "directive" -> DIRECTIVE
          "order" -> ORDER
          "original-order" -> ORIGINALORDER
          "reflex-order" -> REFLEXORDER
          "filler-order" -> FILLERORDER
          "instance-order" -> INSTANCEORDER
          "option" -> OPTION
          else -> NULL
        }
      }
    }
  }

  /**
   * This may not represent all the Request Resource status. For the activity flow, we may ony be
   * interested in a few values and they should be represented here.
   */
  enum class Status(val string: String) {
    DRAFT("draft"),
    ACTIVE("active"),
    ONHOLD("on-hold"),
    REVOKED("revoked"),
    COMPLETED("completed"),
    ENTEREDINERROR("entered-in-error"),
    UNKNOWN("unknown"),
    NULL("null"),
    ;

    companion object {
      fun of(code: String): Status {
        return when (code) {
          "draft" -> DRAFT
          "active" -> ACTIVE
          "on-hold" -> ONHOLD
          "revoked" -> REVOKED
          "completed" -> COMPLETED
          "entered-in-error" -> ENTEREDINERROR
          "unknown" -> UNKNOWN
          "null" -> NULL
          else -> NULL
        }
      }
    }
  }

  fun copy(id: String, status: Status, intent: Intent): Request<Resource> {
    val parent = this
    return Request(parent.resource.copy()).apply {
      resource.idElement = IdType.of(resource).setValue(id)
      setStatus(status)
      setIntent(intent)
      setBasedOn(Reference("${parent.resource.resourceType}/${parent.resource.logicalId}"))
    }.also {
      println(" Request-created ${it.resource.id}   ${it.getBasedOn()!!.reference}")
    }
  }

   fun setStatus(status: Status) {
    when (resource) {
      // SEND_MESSAGE
      is CommunicationRequest -> {
        resource.status = CommunicationRequest.CommunicationRequestStatus.fromCode(status.string)
      }

      // COLLECT_INFORMATION
      // DISPENSE_MEDICATION
      // ADMINISTER_MEDICATION
      // DOCUMENT_MEDICATION
      // ENROLLMENT
      // GENERATE_REPORT
      // PROPOSE_DIAGNOSIS
      // RECORD_DETECTED_ISSUE
      // RECORD_INFERENCE
      // REPORT_FLAG

      is Task -> {
        resource.status = Task.TaskStatus.fromCode(status.string)
      }
      // ORDER_MEDICATION
      // RECOMMEND_IMMUNIZATION
      is MedicationRequest -> {
        resource.status = MedicationRequest.MedicationRequestStatus.fromCode(status.string)
      }

      // ORDER_SERVICE
      is ServiceRequest -> {
        resource.status = ServiceRequest.ServiceRequestStatus.fromCode(status.string)
      }
    }
  }

   fun setIntent(intent: Intent) {
    when (resource) {
      // SEND_MESSAGE
      is CommunicationRequest -> {
        // TODO: Check what to do here
        if (resource.hasExtension("http://hl7.org/fhir/StructureDefinition/request-intent")) {
          resource.getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/request-intent").setValue(StringType(intent.code))
        } else {
          resource.addExtension("http://hl7.org/fhir/StructureDefinition/request-intent", StringType(intent.code))
        }
      }

      // COLLECT_INFORMATION
      // DISPENSE_MEDICATION
      // ADMINISTER_MEDICATION
      // DOCUMENT_MEDICATION
      // ENROLLMENT
      // GENERATE_REPORT
      // PROPOSE_DIAGNOSIS
      // RECORD_DETECTED_ISSUE
      // RECORD_INFERENCE
      // REPORT_FLAG

      is Task -> {
        resource.intent = Task.TaskIntent.fromCode(intent.code)
      }
      // ORDER_MEDICATION
      // RECOMMEND_IMMUNIZATION
      is MedicationRequest -> {
        resource.intent = MedicationRequest.MedicationRequestIntent.fromCode(intent.code)
      }

      // ORDER_SERVICE
      is ServiceRequest -> {
        resource.intent = ServiceRequest.ServiceRequestIntent.fromCode(intent.code)
      }
    }
  }

  private fun setBasedOn(basedOn: Reference) {
    when (resource) {
      // SEND_MESSAGE
      is CommunicationRequest -> {
        resource.addBasedOn(basedOn)
      }

      // COLLECT_INFORMATION
      // DISPENSE_MEDICATION
      // ADMINISTER_MEDICATION
      // DOCUMENT_MEDICATION
      // ENROLLMENT
      // GENERATE_REPORT
      // PROPOSE_DIAGNOSIS
      // RECORD_DETECTED_ISSUE
      // RECORD_INFERENCE
      // REPORT_FLAG

      is Task -> {
        resource.addBasedOn(basedOn)
      }
      // ORDER_MEDICATION
      // RECOMMEND_IMMUNIZATION
      is MedicationRequest -> {
        resource.addBasedOn(basedOn)
      }

      // ORDER_SERVICE
      is ServiceRequest -> {
        resource.addBasedOn(basedOn)
      }
    }
  }

   fun getBasedOn() : Reference?{
   return when (resource) {
      // SEND_MESSAGE
      is CommunicationRequest -> {
        resource.basedOn.lastOrNull()
      }

      // COLLECT_INFORMATION
      // DISPENSE_MEDICATION
      // ADMINISTER_MEDICATION
      // DOCUMENT_MEDICATION
      // ENROLLMENT
      // GENERATE_REPORT
      // PROPOSE_DIAGNOSIS
      // RECORD_DETECTED_ISSUE
      // RECORD_INFERENCE
      // REPORT_FLAG

      is Task -> {
        resource.basedOn.lastOrNull()
      }
      // ORDER_MEDICATION
      // RECOMMEND_IMMUNIZATION
      is MedicationRequest -> {
        resource.basedOn.lastOrNull()
      }

      // ORDER_SERVICE
      is ServiceRequest -> {
        resource.basedOn.lastOrNull()
      }

     else -> {null}
   }
  }

  suspend fun createEventResource(fhirEngine: FhirEngine): Event<Resource> {
    // This needs to be filled
    lateinit var activity: CPGActivity
    if ( 1 != 0) {
      activity =  CPGActivity.SEND_MESSAGE
    }
    val event =
      when (activity) {
        CPGActivity.SEND_MESSAGE -> {
          Communication().apply {
            id = UUID.randomUUID().toString()
            status = Communication.CommunicationStatus.PREPARATION
            if (resource is CommunicationRequest) {
              category = resource.category
              priority = CommunicationPriority.fromCode(resource.priority?.toCode())
              subject = resource.subject
              about = resource.about
              encounter = resource.encounter
              recipient = resource.recipient
              resource.payload.forEach {
                addPayload(Communication.CommunicationPayloadComponent(it.content))
              }
            }
          }
        }

        CPGActivity.COLLECT_INFORMATION -> {
          QuestionnaireResponse().apply {
            id = UUID.randomUUID().toString()
            status = QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS
            if (resource is Task) {
              subject = resource.`for`
              encounter = resource.encounter
            }
          }
        }

        CPGActivity.ORDER_MEDICATION -> {
          // The values here will come from MedicationRequest.
          MedicationDispense().apply {
            id = UUID.randomUUID().toString()
            status = MedicationDispense.MedicationDispenseStatus.PREPARATION

            if (resource is MedicationRequest) {
              subject = resource.subject
              medication = resource.medication
              // TODO: Find appropriate type value
              //            type = r.performerType
              dosageInstruction = resource.dosageInstruction
              // TODO Maybe the client should create this as they would need to fill in
              //            quantity = r.dosageInstruction.filter { "doseQuantity"  }
              //            daysSupply = ???
              //            whenPrepared = ???
            }
          }
        }
        CPGActivity.DISPENSE_MEDICATION -> {
          // The values here will come from MedicationRequest.
          MedicationDispense().apply {
            id = UUID.randomUUID().toString()
            status = MedicationDispense.MedicationDispenseStatus.PREPARATION
            if (resource is Task) {
              //            "input" : [
              //            {
              //              "type" : {
              //              "coding" : [
              //              {
              //                "system" :
              // "http://hl7.org/fhir/uv/cpg/CodeSystem/cpg-activity-type",
              //                "code" : "order-medication",
              //                "display" : "Order a medication"
              //              }
              //              ]
              //            },
              //              "valueReference" : {
              //              "reference" : "MedicationRequest/dm-scenario2"
              //            }
              //            }
              //            ]
              val medicationRequestReference = resource.input.first().value as Reference
              fhirEngine.search(medicationRequestReference.reference).first().resource.let { r ->
                if (r is MedicationRequest) {
                  subject = r.subject
                  medication = r.medication

                  // TODO: Find appropriate type value
                  //            type = r.performerType
                  dosageInstruction = r.dosageInstruction
                  // TODO Maybe the client should create this as they would need to fill in
                  //            quantity = r.dosageInstruction.filter { "doseQuantity"  }
                  //            daysSupply = ???
                  //            whenPrepared = ???
                }
              }
            }
          }
        }
        CPGActivity.ADMINISTER_MEDICATION -> {
          // The values here will come from MedicationRequest.
          MedicationAdministration().apply {
            id = UUID.randomUUID().toString()
            status = MedicationAdministration.MedicationAdministrationStatus.UNKNOWN
            if (this@Request.resource is Task) {
              //            "input" : [
              //            {
              //              "type" : {
              //              "coding" : [
              //              {
              //                "system" :
              // "http://hl7.org/fhir/uv/cpg/CodeSystem/cpg-activity-type",
              //                "code" : "administer-medication",
              //                "display" : "Administer a medication"
              //              }
              //              ]
              //            },
              //              "valueReference" : {
              //              "reference" : "MedicationRequest/am-scenario2"
              //            }
              //            }
              //            ]
              val medicationRequestReference = this@Request.resource.input.first().value as Reference
              fhirEngine.search(medicationRequestReference.reference).first().resource.let { r ->
                if (r is MedicationRequest) {
                  subject = r.subject
                  medication = r.medication
                  request = medicationRequestReference
                  // TODO: Find appropriate type value
                  //            type = r.performerType
                  // TODO Maybe the client should create this as they would need to fill in
                  // effectivePeriod = ??
                  //                dosage = r.dosageInstruction ??
                  medication = r.medication
                }
              }
            }
          }
        }
        CPGActivity.DOCUMENT_MEDICATION -> {
          MedicationStatement().apply {
            id = UUID.randomUUID().toString()
            status = MedicationStatement.MedicationStatementStatus.UNKNOWN
            if (resource is Task) {
              //            "input" : [
              //            {
              //              "type" : {
              //              "coding" : [
              //              {
              //                "system" :
              // "http://hl7.org/fhir/uv/cpg/CodeSystem/cpg-activity-type",
              //                "code" : "administer-medication",
              //                "display" : "Administer a medication"
              //              }
              //              ]
              //            },
              //              "valueReference" : {
              //              "reference" : "MedicationRequest/am-scenario2"
              //            }
              //            }
              //            ]
              val medicationRequestReference = resource.input.first().value as Reference
              fhirEngine.search(medicationRequestReference.reference).first().resource.let { r ->
                if (r is MedicationRequest) {
                  subject = r.subject
                  medication = r.medication
                  addDerivedFrom(medicationRequestReference)
                  informationSource = resource.`for`

                  // TODO Maybe the client should create this as they would need to fill in
                  //                effectivePeriod = ??
                  //                effectiveDateTimeType =??
                }
              }
            }
          }
        }
        CPGActivity.RECOMMEND_IMMUNIZATION -> {
          Immunization().apply {
            id = UUID.randomUUID().toString()
            status = Immunization.ImmunizationStatus.NOTDONE
            if (resource is ImmunizationRecommendation) {
              vaccineCode = resource.recommendationFirstRep.vaccineCode.first()
              patient = resource.patient
              // TODO Maybe the client should create this as they would need to fill in
              //          occurrence = ?? DateTime.now()
            }
          }
        }
        CPGActivity.ORDER_SERVICE -> {
          Procedure().apply {
            id = UUID.randomUUID().toString()
            status = Procedure.ProcedureStatus.UNKNOWN
            if (resource is ServiceRequest) {
              bodySite = resource.bodySite
              subject = resource.subject
              reasonCode = resource.reasonCode
              code = resource.code
              category = resource.category.first()
            }
          }
        }
        CPGActivity.ENROLLMENT -> {
          EpisodeOfCare().apply { id = UUID.randomUUID().toString() }
        }
        CPGActivity.GENERATE_REPORT -> {
          Composition().apply { id = UUID.randomUUID().toString() }
        }
        CPGActivity.PROPOSE_DIAGNOSIS -> {
          Condition().apply { id = UUID.randomUUID().toString() }
        }
        CPGActivity.RECORD_DETECTED_ISSUE -> {
          DetectedIssue().apply { id = UUID.randomUUID().toString() }
        }
        CPGActivity.RECORD_INFERENCE -> {
          Observation().apply { id = UUID.randomUUID().toString() }
        }
        CPGActivity.REPORT_FLAG -> {
          Flag().apply { id = UUID.randomUUID().toString() }
        }
      }
    return Event(event)
  }

  fun asReference()  = Reference("${resource.resourceType}/${resource.logicalId}")
}
