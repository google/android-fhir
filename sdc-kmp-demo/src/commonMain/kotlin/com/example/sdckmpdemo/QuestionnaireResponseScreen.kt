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

package com.example.sdckmpdemo

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireResponseScreen(
  responseJson: String,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Questionnaire Response") },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { paddingValues ->
    Box(
      modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
    ) {
      Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
      ) {
        Text(
          text = "Submitted Response:",
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.padding(vertical = 16.dp),
        )

        Card(
          modifier = Modifier.fillMaxWidth(),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
          Text(
            text = responseJson,
            style =
              MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
              ),
            modifier = Modifier.padding(16.dp).horizontalScroll(rememberScrollState()),
          )
        }
      }
    }
  }
}
