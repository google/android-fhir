/*
 * Copyright 2022-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.ErrorText
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem

internal object GroupViewFactory : QuestionnaireItemViewFactory {
  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val validationMessage =
      remember(questionnaireViewItem.validationResult) {
        (questionnaireViewItem.validationResult as? Invalid)?.getSingleStringValidationMessage()
          ?: ""
      }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
      Header(questionnaireViewItem)
      if (validationMessage.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        ErrorText(validationMessage)
      }
      Spacer(modifier = Modifier)
      questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }
    }
  }
}
