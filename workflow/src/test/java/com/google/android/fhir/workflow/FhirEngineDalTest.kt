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
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.search.search
import com.google.android.fhir.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirEngineDalTest {

  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()
  private lateinit var fhirEngine: FhirEngine

  private lateinit var fhirEngineDal: FhirEngineDal

  @Before
  fun setupTest() {
    fhirEngine = FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
    fhirEngineDal = FhirEngineDal(fhirEngine)
    runBlocking { fhirEngine.create(testPatient) }
  }

  @Test
  fun testDalRead() = runBlocking {
    val result = fhirEngineDal.read(IdType("Patient/${testPatient.id}"))
    Truth.assertThat(result).isInstanceOf(Patient::class.java)
    Truth.assertThat((result as Patient).nameFirstRep.givenAsSingleString)
      .isEqualTo(testPatient.nameFirstRep.givenAsSingleString)
  }

  @Test
  fun testDalCreate() = runBlocking {
    val patient =
      Patient().apply {
        id = "Patient/2"
        addName(HumanName().apply { addGiven("John") })
      }
    fhirEngineDal.create(patient)
    val result = fhirEngine.get(ResourceType.Patient, "2") as Patient
    Truth.assertThat(result.nameFirstRep.givenAsSingleString)
      .isEqualTo(patient.nameFirstRep.givenAsSingleString)
  }

  @Test
  fun testDalUpdate() = runBlocking {
    testPatient.name = mutableListOf(HumanName().apply { addGiven("Eve") })
    fhirEngineDal.update(testPatient)
    val result = fhirEngine.search<Patient> {}.single()
    Truth.assertThat(result.nameFirstRep.givenAsSingleString).isEqualTo("Eve")
  }

  @Test
  fun testDalDelete() = runBlocking {
    fhirEngineDal.delete(testPatient.idElement)
    val result = fhirEngine.search<Patient> {}
    Truth.assertThat(result).isEmpty()
  }

  @After fun fhirEngine() = runBlocking { fhirEngine.delete(ResourceType.Patient, "Patient/1") }

  companion object {
    val testPatient =
      Patient().apply {
        id = "Patient/1"
        addName(HumanName().apply { addGiven("Jane") })
      }
  }
}
