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
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.testing.FhirEngineProviderTestRule
import com.google.android.fhir.workflow.testing.Loadable
import com.google.common.truth.Truth.assertThat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.cql.engine.exception.TerminologyProviderException
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirEngineRetrieveProviderTest : Loadable() {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()
  private lateinit var fhirEngine: FhirEngine
  private lateinit var retrieveProvider: FhirEngineRetrieveProvider

  private fun parseJson(path: String): Bundle =
    FhirContext.forR4().newJsonParser().parseResource(open(path)) as Bundle

  @Before
  fun setupTest() {
    val context: Context = ApplicationProvider.getApplicationContext()
    fhirEngine = FhirEngineProvider.getInstance(context)
    retrieveProvider =
      FhirEngineRetrieveProvider(fhirEngine).apply {
        terminologyProvider =
          FhirEngineTerminologyProvider(
            FhirContext.forR4Cached(),
            fhirEngine,
            KnowledgeManager.createInMemory(context)
          )
        isExpandValueSets = true
      }
  }

  private suspend fun loadBundle(bundle: Bundle) {
    for (entry in bundle.entry) {
      when (entry.resource.resourceType) {
        ResourceType.Bundle -> Unit
        else -> fhirEngine.create(entry.resource)
      }
    }
  }

  @Test
  fun testFilterToDataTypeDataTypeNotPresent() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))

    assertThat(
        retrieveProvider
          .retrieve(
            context = null,
            contextPath = null,
            contextValue = null,
            dataType = null,
            templateId = null,
            codePath = null,
            codes = null,
            valueSet = null,
            datePath = null,
            dateLowPath = null,
            dateHighPath = null,
            dateRange = null
          )
          .toList()
      )
      .isEmpty()
  }

  @Test
  fun testNoResultsReturnsEmptySet() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))

    val results: Iterable<Any> =
      retrieveProvider.retrieve(
        context = null,
        contextPath = null,
        contextValue = null,
        dataType = "PlanDefinition",
        templateId = null,
        codePath = null,
        codes = null,
        valueSet = null,
        datePath = null,
        dateLowPath = null,
        dateHighPath = null,
        dateRange = null
      )

    assertThat(results).isNotNull()
    assertThat(results.toList()).isEmpty()
  }

  @Test
  fun testFilterToDataType() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))

    val resultList =
      retrieveProvider
        .retrieve(
          context = null,
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
        .toList()

    assertThat(resultList.size).isEqualTo(2)
    assertThat(resultList[0]).isInstanceOf(Patient::class.java)
    assertThat(resultList[1]).isInstanceOf(Patient::class.java)
  }

  @Test
  fun testFilterToContext() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))

    val resultList =
      retrieveProvider
        .retrieve(
          context = "Patient",
          contextPath = "subject",
          contextValue = "test-one-r4",
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
        .toList()

    assertThat(resultList.size).isEqualTo(2)
    assertThat(resultList[0]).isInstanceOf(Condition::class.java)
    assertThat((resultList[0] as Condition).id).isEqualTo("Condition/test-one-r4-2")
    assertThat(resultList[1]).isInstanceOf(Condition::class.java)
    assertThat((resultList[1] as Condition).id).isEqualTo("Condition/test-one-r4-6")
  }

  @Test
  fun testFilterToContextNoContextRelation() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))

    val resultList =
      retrieveProvider
        .retrieve(
          context = "Patient",
          contextPath = null,
          contextValue = "test-one-r4",
          dataType = "Medication",
          templateId = null,
          codePath = null,
          codes = null,
          valueSet = null,
          datePath = null,
          dateLowPath = null,
          dateHighPath = null,
          dateRange = null
        )
        .toList()

    assertThat(resultList.size).isEqualTo(1)
    assertThat(resultList.first()).isInstanceOf(Medication::class.java)
  }

  @Test
  fun testFilterById() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))

    // Id does exist
    var codes = mutableListOf(Code().withCode("test-med"))
    var resultList =
      retrieveProvider
        .retrieve(
          context = "Patient",
          contextPath = null,
          contextValue = "test-one-r4",
          dataType = "Medication",
          templateId = null,
          codePath = "id",
          codes = codes,
          valueSet = null,
          datePath = null,
          dateLowPath = null,
          dateHighPath = null,
          dateRange = null
        )
        .toList()

    assertThat(resultList.size).isEqualTo(1)
    assertThat(resultList.first()).isInstanceOf(Medication::class.java)

    // Id does not exist
    codes = mutableListOf(Code().withCode("test-med-does-exist"))
    resultList =
      retrieveProvider
        .retrieve(
          context = "Patient",
          contextPath = null,
          contextValue = "test-one-r4",
          dataType = "Medication",
          templateId = null,
          codePath = "id",
          codes = codes,
          valueSet = null,
          datePath = null,
          dateLowPath = null,
          dateHighPath = null,
          dateRange = null
        )
        .toList()

    assertThat(resultList).isEmpty()
  }

  @Test
  fun testFilterToCodes() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))

    // Code doesn't match
    var code = Code().withCode("not-a-code").withSystem("not-a-system")
    var results: Iterable<Any> =
      retrieveProvider.retrieve(
        context = "Patient",
        contextPath = "subject",
        contextValue = "test-one-r4",
        dataType = "Condition",
        templateId = null,
        codePath = "code",
        codes = mutableSetOf(code),
        valueSet = null,
        datePath = null,
        dateLowPath = null,
        dateHighPath = null,
        dateRange = null
      )
    assertThat(results).isNotNull()
    assertThat(results.toList()).isEmpty()

    // Codes does match
    code = Code().withCode("10327003").withSystem("http://snomed.info/sct")
    results =
      retrieveProvider.retrieve(
        context = "Patient",
        contextPath = "subject",
        contextValue = "test-one-r4",
        dataType = "Condition",
        templateId = null,
        codePath = "code",
        codes = mutableSetOf(code),
        valueSet = null,
        datePath = null,
        dateLowPath = null,
        dateHighPath = null,
        dateRange = null
      )
    assertThat(results).isNotNull()

    val resultList = results.toList()
    assertThat(resultList.size).isEqualTo(1)
    assertThat(resultList.first()).isInstanceOf(Condition::class.java)
    assertThat((resultList.first() as Condition).subject.referenceElement.idPart)
      .isEqualTo("test-one-r4")
  }

  @Test(expected = TerminologyProviderException::class)
  fun testFilterToValueSetNoTerminologyProvider(): Unit = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))

    retrieveProvider.retrieve(
      context = "Patient",
      contextPath = "subject",
      contextValue = "test-one-r4",
      dataType = "Condition",
      templateId = null,
      codePath = "code",
      codes = null,
      valueSet = "value-set-url", // does not exist.
      datePath = null,
      dateLowPath = null,
      dateHighPath = null,
      dateRange = null
    )
  }

  @Test
  fun testFilterToValueSet() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))
    loadBundle(parseJson("/retrieve-provider/TestBundleValueSets.json"))

    // Not in the value set
    var results: Iterable<Any> =
      retrieveProvider.retrieve(
        context = "Patient",
        contextPath = "subject",
        contextValue = "test-one-r4",
        dataType = "Condition",
        templateId = null,
        codePath = "code",
        codes = null,
        valueSet = "http://localhost/fhir/ValueSet/value-set-three",
        datePath = null,
        dateLowPath = null,
        dateHighPath = null,
        dateRange = null
      )

    assertThat(results).isNotNull()
    assertThat(results.toList()).isEmpty()

    // In the value set
    results =
      retrieveProvider.retrieve(
        context = "Patient",
        contextPath = "subject",
        contextValue = "test-one-r4",
        dataType = "Condition",
        templateId = null,
        codePath = "code",
        codes = null,
        valueSet = "http://localhost/fhir/ValueSet/value-set-one",
        datePath = null,
        dateLowPath = null,
        dateHighPath = null,
        dateRange = null
      )
    assertThat(results).isNotNull()
    assertThat(results.toList().size).isEqualTo(1)
  }

  @Test
  fun testRetrieveByUrn() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleUrns.json"))

    var resultList =
      retrieveProvider
        .retrieve(
          context = "Patient",
          contextPath = "id",
          contextValue = "e527283b-e4b1-4f4e-9aef-8a5162816e32",
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
        .toList()

    assertThat(resultList.size).isEqualTo(1)
    assertThat(resultList.first()).isInstanceOf(Patient::class.java)

    resultList =
      retrieveProvider
        .retrieve(
          context = "Patient",
          contextPath = "subject",
          contextValue = "e527283b-e4b1-4f4e-9aef-8a5162816e32",
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
        .toList()

    assertThat(resultList.size).isEqualTo(1)
    assertThat(resultList.first()).isInstanceOf(Condition::class.java)
  }

  @Test
  fun testRetrieveByDate() = runBlockingOnWorkerThread {
    loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))

    // searching between 2020 and 2021.
    val start: OffsetDateTime = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    val end: OffsetDateTime = start.plusYears(1)
    val interval = Interval(DateTime(start), true, DateTime(end), false)

    var resultList =
      retrieveProvider
        .retrieve(
          context = "Patient",
          contextPath = "subject",
          contextValue = "test-one-r4",
          dataType = "Condition",
          templateId = null,
          codePath = null,
          codes = null,
          valueSet = null,
          datePath = "onset-date",
          dateLowPath = null,
          dateHighPath = null,
          dateRange = interval
        )
        .toList()

    assertThat(resultList.size).isEqualTo(1)
    assertThat(resultList.first()).isInstanceOf(Condition::class.java)

    resultList =
      retrieveProvider
        .retrieve(
          context = "Patient",
          contextPath = "subject",
          contextValue = "test-one-r4",
          dataType = "Condition",
          templateId = null,
          codePath = null,
          codes = null,
          valueSet = null,
          datePath = null,
          dateLowPath = "onset-date",
          dateHighPath = "onset-date",
          dateRange = interval
        )
        .toList()

    assertThat(resultList.size).isEqualTo(1)
    assertThat(resultList.first()).isInstanceOf(Condition::class.java)
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `retrieve when called from main thread should throw BlockingMainThreadException`(): Unit =
    runBlocking {
      loadBundle(parseJson("/retrieve-provider/TestBundleTwoPatients.json"))
      retrieveProvider.retrieve(
        context = null,
        contextPath = null,
        contextValue = null,
        dataType = null,
        templateId = null,
        codePath = null,
        codes = null,
        valueSet = null,
        datePath = null,
        dateLowPath = null,
        dateHighPath = null,
        dateRange = null
      )
    }
}
