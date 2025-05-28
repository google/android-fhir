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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.fhir.engine.benchmarks.app.SearchApiUiState
import com.google.android.fhir.engine.benchmarks.app.SearchApiViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun SearchApiDetail(viewModel: SearchApiViewModel, navigateToHome: () -> Unit) {
  val benchmarkProgressState = viewModel.benchmarkProgressStateFlow.collectAsStateWithLifecycle()
  val benchmarkProgressStateValue by remember { benchmarkProgressState }

  val searchApiUiState = viewModel.searchApiUiStateFlow.collectAsStateWithLifecycle()
  val searchApiUiStateValue by remember { searchApiUiState }

  DetailScaffold("Search API", navigateToHome) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      if (benchmarkProgressStateValue) {
        item {
          Text(
            text = "Loading \u2026",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
      items(searchApiUiStateValue, SearchApiUiState::first) {
        SearchBenchmarkResultView(it.first, it.second)
      }
    }
  }
}

@Composable
internal fun SearchBenchmarkResultView(name: String, duration: Duration) {
  Column(Modifier.padding(8.dp)) {
    Text(name)
    Text(
      "$duration",
      style = MaterialTheme.typography.displaySmall,
      fontFamily = FontFamily.Monospace,
    )
  }
}

@Preview(showBackground = true)
@Composable
internal fun PreviewSearchBenchmarkResultView() {
  SearchBenchmarkResultView("SearchCountAllResources", 3000.milliseconds)
}
