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

package com.example.sdckmpdemo

import android_fhir.sdc_kmp_demo.generated.resources.Res
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.fhir.datacapture.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
  onBackClick: () -> Unit,
  navigateToResponse: (suspend () -> QuestionnaireResponse) -> Unit,
) {
  var questionnaireJson by remember { mutableStateOf<String?>(null) }

  // Load questionnaire JSON from resources
  LaunchedEffect(Unit) {
    questionnaireJson = Res.readBytes("files/sample-questionnaire.json").decodeToString()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Fill Questionnaire") },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      questionnaireJson?.let { json ->
        Questionnaire(
          questionnaireJson = json,
          showSubmitButton = true,
          showCancelButton = true,
          showReviewPage = true,
          onSubmit = { response -> navigateToResponse(response) },
          onCancel = {
            // Navigate back on cancel
            onBackClick()
          },
        )
      }
        ?: run { Text("Loading questionnaire...") }
    }
  }
}
