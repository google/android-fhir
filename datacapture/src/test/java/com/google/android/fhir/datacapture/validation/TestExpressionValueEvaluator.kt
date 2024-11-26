/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.datacapture.validation

import com.google.android.fhir.datacapture.fhirpath.evaluateToBase
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Type

object TestExpressionValueEvaluator {
  /**
   * Doesn't handle expressions containing FHIRPath supplements
   * https://build.fhir.org/ig/HL7/sdc/expressions.html#fhirpath-supplements
   */
  fun evaluate(base: Base, expression: Expression): Type? =
    try {
      evaluateToBase(base, expression.expression).singleOrNull() as? Type
    } catch (_: Exception) {
      null
    }
}
