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

import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.testing.FhirEngineProviderTestRule
import com.google.android.fhir.workflow.testing.CqlBuilderUtils
import com.google.android.fhir.workflow.testing.toLocalDate
import com.google.common.truth.Truth.assertThat
import java.io.InputStream
import java.util.Base64
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Measure
import org.hl7.fhir.r4.model.MeasureReport
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.cql.evaluator.measure.common.MeasureEvalType
import org.robolectric.RobolectricTestRunner
import org.skyscreamer.jsonassert.JSONAssert

@RunWith(RobolectricTestRunner::class)
class FhirOperatorTest {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private lateinit var fhirEngine: FhirEngine
  private lateinit var fhirOperator: FhirOperator

  companion object {
    private val libraryBundle: Bundle by lazy { parseJson("/ANCIND01-bundle.json") }
    private val fhirContext = FhirContext.forR4()
    private val jsonParser = fhirContext.newJsonParser()
    private val xmlParser = fhirContext.newXmlParser()

    private fun open(path: String) = FhirOperatorTest.javaClass.getResourceAsStream(path)!!

    private fun parseJson(path: String): Bundle = jsonParser.parseResource(open(path)) as Bundle

    private fun readResourceAsString(path: String) = open(path).readBytes().decodeToString()

    private fun <T> parseResource(path: String) =
      jsonParser.parseResource(readResourceAsString(path)) as T
  }

  @Before
  fun setUp() = runBlocking {
    fhirEngine = FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
    fhirOperator = FhirOperator(fhirContext, fhirEngine)
  }

  @Test
  @Ignore("Refactor the API to accommodate local end points")
  fun generateCarePlan() = runBlocking {
    loadBundle(libraryBundle)
    fhirEngine.run {
      loadBundle(parseJson("/RuleFilters-1.0.0-bundle.json"))
      loadBundle(parseJson("/tests-Reportable-bundle.json"))
      loadBundle(parseJson("/tests-NotReportable-bundle.json"))

      loadFile("/first-contact/01-registration/patient-charity-otala-1.json")
      loadFile("/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml")
      loadFile("/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml")
      loadFile("/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml")
    }

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
  fun evaluatePopulationMeasure() = runBlocking {
    loadBundle(libraryBundle)
    fhirEngine.run {
      loadFile("/first-contact/01-registration/patient-charity-otala-1.json")
      loadFile("/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml")
      loadFile("/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml")
      loadFile("/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml")
    }

    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
        start = "2019-01-01",
        end = "2021-12-31",
        reportType = MeasureEvalType.POPULATION.toCode(),
        subject = null,
        practitioner = null,
        lastReceivedOn = null
      )

    measureReport.date = null

    JSONAssert.assertEquals(
      readResourceAsString("/first-contact/04-results/population-report.json"),
      jsonParser.setPrettyPrint(true).encodeResourceToString(measureReport),
      true
    )
  }

  @Test
  fun evaluateGroupPopulationMeasure() = runBlocking {
    val resourceBundle =
      Bundle().apply {
        addEntry().apply {
          resource = toFhirLibrary(open("/group-measure/PatientGroups-1.0.0.cql"))
        }
        addEntry().apply {
          resource = parseResource<Measure>("/group-measure/PatientGroupsMeasure.json")
        }
      }

    loadBundle(resourceBundle)
    loadBundle(parseJson("/group-measure/Data-Patients-bundle.json"))
    loadBundle(parseJson("/group-measure/Data-Groups-bundle.json"))

    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "Measure/PatientGroupsMeasure",
        start = "2019-01-01",
        end = "2022-12-31",
        reportType = MeasureEvalType.POPULATION.toCode(),
        subject = null,
        practitioner = null,
        lastReceivedOn = null
      )

    measureReport.date = null

