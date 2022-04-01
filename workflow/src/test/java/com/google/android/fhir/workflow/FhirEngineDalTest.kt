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
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.search.search
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Patient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirEngineDalTest2 {
  private val fhirEngine =
    FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
  private val fhirEngineDal = FhirEngineDal(fhirEngine)

  @Before
  fun setupTest() {
    runBlocking { fhirEngine.save(testPatient) }
  }

  @After fun tearDown() {}

  @Test
  fun testDalRead() = runBlocking {
    val result = fhirEngineDal.read(IdType("Patient/${testPatient.id}"))
    assertThat(result).isInstanceOf(Patient::class.java)
    assertThat((result as Patient).nameFirstRep.givenAsSingleString)
      .isEqualTo(testPatient.nameFirstRep.givenAsSingleString)
  }

  @Test
  fun testDalCreate() = runBlocking {
    val patient =
      Patient().apply {
        id = "2"
        addName(HumanName().apply { addGiven("John") })
      }
    fhirEngineDal.create(patient)
    val result = fhirEngine.load(Patient::class.java, "2")
    assertThat(result.nameFirstRep.givenAsSingleString)
      .isEqualTo(patient.nameFirstRep.givenAsSingleString)
  }

  @Test
  fun testDalUpdate() = runBlocking {
    testPatient.name = mutableListOf(HumanName().apply { addGiven("Eve") })
    fhirEngineDal.update(testPatient)
    val result = fhirEngine.search<Patient> {}.single()
    assertThat(result.nameFirstRep.givenAsSingleString).isEqualTo("Eve")
  }

  @Test
  fun testDalDelete() = runBlocking {
    fhirEngineDal.delete(testPatient.idElement)
    val result = fhirEngine.search<Patient> {}
    assertThat(result).isEmpty()
  }

  companion object {
    val testPatient =
      Patient().apply {
        id = "1"
        addName(HumanName().apply { addGiven("Jane") })
      }
  }
}
