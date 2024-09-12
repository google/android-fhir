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
import com.google.android.fhir.workflow.activity.phase.Phase
import com.google.android.fhir.workflow.activity.resource.event.CPGCommunicationEvent
import com.google.android.fhir.workflow.activity.resource.event.CPGMedicationDispenseEvent
import com.google.android.fhir.workflow.activity.resource.event.EventStatus
import com.google.android.fhir.workflow.activity.resource.request.CPGCommunicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGMedicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Status
import com.google.android.fhir.workflow.repositories.FhirEngineRepository
import com.google.android.fhir.workflow.runBlockingOnWorkerThread
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import kotlin.test.fail
import org.hl7.fhir.r4.model.Annotation
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MarkdownType
import org.hl7.fhir.r4.model.MedicationDispense
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Reference
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
  fun `order medication flow for medication dispense`(): Unit = runBlockingOnWorkerThread {
    val cpgMedicationRequest =
      CPGMedicationRequest(
        MedicationRequest().apply {
          id = "med-req-01"
          subject = Reference("Patient/pat-01")
          intent = MedicationRequest.MedicationRequestIntent.PROPOSAL
          meta.addProfile("http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-medicationrequest")
          status = MedicationRequest.MedicationRequestStatus.ACTIVE
          addNote(Annotation(MarkdownType("Proposal looks OK.")))
        },
      )

    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    repository.create(cpgMedicationRequest.resource)

    val flow = ActivityFlow.of(repository, cpgMedicationRequest)

    var cachedPlanId = ""

    flow
      .draftPlan()
      .onSuccess { draftPlan ->
        draftPlan.update { addNote(Annotation(MarkdownType("Draft plan looks OK."))) }

        flow.startPlan(draftPlan).onSuccess { planPhase ->
          val updatedPlan =
            planPhase.getRequest().copy().apply {
              setStatus(Status.ACTIVE)
              update { addNote(Annotation(MarkdownType("Plan looks OK."))) }
            }

          planPhase
            .update(updatedPlan)
            .onSuccess {
              println("planPhase.update(updatedPlan).onSuccess")
              cachedPlanId = updatedPlan.logicalId
            }
            .onFailure {
              println("onFailure")
              fail("Should have succeeded", it)
            }
        }
      }
      .onFailure { fail("Unexpected", it) }

    val planToResume =
      repository
        .read(MedicationRequest::class.java, IdType("MedicationRequest", cachedPlanId))
        .let { CPGMedicationRequest(it) }
    val resumedPlanFlow = ActivityFlow.of(repository, planToResume)

    assertThat(resumedPlanFlow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PLAN)

    assertThat(resumedPlanFlow.draftPlan().isFailure).isTrue()

    resumedPlanFlow
      .draftOrder()
      .onSuccess { draftOrder ->
        draftOrder.update { addNote(Annotation(MarkdownType("Draft order looks OK."))) }

        resumedPlanFlow.startOrder(draftOrder).onSuccess { orderPhase ->
          val updatedOrder =
            orderPhase.getRequest().copy().apply {
              setStatus(Status.ACTIVE)
              update { addNote(Annotation(MarkdownType("Order looks OK."))) }
            }

          orderPhase
            .update(updatedOrder)
            .onSuccess { cachedPlanId = updatedOrder.logicalId }
            .onFailure { fail("Should have succeeded", it) }
        }
      }
      .onFailure { fail("Unexpected", it) }

    val orderToResume =
      repository
        .read(MedicationRequest::class.java, IdType("MedicationRequest", cachedPlanId))
        .let { CPGMedicationRequest(it) }
    val resumedOrderFlow = ActivityFlow.of(repository, orderToResume)

    assertThat(resumedOrderFlow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.ORDER)

    assertThat(resumedOrderFlow.draftPlan().isFailure).isTrue()
    assertThat(resumedOrderFlow.draftOrder().isFailure).isTrue()

    resumedOrderFlow
      .draftPerform(CPGMedicationDispenseEvent::class.java)
      .onSuccess { draftEvent ->
        draftEvent.update { addNote(Annotation(MarkdownType("Draft event looks OK."))) }

        resumedOrderFlow.startPerform(draftEvent).onSuccess { performPhase ->
          val updatedEvent =
            performPhase.getEvent().copy().apply {
              setStatus(EventStatus.INPROGRESS)

              update { addNote(Annotation(MarkdownType("Event looks OK."))) }
            }

          performPhase
            .update(updatedEvent)
            .onSuccess { cachedPlanId = updatedEvent.logicalId }
            .onFailure { fail("Should have succeeded", it) }
        }
      }
      .onFailure { fail("Unexpected", it) }

    val eventToResume =
      repository
        .read(MedicationDispense::class.java, IdType("MedicationDispense", cachedPlanId))
        .let { CPGMedicationDispenseEvent(it) }

    val resumedPerformFlow = ActivityFlow.of(repository, eventToResume)
    assertThat(resumedPerformFlow.getCurrentPhase().getPhaseName())
      .isEqualTo(Phase.PhaseName.PERFORM)

    assertThat(resumedPerformFlow.draftPlan().isFailure).isTrue()
    assertThat(resumedPerformFlow.draftOrder().isFailure).isTrue()
    assertThat(resumedPerformFlow.draftPerform(CPGMedicationDispenseEvent::class.java).isFailure)
      .isTrue()

    (resumedPerformFlow.getCurrentPhase() as Phase.EventPhase<*>).complete()

    val completedEvent =
      repository
        .read(MedicationDispense::class.java, IdType("MedicationDispense", cachedPlanId))
        .let { CPGMedicationDispenseEvent(it) }

    assertThat(completedEvent.getStatus()).isEqualTo(EventStatus.COMPLETED)
  }

  @Test
  fun `communication request flow new api`(): Unit = runBlockingOnWorkerThread {
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

    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    repository.create(cpgCommunicationRequest.resource)

    val flow: ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
      ActivityFlow.of(repository, cpgCommunicationRequest)

    val draftPlanResult =
      flow
        .draftPlan()
        .onSuccess {
          it.update { addNote().apply { text = "This Draft Plan looks OK, so approving it." } }
        }
        .onFailure { throw it }

    val draftPlan = draftPlanResult.getOrNull()!!

    val newPhase = flow.startPlan(draftPlan)
    assertThat(newPhase.isSuccess).isTrue()
    assertThat(newPhase.getOrNull()!!.getPhaseName()).isEqualTo(Phase.PhaseName.PLAN)

    newPhase.onSuccess { planPhase ->
      val request = planPhase.getRequest()
      request.update { addNote().apply { text = "This Plan looks OK, so marking it active." } }
      request.setStatus(Status.ACTIVE)

      planPhase.update(request).onSuccess {
        assertThat(planPhase.resume().isFailure).isTrue()

        // Get latest request from the phase.
        val request = planPhase.getRequest()

        request.update { addNote().apply { text = "This Plan looks OK, so marking it active." } }

        val draftOrder =
          flow.draftOrder().onSuccess {
            it.update { addNote().apply { text = "This Draft Order looks OK, so approving it." } }
          }

        val newPhase = flow.startOrder(draftOrder.getOrThrow())
        assertThat(newPhase.isSuccess).isTrue()

        newPhase.onSuccess { orderPhase ->
          val order = orderPhase.getRequest()
          println(" order == draftOrder : ${order == draftOrder.getOrNull()}")

          order.update { addNote().apply { text = "This Order looks OK, so marking it active." } }
          order.setStatus(Status.ACTIVE)

          orderPhase.update(order)

          assertThat(orderPhase.resume().isFailure).isTrue()

          println(
            "\nPrinting all the notes for the request in Phase: ${flow.getCurrentPhase().getPhaseName().name} Status: ${orderPhase.getRequest().getStatus()}",
          )
          orderPhase.getRequest().resource.note.forEachIndexed { a, b ->
            println("Note $a. ${b.text}")
          }

          val draftEvent =
            flow.draftPerform(CPGCommunicationEvent::class.java).onSuccess {
              it.update { addNote().apply { text = "This Event looks OK, so approving it." } }
            }

          assertThat(draftEvent.isSuccess).isTrue()

          val newPhase = flow.startPerform(draftEvent.getOrThrow())

          assertThat(newPhase.isSuccess).isTrue()

          newPhase.onSuccess { performPhase ->
            val event = performPhase.getEvent()
            event.update { addNote().apply { text = "This Event looks OK, so marking it active." } }
            event.setStatus(EventStatus.INPROGRESS)

            performPhase.update(event)
            performPhase.complete()

            val phase = flow.getCurrentPhase() as Phase.EventPhase<CPGCommunicationEvent>
            println("\nPrinting all the notes for the event in ${phase.getEvent().getStatus()}")
            phase.getEvent().resource.note.forEachIndexed { a, b -> println("Note $a. ${b.text}") }
          }
        }
      }
    }
  }
}