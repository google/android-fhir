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
import com.google.android.fhir.workflow.activity.resource.event.EventStatus
import com.google.android.fhir.workflow.activity.resource.request.CPGCommunicationRequest
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource
import com.google.android.fhir.workflow.activity.resource.request.Intent
import com.google.android.fhir.workflow.activity.resource.request.Status
import com.google.android.fhir.workflow.repositories.FhirEngineRepository
import com.google.android.fhir.workflow.runBlockingOnWorkerThread
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import kotlin.test.fail
import org.hl7.fhir.r4.model.CommunicationRequest
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

  @Test
  fun `initiatePlan should move the flow to plan phase when correct prepared plan is provided`() =
    runBlockingOnWorkerThread {
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

      assertThat(flow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PROPOSAL)

      val preparePlan = flow.preparePlan()
      assertThat(preparePlan.isSuccess).isTrue()

      val initiatedPlan =
        preparePlan.getOrThrow().let {
          it.setStatus(Status.ACTIVE)
          flow.initiatePlan(it)
        }

      assertThat(initiatedPlan.isSuccess).isTrue()
      assertThat(initiatedPlan.getOrThrow().getPhaseName()).isEqualTo(Phase.PhaseName.PLAN)
    }

  @Test
  fun `initiatePlan should fail when provided plan when corrupted prepared plan is provided`() =
    runBlockingOnWorkerThread {
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

      assertThat(flow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PROPOSAL)

      val preparePlan = flow.preparePlan()
      assertThat(preparePlan.isSuccess).isTrue()

      val preparedPlanResource = preparePlan.getOrThrow()
      preparedPlanResource.let {
        it.setStatus(Status.ACTIVE)
        it.resource.basedOn.last().apply { this.reference = "" }
      }
      val initiatedPlan = preparePlan.getOrThrow().let { flow.initiatePlan(it) }

      assertThat(initiatedPlan.isFailure).isTrue()
      // check that the flow is still in old phase (proposal).
      assertThat(flow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PROPOSAL)
    }

  @Test
  fun `initiateOrder should move the flow to order phase when correct prepared order is provided`() =
    runBlockingOnWorkerThread {
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

      assertThat(flow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PROPOSAL)

      val prepareOrder = flow.prepareOrder()
      assertThat(prepareOrder.isSuccess).isTrue()

      val initiatedOrder =
        prepareOrder.getOrThrow().let {
          it.setStatus(Status.ACTIVE)
          flow.initiateOrder(it)
        }

      assertThat(initiatedOrder.isSuccess).isTrue()
      assertThat(initiatedOrder.getOrThrow().getPhaseName()).isEqualTo(Phase.PhaseName.ORDER)
    }

  @Test
  fun `initiateOrder should fail when provided order when corrupted prepared order is provided`() =
    runBlockingOnWorkerThread {
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

      assertThat(flow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PROPOSAL)

      val prepareOrder = flow.prepareOrder()
      assertThat(prepareOrder.isSuccess).isTrue()

      val preparedPlanResource = prepareOrder.getOrThrow()
      preparedPlanResource.let {
        it.setStatus(Status.ACTIVE)
        it.resource.basedOn.last().apply { this.reference = "" }
      }
      val initiatedOrder = prepareOrder.getOrThrow().let { flow.initiateOrder(it) }

      assertThat(initiatedOrder.isFailure).isTrue()
      // check that the flow is still in old phase (proposal).
      assertThat(flow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PROPOSAL)
    }

  @Test
  fun `initiatePerform should move the flow to perform phase when correct prepared event is provided`() =
    runBlockingOnWorkerThread {
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

      assertThat(flow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PROPOSAL)

      val preparePerform = flow.preparePerform(CPGCommunicationEvent::class.java)
      assertThat(preparePerform.isSuccess).isTrue()

      val preparedEvent = preparePerform.getOrThrow()
      preparedEvent.let { it.setStatus(EventStatus.INPROGRESS) }
      val initiatedPerform = preparePerform.getOrThrow().let { flow.initiatePerform(it) }
      assertThat(initiatedPerform.isSuccess).isTrue()
      assertThat(initiatedPerform.getOrThrow().getPhaseName()).isEqualTo(Phase.PhaseName.PERFORM)
    }

  @Test
  fun `initiatePerform should fail when corrupted prepared event is provided`() =
    runBlockingOnWorkerThread {
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

      assertThat(flow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PROPOSAL)

      val preparePerform = flow.preparePerform(CPGCommunicationEvent::class.java)
      assertThat(preparePerform.isSuccess).isTrue()

      val preparedEvent = preparePerform.getOrThrow()
      preparedEvent.let {
        it.setStatus(EventStatus.INPROGRESS)
        it.resource.basedOn.last().apply { this.reference = "" }
      }
      val initiatedPerform = preparePerform.getOrThrow().let { flow.initiatePerform(it) }

      assertThat(initiatedPerform.isFailure).isTrue()
      // check that the flow is still in old phase (proposal).
      assertThat(flow.getCurrentPhase().getPhaseName()).isEqualTo(Phase.PhaseName.PROPOSAL)
    }

  @Test
  fun `getPreviousPhases should return a list of all previous phases`(): Unit =
    runBlockingOnWorkerThread {
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

      flow.initiatePlan(
        flow.preparePlan().getOrThrow().apply { setStatus(Status.ACTIVE) },
      )

      flow.initiateOrder(
        flow.prepareOrder().getOrThrow().apply { setStatus(Status.ACTIVE) },
      )

      flow.initiatePerform(
        flow.preparePerform(CPGCommunicationEvent::class.java).getOrThrow().apply {
          setStatus(EventStatus.INPROGRESS)
        },
      )

      val result = flow.getPreviousPhases()
      assertThat(result.map { it.getPhaseName() })
        .containsExactly(Phase.PhaseName.ORDER, Phase.PhaseName.PLAN, Phase.PhaseName.PROPOSAL)
    }
}
