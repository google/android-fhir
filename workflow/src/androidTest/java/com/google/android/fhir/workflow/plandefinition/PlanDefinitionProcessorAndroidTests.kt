/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.workflow.plandefinition

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.workflow.testing.plandefinition.PlanDefinitionProcessorTestBase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlanDefinitionProcessorAndroidTests : PlanDefinitionProcessorTestBase() {
  @Test
  fun testChildRoutineVisit() {
    test(
      "/plan-definition-apply/child-routine-visit/child_routine_visit_patient.json",
      "/plan-definition-apply/child-routine-visit/child_routine_visit_plan_definition.json",
      "ChildRoutineVisit-PlanDefinition-1.0.0",
      "Patient/ChildRoutine-Reportable",
      null,
      "/plan-definition-apply/child-routine-visit/child_routine_visit_careplan.json"
    )
  }

  @Test
  fun testHelloWorld() {
    test(
      "/plan-definition-apply/hello-world/hello-world-patient-data.json",
      "/plan-definition-apply/hello-world/hello-world-patient-view-bundle.json",
      "hello-world-patient-view",
      "helloworld-patient-1",
      "helloworld-patient-1-encounter-1",
      "/plan-definition-apply/hello-world/hello-world-careplan.json"
    )
  }

  @Test
  fun testOpioidRec10PatientView() {
    test(
      "/plan-definition-apply/opioid-Rec10-patient-view/opioid-Rec10-patient-view-patient-data.json",
      "/plan-definition-apply/opioid-Rec10-patient-view/opioid-Rec10-patient-view-bundle.json",
      "opioidcds-10-patient-view",
      "example-rec-10-patient-view-POS-Cocaine-drugs",
      "example-rec-10-patient-view-POS-Cocaine-drugs-prefetch",
      "/plan-definition-apply/opioid-Rec10-patient-view/opioid-Rec10-patient-view-careplan.json"
    )
  }

  @Test
  fun testRuleFiltersNotReportable() {
    test(
      "/plan-definition-apply/rule-filters/tests-NotReportable-bundle.json",
      "/plan-definition-apply/rule-filters/RuleFilters-1.0.0-bundle.json",
      "plandefinition-RuleFilters-1.0.0",
      "NotReportable",
      null,
      "/plan-definition-apply/rule-filters/NotReportableCarePlan.json"
    )
  }

  @Test
  fun testRuleFiltersReportable() {
    test(
      "/plan-definition-apply/rule-filters/tests-Reportable-bundle.json",
      "/plan-definition-apply/rule-filters/RuleFilters-1.0.0-bundle.json",
      "plandefinition-RuleFilters-1.0.0",
      "Reportable",
      null,
      "/plan-definition-apply/rule-filters/ReportableCarePlan.json"
    )
  }
}
