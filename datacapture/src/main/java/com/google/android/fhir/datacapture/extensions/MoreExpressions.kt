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

package com.google.android.fhir.datacapture.extensions

import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Resource

internal val Expression.isXFhirQuery: Boolean
  get() = this.language == Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()

internal val Expression.isFhirPath: Boolean
  get() = this.language == Expression.ExpressionLanguage.TEXT_FHIRPATH.toCode()

/**
 * Creates an x-fhir-query from an [Expression]. If the questionnaire resource context is set, the
 * expression is first checked for any FHIR Path expressions to evaluate first. See:
 * https://build.fhir.org/ig/HL7/sdc/expressions.html#fhirquery
 */
internal fun Expression.createXFhirQueryFromExpression(
  questionnaireResourceContext: Resource?
): String =
  questionnaireResourceContext?.let { resource ->
    ExpressionEvaluator.evaluateXFhirEnhancement(this, resource)
      .map { it.first to (it.second?.asExpectedType()?.asStringValue() ?: "") }
      .fold(this.expression) { acc: String, pair: Pair<String, String> ->
        acc.replace(pair.first, pair.second)
      }
  }
    ?: this.expression
