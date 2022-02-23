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
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Endpoint
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.cql.evaluator.builder.Constants
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirOperatorTest {
  private val fhirEngine =
    FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
  private val fhirContext = FhirContext.forR4()
  private val jsonParser = fhirContext.newJsonParser()
  private val xmlParser = fhirContext.newXmlParser()
  private val fhirOperator = FhirOperator(fhirContext, fhirEngine)

  @Before
  fun setUp() = runBlocking {
    val bundle =
      jsonParser.parseResource(javaClass.getResourceAsStream("/ANCIND01-bundle.json")) as Bundle
    for (entry in bundle.entry) {
      if (entry.resource.resourceType == ResourceType.Library) {
        fhirOperator.loadLib(entry.resource as Library)
      } else {
        fhirEngine.create(entry.resource)
      }
    }

    fhirEngine.run {
      loadFile("/first-contact/01-registration/patient-charity-otala-1.json")
      loadFile("/first-contact/02-enrollment/careplan-charity-otala-1-pregnancy-plan.xml")
      loadFile("/first-contact/02-enrollment/episodeofcare-charity-otala-1-pregnancy-episode.xml")
      loadFile("/first-contact/03-contact/encounter-anc-encounter-charity-otala-1.xml")
    }
  }

  @Test
  fun evaluateIndividualSubjectMeasure() = runBlocking {
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
    val measureReportJSON =
      FhirContext.forR4().newJsonParser().encodeResourceToString(measureReport)
    assertThat(measureReportJSON).isNotNull()
    assertThat(measureReport).isNotNull()
    assertThat(measureReport.type.display).isEqualTo("Individual")
  }

  @Test
  @Ignore("Fix OutOfMemoryException")
  fun evaluatePopulationMeasure() = runBlocking {
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
    assertThat(measureReportJSON).isNotNull()
    assertThat(measureReport).isNotNull()
    assertThat(measureReport.type.display).isEqualTo("Summary")
  }

  @Test
  @Ignore("Refactor the API to accommodate local end points")
  fun generateCarePlan() = runBlocking {
    val endpoint =
      Endpoint()
        .setAddress("RuleFilters-1.0.0-bundle.json")
        .setConnectionType(Coding().setCode(Constants.HL7_FHIR_FILES))
    val dataEndpoint: Endpoint =
      Endpoint()
        .setAddress("tests-Reportable-bundle.json")
        .setConnectionType(Coding().setCode(Constants.HL7_FHIR_FILES))
    fhirOperator.generateCarePlan(
      planDefinitionId = "plandefinition-RuleFilters-1.0.0",
      patientId = "Reportable",
      encounterId = "reportable-encounter",
      dataEndpoint = dataEndpoint,
      contentEndpoint = endpoint,
      terminologyEndpoint = endpoint
    )
  }


  private suspend fun FhirEngine.loadFile(path: String) {
    if (path.endsWith(suffix = ".xml")) {
      val resource = xmlParser.parseResource(javaClass.getResourceAsStream(path)) as Resource
      create(resource)
    } else if (path.endsWith(".json")) {
      val resource = jsonParser.parseResource(javaClass.getResourceAsStream(path)) as Resource
      create(resource)
    }
  }
}
