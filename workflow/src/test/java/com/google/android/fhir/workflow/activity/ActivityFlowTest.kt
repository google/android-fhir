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
import com.google.android.fhir.workflow.activity.event.CPGCommunicationEvent
import com.google.android.fhir.workflow.activity.event.CPGMedicationDispenseEvent
import com.google.android.fhir.workflow.activity.event.CPGObservationEvent
import com.google.android.fhir.workflow.activity.event.CPGProcedureEvent
import com.google.android.fhir.workflow.activity.request.CPGCommunicationRequest
import com.google.android.fhir.workflow.activity.request.CPGMedicationRequest
import com.google.android.fhir.workflow.activity.request.CPGRequestResource
import com.google.android.fhir.workflow.repositories.FhirEngineRepository
import com.google.android.fhir.workflow.runBlockingOnWorkerThread
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.hl7.fhir.r4.model.Annotation
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MarkdownType
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

@Suppress(
  "UnstableApiUsage", /*Repository is marked @Beta */
  "UNCHECKED_CAST", /*Cast type erased ActivityFlow to a concrete type ActivityFlow*/
)
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
  fun `communication request flow`(): Unit = runBlockingOnWorkerThread {
    val cpgCommunicationRequest =
      CPGRequestResource.of(
        CommunicationRequest().apply {
          id = "com-req-01"
          status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
          subject = Reference("Patient/pat-01")
          meta.addProfile("http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest")

          addPayload().apply { content = StringType("Proposal") }
        },
      )

    val stageToRequestLogicalIdMap = mutableMapOf<String, String>()

    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    val communicationFlow: ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
      ActivityFlow.of(repository, cpgCommunicationRequest)

    communicationFlow
      .startPlan { // CPGCommunicationRequest
        stageToRequestLogicalIdMap["startPlan"] = this.logicalId
        update { // CommunicationRequest
          addPayload().apply { content = StringType("Start Plan") }
        }
      }
      .endPlan { // CPGCommunicationRequest
        stageToRequestLogicalIdMap["endPlan"] = this.logicalId
        update { // CommunicationRequest
          addPayload().apply { content = StringType("End Plan") }
        }
      }
      .startOrder { // CPGCommunicationRequest
        stageToRequestLogicalIdMap["startOrder"] = this.logicalId
        update { // CommunicationRequest
          status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
          addPayload().apply { content = StringType("Start Order") }
        }
      }
      .endOrder { // CPGCommunicationRequest
        stageToRequestLogicalIdMap["endOrder"] = this.logicalId
        update { // CommunicationRequest
          addPayload().apply { content = StringType("End Order") }
        }
      }

    val resumedFlow =
      ActivityFlow.of(repository, "pat-01").first { it.requestResource is CPGCommunicationRequest }
        as CPGCommunicationActivity

    assertFailsWith(Exception::class) {
      resumedFlow.startPlan {
        // should throw exception
      }
    }

    assertFailsWith(Exception::class) {
      resumedFlow.startOrder {
        // should throw exception
      }
    }

    resumedFlow
      .startPerform(CPGCommunicationEvent::class.java) { // CPGCommunicationRequest
        stageToRequestLogicalIdMap["startPerform"] = this.logicalId
        update { // CommunicationRequest
          status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
          addPayload().apply { content = StringType("Start Perform") }
        }
      }
      .endPerform { // CPGCommunicationEvent
        stageToRequestLogicalIdMap["endPerform"] = this.logicalId
        update { // Communication
          addPayload().apply { content = StringType("End Perform") }
        }
      }

    // Assert that each phase creates a new resource.
    assertThat(stageToRequestLogicalIdMap["startPlan"]).isEqualTo(cpgCommunicationRequest.logicalId)
    assertThat(stageToRequestLogicalIdMap["endPlan"])
      .isNotEqualTo(stageToRequestLogicalIdMap["startPlan"])

    assertThat(stageToRequestLogicalIdMap["startOrder"])
      .isEqualTo(stageToRequestLogicalIdMap["endPlan"])
    assertThat(stageToRequestLogicalIdMap["endOrder"])
      .isNotEqualTo(stageToRequestLogicalIdMap["startOrder"])

    assertThat(stageToRequestLogicalIdMap["startPerform"])
      .isEqualTo(stageToRequestLogicalIdMap["endOrder"])
    assertThat(stageToRequestLogicalIdMap["endPerform"])
      .isNotEqualTo(stageToRequestLogicalIdMap["startPerform"])

    val plan =
      repository
        .read(CommunicationRequest::class.java, IdType(stageToRequestLogicalIdMap["endPlan"]))
        .let { CPGRequestResource.of(it) }

    val order =
      repository
        .read(CommunicationRequest::class.java, IdType(stageToRequestLogicalIdMap["endOrder"]))
        .let { CPGRequestResource.of(it) }

    val event =
      repository
        .read(Communication::class.java, IdType(stageToRequestLogicalIdMap["endPerform"]))
        .let { CPGCommunicationEvent(it) }

    // check that current phase resource is based on the previous phase's resource
    assertThat(plan.getBasedOn()!!.reference)
      .isEqualTo(cpgCommunicationRequest.asReference().reference)
    assertThat(order.getBasedOn()!!.reference).isEqualTo(plan.asReference().reference)
    assertThat(event.getBasedOn()!!.reference).isEqualTo(order.asReference().reference)
  }

  @Test
  fun `order service flow`(): Unit = runBlockingOnWorkerThread {
    val cpgServiceRequest =
      CPGRequestResource.of(
        ServiceRequest().apply {
          id = "service-request"
          subject = Reference("Patient/patient-001")
          intent = ServiceRequest.ServiceRequestIntent.PROPOSAL
          meta.addProfile("http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-servicerequest")
        },
      )
    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    val stageToRequestLogicalIdMap = mutableMapOf<String, String>()
    val flow = ActivityFlow.of(repository, cpgServiceRequest)

    flow
      .startPlan {
        stageToRequestLogicalIdMap["startPlan"] = this.logicalId
        update {
          status = ServiceRequest.ServiceRequestStatus.ACTIVE
          addNote().apply { text = "Start Plan annotation.." }
        }
      }
      .endPlan {
        stageToRequestLogicalIdMap["endPlan"] = this.logicalId
        update { addNote().apply { text = "End Plan annotation.." } }
      }
      .startOrder {
        stageToRequestLogicalIdMap["startOrder"] = this.logicalId
        update {
          status = ServiceRequest.ServiceRequestStatus.ACTIVE
          addNote().apply { text = "Start order annotation.." }
        }
      }
      .endOrder {
        stageToRequestLogicalIdMap["endOrder"] = this.logicalId
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
        stageToRequestLogicalIdMap["startPerform"] = this.logicalId
        update {
          status = ServiceRequest.ServiceRequestStatus.ACTIVE
          addNote().apply { text = "Start Perform procedure annotation.." }
        }
      }
      .endPerform {
        stageToRequestLogicalIdMap["endPerform"] = this.logicalId
        update { addNote().apply { text = "End Perform observation annotation.." } }
      }

    assertFailsWith(Exception::class) {
      performFlow
        .startPerform(klass = CPGObservationEvent::class.java) {
          update {
            status = ServiceRequest.ServiceRequestStatus.ACTIVE
            addNote().apply { text = "Start Perform observation annotation.." }
          }
        }
        .endPerform { update { addNote().apply { text = "End Perform observation annotation.." } } }
    }

    // Assert that each phase creates a new resource.
    assertThat(stageToRequestLogicalIdMap["startPlan"]).isEqualTo(cpgServiceRequest.logicalId)
    assertThat(stageToRequestLogicalIdMap["endPlan"])
      .isNotEqualTo(stageToRequestLogicalIdMap["startPlan"])

    assertThat(stageToRequestLogicalIdMap["startOrder"])
      .isEqualTo(stageToRequestLogicalIdMap["endPlan"])
    assertThat(stageToRequestLogicalIdMap["endOrder"])
      .isNotEqualTo(stageToRequestLogicalIdMap["startOrder"])

    assertThat(stageToRequestLogicalIdMap["startPerform"])
      .isEqualTo(stageToRequestLogicalIdMap["endOrder"])
    assertThat(stageToRequestLogicalIdMap["endPerform"])
      .isNotEqualTo(stageToRequestLogicalIdMap["startPerform"])

    val plan =
      repository
        .read(MedicationRequest::class.java, IdType(stageToRequestLogicalIdMap["endPlan"]))
        .let { CPGRequestResource.of(it) }

    val order =
      repository
        .read(MedicationRequest::class.java, IdType(stageToRequestLogicalIdMap["endOrder"]))
        .let { CPGRequestResource.of(it) }

    val event =
      repository
        .read(MedicationDispense::class.java, IdType(stageToRequestLogicalIdMap["endPerform"]))
        .let { CPGMedicationDispenseEvent(it) }

    // check that current phase resource is based on the previous phase's resource
    assertThat(plan.getBasedOn()!!.reference).isEqualTo(cpgServiceRequest.asReference().reference)
    assertThat(order.getBasedOn()!!.reference).isEqualTo(plan.asReference().reference)
    assertThat(event.getBasedOn()!!.reference).isEqualTo(order.asReference().reference)
  }

  @Test
  fun `order medication flow for medication dispense`(): Unit = runBlockingOnWorkerThread {
    val cpgMedicationRequest =
      CPGMedicationRequest(
        MedicationRequest().apply {
          id = "med-req-01"
          subject = Reference("Patient/pat-01")
          intent = MedicationRequest.MedicationRequestIntent.PROPOSAL
          meta.addProfile("http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-medicationrequest")

          addNote(Annotation(MarkdownType("Proposal")))
        },
      )

    val stageToRequestLogicalIdMap = mutableMapOf<String, String>()

    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    val flow = ActivityFlow.of(repository, cpgMedicationRequest)

    flow
      .startPlan {
        stageToRequestLogicalIdMap["startPlan"] = this.logicalId
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Start Plan")))
        }
      }
      .endPlan {
        stageToRequestLogicalIdMap["endPlan"] = this.logicalId
        update { addNote(Annotation(MarkdownType("End Plan"))) }
      }
      .startOrder {
        stageToRequestLogicalIdMap["startOrder"] = this.logicalId
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Start Order")))
        }
      }
      .endOrder {
        stageToRequestLogicalIdMap["endOrder"] = this.logicalId
        update { addNote(Annotation(MarkdownType("End Order"))) }
      }

    val performFlow =
      ActivityFlow.of(repository, "pat-01").first { it.requestResource is CPGMedicationRequest }
        as CPGMedicationRequestActivity

    performFlow
      .startPerform(CPGMedicationDispenseEvent::class.java) {
        stageToRequestLogicalIdMap["startPerform"] = this.logicalId
        update {
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Perform Order for administration")))
        }
      }
      .endPerform {
        stageToRequestLogicalIdMap["endPerform"] = this.logicalId
        update { status = MedicationDispense.MedicationDispenseStatus.INPROGRESS }
      }

    // Assert that each phase creates a new resource.
    assertThat(stageToRequestLogicalIdMap["startPlan"]).isEqualTo(cpgMedicationRequest.logicalId)
    assertThat(stageToRequestLogicalIdMap["endPlan"])
      .isNotEqualTo(stageToRequestLogicalIdMap["startPlan"])

    assertThat(stageToRequestLogicalIdMap["startOrder"])
      .isEqualTo(stageToRequestLogicalIdMap["endPlan"])
    assertThat(stageToRequestLogicalIdMap["endOrder"])
      .isNotEqualTo(stageToRequestLogicalIdMap["startOrder"])

    assertThat(stageToRequestLogicalIdMap["startPerform"])
      .isEqualTo(stageToRequestLogicalIdMap["endOrder"])
    assertThat(stageToRequestLogicalIdMap["endPerform"])
      .isNotEqualTo(stageToRequestLogicalIdMap["startPerform"])

    val plan =
      repository
        .read(MedicationRequest::class.java, IdType(stageToRequestLogicalIdMap["endPlan"]))
        .let { CPGRequestResource.of(it) }

    val order =
      repository
        .read(MedicationRequest::class.java, IdType(stageToRequestLogicalIdMap["endOrder"]))
        .let { CPGRequestResource.of(it) }

    val event =
      repository
        .read(MedicationDispense::class.java, IdType(stageToRequestLogicalIdMap["endPerform"]))
        .let { CPGMedicationDispenseEvent(it) }

    // check that current phase resource is based on the previous phase's resource
    assertThat(plan.getBasedOn()!!.reference)
      .isEqualTo(cpgMedicationRequest.asReference().reference)
    assertThat(order.getBasedOn()!!.reference).isEqualTo(plan.asReference().reference)
    assertThat(event.getBasedOn()!!.reference).isEqualTo(order.asReference().reference)
  }
}
