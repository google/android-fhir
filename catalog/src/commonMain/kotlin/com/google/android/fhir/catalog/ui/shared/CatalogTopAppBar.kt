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

package com.google.android.fhir.catalog.ui.shared

import android_fhir.catalog.generated.resources.Res
import android_fhir.catalog.generated.resources.open_questionnaire
import android_fhir.catalog.generated.resources.toolbar_text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogTopAppBar(
  onOpenQuestionnaireAction: () -> Unit = {},
) {
  var showMenu by remember { mutableStateOf(false) }

  CenterAlignedTopAppBar(
    title = {
      val text = stringResource(Res.string.toolbar_text)
      val lines = text.split("\n")
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (lines.size > 1) {
          Text(
            text = lines.first().trim(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = lines.last().trim(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        } else {
          Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
      }
    },
    actions = {
      Box {
        IconButton(onClick = { showMenu = !showMenu }) {
          Icon(Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
          DropdownMenuItem(
            text = { Text(stringResource(Res.string.open_questionnaire)) },
            onClick = {
              showMenu = false
              onOpenQuestionnaireAction()
            },
          )
        }
      }
    },
  )
}
