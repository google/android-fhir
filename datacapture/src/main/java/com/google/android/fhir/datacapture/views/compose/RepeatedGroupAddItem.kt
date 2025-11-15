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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Composable for the "Add" button for repeated group items. This is the Compose equivalent of
 * [RepeatedGroupAddItemViewHolder].
 */
@Composable
fun RepeatedGroupAddItem(questionnaireViewItem: QuestionnaireViewItem) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val questionText = questionnaireViewItem.questionText?.toString() ?: ""
  val buttonText = context.getString(R.string.add_repeated_group_item, questionText)

  if (questionnaireViewItem.questionnaireItem.repeats) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      OutlinedButton(
        onClick = {
          coroutineScope.launch {
            questionnaireViewItem.addAnswer(
              // Nested items will be added in answerChangedCallback in the QuestionnaireViewModel
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent(),
            )
          }
        },
        enabled = !questionnaireViewItem.questionnaireItem.readOnly,
        modifier = Modifier.testTag(ADD_REPEATED_GROUP_BUTTON_TAG),
      ) {
        Text(
          text = buttonText,
          // Material3 OutlinedButton doesn't use textAllCaps by default, matching the style
        )
      }
    }
  }
}

const val ADD_REPEATED_GROUP_BUTTON_TAG = "addRepeatedGroupButton"
