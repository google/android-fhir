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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.fhir.engine.benchmarks.app.BenchmarkDuration
import com.google.android.fhir.engine.benchmarks.app.BenchmarkResult
import com.google.android.fhir.engine.benchmarks.app.CrudApiViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun CrudDetail(
  viewModel: CrudApiViewModel,
  navigateToHome: () -> Unit,
) {
  val crudUiState = viewModel.crudStateFlow.collectAsStateWithLifecycle()
  val crudUiStateValue by remember { crudUiState }

  DetailScaffold("CRUD", navigateToHome) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      CrudBenchmarkResultView("FhirEngine#Create", crudUiStateValue.create)
      CrudBenchmarkResultView("FhirEngine#Get", crudUiStateValue.read)
      CrudBenchmarkResultView("FhirEngine#Update", crudUiStateValue.update)
      CrudBenchmarkResultView("FhirEngine#Delete", crudUiStateValue.delete)
    }
  }
}

@Composable
internal fun CrudBenchmarkResultView(headline: String, result: BenchmarkResult) {
  Column(Modifier.padding(8.dp)) {
    Text(headline, style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.size(8.dp))
    when (result) {
      is BenchmarkDuration -> {
        Text("Takes ~${result.duration} for ${result.size} resources")
        Text("Average: ~${result.averageDuration}")
      }
      is BenchmarkResult.Nil -> {
        Text("Waiting for results\u2026")
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
internal fun PreviewCrudBenchmarkResultView() {
  CrudBenchmarkResultView("FhirEngine#Create", BenchmarkDuration(20, 1008.milliseconds))
}

@Preview(showBackground = true)
@Composable
internal fun PreviewCrudNilBenchmarkResultView() {
  CrudBenchmarkResultView("FhirEngine#Create", BenchmarkResult.Nil)
}
