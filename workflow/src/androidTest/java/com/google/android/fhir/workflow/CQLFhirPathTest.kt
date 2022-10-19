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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.workflow.testing.CqlBuilder
import com.google.common.truth.Truth.assertThat
import java.io.InputStream
import org.hl7.elm.r1.Now
import org.hl7.elm.r1.TimeOfDay
import org.hl7.elm.r1.Today
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CQLFhirPathTest {

  private fun open(asset: String): InputStream? {
    return javaClass.getResourceAsStream(asset)
  }

  // Forces the evaluation of FHIR Path functions inside CQL to make sure dependencies are present.
  @Test
  fun testFHIRPath() {
    val translator = CqlBuilder.compile(open("/cql-fhir-path/TestFHIRPath.cql")!!)
    val library = translator.translatedLibrary

    var expressionDef = library.resolveExpressionRef("TestNow")
    assertThat(expressionDef).isNotNull()
    assertThat(expressionDef.expression).isInstanceOf(Now::class.java)

    expressionDef = library.resolveExpressionRef("TestToday")
    assertThat(expressionDef).isNotNull()
    assertThat(expressionDef.expression).isInstanceOf(Today::class.java)

    expressionDef = library.resolveExpressionRef("TestTimeOfDay")
    assertThat(expressionDef).isNotNull()
    assertThat(expressionDef.expression).isInstanceOf(TimeOfDay::class.java)
  }
}
