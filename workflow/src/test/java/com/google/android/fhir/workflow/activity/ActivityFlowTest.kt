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
import com.google.android.fhir.workflow.activity.phase.event.PerformPhase
import com.google.android.fhir.workflow.activity.phase.request.OrderPhase
import com.google.android.fhir.workflow.activity.phase.request.PlanPhase
import com.google.android.fhir.workflow.activity.phase.request.ProposalPhase
import com.google.android.fhir.workflow.activity.resource.event.CPGCommunicationEvent
import com.google.android.fhir.workflow.activity.resource.event.CPGMedicationDispenseEvent
import com.google.android.fhir.workflow.activity.resource.event.EventStatus
import com.google.android.fhir.workflow.activity.resource.request.CPGCommunicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGMedicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Intent
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

    var cachedResourceId = ""

    flow
      .preparePlan()
      .onSuccess { preparePlan ->
        preparePlan.resource.addNote(Annotation(MarkdownType("prepare plan looks OK.")))

        flow.initiatePlan(preparePlan).onSuccess { planPhase ->
          val updatedPlan =
            planPhase.getRequestResource().copy().apply {
              setStatus(Status.ACTIVE)
              resource.addNote(Annotation(MarkdownType("Plan looks OK.")))
            }

          planPhase
            .update(updatedPlan)
            .onSuccess { cachedResourceId = updatedPlan.logicalId }
            .onFailure { fail("Should have succeeded", it) }
        }
      }
      .onFailure { fail("Unexpected", it) }

    val planToResume =
      repository
        .read(MedicationRequest::class.java, IdType("MedicationRequest", cachedResourceId))
        .let { CPGMedicationRequest(it) }
    val resumedPlanFlow = ActivityFlow.of(repository, planToResume)
    (resumedPlanFlow.getCurrentPhase() as Phase.RequestPhase<CPGMedicationRequest>)
      .getRequestResource()
    assertThat(resumedPlanFlow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PLAN)

    assertThat(resumedPlanFlow.preparePlan().isFailure).isTrue()

    resumedPlanFlow
      .prepareOrder()
      .onSuccess { prepareOrder ->
        prepareOrder.resource.addNote(Annotation(MarkdownType("prepare order looks OK.")))

        resumedPlanFlow.initiateOrder(prepareOrder).onSuccess { orderPhase ->
          val updatedOrder =
            orderPhase.getRequestResource().copy().apply {
              setStatus(Status.ACTIVE)
              resource.addNote(Annotation(MarkdownType("Order looks OK.")))
            }

          orderPhase
            .update(updatedOrder)
            .onSuccess { cachedResourceId = updatedOrder.logicalId }
            .onFailure { fail("Should have succeeded", it) }
        }
      }
      .onFailure { fail("Unexpected", it) }

    val orderToResume =
      repository
        .read(MedicationRequest::class.java, IdType("MedicationRequest", cachedResourceId))
        .let { CPGMedicationRequest(it) }
    val resumedOrderFlow = ActivityFlow.of(repository, orderToResume)

    assertThat(resumedOrderFlow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.ORDER)

    assertThat(resumedOrderFlow.preparePlan().isFailure).isTrue()
    assertThat(resumedOrderFlow.prepareOrder().isFailure).isTrue()

    resumedOrderFlow
      .preparePerform(CPGMedicationDispenseEvent::class.java)
      .onSuccess { prepareEvent ->
        prepareEvent.resource.let {
          it.addNote(Annotation(MarkdownType("prepare event looks OK.")))
        }

        resumedOrderFlow.initiatePerform(prepareEvent).onSuccess { performPhase ->
          val updatedEvent =
            performPhase.getEventResource().copy().apply {
              setStatus(EventStatus.INPROGRESS)

              resource.let { it.addNote(Annotation(MarkdownType("Event looks OK."))) }
            }

          performPhase
            .update(updatedEvent)
            .onSuccess { cachedResourceId = updatedEvent.logicalId }
            .onFailure { fail("Should have succeeded", it) }
        }
      }
      .onFailure { fail("Unexpected", it) }

    val eventToResume =
      repository
        .read(MedicationDispense::class.java, IdType("MedicationDispense", cachedResourceId))
        .let { CPGMedicationDispenseEvent(it) }

    val resumedPerformFlow = ActivityFlow.of(repository, eventToResume)
    assertThat(resumedPerformFlow.getCurrentPhase().getPhaseName())
      .isEqualTo(Phase.PhaseName.PERFORM)

    assertThat(resumedPerformFlow.preparePlan().isFailure).isTrue()
    assertThat(resumedPerformFlow.prepareOrder().isFailure).isTrue()
    assertThat(resumedPerformFlow.preparePerform(CPGMedicationDispenseEvent::class.java).isFailure)
      .isTrue()

    (resumedPerformFlow.getCurrentPhase() as Phase.EventPhase<*>).complete()

    val completedEvent =
      repository
        .read(MedicationDispense::class.java, IdType("MedicationDispense", cachedResourceId))
        .let { CPGMedicationDispenseEvent(it) }

    assertThat(completedEvent.getStatus()).isEqualTo(EventStatus.COMPLETED)
  }

  @Test
  fun `communication request flow`(): Unit = runBlockingOnWorkerThread {
    val cpgCommunicationRequest =
      CPGRequestResource.of(
          CommunicationRequest().apply {
            id = "com-req-01"
            status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
            subject = Reference("Patient/pat-01")
            meta.addProfile(
              "http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest",
            )

            addPayload().apply { content = StringType("Proposal") }
          },
        )
        .apply { setIntent(Intent.PROPOSAL) }
    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    repository.create(cpgCommunicationRequest.resource)

    val flow: ActivityFlow<CPGCommunicationRequest, CPGCommunicationEvent> =
      ActivityFlow.of(repository, cpgCommunicationRequest)

    val preparePlanResult =
      flow
        .preparePlan()
        .onSuccess { planCPGRequest ->
          planCPGRequest.resource.let {
            it.addNote().apply { text = "This prepare Plan looks OK, so approving it." }
          }
        }
        .onFailure { throw it }

    val preparePlan = preparePlanResult.getOrNull()!!

    val newPhase = flow.initiatePlan(preparePlan)
    assertThat(newPhase.isSuccess).isTrue()
    assertThat(newPhase.getOrNull()!!.getPhaseName()).isEqualTo(Phase.PhaseName.PLAN)

    newPhase.onSuccess { planPhase ->
      val request = planPhase.getRequestResource()
      request.resource.let {
        it.addNote().apply { text = "This Plan looks OK, so marking it active." }
      }
      request.setStatus(Status.ACTIVE)

      planPhase.update(request).onSuccess {
        assertThat(planPhase.resume().isFailure).isTrue()

        // Get latest request from the phase.
        val request = planPhase.getRequestResource()

        request.resource.let {
          it.addNote().apply { text = "This Plan looks OK, so marking it active." }
        }

        val prepareOrder =
          flow.prepareOrder().onSuccess {
            request.resource.let {
              it.addNote().apply { text = "This prepare Order looks OK, so approving it." }
            }
          }

        val newPhase = flow.initiateOrder(prepareOrder.getOrThrow())
        assertThat(newPhase.isSuccess).isTrue()

        newPhase.onSuccess { orderPhase ->
          val order = orderPhase.getRequestResource()

          request.resource.let {
            it.addNote().apply { text = "This Order looks OK, so marking it active." }
          }
          order.setStatus(Status.ACTIVE)

          orderPhase.update(order)

          assertThat(orderPhase.resume().isFailure).isTrue()

          val prepareEvent =
            flow.preparePerform(CPGCommunicationEvent::class.java).onSuccess {
              it.resource.let {
                it.addNote().apply { text = "This Event looks OK, so approving it." }
              }
            }

          assertThat(prepareEvent.isSuccess).isTrue()

          val newPhase = flow.initiatePerform(prepareEvent.getOrThrow())

          assertThat(newPhase.isSuccess).isTrue()

          newPhase.onSuccess { performPhase ->
            val event = performPhase.getEventResource()
            event.resource.let {
              it.addNote().apply { text = "This Event looks OK, so marking it active." }
            }
            event.setStatus(EventStatus.INPROGRESS)

            performPhase.update(event)
            performPhase.complete()

            val phase = flow.getCurrentPhase() as Phase.EventPhase<CPGCommunicationEvent>
            assertThat(phase.getEventResource().getStatus()).isEqualTo(EventStatus.COMPLETED)
          }
        }
      }
    }
  }

  @Test
  fun `preparePlan should succeed when in proposal phase`(): Unit = runBlockingOnWorkerThread {
    val cpgCommunicationRequest =
      CPGRequestResource.of(
          CommunicationRequest().apply {
            id = "com-req-01"
            status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
            subject = Reference("Patient/pat-01")
            meta.addProfile(
              "http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest",
            )

            addPayload().apply { content = StringType("Proposal") }
          },
        )
        .apply { setIntent(Intent.PROPOSAL) }
    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    repository.create(cpgCommunicationRequest.resource)

    val proposalPhase = ActivityFlow.of(repository, cpgCommunicationRequest)

    assertThat(proposalPhase.getCurrentPhase()).isInstanceOf(ProposalPhase::class.java)
    assertThat(proposalPhase.preparePlan().isSuccess).isTrue()
  }

  @Test
  fun `preparePlan should fail when in in plan phase`(): Unit = runBlockingOnWorkerThread {
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
    val cpgCommunicationPlanRequest =
      cpgCommunicationRequest.copy(
        id = "com-req-01=plan",
        status = Status.ACTIVE,
        intent = Intent.PLAN,
      ) as CPGCommunicationRequest

    val planPhase = ActivityFlow.of(repository, cpgCommunicationPlanRequest)

    assertThat(planPhase.getCurrentPhase()).isInstanceOf(PlanPhase::class.java)
    assertThat(planPhase.preparePlan().isFailure).isTrue()
  }

  @Test
  fun `preparePlan should fail when in in order phase`(): Unit = runBlockingOnWorkerThread {
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
    val cpgCommunicationOrderRequest =
      cpgCommunicationRequest.copy(
        id = "com-req-01=plan",
        status = Status.ACTIVE,
        intent = Intent.ORDER,
      ) as CPGCommunicationRequest

    val orderPhase = ActivityFlow.of(repository, cpgCommunicationOrderRequest)

    assertThat(orderPhase.getCurrentPhase()).isInstanceOf(OrderPhase::class.java)
    assertThat(orderPhase.preparePlan().isFailure).isTrue()
  }

  @Test
  fun `preparePlan should fail when in in perform phase`(): Unit = runBlockingOnWorkerThread {
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
    val cpgCommunicationEvent = CPGCommunicationEvent.from(cpgCommunicationRequest)

    val performPhase = ActivityFlow.of(repository, cpgCommunicationEvent)

    assertThat(performPhase.getCurrentPhase()).isInstanceOf(PerformPhase::class.java)
    assertThat(performPhase.preparePlan().isFailure).isTrue()
  }

  @Test
  fun `prepareOrder should succeed when in proposal phase`(): Unit = runBlockingOnWorkerThread {
    val cpgCommunicationRequest =
      CPGRequestResource.of(
          CommunicationRequest().apply {
            id = "com-req-01"
            status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
            subject = Reference("Patient/pat-01")
            meta.addProfile(
              "http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest",
            )

            addPayload().apply { content = StringType("Proposal") }
          },
        )
        .apply { setIntent(Intent.PROPOSAL) }
    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    repository.create(cpgCommunicationRequest.resource)

    val proposalPhase = ActivityFlow.of(repository, cpgCommunicationRequest)

    assertThat(proposalPhase.getCurrentPhase()).isInstanceOf(ProposalPhase::class.java)
    assertThat(proposalPhase.prepareOrder().isSuccess).isTrue()
  }

  @Test
  fun `prepareOrder should succeed when in plan phase`(): Unit = runBlockingOnWorkerThread {
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
    val cpgCommunicationPlanRequest =
      cpgCommunicationRequest.copy(
        id = "com-req-01=plan",
        status = Status.ACTIVE,
        intent = Intent.PLAN,
      ) as CPGCommunicationRequest

    val planPhase = ActivityFlow.of(repository, cpgCommunicationPlanRequest)

    assertThat(planPhase.getCurrentPhase()).isInstanceOf(PlanPhase::class.java)
    assertThat(planPhase.prepareOrder().isSuccess).isTrue()
  }

  @Test
  fun `prepareOrder should fail when in in order phase`(): Unit = runBlockingOnWorkerThread {
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
    val cpgCommunicationOrderRequest =
      cpgCommunicationRequest.copy(
        id = "com-req-01=plan",
        status = Status.ACTIVE,
        intent = Intent.ORDER,
      ) as CPGCommunicationRequest

    val orderPhase = ActivityFlow.of(repository, cpgCommunicationOrderRequest)

    assertThat(orderPhase.getCurrentPhase()).isInstanceOf(OrderPhase::class.java)
    assertThat(orderPhase.prepareOrder().isFailure).isTrue()
  }

  @Test
  fun `prepareOrder should fail when in in perform phase`(): Unit = runBlockingOnWorkerThread {
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
    val cpgCommunicationEvent = CPGCommunicationEvent.from(cpgCommunicationRequest)

    val performPhase = ActivityFlow.of(repository, cpgCommunicationEvent)

    assertThat(performPhase.getCurrentPhase()).isInstanceOf(PerformPhase::class.java)
    assertThat(performPhase.prepareOrder().isFailure).isTrue()
  }

  @Test
  fun `preparePerform should succeed when in proposal phase`(): Unit = runBlockingOnWorkerThread {
    val cpgCommunicationRequest =
      CPGRequestResource.of(
          CommunicationRequest().apply {
            id = "com-req-01"
            status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
            subject = Reference("Patient/pat-01")
            meta.addProfile(
              "http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest",
            )

            addPayload().apply { content = StringType("Proposal") }
          },
        )
        .apply { setIntent(Intent.PROPOSAL) }
    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    repository.create(cpgCommunicationRequest.resource)

    val proposalPhase = ActivityFlow.of(repository, cpgCommunicationRequest)

    assertThat(proposalPhase.getCurrentPhase()).isInstanceOf(ProposalPhase::class.java)
    assertThat(proposalPhase.preparePerform(CPGCommunicationEvent::class.java).isSuccess).isTrue()
  }

  @Test
  fun `preparePerform should succeed when in plan phase`(): Unit = runBlockingOnWorkerThread {
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
    val cpgCommunicationPlanRequest =
      cpgCommunicationRequest.copy(
        id = "com-req-01=plan",
        status = Status.ACTIVE,
        intent = Intent.PLAN,
      ) as CPGCommunicationRequest

    val planPhase = ActivityFlow.of(repository, cpgCommunicationPlanRequest)

    assertThat(planPhase.getCurrentPhase()).isInstanceOf(PlanPhase::class.java)
    assertThat(planPhase.preparePerform(CPGCommunicationEvent::class.java).isSuccess).isTrue()
  }

  @Test
  fun `preparePerform should succeed when in order phase`(): Unit = runBlockingOnWorkerThread {
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
    val cpgCommunicationOrderRequest =
      cpgCommunicationRequest.copy(
        id = "com-req-01=plan",
        status = Status.ACTIVE,
        intent = Intent.ORDER,
      ) as CPGCommunicationRequest

    val orderPhase = ActivityFlow.of(repository, cpgCommunicationOrderRequest)

    assertThat(orderPhase.getCurrentPhase()).isInstanceOf(OrderPhase::class.java)
    assertThat(orderPhase.preparePerform(CPGCommunicationEvent::class.java).isSuccess).isTrue()
  }

  @Test
  fun `preparePerform should fail when in in perform phase`(): Unit = runBlockingOnWorkerThread {
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
    val cpgCommunicationEvent = CPGCommunicationEvent.from(cpgCommunicationRequest)

    val performPhase = ActivityFlow.of(repository, cpgCommunicationEvent)

    assertThat(performPhase.getCurrentPhase()).isInstanceOf(PerformPhase::class.java)
    assertThat(performPhase.preparePerform(CPGCommunicationEvent::class.java).isFailure).isTrue()
  }

  @Test
  fun `getCurrentPhase should return the current phase of the flow`() = runBlockingOnWorkerThread {
    val cpgCommunicationRequest =
      CPGRequestResource.of(
          CommunicationRequest().apply {
            id = "com-req-01"
            status = CommunicationRequest.CommunicationRequestStatus.ACTIVE
            subject = Reference("Patient/pat-01")
            meta.addProfile(
              "http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-communicationrequest",
            )

            addPayload().apply { content = StringType("Proposal") }
          },
        )
        .apply { setIntent(Intent.PROPOSAL) }
    val repository = FhirEngineRepository(FhirContext.forR4Cached(), fhirEngine)
    repository.create(cpgCommunicationRequest.resource)

    val flow = ActivityFlow.of(repository, cpgCommunicationRequest)

    assertThat(flow.getCurrentPhase()).isInstanceOf(ProposalPhase::class.java)

    flow
      .preparePlan()
      .onSuccess {
        it.setStatus(Status.ACTIVE)
        flow.initiatePlan(it)
      }
      .onFailure { fail("Unexpected", it) }

    assertThat(flow.getCurrentPhase()).isInstanceOf(PlanPhase::class.java)

    flow
      .prepareOrder()
      .onSuccess {
        it.setStatus(Status.ACTIVE)
        flow.initiateOrder(it)
      }
      .onFailure { fail("Unexpected", it) }

    assertThat(flow.getCurrentPhase()).isInstanceOf(OrderPhase::class.java)
  }
}
