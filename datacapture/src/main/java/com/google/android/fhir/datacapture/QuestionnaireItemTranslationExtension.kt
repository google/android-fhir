/*
 * Copyright 2020 Google LLC
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
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.StringType

internal fun Questionnaire.QuestionnaireItemComponent.localizedText(
  lang: String = Locale.getDefault().toLanguageTag()
): String? {
  return getLocalizedText(textElement, lang)
}

internal fun Questionnaire.QuestionnaireItemComponent.localizedPrefix(
  lang: String = Locale.getDefault().toLanguageTag()
): String? {
  return getLocalizedText(prefixElement, lang)
}

internal fun getLocalizedText(
  textElement: StringType?,
  lang: String = Locale.getDefault().toLanguageTag()
): String? {
  return textElement?.getTranslation(lang)
    ?: textElement?.getTranslation(lang.split("-").first()) ?: textElement?.value
}
