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

package com.google.android.fhir.datacapture

import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.StringType
import org.junit.Assert.assertThrows
import org.junit.Test

class FHIRPathEngineHostServicesTest {

  @Test
  fun testFHIRPathHostServices_resolveConstantValueNotPresent_returnsNull() {

    val answer = FHIRPathEngineHostServices.resolveConstant(mapOf("A" to IntegerType(1)), "B", true)

    assertNull(answer)
  }

  @Test
  fun testFHIRPathHostServices_resolveConstantValuePresent_returnsNotNull() {

    val answer = FHIRPathEngineHostServices.resolveConstant(mapOf("A" to IntegerType(1)), "A", true)

    assertNotNull(answer)
  }

  @Test
  fun testFHIRPathHostServices_resolveConstantNullAppContext_throwsNullPointerException() {

    assertThrows(NullPointerException::class.java) {
      FHIRPathEngineHostServices.resolveConstant(null, "A", true)
    }
  }

  @Test
  fun testFHIRPathHostServices_resolveConstantType_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.resolveConstantType(mapOf<Any, Any>(), "")
    }
  }

  @Test
  fun testFHIRPathHostServices_log_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.log("", mutableListOf())
    }
  }

  @Test
  fun testFHIRPathHostServices_resolveFunction_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.resolveFunction("")
    }
  }

  @Test
  fun testFHIRPathHostServices_checkFunction_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.checkFunction(mapOf<Any, Any>(), "", mutableListOf())
    }
  }

  @Test
  fun testFHIRPathHostServices_executeFunction_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.executeFunction(
        mapOf<Any, Any>(),
        mutableListOf(),
        "",
        mutableListOf()
      )
    }
  }

  @Test
  fun testFHIRPathHostServices_resolveReference_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.resolveReference(mapOf<Any, Any>(), "")
    }
  }

  @Test
  fun testFHIRPathHostServices_conformsToProfile_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.conformsToProfile(mapOf<Any, Any>(), StringType(""), "")
    }
  }

  @Test
  fun testFHIRPathHostServices_resolveValueSet_throwsUnsupportedOperationException() {

    assertThrows(UnsupportedOperationException::class.java) {
      FHIRPathEngineHostServices.resolveValueSet(mapOf<Any, Any>(), "")
    }
  }
}
