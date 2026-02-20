/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.datacapture.fhirpath

import co.touchlab.kermit.Logger
import com.google.fhir.fhirpath.FhirPathEngine
import com.google.fhir.model.r4.Resource

val r4FhirPathEngine = FhirPathEngine.forR4()

fun convertToBoolean(result: List<Any>): Boolean {
  if (result.isEmpty()) return false
  if (result.size == 1) return result.first() as Boolean
  return result.isNotEmpty()
}

internal fun convertToString(results: List<Any>): String {
  return when {
    results.isEmpty() -> ""
    results.size == 1 -> convertSingleResultToString(results.first())
    else -> results.joinToString(", ") { convertSingleResultToString(it) }
  }
}

private fun convertSingleResultToString(value: Any): String {
  return when (value) {
    is com.google.fhir.model.r4.String -> value.value ?: ""
    is com.google.fhir.model.r4.Integer -> value.value?.toString() ?: ""
    is com.google.fhir.model.r4.Decimal -> value.value?.toString() ?: ""
    is com.google.fhir.model.r4.Boolean -> value.value?.toString() ?: ""
    is com.google.fhir.model.r4.Date -> value.value?.toString() ?: ""
    is com.google.fhir.model.r4.DateTime -> value.value?.toString() ?: ""
    is com.google.fhir.model.r4.Time -> value.value?.toString() ?: ""
    is com.google.fhir.model.r4.Code -> value.value ?: ""
    is com.google.fhir.model.r4.Uri -> value.value ?: ""
    is com.google.fhir.model.r4.Coding -> value.display?.value ?: value.code?.value ?: ""
    is com.google.fhir.model.r4.Quantity -> value.value?.value?.toString() ?: ""
    else -> value.toString()
  }
}

internal fun extractResourceTypeFromPath(fhirPath: String): String? {
  val trimmedPath = fhirPath.trim()
  val firstToken = trimmedPath.split('.', '(', '[', ' ').firstOrNull() ?: return null
  return firstToken.takeIf { it.firstOrNull()?.isUpperCase() == true }
}

internal fun evaluateFhirPathToString(
  expression: String,
  resource: Resource?,
): String {
  return try {
    if (resource == null) {
      ""
    } else {
      val results = r4FhirPathEngine.evaluateExpression(expression, resource).toList()
      convertToString(results)
    }
  } catch (throwable: Throwable) {
    Logger.e("Error evaluating fhirPath expression $expression to string", throwable)
    ""
  }
}

/**
 * Evaluates the expressions over list of resources [Resource] and joins to space separated string
 */
internal fun evaluateToDisplay(expressions: List<String>, data: Resource) =
  expressions.joinToString(" ") { evaluateFhirPathToString(it, data) }
