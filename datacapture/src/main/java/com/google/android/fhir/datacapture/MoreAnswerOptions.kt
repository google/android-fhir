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
    if (!hasValueCoding()) {
      throw IllegalArgumentException("Answer option does not having coding.")
    }
    val display = valueCoding.getLocalizedText() ?: valueCoding.display
    return if (display.isNullOrEmpty()) {
      valueCoding.code
    } else {
      display
    }
  }

@Throws(FHIRException::class)
private fun Coding.getTranslation(lang: String): String? {
  for (extension in extension) {
    if (ToolingExtensions.EXT_TRANSLATION != extension.url) {
      continue
    }

    if (lang == ToolingExtensions.readStringExtension(extension, "lang")) {
      return extension.getExtensionString("content")
    }
  }
  return null
}

private fun Coding.getLocalizedText(): String? {
  val lang: String = Locale.getDefault().toLanguageTag()
  return getTranslation(lang) ?: getTranslation(lang.split("-").first())
}
