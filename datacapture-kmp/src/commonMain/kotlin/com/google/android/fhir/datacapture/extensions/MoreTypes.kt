/*
 * Copyright 2025 Google LLC
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

internal const val EXT_TRANSLATION = "http://hl7.org/fhir/StructureDefinition/translation"
