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

package com.google.android.fhir.engine.benchmarks.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScaffold(
  appBarTitle: String,
  navigateToHome: () -> Unit,
  content: @Composable () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(appBarTitle) },
        navigationIcon = {
          IconButton(onClick = navigateToHome) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              contentDescription = "Localized description",
            )
          }
        },
      )
    },
    modifier = Modifier.fillMaxSize().semantics { testTagsAsResourceId = true },
  ) { innerPadding ->
    Card(
      shape = RoundedCornerShape(10.dp),
      colors =
        CardDefaults.cardColors(
          containerColor = Color.White,
        ),
      modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
    ) {
      content()
    }
  }
}

@Preview
@Composable
fun PreviewDetailScaffold() {
  DetailScaffold("Preview", {}) {}
}
