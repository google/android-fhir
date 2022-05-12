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

package com.google.android.fhir.datacapture

import android.os.Build
import com.google.android.fhir.datacapture.testing.DataCaptureTestApplication
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.hl7.fhir.r4.model.IntegerType
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = DataCaptureTestApplication::class)
class FHIRPathEngineHostServicesTest {

  @Test
  fun testFHIRPathHostServices_resolveConstantValuePresent_returnsValue() {

    val answer = FHIRPathEngineHostServices.resolveConstant(mapOf("A" to "1"), "A", true)

    assertNull(answer)
  }

  @Test
  fun testFHIRPathHostServices_resolveConstantValueNotPresent_returnsNotNull() {

    val answer = FHIRPathEngineHostServices.resolveConstant(mapOf("A" to IntegerType(1)), "A", true)

    assertNotNull(answer)
  }

  @Test
  fun testFHIRPathHostServices_resolveConstantType_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.resolveConstantType(any(), any())
    }
  }

  @Test
  fun testFHIRPathHostServices_log_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.log(any(), any())
    }
  }

  @Test
  fun testFHIRPathHostServices_resolveFunction_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.resolveFunction(any())
    }
  }

  @Test
  fun testFHIRPathHostServices_checkFunction_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.checkFunction(any(), any(), any())
    }
  }

  @Test
  fun testFHIRPathHostServices_executeFunction_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.executeFunction(any(), any(), any(), any())
    }
  }

  @Test
  fun testFHIRPathHostServices_resolveReference_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.resolveReference(any(), any())
    }
  }

  @Test
  fun testFHIRPathHostServices_conformsToProfile_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.conformsToProfile(any(), any(), any())
    }
  }

  @Test
  fun testFHIRPathHostServices_resolveValueSet_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.resolveValueSet(any(), any())
    }
  }
}
