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
import java.io.FileInputStream
import java.util.TimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.ActivityDefinition
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ValueSet
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SmartImmunizationTest {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private val fhirContext = FhirContext.forR4()
  private val jsonParser = fhirContext.newJsonParser()

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

    // Installing ANC CDS to the IGManager
    val rootDirectory = File(javaClass.getResource("/smart-imm/ig/")!!.file)

    val bundle = Bundle()
    bundle.type = Bundle.BundleType.TRANSACTION

    for (file in rootDirectory.listFiles()!!) {
      val filename = file.name
      if (filename.contains(".txt") || (filename[0] >= 'a' && filename[0] < 'z') || filename.contains("2.json"))
        continue
      val res = withContext(Dispatchers.IO) {
        jsonParser.parseResource(FileInputStream(file))
      }
      if (res is Resource) {
        // if (res is PlanDefinition || res is Library || res is ActivityDefinition) {
        if (res is ValueSet) {
          val bundleEntryComponent = Bundle.BundleEntryComponent().apply {
            resource = res
            request.method = Bundle.HTTPVerb.PUT
            request.url = "${res.resourceType}/${IdType(res.id).idPart}"
          }
          bundle.addEntry(bundleEntryComponent)
        }
      }
    }
    println(jsonParser.encodeResourceToString(bundle))

    println(rootDirectory)

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
          resourceType = "Library",
          id = "IMMZD2DTMeasles",
        )
        .firstOrNull()

    loader.loadFile(
      "/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/Patient/Patient-IMMZ-Patient-NoVaxeninfant-f.json",
      ::importToFhirEngine,
    )
    loader.loadFile(
      "/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/Observation/Observation-birthweightnormal-NoVaxeninfant-f.json",
      ::importToFhirEngine,
    )

    assertThat(planDef).isNotNull()

    val carePlan =
      fhirOperator.generateCarePlan(
        planDefinitionId = "IMMZD2DTMeasles",
        patientId = "IMMZ-Patient-NoVaxeninfant-f",
      )

    // println(FhirContext.forR4Cached().newJsonParser().encodeResourceToString(carePlan))

    assertThat(carePlan).isNotNull()
  }

  private suspend fun importToFhirEngine(resource: Resource) {
    fhirEngine.create(resource)
  }
}