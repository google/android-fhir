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
import com.google.android.fhir.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Task
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.cql.engine.runtime.Code
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirEngineRetrieveProviderTest {

  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()
  private lateinit var fhirEngine: FhirEngine
  private lateinit var fhirEngineRetrieveProvider: FhirEngineRetrieveProvider

  @Before
  fun setupTest() {
    fhirEngine = FhirEngineProvider.getInstance(ApplicationProvider.getApplicationContext())
    fhirEngineRetrieveProvider = FhirEngineRetrieveProvider(fhirEngine)
  }

  @Test
  fun `retrieve should return filtered Encounters with reason code`() = runBlocking {
    val encounters =
      listOf(
        Encounter().apply {
          id = "Encounter/E1"
          addReasonCode().apply { this.addCoding(Coding("http://codesystem.org", "c1", "C 1")) }
        },
        Encounter().apply {
          id = "Encounter/E2"
          addReasonCode().apply { this.addCoding(Coding("http://codesystem.org", "c2", "C 2")) }
        }
      )

    fhirEngine.create(*encounters.toTypedArray())
    val result =
      fhirEngineRetrieveProvider.retrieve(
          context = "Patient",
          contextPath = "subject",
          contextValue = null,
          dataType = "Encounter",
          templateId = null,
          codePath = "reasonCode",
          codes = mutableListOf(Code().withCode("c2").withSystem("http://codesystem.org")),
          valueSet = null,
          datePath = null,
          dateLowPath = null,
          dateHighPath = null,
          dateRange = null
        )
        .map { it as Encounter }

    assertThat(result.size).isEqualTo(1)
    assertThat(result.first().id).isEqualTo("Encounter/E2")
    assertThat(result.first().reasonCode.first().codingFirstRep.code).isEqualTo("c2")
  }

  @Test
  fun `retrieve should return filtered Conditions with patient id`() = runBlocking {
    val conditions =
      listOf(
        Condition().apply {
          id = "Condition/C1"
          code =
            CodeableConcept().apply { this.addCoding(Coding("http://codesystem.org", "c1", "C 1")) }
          subject = Reference().apply { reference = "Patient/P1" }
        },
        Condition().apply {
          id = "Condition/C2"
          code =
            CodeableConcept().apply { this.addCoding(Coding("http://codesystem.org", "c2", "C 2")) }
          subject = Reference().apply { reference = "Patient/P2" }
        }
      )

    fhirEngine.create(*conditions.toTypedArray())

    val result =
      fhirEngineRetrieveProvider.retrieve(
          context = "Patient",
          contextPath = "subject",
          contextValue = "P2",
          dataType = "Condition",
          templateId = null,
          codePath = null,
          codes = null,
          valueSet = null,
          datePath = null,
          dateLowPath = null,
          dateHighPath = null,
          dateRange = null
        )
        .map { it as Condition }

    assertThat(result.size).isEqualTo(1)
    assertThat(result.first().id).isEqualTo("Condition/C2")
    assertThat(result.first().subject.reference).isEqualTo("Patient/P2")
  }

  @Test
  fun `retrieve should return filtered Tasks with patient id`() = runBlocking {
    val tasks =
      listOf(
        Task().apply {
          id = "Task/T1"
          code =
            CodeableConcept().apply { this.addCoding(Coding("http://codesystem.org", "c1", "C 1")) }
          `for` = Reference().apply { reference = "Patient/P1" }
        },
        Task().apply {
          id = "Task/T2"
          code =
            CodeableConcept().apply { this.addCoding(Coding("http://codesystem.org", "c2", "C 2")) }
          `for` = Reference().apply { reference = "Patient/P2" }
        }
      )

    fhirEngine.create(*tasks.toTypedArray())

    val result =
      fhirEngineRetrieveProvider.retrieve(
          context = "Patient",
          contextPath = "requester",
          contextValue = "P2",
          dataType = "Task",
          templateId = null,
          codePath = null,
          codes = null,
          valueSet = null,
          datePath = null,
          dateLowPath = null,
          dateHighPath = null,
          dateRange = null
        )
        .map { it as Task }

    assertThat(result.size).isEqualTo(1)
    assertThat(result.first().id).isEqualTo("Task/T2")
    assertThat(result.first().`for`.reference).isEqualTo("Patient/P2")
  }

  @Test
  fun `retrieve should return Patients with no filter on active`() = runBlocking {
    val patients =
      listOf(
        Patient().apply {
          id = "Patient/P1"
          active = false
        },
        Patient().apply {
          id = "Patient/P2"
          active = true
        }
      )

    fhirEngine.create(*patients.toTypedArray())

    val result =
      fhirEngineRetrieveProvider.retrieve(
          context = "Patient",
          contextPath = null,
          contextValue = null,
          dataType = "Patient",
          templateId = null,
          codePath = null,
          codes = null,
          valueSet = null,
          datePath = null,
          dateLowPath = null,
          dateHighPath = null,
          dateRange = null
        )
        .map { it as Patient }

    assertThat(result.size).isEqualTo(2)
    assertThat(result.first().id).isEqualTo("Patient/P1")
    assertThat(result.last().id).isEqualTo("Patient/P2")
  }
}
