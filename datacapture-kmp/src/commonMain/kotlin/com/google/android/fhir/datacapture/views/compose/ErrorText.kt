/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.datacapture.views.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme

@Composable
internal fun ErrorText(validationMessage: String) {
  Text(
    text = validationMessage,
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.error,
    modifier =
      Modifier.padding(start = QuestionnaireTheme.dimensions.errorTextMarginHorizontal)
        .testTag(ERROR_TEXT_TAG),
  )
}

const val ERROR_TEXT_TAG = "error_text"
