/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.workflow

import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.workflow.testing.PlanDefinition
import com.google.android.fhir.workflow.testing.TestRepositoryFactory
import org.hl7.fhir.r4.model.IdType
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.fhir.utility.r4.Parameters

@RunWith(AndroidJUnit4::class)
class PlanDefinitionProcessorAndroidTest {
  private val fhirContext = FhirContext.forR4Cached()

  @Test
  fun testChildRoutineVisit() =
    PlanDefinition.Assert.that(
        "ChildRoutineVisit-PlanDefinition-1.0.0",
        "Patient/ChildRoutine-Reportable",
        null,
      )
      .withData("/plan-definition/child-routine-visit/child_routine_visit_patient.json")
      .withContent("/plan-definition/child-routine-visit/child_routine_visit_plan_definition.json")
      .withTerminology(
        "/plan-definition/child-routine-visit/child_routine_visit_plan_definition.json",
      )
      .apply()
      .isEqualsTo("/plan-definition/child-routine-visit/child_routine_visit_careplan.json")

  @Test
  fun testAncVisitContainedActivityDefinition() =
    PlanDefinition.Assert.that("AncVisit-PlanDefinition", "Patient/TEST_PATIENT", null, null)
      .withData("/plan-definition/anc-visit/anc_visit_patient.json")
      .withContent("/plan-definition/anc-visit/anc_visit_plan_definition.json")
      .withTerminology("/plan-definition/anc-visit/anc_visit_plan_definition.json")
      .apply()
      .isEqualsTo("/plan-definition/anc-visit/anc_visit_careplan.json")

  @Test
  @Ignore("works when the full suite is run but not if this individual test is run")
  fun testANCDT17() {
    val repository =
      TestRepositoryFactory.createRepository(
        fhirContext,
        "/plan-definition/anc-dak",
      )
    PlanDefinition.Assert.that(
        "ANCDT17",
        "Patient/5946f880-b197-400b-9caa-a3c661d23041",
        "Encounter/helloworld-patient-1-encounter-1",
        null,
      )
      .withRepository(repository)
      .withParameters(
        Parameters.parameters(
          Parameters.part(
            "encounter",
            "helloworld-patient-1-encounter-1",
          ),
        ),
      )
      .withExpectedCarePlanId(IdType("CarePlan", "ANCDT17"))
      .apply()
      .equalsToExpected()
  }

  @Test
  fun testANCDT17WithElm() {
    PlanDefinition.Assert.that(
        "ANCDT17",
        "Patient/5946f880-b197-400b-9caa-a3c661d23041",
        "Encounter/ANCDT17-encounter",
        null,
      )
      .withData("/plan-definition/anc-dak/data-bundle.json")
      .withContent("/plan-definition/anc-dak/content-bundle.json")
      .withTerminology("/plan-definition/anc-dak/terminology-bundle.json")
      .withParameters(Parameters.parameters(Parameters.part("encounter", "ANCDT17-encounter")))
      .apply()
      .isEqualsTo("/plan-definition/anc-dak/output-careplan.json")
  }

  @Test
  fun testFhirPath() {
    val planDefinitionID = "DischargeInstructionsPlan"
    val patientID = "Patient/Patient1"
    val practitionerID = "Practitioner/Practitioner1"
    val data = "/plan-definition/base-repo/tests/Bundle-DischargeInstructions-Patient-Data.json"
    PlanDefinition.Assert.that(planDefinitionID, patientID, null, practitionerID)
      .withRepositoryPath("/plan-definition/base-repo/")
      .withAdditionalData(data)
      .applyR5()
      .hasCommunicationRequestPayload()
  }

  @Test
  fun testHelloWorld() =
    PlanDefinition.Assert.that(
        "hello-world-patient-view",
        "helloworld-patient-1",
        "helloworld-patient-1-encounter-1",
      )
      .withRepositoryPath("/plan-definition/base-repo")
      .withExpectedCarePlanId(IdType("CarePlan", "hello-world-patient-view"))
      .apply()
      .equalsToExpected()

  @Test
  @Ignore("Something is off in this test")
  fun testOpioidRec10PatientView() {
    val planDefinitionID = "opioidcds-10-patient-view"
    val patientID = "example-rec-10-patient-view-POS-Cocaine-drugs"
    val encounterID = "example-rec-10-patient-view-POS-Cocaine-drugs-prefetch"
    val repository =
      TestRepositoryFactory.createRepository(
        fhirContext,
        "/plan-definition/opioid-Rec10-patient-view",
      )
    PlanDefinition.Assert.that(planDefinitionID, patientID, encounterID, null)
      .withRepository(repository)
      .withExpectedCarePlanId(IdType("CarePlan", "opioidcds-10-patient-view"))
      .apply()
      .equalsToExpected()
  }

