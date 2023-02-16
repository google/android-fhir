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

package com.google.android.fhir.catalog

import com.google.android.fhir.datacapture.QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher
import com.google.android.fhir.datacapture.contrib.views.barcode.QuestionnaireItemBarCodeReaderViewHolderFactory

// TODO Remove this file and move this code to maybe a custom view in catalog app?
class CustomQuestionnaireFragment /*: QuestionnaireFragment()*/ {
  /*override*/ fun getCustomQuestionnaireItemViewHolderFactoryMatchers():
    List<QuestionnaireItemViewHolderFactoryMatcher> {
    return listOf(
      QuestionnaireItemViewHolderFactoryMatcher(CustomNumberPickerFactory) { questionnaireItem ->
        questionnaireItem.getExtensionByUrl(CustomNumberPickerFactory.WIDGET_EXTENSION).let {
          if (it == null) false else it.value.toString() == CustomNumberPickerFactory.WIDGET_TYPE
        }
      },
      QuestionnaireItemViewHolderFactoryMatcher(QuestionnaireItemBarCodeReaderViewHolderFactory) {
        questionnaireItem ->
        questionnaireItem
          .getExtensionByUrl(QuestionnaireItemBarCodeReaderViewHolderFactory.WIDGET_EXTENSION)
          .let {
            if (it == null) false
            else it.value.toString() == QuestionnaireItemBarCodeReaderViewHolderFactory.WIDGET_TYPE
          }
      }
    )
  }
}
