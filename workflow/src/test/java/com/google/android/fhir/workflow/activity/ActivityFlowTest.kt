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
import com.google.android.fhir.workflow.repositories.FhirEngineRepository
import com.google.android.fhir.workflow.runBlockingOnWorkerThread
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import java.lang.Exception
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Annotation
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestPayloadComponent
import org.hl7.fhir.r4.model.MarkdownType
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StringType
import org.junit.Before
import org.junit.Ignore
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
  fun `communication request flow`() = runBlocking {
    val proposalJson =
      """
   {
  "resourceType" : "CommunicationRequest",
  "id" : "sm-scenario2",
  "meta" : {
    "profile" : ["http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest"]
  },
  "text" : {
    "status" : "extensions",
    "div" : "<div xmlns=\"http://www.w3.org/1999/xhtml\"><p><b>Generated Narrative: CommunicationRequest</b><a name=\"sm-scenario2\"> </a></p><div style=\"display: inline-block; background-color: #d9e0e7; padding: 6px; margin: 4px; border: 1px solid #8da1b4; border-radius: 5px; line-height: 60%\"><p style=\"margin-bottom: 0px\">Resource CommunicationRequest &quot;sm-scenario2&quot; </p><p style=\"margin-bottom: 0px\">Profile: <a href=\"StructureDefinition-cpg-communicationrequest.html\">CPG Communication Request</a></p></div><p><b>Definition</b>: <a href=\"ActivityDefinition-activity-example-sendmessage-ad.html\">Activity Example Send Message AD</a></p><p><b>status</b>: active</p><p><b>subject</b>: <a href=\"Patient-sm-scenario2-patient.html\">Patient/sm-scenario2-patient</a> &quot; PATIENT&quot;</p><h3>Payloads</h3><table class=\"grid\"><tr><td style=\"display: none\">-</td><td><b>Content[x]</b></td></tr><tr><td style=\"display: none\">*</td><td>Hello!</td></tr></table><p><b>recipient</b>: <a href=\"Patient-sm-scenario2-patient.html\">Patient/sm-scenario2-patient</a> &quot; PATIENT&quot;</p></div>"
  },
  "extension" : [{
    "url" : "http://hl7.org/fhir/StructureDefinition/workflow-instantiatesCanonical",
    "valueCanonical" : "http://hl7.org/fhir/uv/cpg/ActivityDefinition/activity-example-sendmessage-ad"
  }],
  "status" : "active",
  "subject" : {
    "reference" : "Patient/sm-scenario2-patient"
  },
  "payload" : [{
    "contentString" : "Hello!"
  }],
  "recipient" : [{
    "reference" : "Patient/sm-scenario2-patient"
  }]
}
        """
        .trimIndent()

    val proposalFromCarePlan =
      FhirContext.forR4Cached().newJsonParser().parseResource(proposalJson) as CommunicationRequest

    fhirEngine.create(proposalFromCarePlan)

    println(" Staring Plan with Proposal ${proposalFromCarePlan.typeAndId}")
    val plan = ActivityFlow.with(fhirEngine).startPlan(proposalFromCarePlan.typeAndId)

    assertThat(plan).isInstanceOf(CommunicationRequest::class.java)
    assertThat((plan as CommunicationRequest).status)
      .isEqualTo(CommunicationRequest.CommunicationRequestStatus.DRAFT)

    // Marking the plan active
    plan.status = CommunicationRequest.CommunicationRequestStatus.ACTIVE

    ActivityFlow.with(fhirEngine).endPlan(plan)

    println(" Staring Order with Plan ${plan.typeAndId}")
    val order = ActivityFlow.with(fhirEngine).startOrder(plan.typeAndId)

    assertThat(order).isInstanceOf(CommunicationRequest::class.java)
    assertThat((order as CommunicationRequest).status)
      .isEqualTo(CommunicationRequest.CommunicationRequestStatus.DRAFT)

    order.status = CommunicationRequest.CommunicationRequestStatus.ACTIVE

    ActivityFlow.with(fhirEngine).endOrder(order)

    println(" Performing order with ${order.typeAndId}")
    val perform = ActivityFlow.with(fhirEngine).startPerform(order.typeAndId)

    assertThat(perform).isInstanceOf(Communication::class.java)
    assertThat((perform as Communication).status)
      .isEqualTo(Communication.CommunicationStatus.PREPARATION)

    ActivityFlow.with(fhirEngine).endPerform(perform)
  }

  @Test
  fun `communication request flow2`() = runBlockingOnWorkerThread {
    val proposalJson =
      """
   {
  "resourceType" : "CommunicationRequest",
  "id" : "sm-scenario2",
  "meta" : {
    "profile" : ["http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest"]
  },
  "text" : {
    "status" : "extensions",
    "div" : "<div xmlns=\"http://www.w3.org/1999/xhtml\"><p><b>Generated Narrative: CommunicationRequest</b><a name=\"sm-scenario2\"> </a></p><div style=\"display: inline-block; background-color: #d9e0e7; padding: 6px; margin: 4px; border: 1px solid #8da1b4; border-radius: 5px; line-height: 60%\"><p style=\"margin-bottom: 0px\">Resource CommunicationRequest &quot;sm-scenario2&quot; </p><p style=\"margin-bottom: 0px\">Profile: <a href=\"StructureDefinition-cpg-communicationrequest.html\">CPG Communication Request</a></p></div><p><b>Definition</b>: <a href=\"ActivityDefinition-activity-example-sendmessage-ad.html\">Activity Example Send Message AD</a></p><p><b>status</b>: active</p><p><b>subject</b>: <a href=\"Patient-sm-scenario2-patient.html\">Patient/sm-scenario2-patient</a> &quot; PATIENT&quot;</p><h3>Payloads</h3><table class=\"grid\"><tr><td style=\"display: none\">-</td><td><b>Content[x]</b></td></tr><tr><td style=\"display: none\">*</td><td>Hello!</td></tr></table><p><b>recipient</b>: <a href=\"Patient-sm-scenario2-patient.html\">Patient/sm-scenario2-patient</a> &quot; PATIENT&quot;</p></div>"
  },
  "extension" : [{
    "url" : "http://hl7.org/fhir/StructureDefinition/workflow-instantiatesCanonical",
    "valueCanonical" : "http://hl7.org/fhir/uv/cpg/ActivityDefinition/activity-example-sendmessage-ad"
  }],
  "status" : "active",
  "subject" : {
    "reference" : "Patient/sm-scenario2-patient"
  },
  "payload" : [{
    "contentString" : "Hello!"
  }],
  "recipient" : [{
    "reference" : "Patient/sm-scenario2-patient"
  }]
}
        """
        .trimIndent()

    val proposalFromCarePlan =
      FhirContext.forR4Cached().newJsonParser().parseResource(proposalJson) as CommunicationRequest
    fhirEngine.create(proposalFromCarePlan)

    println(" Staring Plan with Proposal ${proposalFromCarePlan.typeAndId}")
    val sendMessageFlow =
      ActivityFlow2.sendMessage(
        FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine),
        proposalFromCarePlan,
      )
    sendMessageFlow.startPlan { payload.first().content = StringType("Hello in Proposal") }

    assertThat(sendMessageFlow.plan).isInstanceOf(CommunicationRequest::class.java)
    assertThat(sendMessageFlow.plan!!.status)
      .isEqualTo(CommunicationRequest.CommunicationRequestStatus.DRAFT)

    sendMessageFlow
      .endPlan {
        // Marking the plan active
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
      }
      .startOrder {
        addPayload(
          CommunicationRequestPayloadComponent(StringType("Hello in Plan")),
        )
      }

    val order = sendMessageFlow.order!!

    assertThat(order).isInstanceOf(CommunicationRequest::class.java)
    assertThat((order as CommunicationRequest).status)
      .isEqualTo(CommunicationRequest.CommunicationRequestStatus.DRAFT)

    sendMessageFlow
      .endOrder { status = CommunicationRequest.CommunicationRequestStatus.ACTIVE }
      .startPerform<CommunicationRequest> {
        addPayload(
          CommunicationRequestPayloadComponent(StringType("Hello in Order")),
        )
      }

    println(" Performing order with ${order.typeAndId}")
    val perform = sendMessageFlow.event!!

    assertThat(perform).isInstanceOf(Communication::class.java)
    assertThat(perform.status).isEqualTo(Communication.CommunicationStatus.PREPARATION)

    sendMessageFlow.endPerform {
      addPayload(Communication.CommunicationPayloadComponent(StringType("Hello in Event")))
    }

    val communicationEvent =
      fhirEngine.get(ResourceType.Communication, perform.logicalId) as Communication

    assertThat(communicationEvent.payload.map { it.content.primitiveValue() })
      .containsExactly("Hello in Proposal", "Hello in Plan", "Hello in Order", "Hello in Event")
      .inOrder()
  }

  @Test
  fun `communication request flow3`() = runBlockingOnWorkerThread {
    val proposalJson =
      """
           {
          "resourceType" : "CommunicationRequest",
          "id" : "sm-scenario2",
          "meta" : {
            "profile" : ["http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest"]
          },
          "extension" : [{
            "url" : "http://hl7.org/fhir/StructureDefinition/workflow-instantiatesCanonical",
            "valueCanonical" : "http://hl7.org/fhir/uv/cpg/ActivityDefinition/activity-example-sendmessage-ad"
          }],
          "status" : "active",
          "subject" : {
            "reference" : "Patient/sm-scenario2-patient"
          },
          "payload" : [{
            "contentString" : "Hello!"
          }],
          "recipient" : [{
            "reference" : "Patient/sm-scenario2-patient"
          }]
          }
        """
        .trimIndent()

    val proposalFromCarePlan =
      FhirContext.forR4Cached().newJsonParser().parseResource(proposalJson) as CommunicationRequest
    fhirEngine.create(proposalFromCarePlan)

    val sendMessageFlow =
      ActivityFlow2.sendMessage(
          FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine),
          proposalFromCarePlan,
        )
        .startPlan {
          // Change the message in the Proposal Request
          payload.first().content = StringType("Hello in Proposal")
        }
        .endPlan {
          // Marking the plan active
          status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        }
        .startOrder {
          // Update the plan resource to add a new message
          addPayload(
            CommunicationRequestPayloadComponent(StringType("Hello in Plan")),
          )
        }
        .endOrder { status = CommunicationRequest.CommunicationRequestStatus.ACTIVE }
        .startPerform<CommunicationRequest> {
          // Update the order resource to add a new message
          addPayload(
            CommunicationRequestPayloadComponent(StringType("Hello in Order")),
          )
        }
        .endPerform {
          // Add a new message to the event
          addPayload(Communication.CommunicationPayloadComponent(StringType("Hello in Event")))
        }

    // Get all Request and Event resources from the engine and make sure that the changes to the
    // resources are persisted.
    val proposalResource =
      fhirEngine.get(ResourceType.CommunicationRequest, sendMessageFlow.proposal!!.logicalId)
        as CommunicationRequest
    val planResource =
      fhirEngine.get(ResourceType.CommunicationRequest, sendMessageFlow.plan!!.logicalId)
        as CommunicationRequest
    val orderResource =
      fhirEngine.get(ResourceType.CommunicationRequest, sendMessageFlow.order!!.logicalId)
        as CommunicationRequest
    val communicationEvent =
      fhirEngine.get(ResourceType.Communication, sendMessageFlow.event!!.logicalId) as Communication

    assertThat(proposalResource.payload.map { it.content.primitiveValue() })
      .containsExactly("Hello in Proposal")
      .inOrder()
    assertThat(planResource.payload.map { it.content.primitiveValue() })
      .containsExactly("Hello in Proposal", "Hello in Plan")
      .inOrder()
    assertThat(orderResource.payload.map { it.content.primitiveValue() })
      .containsExactly("Hello in Proposal", "Hello in Plan", "Hello in Order")
      .inOrder()
    assertThat(communicationEvent.payload.map { it.content.primitiveValue() })
      .containsExactly("Hello in Proposal", "Hello in Plan", "Hello in Order", "Hello in Event")
      .inOrder()
  }

  @Ignore("Test not complete yet")
  @Test
  fun `order a medication`(): Unit = runBlockingOnWorkerThread {
    val medicationRequest =
      """
      {
        "resourceType" : "MedicationRequest",
        "id" : "dm-scenario4",
        "text" : {
          "status" : "generated",
          "div" : "<div xmlns=\"http://www.w3.org/1999/xhtml\"><p><b>Generated Narrative</b></p><p><b>status</b>: active</p><p><b>intent</b>: order</p><p><b>priority</b>: routine</p><p><b>medication</b>: <span title=\"Codes: {http://snomed.info/sct 376988009}\">Levothyroxine sodium 75 microgram oral tablet</span></p><p><b>subject</b>: <a href=\"Patient-dm-scenario4.html\">Generated Summary: active; Example Patient; gender: female; birthDate: 1990-10-01; </a></p><p><b>reasonCode</b>: <span title=\"Codes: {http://snomed.info/sct 40930008}\">Hypothyroidism (disorder)</span></p><h3>DosageInstructions</h3><table class=\"grid\"><tr><td>-</td></tr><tr><td>*</td></tr></table><blockquote><p><b>dispenseRequest</b></p><p><b>validityPeriod</b>: 2015-01-15 --&gt; 2016-01-15</p><p><b>quantity</b>: 100 Tab</p><h3>ExpectedSupplyDurations</h3><table class=\"grid\"><tr><td>-</td></tr><tr><td>*</td></tr></table></blockquote></div>"
        },
        "status" : "active",
        "intent" : "order",
        "priority" : "routine",
        "medicationCodeableConcept" : {
          "coding" : [
            {
              "system" : "http://snomed.info/sct",
              "code" : "376988009",
              "display" : "Levothyroxine sodium 75 microgram oral tablet"
            }
          ]
        },
        "subject" : {
          "reference" : "Patient/dm-scenario4"
        },
        "reasonCode" : [
          {
            "coding" : [
              {
                "system" : "http://snomed.info/sct",
                "code" : "40930008",
                "display" : "Hypothyroidism (disorder)"
              }
            ]
          }
        ],
        "dosageInstruction" : [
          {
            "sequence" : 1,
            "text" : "75mcg daily",
            "timing" : {
              "repeat" : {
                "frequency" : 1,
                "period" : 1,
                "periodUnit" : "d"
              }
            },
            "route" : {
              "coding" : [
                {
                  "system" : "http://snomed.info/sct",
                  "code" : "26643006",
                  "display" : "Oral Route (qualifier value)"
                }
              ]
            },
            "doseAndRate" : [
              {
                "type" : {
                  "coding" : [
                    {
                      "system" : "http://terminology.hl7.org/CodeSystem/dose-rate-type",
                      "code" : "ordered",
                      "display" : "Ordered"
                    }
                  ]
                },
                "doseQuantity" : {
                  "value" : 75,
                  "unit" : "mcg",
                  "system" : "http://unitsofmeasure.org",
                  "code" : "ug"
                }
              }
            ]
          }
        ],
        "dispenseRequest" : {
          "validityPeriod" : {
            "start" : "2015-01-15",
            "end" : "2016-01-15"
          },
          "quantity" : {
            "value" : 100,
            "unit" : "Tab",
            "system" : "http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm",
            "code" : "TAB"
          },
          "expectedSupplyDuration" : {
            "value" : 30,
            "unit" : "days",
            "system" : "http://unitsofmeasure.org",
            "code" : "d"
          }
        }
      }
        """
        .trimIndent()
        .let { FhirContext.forR4Cached().newJsonParser().parseResource(it) as MedicationRequest }
    fhirEngine.create(medicationRequest)

    ActivityFlow2.orderMedication(
        FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine),
        medicationRequest,
      )
      .startPlan {}

    TODO("Finish test")
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

    ActivityFlow3.of(repository, communicationRequest1)
      .startPlan { addPayload().apply { content = StringType("Start Plan") } }
      .endPlan { addPayload().apply { content = StringType("End Plan") } }

    val communicationRequest2 =
      CommunicationRequest().apply {
        id = "com-req-02"
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        subject = Reference("Patient/pat-01")
        addPayload().apply { content = StringType("Proposal") }
      }
    fhirEngine.create(communicationRequest2)

    ActivityFlow3.of(repository, communicationRequest2)
      .startPlan { addPayload().apply { content = StringType("Start Plan") } }
      .endPlan { addPayload().apply { content = StringType("End Plan") } }
      .startOrder {
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        addPayload().apply { content = StringType("Start Order") }
      }
      .endOrder { addPayload().apply { content = StringType("End Order") } }

    val communicationRequest3 =
      CommunicationRequest().apply {
        id = "com-req-03"
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        subject = Reference("Patient/pat-01")
        addPayload().apply { content = StringType("Proposal") }
      }
    fhirEngine.create(communicationRequest3)

    ActivityFlow3.of(repository, communicationRequest3)
      .startPlan { addPayload().apply { content = StringType("Start Plan") } }
      .endPlan { addPayload().apply { content = StringType("End Plan") } }
      .startOrder {
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        addPayload().apply { content = StringType("Start Order") }
      }
      .endOrder { addPayload().apply { content = StringType("End Order") } }
      .startPerform(Communication::class.java) {
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        addPayload().apply { content = StringType("Start Perform") }
      }

    val communicationRequest4 =
      CommunicationRequest().apply {
        id = "com-req-04"
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        subject = Reference("Patient/pat-01")
        addPayload().apply { content = StringType("Proposal") }
      }
    fhirEngine.create(communicationRequest4)

    ActivityFlow3.of(repository, communicationRequest4)
      .startPlan { addPayload().apply { content = StringType("Start Plan") } }
      .endPlan { addPayload().apply { content = StringType("End Plan") } }
      .startOrder {
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        addPayload().apply { content = StringType("Start Order") }
      }
      .endOrder { addPayload().apply { content = StringType("End Order") } }
      .startPerform(Communication::class.java) {
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        addPayload().apply { content = StringType("Start Perform") }
      }
      .endPerform { addPayload().apply { content = StringType("End Perform") } }

    val medicationRequest1 =
      MedicationRequest().apply {
        id = "med-req-01"
        subject = Reference("Patient/pat-01")
        intent = MedicationRequest.MedicationRequestIntent.PROPOSAL

        addNote(Annotation(MarkdownType("Proposal")))
      }

    fhirEngine.create(medicationRequest1)

    ActivityFlow3.of(repository, medicationRequest1)
      .startPlan {
        status = MedicationRequest.MedicationRequestStatus.ACTIVE
        addNote(Annotation(MarkdownType("Start Plan")))
      }
      .endPlan { addNote(Annotation(MarkdownType("End Plan"))) }
      .startOrder {
        status = MedicationRequest.MedicationRequestStatus.ACTIVE
        addNote(Annotation(MarkdownType("Start Order")))
      }
      .endOrder { addNote(Annotation(MarkdownType("End Order"))) }

    ActivityFlow3.of(repository, "pat-01").forEachIndexed { index, _flow ->
      println(
        "Flow #${index + 1} Request ${_flow.requestResource.resourceType}/${_flow.requestResource.logicalId} -- State ${_flow.currentIntent()}",
      )

      if (_flow.requestResource is CommunicationRequest) {
        println(
          "   Request Payload ${(_flow.requestResource as CommunicationRequest).payload.map { it.contentStringType.value }.joinToString()} -- ",
        )
      } else if (_flow.requestResource is MedicationRequest) {
        println(
          "   Request Payload ${(_flow.requestResource as MedicationRequest).note.map { it.text }.joinToString()} -- ",
        )
      }
    }

    // Lets resume a flow that is in Order state
    val communicationFlowInOrder =
      ActivityFlow3.of(repository, "pat-01")
        .filterIsInstance<ActivityFlow3<CommunicationRequest>>()
        .first { it.currentIntent() == ActivityFlow3.RequestIntent.ORDER }

    assertFailsWith(Exception::class) {
      communicationFlowInOrder.startPlan {
        // should throw exception
      }
    }

    communicationFlowInOrder
      .startPerform(Communication::class.java) {
        status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
        addPayload().apply { content = StringType("Start Perform after resume") }
      }
      .endPerform { addPayload().apply { content = StringType("End Perform after resume") } }

    ActivityFlow3.of(repository, "pat-01").forEachIndexed { index, _flow ->
      println(
        "Flow #${index + 1} Request ${_flow.requestResource.resourceType}/${_flow.requestResource.logicalId} -- State ${_flow.currentIntent()}",
      )

      if (_flow.requestResource is CommunicationRequest) {
        println(
          "   Request Payload ${(_flow.requestResource as CommunicationRequest).payload.map { it.contentStringType.value }.joinToString()} -- ",
        )
      } else if (_flow.requestResource is MedicationRequest) {
        println(
          "   Request Payload ${(_flow.requestResource as MedicationRequest).note.map { it.text }.joinToString()} -- ",
        )
      }
    }
  }
}

private val Resource.typeAndId
  get() = "$resourceType/$logicalId"
