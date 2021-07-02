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

import android.content.Context
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/** A interface for validating FHIR native supported constraints on a questionnaire response. */
internal interface ConstraintValidator {
  /**
   * Validates the `answer`(s) in [questionnaireResponseItem] satisfy any constraints of the
   * [questionnaireItem] according to the [structured data capture implementation guide]
   * (http://build.fhir.org/ig/HL7/sdc/behavior.html). This does not validate the
   * [questionnaireResponseItem] and its child items are structurally consistent with the
   * [questionnaireItem] and its child items.
   * [Learn more](https://www.hl7.org/fhir/questionnaireresponse.html#link).
   */
  fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    context: Context
  ): ConstraintValidationResult

  data class ConstraintValidationResult(val isValid: Boolean, val message: String?)
}
