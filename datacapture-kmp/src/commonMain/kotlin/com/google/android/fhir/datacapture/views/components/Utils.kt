/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.components

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.optional_helper_text
import android_fhir.datacapture_kmp.generated.resources.required
import androidx.compose.runtime.Composable
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import org.jetbrains.compose.resources.stringResource

/**
 * Returns string identified by R.string.required if
 * [QuestionnaireViewItem.questionViewTextConfiguration.showRequiredText] and
 * [com.google.fhir.model.r4.Questionnaire.Item.required] is true, or R.string.optional_text if
 * [QuestionnaireViewItem.questionViewTextConfiguration.showOptionalText] is true.
 */
@Composable
fun getRequiredOrOptionalText(questionnaireViewItem: QuestionnaireViewItem) =
  when {
    (questionnaireViewItem.questionnaireItem.required?.value == true &&
      questionnaireViewItem.questionViewTextConfiguration.showRequiredText) -> {
      stringResource(Res.string.required)
    }
    (questionnaireViewItem.questionnaireItem.required?.value != true &&
      questionnaireViewItem.questionViewTextConfiguration.showOptionalText) -> {
      stringResource(Res.string.optional_helper_text)
    }
    else -> {
      null
    }
  }
