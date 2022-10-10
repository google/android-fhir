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

import com.google.android.fhir.workflow.testing.CqlBuilder
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CqlBuilderJavaTest {
  /**
   * Tests the compilation of CQL expressions into ELM by verifying if the compiled JSONs match.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun shouldCompileAndAssembleImmunityCheck() {
    CqlBuilder.Assert.that("/cql-compiler/ImmunityCheck-1.0.0.cql")
      .compiles()
      .withJsonEqualsTo("/cql-compiler/ImmunityCheck-1.0.0.elm.json")
      .withXmlEqualsTo("/cql-compiler/ImmunityCheck-1.0.0.elm.xml")
      .generatesFhirLibraryEqualsTo("/cql-compiler/ImmunityCheck-1.0.0.fhir.json")
  }

  /**
   * Tests the compilation of large CQL expressions into ELM.
   *
   * This is part of [#1365](https://github.com/google/android-fhir/issues/1365)
   */
  @Test
  fun shouldCompileAndAssembleFhirHelpers() {
    CqlBuilder.Assert.that("/cql-compiler/FHIRHelpers-4.0.1.cql")
      .compiles()
      .withJsonEqualsTo("/cql-compiler/FHIRHelpers-4.0.1.elm.json")
      .withXmlEqualsTo("/cql-compiler/FHIRHelpers-4.0.1.elm.xml")
      .generatesFhirLibraryEqualsTo("/cql-compiler/FHIRHelpers-4.0.1.fhir.json")
  }
}
