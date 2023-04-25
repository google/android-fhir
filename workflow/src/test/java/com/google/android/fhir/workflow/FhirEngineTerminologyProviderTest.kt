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
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.CodeSystem
import org.hl7.fhir.r4.model.ValueSet
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.opencds.cqf.cql.engine.exception.TerminologyProviderException
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirEngineTerminologyProviderTest : Loadable() {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()
  private lateinit var fhirEngine: FhirEngine
  private lateinit var provider: FhirEngineTerminologyProvider

  companion object {
    private const val TEST_DISPLAY = "Display"
    private const val TEST_CODE = "425178004"
    private const val TEST_SYSTEM = "http://snomed.info/sct"
    private const val TEST_SYSTEM_VERSION = "2013-09"
  }

  @Before
  fun setupTest() {
    val context: Context = ApplicationProvider.getApplicationContext()
    fhirEngine = FhirEngineProvider.getInstance(context)
    provider =
      FhirEngineTerminologyProvider(
        FhirContext.forR4Cached(),
        fhirEngine,
        KnowledgeManager.createInMemory(context)
      )
  }

  @Test
  @Throws(Exception::class)
  fun resolveByUrlUsingUrlSucceeds() = runBlockingOnWorkerThread {
    val info: ValueSetInfo = ValueSetInfo().withId("https://cts.nlm.nih.gov/fhir/ValueSet/1.2.3.4")

    val response =
      ValueSet().apply {
        id = "1.2.3.4"
        url = info.id
      }
    fhirEngine.create(response)

    assertThat(provider.resolveValueSetId(info)).isEqualTo(response.id)
  }

  @Test
  fun resolveByUrlUsingIdentifierSucceeds() = runBlockingOnWorkerThread {
    val info: ValueSetInfo = ValueSetInfo().withId("urn:oid:1.2.3.4")

    val response =
      ValueSet().apply {
        id = "1.2.3.4"
        addIdentifier().value = info.id
      }
    fhirEngine.create(response)

    assertThat(provider.resolveValueSetId(info)).isEqualTo(response.id)
  }

  @Test
  fun resolveByUrlUsingResourceIdSucceeds() = runBlockingOnWorkerThread {
    val info: ValueSetInfo = ValueSetInfo().withId("1.2.3.4")

    val response = ValueSet().apply { id = "1.2.3.4" }

    fhirEngine.create(response)

    assertThat(provider.resolveValueSetId(info)).isEqualTo(response.id)
  }

  @Test(expected = IllegalArgumentException::class)
  fun resolveByUrlNoMatchesThrowsException(): Unit = runBlockingOnWorkerThread {
    val info: ValueSetInfo = ValueSetInfo().withId("urn:oid:1.2.3.4")
    provider.resolveValueSetId(info)
  }

  @Test(expected = TerminologyProviderException::class)
  fun expandByUrlNoMatchesThrowsException(): Unit = runBlockingOnWorkerThread {
    val info: ValueSetInfo = ValueSetInfo().withId("urn:oid:1.2.3.4")
    provider.expand(info)
  }

  @Test(expected = UnsupportedOperationException::class)
  fun nonNullVersionUnsupported(): Unit = runBlockingOnWorkerThread {
    val info =
      ValueSetInfo().apply {
        id = "urn:oid:Test"
        version = "1.0.0."
      }
    provider.resolveValueSetId(info)
  }

  @Test(expected = UnsupportedOperationException::class)
  fun nonNullCodeSystemsUnsupported(): Unit = runBlockingOnWorkerThread {
    val codeSystem =
      CodeSystemInfo().apply {
        id = "SNOMED-CT"
        version = "2013-09"
      }
    val info =
      ValueSetInfo().apply {
        id = "urn:oid:Test"
        codeSystems.add(codeSystem)
      }
    provider.resolveValueSetId(info)
  }

  @Test
  fun urnOidPrefixIsStripped() = runBlockingOnWorkerThread {
    val info = ValueSetInfo().withId("urn:oid:Test")

    val response =
      ValueSet().apply {
        id = "Test"
        expansion.containsFirstRep.setSystem(TEST_SYSTEM).code = TEST_CODE
      }

    fhirEngine.create(response)

    assertThat(provider.resolveValueSetId(info)).isEqualTo(response.id)
  }

  @Test(expected = IllegalArgumentException::class)
  fun moreThanOneURLSearchResultIsError(): Unit = runBlockingOnWorkerThread {
    val info = ValueSetInfo().withId("http://localhost/fhir/ValueSet/1.2.3.4")

    val response1 =
      ValueSet().apply {
        id = "1"
        url = info.id
      }

    val response2 =
      ValueSet().apply {
        id = "2"
        url = info.id
      }

    fhirEngine.create(response1)
    fhirEngine.create(response2)

    provider.resolveValueSetId(info)
  }

  @Test(expected = IllegalArgumentException::class)
  fun zeroURLSearchResultIsError(): Unit = runBlockingOnWorkerThread {
    val info = ValueSetInfo().withId("http://localhost/fhir/ValueSet/1.2.3.4")
    provider.resolveValueSetId(info)
  }

  @Test
  fun expandOperationReturnsCorrectCodesMoreThanZero() = runBlockingOnWorkerThread {
    val info = ValueSetInfo().withId("urn:oid:Test")
    val response =
      ValueSet().apply {
        id = "Test"
        expansion.containsFirstRep.setSystem(TEST_SYSTEM).code = TEST_CODE
      }

    fhirEngine.create(response)

    val list = provider.expand(info).toList()
    assertThat(list.size).isEqualTo(1)
    assertThat(list[0].system).isEqualTo(TEST_SYSTEM)
    assertThat(list[0].code).isEqualTo(TEST_CODE)
  }

  @Test
  fun inOperationReturnsTrueWhenFhirReturnsTrue() = runBlockingOnWorkerThread {
    val info = ValueSetInfo().withId("urn:oid:Test")

    val response =
      ValueSet().apply {
        id = "Test"
        expansion.addContains().apply {
          system = TEST_SYSTEM
          code = TEST_CODE
          display = TEST_DISPLAY
        }
      }

    fhirEngine.create(response)

    val code =
      Code().apply {
        system = TEST_SYSTEM
        code = TEST_CODE
      }

    assertThat(provider.`in`(code, info)).isTrue()
  }

  @Test
  fun inOperationReturnsFalseCodeIsNotInTheValueSet() = runBlockingOnWorkerThread {
    val info = ValueSetInfo().withId("urn:oid:Test")

    val response =
      ValueSet().apply {
        id = "Test"
        expansion.addContains().apply {
          code = "Gibberish"
          display = "Gibberish"
        }
      }

    val code =
      Code().apply {
        system = TEST_SYSTEM
        code = TEST_CODE
        display = TEST_DISPLAY
      }

    fhirEngine.create(response)

    assertThat(provider.`in`(code, info)).isFalse()
  }

  @Test
  fun inOperationHandlesNullSystem() = runBlockingOnWorkerThread {
    val info = ValueSetInfo().withId("urn:oid:Test")

    val response =
      ValueSet().apply {
        id = "Test"
        expansion.addContains().apply {
          code = TEST_CODE
          display = TEST_DISPLAY
        }
      }

    val code =
      Code().apply {
        code = TEST_CODE
        display = TEST_DISPLAY
      }

    fhirEngine.create(response)

    assertThat(provider.`in`(code, info)).isTrue()
  }

  @Test
  fun lookupOperationSuccess() = runBlockingOnWorkerThread {
    val info =
      CodeSystemInfo().apply {
        id = TEST_SYSTEM
        version = TEST_SYSTEM_VERSION
      }

    val code = Code().apply { code = TEST_CODE }

    val codeSystem =
      CodeSystem().apply {
        id = TEST_SYSTEM
        version = TEST_SYSTEM_VERSION
        url = TEST_SYSTEM
        addConcept().apply {
          this.code = TEST_CODE
          display = TEST_DISPLAY
        }
      }

    fhirEngine.create(codeSystem)

    val result: Code = provider.lookup(code, info)
    assertThat(result).isNotNull()
    assertThat(result.system).isEqualTo(TEST_SYSTEM)
    assertThat(result.code).isEqualTo(TEST_CODE)
    assertThat(result.display).isEqualTo(TEST_DISPLAY)
  }

  @Test(expected = BlockingMainThreadException::class)
  fun `in when called from main thread should throw BlockingMainThreadException`(): Unit =
    runBlocking {
      val info = ValueSetInfo().withId("urn:oid:Test")
      val code =
        Code().apply {
          code = TEST_CODE
          display = TEST_DISPLAY
        }

      provider.`in`(code, info)
    }

  @Test(expected = BlockingMainThreadException::class)
  fun `expand when called from main thread should throw BlockingMainThreadException`(): Unit =
    runBlocking {
      val info: ValueSetInfo = ValueSetInfo().withId("urn:oid:1.2.3.4")
      provider.expand(info)
    }

  @Test(expected = BlockingMainThreadException::class)
  fun `lookup when called from main thread should throw BlockingMainThreadException`(): Unit =
    runBlocking {
      val info =
        CodeSystemInfo().apply {
          id = TEST_SYSTEM
          version = TEST_SYSTEM_VERSION
        }
      val code =
        Code().apply {
          code = TEST_CODE
          display = TEST_DISPLAY
        }
      provider.lookup(code, info)
    }
}
