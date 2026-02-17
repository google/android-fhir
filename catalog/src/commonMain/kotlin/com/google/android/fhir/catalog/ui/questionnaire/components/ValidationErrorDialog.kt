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

package com.google.android.fhir.catalog.ui.questionnaire.components

import android_fhir.catalog.generated.resources.Res
import android_fhir.catalog.generated.resources.validation_error_item
import android_fhir.catalog.generated.resources.validation_error_message
import android_fhir.catalog.generated.resources.validation_error_title
import android_fhir.catalog.generated.resources.validation_fix_questions
import android_fhir.catalog.generated.resources.validation_submit_anyway
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource

/** Dialog shown when validation errors are found on questionnaire submission. */
@Composable
fun ValidationErrorDialog(
  invalidFields: List<String>,
  onDismiss: () -> Unit,
  onFixQuestions: () -> Unit,
  onSubmitAnyway: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(Res.string.validation_error_title)) },
    text = {
      Column {
        Text(stringResource(Res.string.validation_error_message))
        Spacer(modifier = Modifier.padding(4.dp))
        invalidFields.forEach { field ->
          Text(stringResource(Res.string.validation_error_item).replace("%s", field))
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onFixQuestions) {
        Text(stringResource(Res.string.validation_fix_questions))
      }
    },
    dismissButton = {
      TextButton(onClick = onSubmitAnyway) {
        Text(stringResource(Res.string.validation_submit_anyway))
      }
    },
  )
}
