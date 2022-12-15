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

package com.google.android.fhir.datacapture

import com.google.android.fhir.datacapture.utilities.displayString
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType

internal const val EXTENSION_OPTION_EXCLUSIVE_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-optionExclusive"

/**
 * Text value for answer option [Questionnaire.QuestionnaireItemAnswerOptionComponent] if answer
 * option is [IntegerType], [StringType], [Coding], or [Reference] type.
 */
internal val Questionnaire.QuestionnaireItemAnswerOptionComponent.displayString: String
  get() = displayString(null, value)

/** Indicates that if this answerOption is selected, no other possible answers may be selected. */
internal val Questionnaire.QuestionnaireItemAnswerOptionComponent.optionExclusive: Boolean
  get() {
    val extension =
      this.extension.singleOrNull { it.url == EXTENSION_OPTION_EXCLUSIVE_URL } ?: return false
    val value = extension.value
    if (value is BooleanType) {
      return value.booleanValue()
    }
    return false
  }
