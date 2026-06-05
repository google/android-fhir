/*
 * Copyright 2026 Google LLC
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
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.io.InputStream
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Regression test for value-set-filtered CQL retrieves in the Workflow module.
 *
 * A retrieve filtered by a value set — `exists [Condition: "Test Condition ValueSet"]` — must
 * return the Condition when the patient has one coded in that value set. The CQL engine pushes the
 * value-set filter to the data repository as an unexpanded `code:in <ValueSet canonical>` token;
 * [com.google.android.fhir.workflow.repositories.FhirEngineRepository] must resolve and expand the
 * value set to its member codes before searching, otherwise the retrieve matches nothing and the
 * expression wrongly evaluates to `false`.
 */
@RunWith(AndroidJUnit4::class)
class ConditionValueSetRetrieveTest {

  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private lateinit var fhirEngine: FhirEngine
  private lateinit var fhirOperator: FhirOperator

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val knowledgeManager = KnowledgeManager.create(context = context, inMemory = true)
  private val jsonParser = fhirContext.newJsonParser()

  private fun open(asset: String): InputStream = javaClass.getResourceAsStream(asset)!!

  private fun load(asset: String): IBaseResource = jsonParser.parseResource(open(asset))

  /** Write a knowledge resource (Library / ValueSet) into the app dir and index it. */
  private suspend fun index(asset: String) {
    val resource = load(asset) as Resource
    val file =
      File(context.filesDir, "${resource.fhirType()}-${resource.idPart}.json").apply {
        writeText(jsonParser.encodeResourceToString(resource))
      }
    knowledgeManager.index(file)
  }

  @Before
  fun setUp() = runBlocking {
    fhirEngine = FhirEngineProvider.getInstance(context)
    fhirOperator =
      FhirOperator.Builder(context)
        .fhirContext(fhirContext)
        .fhirEngine(fhirEngine)
        .knowledgeManager(knowledgeManager)
        .build()
  }

  @Test
  fun valueSetFilteredConditionRetrieveResolvesMemberCodes() = runBlocking {
    // A patient with a Condition coded with a member of the value set under test.
    fhirEngine.create(Patient().apply { id = "test-patient" })
    fhirEngine.create(
      Condition().apply {
        id = "test-condition"
        subject = Reference("Patient/test-patient")
        code =
          CodeableConcept()
            .addCoding(Coding("http://example.org/fhir/CodeSystem/test", "has-condition", null))
      },
    )

    index("/condition-valueset/ValueSet-test-condition-vs.json")
    index("/condition-valueset/Library-ConditionValueSetRetrieve.json")

    val results =
      fhirOperator.evaluateLibrary(
        libraryUrl = "http://example.org/fhir/Library/ConditionValueSetRetrieve|1.0.0",
        patientId = "test-patient",
        expressions = setOf("Has Condition In ValueSet"),
      ) as Parameters

    // Before the fix this asserted false: the value-set token reached the engine search
    // unexpanded and matched nothing. After expanding the value set to its member codes, the
    // retrieve finds the Condition.
    assertThat(results.getParameterBool("Has Condition In ValueSet")).isTrue()
  }
}
