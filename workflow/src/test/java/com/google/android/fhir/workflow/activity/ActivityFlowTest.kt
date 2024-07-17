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

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.logicalId
import com.google.android.fhir.workflow.activity.event.CPGCommunicationEvent
import com.google.android.fhir.workflow.activity.event.CPGMedicationAdministrationEvent
import com.google.android.fhir.workflow.activity.event.CPGMedicationDispenseEvent
import com.google.android.fhir.workflow.activity.event.CPGObservationEvent
import com.google.android.fhir.workflow.activity.event.CPGProcedureEvent
import com.google.android.fhir.workflow.activity.request.CPGCommunicationRequest
import com.google.android.fhir.workflow.activity.request.CPGImmunizationRequest
import com.google.android.fhir.workflow.activity.request.CPGMedicationRequest
import com.google.android.fhir.workflow.activity.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.request.Intent
import com.google.android.fhir.workflow.repositories.FhirEngineRepository
import com.google.android.fhir.workflow.runBlockingOnWorkerThread
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import kotlin.test.assertFailsWith
import org.hl7.fhir.r4.model.Annotation
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.MarkdownType
import org.hl7.fhir.r4.model.MedicationAdministration
import org.hl7.fhir.r4.model.MedicationDispense
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.StringType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityFlowTest {

  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()
  private lateinit var fhirEngine: FhirEngine

  @Before
  fun setupTest() {
    val context: Context = ApplicationProvider.getApplicationContext()
    fhirEngine = FhirEngineProvider.getInstance(context)
  }

  @Test
  fun `communication request flow5`(): Unit = runBlockingOnWorkerThread {
    val communicationRequest1 =
      CommunicationRequest().apply {
        id = "com-req-01"
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        subject = Reference("Patient/pat-01")
        addPayload().apply { content = StringType("Proposal") }
      }
    fhirEngine.create(communicationRequest1)
    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)

    ActivityFlow.of(repository, CPGRequestResource.of(communicationRequest1))
      .startPlan { update { addPayload().apply { content = StringType("Start Plan") } } }
      .endPlan { update { addPayload().apply { content = StringType("End Plan") } } }

    val communicationRequest2 =
      CommunicationRequest().apply {
        id = "com-req-02"
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        subject = Reference("Patient/pat-01")
        addPayload().apply { content = StringType("Proposal") }
      }
    fhirEngine.create(communicationRequest2)

    ActivityFlow.of(repository, CPGRequestResource.of(communicationRequest2))
      .startPlan { update { addPayload().apply { content = StringType("Start Plan") } } }
      .endPlan { update { addPayload().apply { content = StringType("End Plan") } } }
      .startOrder {
        update {
          status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
          addPayload().apply { content = StringType("Start Order") }
        }
      }
      .endOrder { update { addPayload().apply { content = StringType("End Order") } } }

    val communicationRequest3 =
      CommunicationRequest().apply {
        id = "com-req-03"
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        subject = Reference("Patient/pat-01")
        addPayload().apply { content = StringType("Proposal") }
      }
    fhirEngine.create(communicationRequest3)

    ActivityFlow.of(repository, CPGRequestResource.of(communicationRequest3))
      .startPlan { update { addPayload().apply { content = StringType("Start Plan") } } }
      .endPlan { update { addPayload().apply { content = StringType("End Plan") } } }
      .startOrder {
        update {
          status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
          addPayload().apply { content = StringType("Start Order") }
        }
      }
      .endOrder { update { addPayload().apply { content = StringType("End Order") } } }
    //      .startPerform(Communication::class.java) {
    //       update {
    //         status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
    //         addPayload().apply { content = StringType("Start Perform") }
    //       }
    //      }

    val communicationRequest4 =
      CommunicationRequest().apply {
        id = "com-req-04"
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        subject = Reference("Patient/pat-01")
        addPayload().apply { content = StringType("Proposal") }
      }
    fhirEngine.create(communicationRequest4)

    ActivityFlow.of(repository, CPGRequestResource.of(communicationRequest4))
      //    CPG_CommunicationActivityFlow(repository, CPGRequestResource.of(communicationRequest4),
      // null)
      .startPlan { update { addPayload().apply { content = StringType("Start Plan") } } }
      .endPlan { update { addPayload().apply { content = StringType("End Plan") } } }
      .startOrder {
        update {
          status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
          addPayload().apply { content = StringType("Start Order") }
        }
      }
      .endOrder { update { addPayload().apply { content = StringType("End Order") } } }
    //      .startPerform(Communication::class.java) {
    //        update {
    //          status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
    //          addPayload().apply { content = StringType("Start Perform") }
    //        }
    //      }
    //      .endPerform { addPayload().apply { content = StringType("End Perform") } }

    val medicationRequest1 =
      MedicationRequest().apply {
        id = "med-req-01"
        subject = Reference("Patient/pat-01")
        intent = MedicationRequest.MedicationRequestIntent.PROPOSAL

        addNote(Annotation(MarkdownType("Proposal")))
      }

    fhirEngine.create(medicationRequest1)

    ActivityFlow.of(repository, CPGMedicationRequest(medicationRequest1))
      .startPlan {
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Start Plan")))
        }
      }
      .endPlan { update { addNote(Annotation(MarkdownType("End Plan"))) } }
      .startOrder {
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Start Order")))
        }
      }
      .endOrder { update { addNote(Annotation(MarkdownType("End Order"))) } }
      .startPerform(klass = CPGMedicationDispenseEvent(MedicationDispense()).javaClass) {
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Start Perform")))
        }
      }
      .endPerform { update {} }

    ActivityFlow.of(repository, "pat-01").forEachIndexed { index, _flow ->
      println(
        "Flow #${index + 1} Request ${_flow.requestResource.resourceType}/${_flow.requestResource.logicalId} -- State ${_flow.intent()}",
      )

      if (_flow.requestResource is CPGCommunicationRequest) {
        println(
          "   Request Payload ${(_flow.requestResource as CPGCommunicationRequest).resource.payload.map { it.contentStringType.value }.joinToString()} -- ",
        )
      } else if (_flow.requestResource is CPGMedicationRequest) {
        println(
          "   Request Payload ${(_flow.requestResource as CPGMedicationRequest).resource.note.map { it.text }.joinToString()} -- ",
        )
      }
    }

    // Lets resume a flow that is in Order state
    val communicationFlowInOrder =
      ActivityFlow.of(repository, "pat-01")
        .filterIsInstance<
          ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent>,
        >()
        .first { it.intent() == Intent.ORDER }

    assertFailsWith(Exception::class) {
      communicationFlowInOrder.startPlan {
        // should throw exception
      }
    }

    //      Failing
    //      val communicationFlowInOrder2 =
    //        ActivityFlow4.of(repository, "pat-01")
    //          .filterIsInstance<ActivityFlow4<CPGResource<Appointment>()
    //          .first { it.intent() == Intent.ORDER }

    communicationFlowInOrder
      .startPerform(CPGCommunicationEvent(Communication()).javaClass) {
        update {
          status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Start Perform after resume")))
        }
      }
      .endPerform {
        update { addPayload().apply { content = StringType("End Perform after resume") } }
      }
  }

  @Test
  fun `test order service`() = runBlockingOnWorkerThread {
    val serviceRequestFromCarePlan =
      ServiceRequest().apply {
        id = "service-request"
        subject = Reference("Patient/patient-001")
      }
    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)

    val flow = ActivityFlow.of(repository, CPGRequestResource.of(serviceRequestFromCarePlan))

    flow
      .startPlan {
        update {
          status = ServiceRequest.ServiceRequestStatus.ACTIVE
          addNote().apply { text = "Start Plan annotation.." }
        }
      }
      .endPlan { update { addNote().apply { text = "End Plan annotation.." } } }
      .startOrder {
        update {
          status = ServiceRequest.ServiceRequestStatus.ACTIVE
          addNote().apply { text = "Start order annotation.." }
        }
      }
      .endOrder {
        update {
          status = ServiceRequest.ServiceRequestStatus.ACTIVE
          addNote().apply { text = "End order annotation.." }
        }
      }

    val performFlow =
      ActivityFlow.of(repository, "patient-001")
        .filterIsInstance<CPGServiceRequestActivity>()
        .first()

    performFlow
      .startPerform(klass = CPGProcedureEvent::class.java) {
        update {
          status = ServiceRequest.ServiceRequestStatus.ACTIVE
          addNote().apply { text = "Start Perform procedure annotation.." }
        }
      }
      .endPerform { update {} }

    performFlow
      .startPerform(klass = CPGObservationEvent::class.java) {
        update {
          status = ServiceRequest.ServiceRequestStatus.ACTIVE
          addNote().apply { text = "Start Perform observation annotation.." }
        }
      }
      .endPerform { update {} }
  }

  @Test
  fun `test order medication`() = runBlockingOnWorkerThread {
    val medicationRequest =
      MedicationRequest().apply {
        id = "med-req-01"
        subject = Reference("Patient/pat-01")
        intent = MedicationRequest.MedicationRequestIntent.PROPOSAL

        addNote(Annotation(MarkdownType("Proposal")))
      }

    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    val flow = ActivityFlow.of(repository, CPGMedicationRequest(medicationRequest))

    flow
      .startPlan {
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Start Plan")))
        }
      }
      .endPlan { update { addNote(Annotation(MarkdownType("End Plan"))) } }
      .startOrder {
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Start Order")))
        }
      }
      .endOrder { update { addNote(Annotation(MarkdownType("End Order"))) } }

    val performFlow =
      ActivityFlow.of(repository, "pat-01").filterIsInstance<CPGMedicationRequestActivity>().first()

    //    performFlow
    //       .startPerform(CPGMedicationDispenseEvent::class.java) {
    //         update {
    //           status = MedicationRequest.MedicationRequestStatus.ACTIVE
    //           addNote(Annotation(MarkdownType("Perform Order for dispense")))
    //         }
    //       }
    //       .endPerform {
    //         update {
    //           status = MedicationDispense.MedicationDispenseStatus.INPROGRESS
    //         }
    //       }

    // ---- Alternate ----
    performFlow
      .startPerform(CPGMedicationAdministrationEvent::class.java) {
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Perform Order for administration")))
        }
      }
      .endPerform {
        update { status = MedicationAdministration.MedicationAdministrationStatus.INPROGRESS }
      }
  }

  @Test
  fun `test task based`() = runBlockingOnWorkerThread {
    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)

    val flow =
      ActivityFlow.of(
        repository,
        CPGImmunizationRequest(
          MedicationRequest().apply {
            id = "immunization-request-01"
            subject = Reference("Patient/pa-1001")
            intent = MedicationRequest.MedicationRequestIntent.PROPOSAL
            meta.apply {
              addProfile("http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-immunizationrequest")
            }
          },
        ),
      )

    flow
      .startPlan {
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote().apply { text = "ImmunizationRequest start plan.." }
        }
      }
      .endPlan {
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote().apply { text = "ImmunizationRequest end plan.." }
        }
      }

    val allFlows = ActivityFlow.of(repository, "pa-1001")
    allFlows.first().let { println("${it.requestResource is CPGImmunizationRequest} ") }

    println(
      " CPGRecommendImmunizationActivity ${allFlows.filterIsInstance<CPGServiceRequestActivity>()}",
    )

    println(
      " CPGMedicationRequestActivity ${allFlows.filterIsInstance<CPGMedicationRequestActivity>()}",
    )
  }
}
