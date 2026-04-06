/*
 * Copyright 2026 Google LLC
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
import android_fhir.datacapture_kmp.generated.resources.questionnaire_validation_error_fix_button_text
import android_fhir.datacapture_kmp.generated.resources.questionnaire_validation_error_headline
import android_fhir.datacapture_kmp.generated.resources.questionnaire_validation_error_item_text_with_bullet
import android_fhir.datacapture_kmp.generated.resources.questionnaire_validation_error_submit_button_text
import android_fhir.datacapture_kmp.generated.resources.questionnaire_validation_error_supporting_text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.stringResource

/** Dialog shown when validation errors are found on questionnaire submission. */
@Composable
fun ValidationErrorDialog(
  invalidFields: List<AnnotatedString>,
  showSubmitAnyway: Boolean = true,
  onDismiss: () -> Unit,
  onFixQuestions: () -> Unit,
  onSubmitAnyway: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(Res.string.questionnaire_validation_error_headline)) },
    text = {
      Column {
        Text(stringResource(Res.string.questionnaire_validation_error_supporting_text))
        Spacer(modifier = Modifier.padding(4.dp))
        invalidFields.forEach { field ->
          Text(
            stringResource(Res.string.questionnaire_validation_error_item_text_with_bullet, field),
          )
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onFixQuestions) {
        Text(stringResource(Res.string.questionnaire_validation_error_fix_button_text))
      }
    },
    dismissButton =
      if (showSubmitAnyway) {
        {
          TextButton(onClick = onSubmitAnyway) {
            Text(stringResource(Res.string.questionnaire_validation_error_submit_button_text))
          }
        }
      } else {
        null
      },
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
  )
}
