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
import android_fhir.catalog.generated.resources.options
import android_fhir.catalog.generated.resources.show_error_state
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource

/** Action button that toggles error state via a bottom sheet. */
@Composable
fun ErrorStateToggleAction(
  isErrorState: Boolean,
  onToggle: (Boolean) -> Unit,
) {
  var showBottomSheet by remember { mutableStateOf(false) }

  IconButton(onClick = { showBottomSheet = true }) {
    Icon(Icons.Outlined.Settings, contentDescription = "Options")
  }

  if (showBottomSheet) {
    ErrorStateBottomSheet(
      isErrorState = isErrorState,
      onDismiss = { showBottomSheet = false },
      onToggle = { onToggle(!isErrorState) },
    )
  }
}

/** Bottom sheet for toggling error state display. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorStateBottomSheet(
  isErrorState: Boolean,
  onDismiss: () -> Unit,
  onToggle: () -> Unit,
) {
  ModalBottomSheet(onDismissRequest = onDismiss) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = stringResource(Res.string.options),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 24.dp),
      )
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
          CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
          ),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Checkbox(
            checked = isErrorState,
            onCheckedChange = { onToggle() },
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = stringResource(Res.string.show_error_state),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }
      Spacer(modifier = Modifier.padding(bottom = 32.dp))
    }
  }
}
