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
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import java.io.InputStream
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Parameters
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FhirOperatorLibraryEvaluateTest {

  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private lateinit var fhirEngine: FhirEngine
  private lateinit var fhirOperator: FhirOperator

  private val fhirContext = FhirContext.forR4()
  private val jsonParser = fhirContext.newJsonParser()

  private fun open(asset: String): InputStream? {
    return javaClass.getResourceAsStream(asset)
  }

  private fun load(asset: String): Bundle {
    return jsonParser.parseResource(open(asset)) as Bundle
  }

  @Before
  fun setUp() = runBlocking {
    fhirEngine = FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
    fhirOperator = FhirOperator(fhirContext, fhirEngine)
  }

  /**
   * Evaluates a compiled CQL that was exported to Jxson and included inside a FHIRLibrary Json The
   * CQL file is encoded in Base64 and placed inside the JSON Library. The expression
   * CompletedImmunization and simply checks if a vaccination protocol has been finished as below.
   * This test requires the CQLEvaluator to
   * 1. load the patient using a `FhirEngineRetrieveProvider`,
   * 2. load dependent Immunization records from that patient,
   * 3. load the Library using a `FhirEngineLibraryContentProvider`
   * 4. evaluate if the immunization record presents a Protocol where the number of doses taken
   * matches the number of required doses or if the number of required doses is null.
   *
   * ```
   * library ImmunityCheck version '1.0.0'
   *
   * using FHIR version '4.0.0'
   * include "FHIRHelpers" version '4.0.0' called FHIRHelpers
   * context Immunization
   *
   * define "CompletedImmunization":
   *   exists(GetFinalDose) or exists(GetSingleDose)
   *
   * define "GetFinalDose":
   *   [Immunization] I
   *     where exists(I.protocolApplied)
   *     and I.protocolApplied.doseNumber.value = I.protocolApplied.seriesDoses.value
   *
   * define "GetSingleDose":
   *   [Immunization] I
   *     where exists(I.protocolApplied)
   *     and exists(I.protocolApplied.doseNumber.value)
   *     and not exists(I.protocolApplied.seriesDoses.value)
   * ```
   */
  @Test
  fun evaluateImmunityCheck() = runBlocking {
    // Load patient
    val patientImmunizationHistory = load("/immunity-check/ImmunizationHistory.json")
    for (entry in patientImmunizationHistory.entry) {
      fhirEngine.create(entry.resource)
    }

    // Load Library that checks if Patient has taken a vaccine
    fhirOperator.loadLibs(load("/immunity-check/ImmunityCheck.json"))

    // Evaluates a specific Patient
    val results =
      fhirOperator.evaluateLibrary(
        "http://localhost/Library/ImmunityCheck|1.0.0",
        "d4d35004-24f8-40e4-8084-1ad75924514f",
        setOf("CompletedImmunization")
      ) as
        Parameters

    assertThat(results.getParameterBool("CompletedImmunization")).isTrue()
  }
}
