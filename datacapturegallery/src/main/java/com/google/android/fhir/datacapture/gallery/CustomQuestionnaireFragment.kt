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

package com.google.android.fhir.datacapture.gallery

import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.contrib.views.QuestionnaireItemPhoneNumberViewHolderFactory

internal const val EXTENSION_QUESTIONNAIRE_INSTRUCTION =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-instruction"

internal const val EXTENSION_EXAMPLE_WIDGET = "http://dummy-widget-type-extension"

internal const val NUMBER_PICKER = "number-picker"
internal const val MOBILE_PHONE_NUMBER = "Mobile phone number"

class CustomQuestionnaireFragment : QuestionnaireFragment() {
  override fun getCustomQuestionnaireItemViewHolderFactoryMatchers():
    List<QuestionnaireItemViewHolderFactoryMatcher> {
    return listOf(
      QuestionnaireItemViewHolderFactoryMatcher(CustomNumberPickerFactory) { questionnaireItem ->
        questionnaireItem.getExtensionByUrl(EXTENSION_EXAMPLE_WIDGET).let {
          if (it == null) false else it.value.toString() == NUMBER_PICKER
        }
      },
      QuestionnaireItemViewHolderFactoryMatcher(QuestionnaireItemPhoneNumberViewHolderFactory) {
        questionnaireItem ->
        questionnaireItem.getExtensionByUrl(EXTENSION_QUESTIONNAIRE_INSTRUCTION).let {
          if (it == null) false else it.value.toString() == MOBILE_PHONE_NUMBER
        }
      }
    )
  }
}
