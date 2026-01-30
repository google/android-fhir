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

package com.google.android.fhir.datacapture.extensions

import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Quantity

internal fun com.google.fhir.model.r4.String.getLocalizedText(lang: String = "en"): String? {
  return getTranslation(lang) ?: getTranslation(lang.split("-").firstOrNull()) ?: value
}

internal fun com.google.fhir.model.r4.String.getTranslation(l: String?): String? {
  for (e in extension) {
    if (e.url == EXT_TRANSLATION) {
      val langExtValue = e.readStringExtension("lang")
      if (langExtValue == l) return e.readStringExtension("content")
    }
  }
  return null
}

/**
 * Converts Quantity to Coding type. The resulting Coding properties are equivalent of Coding.system
 * = Quantity.system Coding.code = Quantity.code Coding.display = Quantity.unit
 */
internal fun Quantity.toCoding(): Coding =
  Coding(system = this.system, code = this.code, display = this.unit)

internal fun Coding.hasCode() = !this.code?.value.isNullOrBlank()

internal fun Coding.hasDisplay() = !this.display?.value.isNullOrBlank()

internal fun Coding.hasSystem() = !this.system?.value.isNullOrBlank()

internal fun Coding.hasVersion() = !this.version?.value.isNullOrBlank()

typealias FhirR4Boolean = com.google.fhir.model.r4.Boolean

typealias FhirR4Integer = com.google.fhir.model.r4.Integer

typealias FhirR4String = com.google.fhir.model.r4.String

internal const val EXT_TRANSLATION = "http://hl7.org/fhir/StructureDefinition/translation"
