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

package com.google.android.fhir.catalog.ui.questionnaire

import android_fhir.catalog.generated.resources.Res
import android_fhir.catalog.generated.resources.close
import android_fhir.catalog.generated.resources.questionnaire_response_subtitle
import android_fhir.catalog.generated.resources.questionnaire_response_title
import android_fhir.catalog.generated.resources.questionnaire_submitted
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireResponseScreen(
  responseJson: String,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(Res.string.questionnaire_response_title),
            modifier = Modifier.fillMaxWidth(),
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(padding)
          .padding(horizontal = 24.dp)
          .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.Top,
    ) {
      Text(
        text = stringResource(Res.string.questionnaire_submitted),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 8.dp),
      )
      Text(
        text = stringResource(Res.string.questionnaire_response_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 16.dp),
      )
      HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))
      Text(
        text = responseJson,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Spacer(modifier = Modifier.height(24.dp))
      Button(
        onClick = onBackClick,
        modifier = Modifier.width(132.dp).align(Alignment.CenterHorizontally),
      ) {
        Text(stringResource(Res.string.close))
      }
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
