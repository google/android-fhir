/*
 * Copyright 2022-2026 Google LLC
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

package com.google.android.fhir.datacapture.extensions

import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Expression
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoreExpressionsTest {

  @Test
  fun is_xfhir_query_should_return_true() {
    val expression =
      Expression(
        language = Enumeration(value = Expression.ExpressionLanguage.Application_X_Fhir_Query),
      )

    assertTrue(expression.isXFhirQuery)
  }

  @Test
  fun is_xfhir_query_should_return_false() {
    val expression =
      Expression(language = Enumeration(value = Expression.ExpressionLanguage.Text_Cql))

    assertFalse(expression.isXFhirQuery)
  }

  @Test
  fun is_fhir_path_should_return_true() {
    val expression =
      Expression(language = Enumeration(value = Expression.ExpressionLanguage.Text_Fhirpath))

    assertTrue(expression.isFhirPath)
  }

  @Test
  fun is_fhir_path_should_return_false() {
    val expression =
      Expression(
        language = Enumeration(value = Expression.ExpressionLanguage.Application_X_Fhir_Query),
      )

    assertFalse(expression.isFhirPath)
  }
}
