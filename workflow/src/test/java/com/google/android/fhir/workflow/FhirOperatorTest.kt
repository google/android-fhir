/*
 * Copyright 2021 Google LLC
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
import com.google.common.truth.Truth.assertThat
import java.util.Base64
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.CqlTranslatorOptions
import org.cqframework.cql.cql2elm.FhirLibrarySourceProvider
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
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
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirOperatorTest {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private lateinit var fhirEngine: FhirEngine
  private val fhirContext = FhirContext.forR4()
  private val jsonParser = fhirContext.newJsonParser()
  private val xmlParser = fhirContext.newXmlParser()
  private lateinit var fhirOperator: FhirOperator

  companion object {
    private val libraryBundle: Bundle by lazy { parseJson("/ANCIND01-bundle.json") }
    private val fhirContext = FhirContext.forR4()
    private val jsonParser = fhirContext.newJsonParser()

    private fun parseJson(path: String): Bundle =
      jsonParser.parseResource(jsonParser.javaClass.getResourceAsStream(path)) as Bundle

    private fun readResourceAsString(path: String) =
      FhirOperatorTest::class.java.getResourceAsStream(path)!!.readBytes().decodeToString()

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
  @Ignore("fix the test or remove it if it's obsolete")
  fun `evaluateMeasure for subject with observation has denominator and numerator`() = runBlocking {
    loadBundle(libraryBundle)
    fhirEngine.run {
      loadFile("/validated-resources/anc-patient-example.json")
      loadFile("/validated-resources/Antenatal-care-case.json")
      loadFile("/validated-resources/First-antenatal-care-contact.json")
      loadFile("/validated-resources/observation-anc-b6-de17-example.json")
      loadFile("/validated-resources2/anc-patient-example-1.json")
      loadFile("/validated-resources2/First-antenatal-care-contact-1.json")
      loadFile("/validated-resources2/observation-anc-b6-de17-example-1.json")
      loadFile("/validated-resources2/Practitioner.xml")
      loadFile("/validated-resources2/PractitionerRole.xml")
    }

    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
        start = "2020-01-01",
        end = "2020-01-31",
        reportType = "population",
        subject = null,
        practitioner = "jane",
        lastReceivedOn = null
      )
    assertThat(measureReport.evaluatedResource[1].reference)
      .isEqualTo("Observation/anc-b6-de17-example-1")
    assertThat(measureReport.evaluatedResource[0].reference)
      .isEqualTo("EpisodeOfCare/antenatal-care-case-example-1")
    assertThat(measureReport.evaluatedResource[2].reference)
      .isEqualTo("Patient/anc-patient-example-1")
    assertThat(measureReport.evaluatedResource[3].reference)
      .isEqualTo("Encounter/First-antenatal-care-contact-example-1")
    val population = measureReport.group.first().population
    assertThat(population[1].id).isEqualTo("denominator")
    assertThat(population[2].id).isEqualTo("numerator")
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
        reportType = "population",
        subject = null,
        practitioner = "jane",
        lastReceivedOn = null
      )
    val measureReportJSON =
      FhirContext.forR4().newJsonParser().encodeResourceToString(measureReport)

    assertThat(MeasureReport.MeasureReportStatus.COMPLETE).isEqualTo(measureReport.status)
    assertThat(MeasureReport.MeasureReportType.SUMMARY).isEqualTo(measureReport.type)
    assertThat(DateType(measureReport.period.start).toLocalDate.toString()).isEqualTo("2019-01-01")
    assertThat(DateType(measureReport.period.end).toLocalDate.toString()).isEqualTo("2021-12-31")
    assertThat(DateType(Date()).toLocalDate).isEqualTo(DateType(measureReport.date).toLocalDate)

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

    assertThat(measureReport.type.display).isEqualTo("Summary")
  }

  @Test
  fun evaluateGroupPopulationMeasure() = runBlocking {
    val resourceBundle = Bundle()

    val resourceDir = "/group-measure"
    val cqlElm = toJsonElm(readResourceAsString("$resourceDir/cql.txt"))

    resourceBundle.addEntry().apply {
      this.resource =
        jsonParser.parseResource(
          readResourceAsString("$resourceDir/library.json")
            .replace("#library-elm.json", cqlElm.readStringToBase64Encoded())
        ) as
          Library
    }

    resourceBundle.addEntry().apply {
      this.resource = parseResource<Library>("/lib-fhir-helper.json")
    }
    resourceBundle.addEntry().apply {
      this.resource = parseResource<Measure>("$resourceDir/measure.json")
    }

    loadBundle(resourceBundle)
    loadBundle(parseJson("$resourceDir/groups-bundle.json"))
    loadBundle(parseJson("$resourceDir/patients-bundle.json"))

    val measureReport =
      fhirOperator.evaluateMeasure(
        measureUrl = "Measure/group-measure",
        start = "2019-01-01",
        end = "2022-12-31",
        reportType = "population",
        subject = null,
        practitioner = null,
        lastReceivedOn = null
      )

    with(measureReport.group[0]) {
      assertThat(id).isEqualTo("groups")
      assertThat(population[0].id).isEqualTo("initial-population")
      assertThat(population[0].count).isEqualTo(17)
      assertThat(population[1].id).isEqualTo("denominator")
      assertThat(population[1].count).isEqualTo(17)
      assertThat(population[2].id).isEqualTo("numerator")
      assertThat(population[2].count).isEqualTo(17)
      assertThat(measureScore.value.toPlainString()).isEqualTo("1.0")
      assertThat(stratifierFirstRep.id).isNull()
      assertThat(stratifierFirstRep.stratum).isEmpty()
    }
    with(measureReport.group[1]) {
      assertThat(id).isEqualTo("males")
      assertThat(population[0].id).isEqualTo("initial-population")
      assertThat(population[0].count).isEqualTo(17)
      assertThat(population[1].id).isEqualTo("denominator")
      assertThat(population[1].count).isEqualTo(17)
      assertThat(population[2].id).isEqualTo("numerator")
      assertThat(population[2].count).isEqualTo(7)
      assertThat(measureScore.value.toPlainString()).isEqualTo("0.4117647058823529")
      assertThat(stratifierFirstRep.id).isEqualTo("by-age")
      with(stratifierFirstRep.stratum[0]) {
        assertThat(value.text).isEqualTo("P0Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(1)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(1)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(1)
      }
      with(stratifierFirstRep.stratum[1]) {
        assertThat(value.text).isEqualTo("P50Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(1)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(1)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(0)
      }
      with(stratifierFirstRep.stratum[2]) {
        assertThat(value.text).isEqualTo("P15-49Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(2)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(2)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(1)
      }
      with(stratifierFirstRep.stratum[3]) {
        assertThat(value.text).isEqualTo("P6-14Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(2)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(2)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(0)
      }
      with(stratifierFirstRep.stratum[4]) {
        assertThat(value.text).isEqualTo("P1-5Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(11)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(11)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(5)
      }
    }
    with(measureReport.group[2]) {
      assertThat(id).isEqualTo("females")
      assertThat(population[0].id).isEqualTo("initial-population")
      assertThat(population[0].count).isEqualTo(17)
      assertThat(population[1].id).isEqualTo("denominator")
      assertThat(population[1].count).isEqualTo(17)
      assertThat(population[2].id).isEqualTo("numerator")
      assertThat(population[2].count).isEqualTo(10)
      assertThat(measureScore.value.toPlainString()).isEqualTo("0.5882352941176471")
      assertThat(stratifierFirstRep.id).isEqualTo("by-age")
      with(stratifierFirstRep.stratum[0]) {
        assertThat(value.text).isEqualTo("P0Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(1)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(1)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(0)
      }
      with(stratifierFirstRep.stratum[1]) {
        assertThat(value.text).isEqualTo("P50Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(1)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(1)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(1)
      }
      with(stratifierFirstRep.stratum[2]) {
        assertThat(value.text).isEqualTo("P15-49Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(2)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(2)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(1)
      }
      with(stratifierFirstRep.stratum[3]) {
        assertThat(value.text).isEqualTo("P6-14Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(2)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(2)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(2)
      }
      with(stratifierFirstRep.stratum[4]) {
        assertThat(value.text).isEqualTo("P1-5Y")
        assertThat(population[0].id).isEqualTo("initial-population")
        assertThat(population[0].count).isEqualTo(11)
        assertThat(population[1].id).isEqualTo("denominator")
        assertThat(population[1].count).isEqualTo(11)
        assertThat(population[2].id).isEqualTo("numerator")
        assertThat(population[2].count).isEqualTo(6)
      }
    }
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

  private fun toJsonElm(cql: String): String {
    val libraryManager = LibraryManager(ModelManager())
    libraryManager.librarySourceLoader.registerProvider(FhirLibrarySourceProvider())

    val translator: CqlTranslator =
      CqlTranslator.fromText(
        cql,
        libraryManager.modelManager,
        libraryManager,
        *CqlTranslatorOptions.defaultOptions().options.toTypedArray()
      )

    return translator.toJxson()
  }

  private fun String.readStringToBase64Encoded(): String {
    return Base64.getEncoder().encodeToString(this.toByteArray())
  }
}
