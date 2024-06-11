/*
 * Copyright 2022-2023 Google LLC
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
import com.google.android.fhir.search.search
import com.google.android.fhir.workflow.repositories.FhirEngineRepository
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
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
class FhirEngineRepositoryTest {

  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()
  private lateinit var fhirEngine: FhirEngine

  private lateinit var fhirEngineRepository: FhirEngineRepository

  @Before
  fun setupTest() {
    val context: Context = ApplicationProvider.getApplicationContext()
    fhirEngine = FhirEngineProvider.getInstance(context)
    fhirEngineRepository = FhirEngineRepository(FhirContext.forR4(), fhirEngine)
    runBlocking { fhirEngine.create(testPatient) }
  }

  @Test
  fun testRepoRead() = runBlockingOnWorkerThread {
    val result = fhirEngineRepository.read(Patient::class.java, IdType("Patient/${testPatient.id}"))

    assertThat(result).isInstanceOf(Patient::class.java)
    assertThat((result as Patient).nameFirstRep.givenAsSingleString)
      .isEqualTo(testPatient.nameFirstRep.givenAsSingleString)
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `testRepoRead when called from main thread should throw BlockingMainThreadException`(): Unit =
    runBlocking {
      fhirEngineRepository.read(Patient::class.java, IdType("Patient/${testPatient.id}"))
    }

  @Test
  fun testRepoCreate() = runBlockingOnWorkerThread {
    val patient =
      Patient().apply {
        id = "Patient/2"
        addName(HumanName().addGiven("John"))
      }

    fhirEngineRepository.create(patient)
    val result = fhirEngine.get(ResourceType.Patient, "2") as Patient

    assertThat(result.nameFirstRep.givenAsSingleString)
      .isEqualTo(patient.nameFirstRep.givenAsSingleString)
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `testRepoCreate when called from main thread should throw BlockingMainThreadException`():
    Unit = runBlocking { fhirEngineRepository.create(testPatient) }

  @Test
  fun testRepoUpdate() = runBlockingOnWorkerThread {
    testPatient.name = listOf(HumanName().addGiven("Eve"))

    fhirEngineRepository.update(testPatient)
    val result = fhirEngine.search<Patient> {}.single()

    assertThat(result.resource.nameFirstRep.givenAsSingleString).isEqualTo("Eve")
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `testRepoUpdate when called from main thread should throw BlockingMainThreadException`():
    Unit = runBlocking { fhirEngineRepository.update(testPatient) }

  @Test
  fun testRepoDelete() = runBlockingOnWorkerThread {
    fhirEngineRepository.delete(Patient::class.java, testPatient.idElement)

    val result = fhirEngine.search<Patient> {}

    assertThat(result).isEmpty()
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `testRepoDelete when called from main thread should throw BlockingMainThreadException`() =
    runBlocking {
      fhirEngineRepository.delete(Patient::class.java, testPatient.idElement)
      Unit
    }

  @After fun fhirEngine() = runBlocking { fhirEngine.delete(ResourceType.Patient, "1") }

  companion object {
    val testPatient =
      Patient().apply {
        id = "1"
        addName(HumanName().addGiven("Jane"))
      }
  }
}
