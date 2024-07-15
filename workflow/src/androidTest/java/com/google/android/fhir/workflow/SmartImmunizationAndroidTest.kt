/*
 * Copyright 2022-2024 Google LLC
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
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.get
import com.google.android.fhir.knowledge.FhirNpmPackage
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.io.InputStream
import java.lang.RuntimeException
import java.util.TimeZone
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.skyscreamer.jsonassert.JSONAssert

@RunWith(AndroidJUnit4::class)
class SmartImmunizationAndroidTest {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private lateinit var fhirEngine: FhirEngine
  private lateinit var fhirOperator: FhirOperator
  private lateinit var knowledgeManager: KnowledgeManager

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val jsonParser = fhirContext.newJsonParser()

  private fun open(asset: String): InputStream {
    return javaClass.getResourceAsStream(asset)!!
  }

  private fun load(asset: String): IBaseResource {
    return jsonParser.parseResource(open(asset))
  }

  private fun loadStr(asset: String): String {
    return open(asset).bufferedReader().use { it.readText() }
  }

  private fun copyResourceIntoApp(outputDir: File, asset: String): File {
    val resourceJson = open(asset).bufferedReader().use { it.readText() }
    val resource = load(asset) as Resource

    return File(outputDir, "${resource.resourceType.name}-${resource.idPart}.json").apply {
      delete()
      createNewFile()
      writeText(resourceJson)
    }
  }

  fun moveAllIGResourcesIntoFilesDir(igName: String) {
    val inputBaseIgDir = "/$igName/ig"

    // cleans up
    val outputDir =
      File(context.filesDir, igName).apply {
        if (!deleteRecursively()) {
          throw RuntimeException("Failed to clean up directory")
        }
        mkdirs()
      }

    javaClass
      .getResourceAsStream("$inputBaseIgDir/contents.txt")
      ?.bufferedReader()
      ?.use { bufferReader -> bufferReader.readText() }
      ?.split("\n")
      ?.forEach { fileName ->
        runCatching { copyResourceIntoApp(outputDir, "$inputBaseIgDir/$fileName") }
          .onFailure { println("Ignoring $inputBaseIgDir/$fileName. Not a valid Fhir Resource") }
      }
  }

  @Before
  fun setUp() = runBlocking {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

    knowledgeManager = KnowledgeManager.create(context = context, inMemory = true)

    fhirEngine = FhirEngineProvider.getInstance(context)
    fhirOperator =
      FhirOperator.Builder(context)
        .fhirContext(fhirContext)
        .fhirEngine(fhirEngine)
        .knowledgeManager(knowledgeManager)
        .build()

    moveAllIGResourcesIntoFilesDir("smart-imm")

    knowledgeManager.install(
      FhirNpmPackage(
        "who.fhir.immunization",
        "1.0.0",
        "https://github.com/WorldHealthOrganization/smart-immunizations",
      ),
      File(context.filesDir, "smart-imm"),
    )
  }

  @Test
  fun testIMMZD2DTMeasles() = runBlocking {
    val planDef =
      knowledgeManager
        .loadResources(
          resourceType = "PlanDefinition",
          url = "http://fhir.org/guides/who/smart-immunization/PlanDefinition/IMMZD2DTMeasles",
        )
        .single()

    assertThat(planDef.idElement.idPart).isEqualTo("26")

    val patient =
      load(
        "/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/Patient/Patient-IMMZ-Patient-NoVaxeninfant-f.json",
      )
        as Patient
    val observation =
      load(
        "/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/Observation/Observation-birthweightnormal-NoVaxeninfant-f.json",
      )
        as Observation

    fhirEngine.create(patient)
    fhirEngine.create(observation)

    assertThat(patient.id).isEqualTo("Patient/IMMZ-Patient-NoVaxeninfant-f")
    assertThat(observation.id).isEqualTo("Observation/birthweightnormal-NoVaxeninfant-f")

    val carePlan =
      fhirOperator.generateCarePlan(
        planDefinitionCanonical =
          CanonicalType(
            "http://fhir.org/guides/who/smart-immunization/PlanDefinition/IMMZD2DTMeasles",
          ),
        subject = "Patient/IMMZ-Patient-NoVaxeninfant-f",
      )

    val parser = FhirContext.forR4Cached().newJsonParser()

    assertThat(carePlan).isNotNull()

    JSONAssert.assertEquals(
      loadStr("/smart-imm/tests/IMMZ-Patient-NoVaxeninfant-f/CarePlan/CarePlan.json"),
      parser.encodeResourceToString(carePlan),
      true,
    )
  }
}