    JSONAssert.assertEquals(
      readResourceAsString("/group-measure/Results-Measure-report.json"),
      jsonParser.setPrettyPrint(true).encodeResourceToString(measureReport),
      true
    )
  }

  @Test
  fun evaluateIndividualSubjectMeasure() = runBlocking {
    loadBundle(libraryBundle)
    fhirEngine.run {
      loadFile("/first-contact/01-registration/patient-charity-otala-1.json")
      loadFile("/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml")
      loadFile("/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml")
      loadFile("/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml")
    }
    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
        start = "2020-01-01",
        end = "2020-01-31",
        reportType = "subject",
        subject = "charity-otala-1",
        practitioner = "jane",
        lastReceivedOn = null
      )
    val measureReportJSON = jsonParser.encodeResourceToString(measureReport)
    assertThat(MeasureReport.MeasureReportStatus.COMPLETE).isEqualTo(measureReport.status)
    assertThat(MeasureReport.MeasureReportType.INDIVIDUAL).isEqualTo(measureReport.type)
    assertThat(DateType(Date()).toLocalDate).isEqualTo(DateType(measureReport.date).toLocalDate)
    assertThat("2020-01-01").isEqualTo(DateType(measureReport.period.start).toLocalDate.toString())
    assertThat("2020-01-31").isEqualTo(DateType(measureReport.period.end).toLocalDate.toString())
    assertThat("Patient/charity-otala-1").isEqualTo(measureReport.subject.reference)
    assertThat(measureReportJSON).isNotNull()
    assertThat(measureReport).isNotNull()

    assertThat(measureReport.extension[0].value.toString())
      .isEqualTo(
        "Percentage of pregnant women with first ANC contact in the first trimester (before 12 weeks of gestation)"
      )
    assertThat(measureReport.extension[0].url)
      .isEqualTo(
        "http://hl7.org/fhir/5.0/StructureDefinition/extension-MeasureReport.population.description"
      )

    assertThat(measureReport.measure.toString())
      .isEqualTo("http://fhir.org/guides/who/anc-cds/Measure/ANCIND01")
    assertThat(measureReport.improvementNotation.coding[0].system)
      .isEqualTo("http://terminology.hl7.org/CodeSystem/measure-improvement-notation")
    assertThat(measureReport.improvementNotation.coding[0].code.toString()).isEqualTo("increase")

    val evaluatedResource = measureReport.evaluatedResource

    assertThat(evaluatedResource[0].reference).isEqualTo("Encounter/anc-encounter-charity-otala-1")
    assertThat(evaluatedResource[0].extension[0].url)
      .isEqualTo(
        "http://hl7.org/fhir/us/davinci-deqm/StructureDefinition/extension-populationReference"
      )
    assertThat(evaluatedResource[0].extension[0].value.toString()).isEqualTo("denominator")

    assertThat(evaluatedResource[1].reference)
      .isEqualTo("EpisodeOfCare/charity-otala-1-pregnancy-episode")
    assertThat(evaluatedResource[1].extension[0].url)
      .isEqualTo(
        "http://hl7.org/fhir/us/davinci-deqm/StructureDefinition/extension-populationReference"
      )
    assertThat(evaluatedResource[1].extension[0].value.toString()).isEqualTo("initial-population")

    assertThat(evaluatedResource[2].reference).isEqualTo("Patient/charity-otala-1")
    assertThat(evaluatedResource[2].extension[0].url)
      .isEqualTo(
        "http://hl7.org/fhir/us/davinci-deqm/StructureDefinition/extension-populationReference"
      )
    assertThat(evaluatedResource[2].extension[0].value.toString()).isEqualTo("initial-population")
    assertThat(evaluatedResource[2].extension[1].url)
      .isEqualTo(
        "http://hl7.org/fhir/us/davinci-deqm/StructureDefinition/extension-populationReference"
      )
    assertThat(evaluatedResource[2].extension[1].value.toString()).isEqualTo("denominator")

    val population = measureReport.group[0].population

    assertThat(population[0].id).isEqualTo("initial-population")
    assertThat(population[0].code.coding[0].code.toString()).isEqualTo("initial-population")
    assertThat(population[0].code.coding[0].system)
      .isEqualTo("http://terminology.hl7.org/CodeSystem/measure-population")

    assertThat(population[1].id).isEqualTo("denominator")
    assertThat(population[1].code.coding[0].code.toString()).isEqualTo("denominator")
    assertThat(population[1].code.coding[0].system)
      .isEqualTo("http://terminology.hl7.org/CodeSystem/measure-population")

    assertThat(population[2].id).isEqualTo("numerator")
    assertThat(population[2].code.coding[0].code.toString()).isEqualTo("numerator")
    assertThat(population[2].code.coding[0].system)
      .isEqualTo("http://terminology.hl7.org/CodeSystem/measure-population")

    assertThat(measureReport.type.display).isEqualTo("Individual")
  }

  private suspend fun loadFile(path: String) {
    if (path.endsWith(suffix = ".xml")) {
      val resource = xmlParser.parseResource(javaClass.getResourceAsStream(path)) as Resource
      fhirEngine.create(resource)
    } else if (path.endsWith(".json")) {
      val resource = jsonParser.parseResource(javaClass.getResourceAsStream(path)) as Resource
      fhirEngine.create(resource)
    }
  }

  private suspend fun loadBundle(bundle: Bundle) {
    for (entry in bundle.entry) {
      when (entry.resource.resourceType) {
        ResourceType.Library -> fhirOperator.loadLib(entry.resource as Library)
        ResourceType.Bundle -> Unit
        else -> fhirEngine.create(entry.resource)
      }
    }
  }

  private fun toFhirLibrary(cql: InputStream): Library {
    return CqlBuilderUtils.compileAndBuild(cql)
  }

  private fun String.readStringToBase64Encoded(): String {
    return Base64.getEncoder().encodeToString(this.toByteArray())
  }
}
