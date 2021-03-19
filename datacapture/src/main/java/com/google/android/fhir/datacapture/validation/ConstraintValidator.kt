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

package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

interface ConstraintValidator {
  /**
   * Validates the response by the user
   *
   * @param questionnaireItem
   * @param questionnaireResponseItemBuilder
   * @return
   */
  fun validate(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder
  ): QuestionnaireResponseItemValidator.ValidationResult

  /**
   * Returns an extension if exists by the URL
   *
   * @param extensionUrlValue
   * @return
   */
  fun Questionnaire.Item.getExtensionsByUrl(extensionUrlValue: String): List<Extension> {
    val extensions = mutableListOf<Extension>()
    val extensionIterator = this.extensionList.iterator()
    while (extensionIterator.hasNext()) {
      val extension = extensionIterator.next()
      if (extension.url.value == extensionUrlValue) {
        extensions.add(extension)
      }
    }
    return extensions
  }
}
