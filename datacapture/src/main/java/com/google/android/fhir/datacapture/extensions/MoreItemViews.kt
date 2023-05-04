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

package com.google.android.fhir.datacapture.extensions

import android.content.Context
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import org.hl7.fhir.r4.model.Questionnaire

/**
 * Returns [R.string.required] if [QuestionnaireViewItem.showRequiredText] and
 * [Questionnaire.QuestionnaireItemComponent.required] is true, or [R.string.optional_text] if
 * [QuestionnaireViewItem.showOptionalText] is true.
 */
internal fun getRequiredOrOptionalText(
  questionnaireViewItem: QuestionnaireViewItem,
  context: Context
) =
  when {
    (questionnaireViewItem.questionnaireItem.required &&
      questionnaireViewItem.showRequiredText) -> {
      context.getString(R.string.required)
    }
    (!questionnaireViewItem.questionnaireItem.required &&
      questionnaireViewItem.showOptionalText) -> {
      context.getString(R.string.optional_helper_text)
    }
    else -> {
      null
    }
  }
