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

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.search.search
import com.google.android.fhir.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
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
    val context: Context = ApplicationProvider.getApplicationContext()
    fhirEngine = FhirEngineProvider.getInstance(context)
    fhirEngineDal = FhirEngineDal(fhirEngine, KnowledgeManager.createInMemory(context))
    runBlocking { fhirEngine.create(testPatient) }
  }

  @Test
  fun testDalRead() = runBlockingOnWorkerThread {
    val result = fhirEngineDal.read(IdType("Patient/${testPatient.id}"))

    assertThat(result).isInstanceOf(Patient::class.java)
    assertThat((result as Patient).nameFirstRep.givenAsSingleString)
      .isEqualTo(testPatient.nameFirstRep.givenAsSingleString)
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `testDalRead when called from main thread should throw BlockingMainThreadException`(): Unit =
    runBlocking {
      fhirEngineDal.read(IdType("Patient/${testPatient.id}"))
    }

  @Test
  fun testDalCreate() = runBlockingOnWorkerThread {
    val patient =
      Patient().apply {
        id = "Patient/2"
        addName(HumanName().addGiven("John"))
      }

    fhirEngineDal.create(patient)
    val result = fhirEngine.get(ResourceType.Patient, "2") as Patient

    assertThat(result.nameFirstRep.givenAsSingleString)
      .isEqualTo(patient.nameFirstRep.givenAsSingleString)
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `testDalCreate when called from main thread should throw BlockingMainThreadException`():
    Unit = runBlocking { fhirEngineDal.create(testPatient) }

  @Test
  fun testDalUpdate() = runBlockingOnWorkerThread {
    testPatient.name = listOf(HumanName().addGiven("Eve"))

    fhirEngineDal.update(testPatient)
    val result = fhirEngine.search<Patient> {}.single()

    assertThat(result.nameFirstRep.givenAsSingleString).isEqualTo("Eve")
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `testDalUpdate when called from main thread should throw BlockingMainThreadException`():
    Unit = runBlocking { fhirEngineDal.update(testPatient) }

  @Test
  fun testDalDelete() = runBlockingOnWorkerThread {
    fhirEngineDal.delete(testPatient.idElement)

    val result = fhirEngine.search<Patient> {}

    assertThat(result).isEmpty()
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `testDalDelete when called from main thread should throw BlockingMainThreadException`() =
    runBlocking {
      fhirEngineDal.delete(testPatient.idElement)
    }

  @After fun fhirEngine() = runBlocking { fhirEngine.delete(ResourceType.Patient, "Patient/1") }

  companion object {
    val testPatient =
      Patient().apply {
        id = "Patient/1"
        addName(HumanName().addGiven("Jane"))
      }
  }
}
