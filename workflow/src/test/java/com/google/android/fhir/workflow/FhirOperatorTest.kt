/*
 * Copyright 2023 Google LLC
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

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.knowledge.FhirNpmPackage
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.util.TimeZone
import kotlin.reflect.KSuspendFunction1
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.Resource
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.fhir.cr.measure.common.MeasureEvalType
import org.robolectric.RobolectricTestRunner
import org.skyscreamer.jsonassert.JSONAssert.assertEquals

@RunWith(RobolectricTestRunner::class)
class FhirOperatorTest {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val knowledgeManager = KnowledgeManager.create(context = context, inMemory = true)
  private val fhirContext = FhirContext.forR4()
  private val jsonWriter = fhirContext.newJsonParser()

  private val loader = TestBundleLoader(fhirContext)

  private lateinit var fhirEngine: FhirEngine
  private lateinit var fhirOperator: FhirOperator

  @Before
  fun setUp() = runBlockingOnWorkerThread {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    fhirEngine = FhirEngineProvider.getInstance(context)
    fhirOperator = FhirOperator(fhirContext, fhirEngine, knowledgeManager)

    // Installing ANC CDS to the IGManager
    val rootDirectory = File(javaClass.getResource("/anc-cds")!!.file)
    knowledgeManager.install(
      FhirNpmPackage(
        "com.google.android.fhir",
        "1.0.0",
        "http://github.com/google/android-fhir",
      ),
      rootDirectory,
    )
  }

  @Test
  fun generateCarePlan() = runBlockingOnWorkerThread {
    loader.loadFile(
      "/plan-definition/rule-filters/RuleFilters-1.0.0-bundle.json",
      ::installToIgManager,
    )
    loader.loadFile(
      "/plan-definition/rule-filters/tests-Reportable-bundle.json",
      ::installToIgManager,
    )
    loader.loadFile(
      "/plan-definition/rule-filters/tests-NotReportable-bundle.json",
      ::installToIgManager,
    )

    loader.loadFile(
      "/first-contact/01-registration/patient-charity-otala-1.json",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml",
      ::importToFhirEngine,
    )

    assertThat(
        fhirOperator.generateCarePlan(
          planDefinition =
            CanonicalType(
              "http://hl7.org/fhir/us/ecr/PlanDefinition/plandefinition-RuleFilters-1.0.0",
            ),
          subject = "Patient/Reportable",
          encounterId = "reportable-encounter",
        ),
      )
      .isNotNull()
  }

  @Test
  fun generateCarePlanWithoutEncounter() = runBlockingOnWorkerThread {
    loader.loadFile("/plan-definition/med-request/med_request_patient.json", ::importToFhirEngine)
    loader.loadFile(
      "/plan-definition/med-request/med_request_plan_definition.json",
      ::installToIgManager,
    )

    val carePlan =
      fhirOperator.generateCarePlan(
        planDefinition = CanonicalType("http://localhost/PlanDefinition/MedRequest-Example"),
        subject = "Patient/Patient-Example",
      )

    assertEquals(
      loader.readResourceAsString("/plan-definition/med-request/med_request_careplan.json"),
      jsonWriter.encodeResourceToString(carePlan),
      true,
    )
  }

  @Test
  fun generateCarePlanWithCqlApplicabilityCondition() = runBlockingOnWorkerThread {
    loader.loadFile(
      "/plan-definition/cql-applicability-condition/patient.json",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/plan-definition/cql-applicability-condition/plan_definition.json",
      ::installToIgManager,
    )
    loader.loadFile(
      "/plan-definition/cql-applicability-condition/example-1.0.0.cql",
      ::installToIgManager,
    )

    val carePlan =
      fhirOperator.generateCarePlan(
        planDefinition = CanonicalType("http://example.com/PlanDefinition/Plan-Definition-Example"),
        subject = "Patient/Female-Patient-Example",
      )

    println(jsonWriter.setPrettyPrint(true).encodeResourceToString(carePlan))

    assertEquals(
      loader.readResourceAsString("/plan-definition/cql-applicability-condition/care_plan.json"),
      jsonWriter.setPrettyPrint(true).encodeResourceToString(carePlan),
      true,
    )
  }

  @Test
  @Ignore("Bug on workflow incorrectly returns 2022-12-31T00:00:00 instead of 2021-12-31T23:59:59")
  fun evaluatePopulationMeasure() = runBlockingOnWorkerThread {
    loader.loadFile(
      "/first-contact/01-registration/patient-charity-otala-1.json",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml",
      ::importToFhirEngine,
    )

    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
        start = "2019-01-01",
        end = "2021-12-31",
        reportType = MeasureEvalType.POPULATION.toCode(),
        subjectId = null,
        practitioner = null,
      )

    measureReport.date = null

    assertEquals(
      loader.readResourceAsString("/first-contact/04-results/population-report.json"),
      jsonWriter.setPrettyPrint(true).encodeResourceToString(measureReport),
      true,
    )
  }

  @Test
  fun evaluateGroupPopulationMeasure() = runBlockingOnWorkerThread {
    loader.loadFile("/group-measure/PatientGroups-1.0.0.cql", ::installToIgManager)
    loader.loadFile("/group-measure/PatientGroupsMeasure.json", ::installToIgManager)

    loader.loadFile("/group-measure/Data-Patients-bundle.json", ::importToFhirEngine)
    loader.loadFile("/group-measure/Data-Groups-bundle.json", ::importToFhirEngine)

    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "Measure/PatientGroupsMeasure",
        start = "2019-01-01",
        end = "2022-12-31",
        reportType = MeasureEvalType.POPULATION.toCode(),
        subjectId = null,
        practitioner = null,
      )

    measureReport.date = null

    assertEquals(
      loader.readResourceAsString("/group-measure/Results-Measure-report.json"),
      jsonWriter.setPrettyPrint(true).encodeResourceToString(measureReport),
      true,
    )
  }

  @Test
  @Ignore("Bug on workflow incorrectly returns 2022-12-31T00:00:00 instead of 2021-12-31T23:59:59")
  fun evaluateIndividualSubjectMeasure() = runBlockingOnWorkerThread {
    loader.loadFile(
      "/first-contact/01-registration/patient-charity-otala-1.json",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml",
      ::importToFhirEngine,
    )
    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
        start = "2020-01-01",
        end = "2020-01-31",
        reportType = MeasureEvalType.SUBJECT.toCode(),
        subjectId = "charity-otala-1",
        practitioner = "jane",
      )

    measureReport.date = null

    println(jsonWriter.setPrettyPrint(true).encodeResourceToString(measureReport))

    assertEquals(
      loader.readResourceAsString("/first-contact/04-results/subject-report.json"),
      jsonWriter.setPrettyPrint(true).encodeResourceToString(measureReport),
      true,
    )
  }

  private suspend fun importToFhirEngine(resource: Resource) {
    fhirEngine.create(resource)
  }

  private suspend fun installToIgManager(resource: Resource) {
    knowledgeManager.install(writeToFile(resource))
  }

  private fun writeToFile(resource: Resource): File {
    val fileName =
      if (resource is MetadataResource && resource.name != null) {
        if (resource.version != null) {
          resource.name + "-" + resource.version
        } else {
          resource.name
        }
      } else {
        resource.idElement.toString()
      }
    return File(context.filesDir, fileName).apply {
      this.parentFile.mkdirs()
      writeText(jsonWriter.encodeResourceToString(resource))
    }
  }
}
