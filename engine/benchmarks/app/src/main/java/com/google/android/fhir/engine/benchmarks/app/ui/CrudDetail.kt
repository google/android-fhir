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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.fhir.engine.benchmarks.app.CrudApiViewModel

@Composable
internal fun CrudDetail(
  viewModel: CrudApiViewModel,
  navigateToHome: () -> Unit,
) {
  val createUiState = viewModel.createStateFlow.collectAsStateWithLifecycle()
  val getUiState = viewModel.getStateFlow.collectAsStateWithLifecycle()
  val updateUiState = viewModel.updateStateFlow.collectAsStateWithLifecycle()
  val deleteUiState = viewModel.deleteStateFlow.collectAsStateWithLifecycle()

  DetailScaffold("CRUD", navigateToHome) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
      if (createUiState.value.isNotEmpty()) {
        Column(Modifier.padding(8.dp)) {
          Text("Create API")
          Spacer(Modifier.size(8.dp))
          FlowRow {
            createUiState.value.forEach {
              Column {
                Text("${it.first}")
                Text("${it.second}")
              }
              Spacer(Modifier.size(8.dp))
            }
          }
        }
      } else {
        // Show loading
      }

      if (getUiState.value.isNotEmpty()) {
        Column(Modifier.padding(8.dp)) {
          Text("Get API")
          Spacer(Modifier.size(8.dp))
          FlowRow {
            getUiState.value.forEach {
              Column {
                Text("${it.first}")
                Text("${it.second}")
              }
              Spacer(Modifier.size(8.dp))
            }
          }
        }
      } else {
        // Show loading
      }

      if (updateUiState.value.isNotEmpty()) {
        Column(Modifier.padding(8.dp)) {
          Text("Update API")
          Spacer(Modifier.size(8.dp))
          FlowRow {
            updateUiState.value.forEach {
              Column {
                Text("${it.first}")
                Text("${it.second}")
              }
              Spacer(Modifier.size(8.dp))
            }
          }
        }
      } else {
        // Show loading
      }

      if (deleteUiState.value.isNotEmpty()) {
        Column(Modifier.padding(8.dp)) {
          Text("Delete API")
          Spacer(Modifier.size(8.dp))
          FlowRow {
            deleteUiState.value.forEach {
              Column {
                Text("${it.first}")
                Text("${it.second}")
              }
              Spacer(Modifier.size(8.dp))
            }
          }
        }
      } else {
        // Show loading
      }
    }
  }
}
