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

package com.google.android.fhir.datacapture.views.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse

/** Composable for the "Add" button for repeated group items. */
@Composable
fun RepeatedGroupAddItem(questionnaireViewItem: QuestionnaireViewItem) {
  val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
  val questionText =
    remember(questionnaireViewItem) { questionnaireViewItem.questionText?.toString() ?: "" }
  val isQuestionnaireViewItemRepeatedGroup =
    remember(questionnaireViewItem.questionnaireItem.repeats) {
      questionnaireViewItem.questionnaireItem.repeats
    }
  val isEnabled =
    remember(questionnaireViewItem.questionnaireItem.readOnly) {
      !questionnaireViewItem.questionnaireItem.readOnly
    }
  val color =
    if (isEnabled) {
      MaterialTheme.colorScheme.primary
    } else {
      MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

  val buttonText = stringResource(R.string.add_repeated_group_item, questionText)

  if (isQuestionnaireViewItemRepeatedGroup) {
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
        enabled = isEnabled,
        modifier = Modifier.testTag(ADD_REPEATED_GROUP_BUTTON_TAG),
        shape = RoundedCornerShape(4.dp),
        border = ButtonDefaults.outlinedButtonBorder(isEnabled).copy(brush = SolidColor(color)),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
      ) {
        Icon(
          painter = painterResource(R.drawable.add_24px),
          contentDescription = ADD_REPEATED_GROUP_BUTTON_TAG,
          tint = color,
        )
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        Text(
          text = buttonText,
          color = color,
        )
      }
    }
  }
}

const val ADD_REPEATED_GROUP_BUTTON_TAG = "addRepeatedGroupButton"
