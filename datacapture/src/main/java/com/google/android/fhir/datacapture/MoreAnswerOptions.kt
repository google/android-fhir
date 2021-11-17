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

import java.util.Locale
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.utils.ToolingExtensions

val Questionnaire.QuestionnaireItemAnswerOptionComponent.displayString: String
  get() {
    if (hasValueCoding()) {
      val localizedDisplayText = valueCoding.getLocalizedText()
      val display = localizedDisplayText ?: valueCoding.display
      return if (display.isNullOrEmpty()) {
        valueCoding.code
      } else {
        display
      }
    } else {
      throw IllegalArgumentException("Answer option does not having coding.")
    }
  }

@Throws(FHIRException::class)
fun Coding.getTranslation(l: String): String? {
  for (e in extension) {
    if (e.url == ToolingExtensions.EXT_TRANSLATION) {
      val lang = ToolingExtensions.readStringExtension(e, "lang")
      if (lang == l) return e.getExtensionString("content")
    }
  }
  return null
}

private fun Coding.getLocalizedText(lang: String = Locale.getDefault().toLanguageTag()): String? {
  return getTranslation(lang) ?: getTranslation(lang.split("-").first())
}