  @Test
  fun testCDSHooksMultipleActions() {
    val planDefinitionID = "CdsHooksMultipleActions-PlanDefinition-1.0.0"
    val patientID = "patient-CdsHooksMultipleActions"
    val data =
      "/plan-definition/cds-hooks-multiple-actions/cds_hooks_multiple_actions_patient_data.json"
    val content =
      "/plan-definition/cds-hooks-multiple-actions/cds_hooks_multiple_actions_plan_definition.json"
    PlanDefinition.Assert.that(planDefinitionID, patientID, null, null)
      .withData(data)
      .withContent(content)
      .withTerminology(content)
      .apply()
      .isEqualsTo(
        "/plan-definition/cds-hooks-multiple-actions/cds_hooks_multiple_actions_careplan.json",
      )
  }

  @Test
  fun testQuestionnairePrepopulate() {
    val planDefinitionID = "prepopulate"
    val patientID = "OPA-Patient1"
    val parameters = Parameters.parameters(Parameters.stringPart("ClaimId", "OPA-Claim1"))
    PlanDefinition.Assert.that(planDefinitionID, patientID, null, null)
      .withRepositoryPath("/plan-definition/base-repo")
      .withParameters(parameters)
      .withExpectedCarePlanId(IdType("CarePlan", "prepopulate"))
      .apply()
      .equalsToExpected()
  }

  @Test
  fun testQuestionnairePrepopulate_NoLibrary() {
    val planDefinitionID = "prepopulate-noLibrary"
    val patientID = "OPA-Patient1"
    val parameters = Parameters.parameters(Parameters.stringPart("ClaimId", "OPA-Claim1"))
    PlanDefinition.Assert.that(planDefinitionID, patientID, null, null)
      .withRepositoryPath("/plan-definition/base-repo")
      .withParameters(parameters)
      .apply()
      .hasOperationOutcome()
  }

  @Test
  fun testQuestionnaireResponse() {
    val planDefinitionID = "prepopulate"
    val patientID = "OPA-Patient1"
    val dataId = IdType("QuestionnaireResponse", "OutpatientPriorAuthorizationRequest-OPA-Patient1")
    val parameters = Parameters.parameters(Parameters.stringPart("ClaimId", "OPA-Claim1"))
    PlanDefinition.Assert.that(planDefinitionID, patientID, null, null)
      .withRepositoryPath("/plan-definition/base-repo")
      .withAdditionalDataId(dataId)
      .withParameters(parameters)
      .apply()
      .hasContained(4)
  }

  @Test
  fun testGenerateQuestionnaire() {
    val planDefinitionID = "generate-questionnaire"
    val patientID = "OPA-Patient1"
    val parameters = Parameters.parameters(Parameters.stringPart("ClaimId", "OPA-Claim1"))
    PlanDefinition.Assert.that(planDefinitionID, patientID, null, null)
      .withRepositoryPath("/plan-definition/base-repo")
      .withParameters(parameters)
      .withExpectedCarePlanId(IdType("CarePlan", "generate-questionnaire"))
      .apply()
      .equalsToExpected()
  }

  @Test
  fun testASLPA1() {
    val planDefinitionID = "ASLPA1"
    val patientID = "positive"
    val parameters =
      Parameters.parameters(
        Parameters.stringPart("Service Request Id", "SleepStudy"),
        Parameters.stringPart("Service Request Id", "SleepStudy2"),
        Parameters.stringPart("Coverage Id", "Coverage-positive"),
      )
    val repository =
      TestRepositoryFactory.createRepository(
        fhirContext,
        "/plan-definition/pa-aslp",
      )
    PlanDefinition.Assert.that(planDefinitionID, patientID, null, null)
      .withParameters(parameters)
      .withRepository(repository)
      .applyR5()
      .hasEntry(2)
  }

  @Test
  fun testPackageASLPA1() {
    val planDefinitionID = "ASLPA1"
    val repository =
      TestRepositoryFactory.createRepository(
        fhirContext,
        "/plan-definition/pa-aslp",
      )
    PlanDefinition.Assert.that(planDefinitionID, null, null, null)
      .withRepository(repository)
      .packagePlanDefinition()
      .hasEntry(20)
  }
}
