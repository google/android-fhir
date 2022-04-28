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
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngineProvider
import com.google.common.truth.Truth.assertThat
import java.io.InputStream
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FhirOperatorLibraryEvaluateTest {

  private val fhirEngine =
    FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val fhirOperator = FhirOperator(fhirContext, fhirEngine)

  private val json = fhirContext.newJsonParser()

  fun open(assetName: String): InputStream? {
    return javaClass.getResourceAsStream(assetName)
  }

  @Before
  fun setUp() = runBlocking {
    fhirEngine.delete(ResourceType.Composition, "fb83f2c1-908f-4085-97e5-eecf8234f43e")
    fhirEngine.delete(ResourceType.Patient, "d4d35004-24f8-40e4-8084-1ad75924514f")
    fhirEngine.delete(ResourceType.Immunization, "8aa553e8-8847-482a-8bcf-2eca4e9598ef")
    fhirEngine.delete(ResourceType.Organization, "6a91ae12-4627-4b5c-9a3c-1c3d057ac6bc")

    val bundle = json.parseResource(open("/covid-check/COVIDImmunizationHistory.json")) as Bundle
    for (entry in bundle.entry) {
      fhirEngine.create(entry.resource)
    }
    fhirOperator.loadLibs(
      json.parseResource(open("/covid-check/COVIDCheck-FHIRLibraryBundle.json")) as Bundle
    )
  }

  @Test
  fun evaluateCOVIDCheck() = runBlocking {
    assertThat(fhirEngine.get(ResourceType.Composition, "fb83f2c1-908f-4085-97e5-eecf8234f43e"))
      .isNotNull()
    assertThat(fhirEngine.get(ResourceType.Patient, "d4d35004-24f8-40e4-8084-1ad75924514f"))
      .isNotNull()
    assertThat(fhirEngine.get(ResourceType.Immunization, "8aa553e8-8847-482a-8bcf-2eca4e9598ef"))
      .isNotNull()
    assertThat(fhirEngine.get(ResourceType.Organization, "6a91ae12-4627-4b5c-9a3c-1c3d057ac6bc"))
      .isNotNull()

    val results =
      fhirOperator.evaluateLibrary(
        "http://localhost/Library/COVIDCheck|1.0.0",
        "d4d35004-24f8-40e4-8084-1ad75924514f",
        setOf(
          "CompletedImmunization",
          "GetFinalDose",
          "GetSingleDose",
          "ModernaProtocol",
          "PfizerProtocol"
        )
      ) as
        Parameters

    assertThat(results.getParameterBool("CompletedImmunization")).isTrue()
    assertThat(results.getParameterBool("ModernaProtocol")).isFalse()
    assertThat(results.getParameterBool("PfizerProtocol")).isFalse()
  }
}
