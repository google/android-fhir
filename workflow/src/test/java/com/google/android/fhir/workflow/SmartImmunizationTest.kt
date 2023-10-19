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
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.get
import com.google.android.fhir.knowledge.FhirNpmPackage
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.getQuery
import com.google.android.fhir.search.search
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.util.TimeZone
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.skyscreamer.jsonassert.JSONAssert

@RunWith(RobolectricTestRunner::class)
class SmartImmunizationTest {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private val fhirContext = FhirContext.forR4()

  private lateinit var fhirEngine: FhirEngine
  private lateinit var fhirOperator: FhirOperator

  private val loader = TestBundleLoader(fhirContext)

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val knowledgeManager = KnowledgeManager.create(context = context, inMemory = true)

  @Before
  fun setUp() = runBlockingOnWorkerThread {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

    fhirEngine = FhirEngineProvider.getInstance(context)
    fhirOperator = FhirOperator(fhirContext, fhirEngine, knowledgeManager)

    // Installing SmartImmunizations IG into the IGManager
    val rootDirectory = File(javaClass.getResource("/smart-imm/ig/")!!.file)

    knowledgeManager.install(
      FhirNpmPackage(
        "who.fhir.immunization",
        "1.0.0",
        "https://github.com/WorldHealthOrganization/smart-immunizations",
      ),
      rootDirectory,
    )
  }

  @Test
  fun testIMMZD2DTMeasles() = runBlockingOnWorkerThread {
    val planDef =
      knowledgeManager
        .loadResources(
          resourceType = "PlanDefinition",
          url = "http://fhir.org/guides/who/smart-immunization/PlanDefinition/IMMZD2DTMeasles",
        )
        .firstOrNull()

    assertThat(planDef?.idElement?.idPart).isEqualTo("26")

    loader.loadFile(
      "/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/Patient/Patient-IMMZ-Patient-NoVaxeninfant-f.json",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/Observation/Observation-birthweightnormal-NoVaxeninfant-f.json",
      ::importToFhirEngine,
    )

    assertThat(fhirEngine.get<Patient>("IMMZ-Patient-NoVaxeninfant-f").id).isNotNull()
    assertThat(fhirEngine.get<Observation>("birthweightnormal-NoVaxeninfant-f").id).isNotNull()

    val carePlan =
      fhirOperator.generateCarePlan(
        planDefinition =
          CanonicalType(
            "http://fhir.org/guides/who/smart-immunization/PlanDefinition/IMMZD2DTMeasles",
          ),
        subject = "Patient/IMMZ-Patient-NoVaxeninfant-f",
      )

    val parser = FhirContext.forR4Cached().newJsonParser()

    assertThat(carePlan).isNotNull()

    JSONAssert.assertEquals(
      loader.readResourceAsString(
        "/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/CarePlan/CarePlan.json",
      ),
      parser.encodeResourceToString(carePlan),
      true,
    )
  }

  private suspend fun importToFhirEngine(resource: Resource) {
    fhirEngine.create(resource)
  }
}
