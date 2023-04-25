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

package com.google.android.fhir.workflow

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.knowledge.ImplementationGuide
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.testing.FhirEngineProviderTestRule
import com.google.android.fhir.workflow.testing.CqlBuilder
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.util.TimeZone
import kotlin.reflect.KSuspendFunction1
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.cql.evaluator.measure.common.MeasureEvalType
import org.robolectric.RobolectricTestRunner
import org.skyscreamer.jsonassert.JSONAssert.assertEquals

@RunWith(RobolectricTestRunner::class)
class FhirOperatorTest {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val knowledgeManager = KnowledgeManager.createInMemory(context)
  private val fhirContext = FhirContext.forR4()
  private val jsonParser = fhirContext.newJsonParser()
  private val xmlParser = fhirContext.newXmlParser()

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
      ImplementationGuide(
        "com.google.android.fhir",
        "1.0.0",
        "http://github.com/google/android-fhir"
      ),
      rootDirectory
    )
  }

  @After
  fun tearDown() {
    knowledgeManager.close()
  }

  @Test
  fun generateCarePlan() = runBlockingOnWorkerThread {
    loadFile("/plan-definition/rule-filters/RuleFilters-1.0.0-bundle.json", ::importToFhirEngine)
    loadFile("/plan-definition/rule-filters/tests-Reportable-bundle.json", ::importToFhirEngine)
    loadFile("/plan-definition/rule-filters/tests-NotReportable-bundle.json", ::importToFhirEngine)

    loadFile("/first-contact/01-registration/patient-charity-otala-1.json", ::importToFhirEngine)
    loadFile(
      "/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml",
      ::importToFhirEngine
    )
    loadFile(
      "/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml",
      ::importToFhirEngine
    )
    loadFile(
      "/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml",
      ::importToFhirEngine
    )

    assertThat(
        fhirOperator.generateCarePlan(
          planDefinitionId = "plandefinition-RuleFilters-1.0.0",
          patientId = "Reportable",
          encounterId = "reportable-encounter"
        )
      )
      .isNotNull()
  }

  @Test
  fun generateCarePlanWithoutEncounter() = runBlockingOnWorkerThread {
    loadFile("/plan-definition/med-request/med_request_patient.json", ::importToFhirEngine)
    loadFile("/plan-definition/med-request/med_request_plan_definition.json", ::installToIgManager)

    val carePlan =
      fhirOperator.generateCarePlan(
        planDefinitionId = "MedRequest-Example",
        patientId = "Patient/Patient-Example"
      )

    println(jsonParser.encodeResourceToString(carePlan))

    assertEquals(
      readResourceAsString("/plan-definition/med-request/med_request_careplan.json"),
      jsonParser.encodeResourceToString(carePlan),
      true
    )
  }

  @Test
  fun generateCarePlanWithCqlApplicabilityCondition() = runBlockingOnWorkerThread {
    loadFile("/plan-definition/cql-applicability-condition/patient.json", ::importToFhirEngine)
    loadFile(
      "/plan-definition/cql-applicability-condition/plan_definition.json",
      ::installToIgManager
    )
    loadFile("/plan-definition/cql-applicability-condition/example-1.0.0.cql", ::installToIgManager)

    val carePlan =
      fhirOperator.generateCarePlan(
        planDefinitionId = "Plan-Definition-Example",
        patientId = "Patient/Female-Patient-Example"
      )

    assertEquals(
      readResourceAsString("/plan-definition/cql-applicability-condition/care_plan.json"),
      jsonParser.encodeResourceToString(carePlan),
      true
    )
  }

  @Test
  fun evaluatePopulationMeasure() = runBlockingOnWorkerThread {
    loadFile("/first-contact/01-registration/patient-charity-otala-1.json", ::importToFhirEngine)
    loadFile(
      "/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml",
      ::importToFhirEngine
    )
    loadFile(
      "/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml",
      ::importToFhirEngine
    )
    loadFile(
      "/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml",
      ::importToFhirEngine
    )

    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
        start = "2019-01-01",
        end = "2021-12-31",
        reportType = MeasureEvalType.POPULATION.toCode(),
        subject = null,
        practitioner = null
      )

    measureReport.date = null

    assertEquals(
      readResourceAsString("/first-contact/04-results/population-report.json"),
      jsonParser.setPrettyPrint(true).encodeResourceToString(measureReport),
      true
    )
  }

  @Test
  fun evaluateGroupPopulationMeasure() = runBlockingOnWorkerThread {
    loadFile("/group-measure/PatientGroups-1.0.0.cql", ::installToIgManager)
    loadFile("/group-measure/PatientGroupsMeasure.json", ::installToIgManager)

    loadFile("/group-measure/Data-Patients-bundle.json", ::importToFhirEngine)
    loadFile("/group-measure/Data-Groups-bundle.json", ::importToFhirEngine)

    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "Measure/PatientGroupsMeasure",
        start = "2019-01-01",
        end = "2022-12-31",
        reportType = MeasureEvalType.POPULATION.toCode(),
        subject = null,
        practitioner = null
      )

    measureReport.date = null

    assertEquals(
      readResourceAsString("/group-measure/Results-Measure-report.json"),
      jsonParser.setPrettyPrint(true).encodeResourceToString(measureReport),
      true
    )
  }

  @Test
  fun evaluateIndividualSubjectMeasure() = runBlockingOnWorkerThread {
    loadFile("/first-contact/01-registration/patient-charity-otala-1.json", ::importToFhirEngine)
    loadFile(
      "/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml",
      ::importToFhirEngine
    )
    loadFile(
      "/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml",
      ::importToFhirEngine
    )
    loadFile(
      "/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml",
      ::importToFhirEngine
    )
    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
        start = "2020-01-01",
        end = "2020-01-31",
        reportType = MeasureEvalType.SUBJECT.toCode(),
        subject = "charity-otala-1",
        practitioner = "jane"
      )

    measureReport.date = null

    assertEquals(
      readResourceAsString("/first-contact/04-results/subject-report.json"),
      jsonParser.setPrettyPrint(true).encodeResourceToString(measureReport),
      true
    )
  }

  private suspend fun loadFile(path: String, importFunction: KSuspendFunction1<Resource, Unit>) {
    val resource =
      if (path.endsWith(suffix = ".xml")) {
        xmlParser.parseResource(open(path)) as Resource
      } else if (path.endsWith(".json")) {
        jsonParser.parseResource(open(path)) as Resource
      } else if (path.endsWith(".cql")) {
        toFhirLibrary(open(path))
      } else {
        throw IllegalArgumentException("Only xml and json and cql files are supported")
      }
    loadResource(resource, importFunction)
  }

  private suspend fun loadResource(
    resource: Resource,
    importFunction: KSuspendFunction1<Resource, Unit>
  ) {
    when (resource.resourceType) {
      ResourceType.Bundle -> loadBundle(resource as Bundle, importFunction)
      else -> importFunction(resource)
    }
  }

  private suspend fun loadBundle(
    bundle: Bundle,
    importFunction: KSuspendFunction1<Resource, Unit>
  ) {
    for (entry in bundle.entry) {
      val resource = entry.resource
      loadResource(resource, importFunction)
    }
  }

  private fun writeToFile(resource: Resource): File {
    val fileName =
      if (resource is MetadataResource && resource.name != null) {
        resource.name
      } else {
        resource.idElement.idPart
      }
    return File(context.filesDir, fileName).apply {
      writeText(jsonParser.encodeResourceToString(resource))
    }
  }

  private fun toFhirLibrary(cql: InputStream): Library {
    return CqlBuilder.compileAndBuild(cql)
  }

  private fun open(path: String) = javaClass.getResourceAsStream(path)!!

  private fun readResourceAsString(path: String) = open(path).readBytes().decodeToString()

  private suspend fun importToFhirEngine(resource: Resource) {
    fhirEngine.create(resource)
  }

  private suspend fun installToIgManager(resource: Resource) {
    knowledgeManager.install(writeToFile(resource))
  }
}
