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

package com.google.android.fhir.engine.benchmarks.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.fhir.engine.benchmarks.app.BenchmarkSyncState
import com.google.android.fhir.engine.benchmarks.app.SyncApiViewModel
import com.google.android.fhir.sync.CurrentSyncJobStatus
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.fhir.sync.SyncOperation
import java.time.OffsetDateTime
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
internal fun SyncApiDetail(viewModel: SyncApiViewModel, navigateToHome: () -> Unit) {
  val downloadSyncBenchmarkState =
    viewModel.downloadBenchmarkSyncStateFlow.collectAsStateWithLifecycle()
  val downloadSyncBenchmarkStateValue by remember { downloadSyncBenchmarkState }

  val bundleUploadSyncBenchmarkState =
    viewModel.bundleUploadBenchmarkSyncStateFlow.collectAsStateWithLifecycle()
  val bundleUploadSyncBenchmarkStateValue by remember { bundleUploadSyncBenchmarkState }

  val perResourceChangeUploadSyncBenchmarkState =
    viewModel.perResourceChangeUploadBenchmarkSyncStateFlow.collectAsStateWithLifecycle()
  val perResourceChangeUploadSyncBenchmarkStateValue by remember {
    perResourceChangeUploadSyncBenchmarkState
  }

  DetailScaffold("Sync API", navigateToHome) {
    SyncApiView(
      downloadSyncBenchmarkStateValue,
      bundleUploadSyncBenchmarkStateValue,
      perResourceChangeUploadSyncBenchmarkStateValue,
    )
  }
}

@Composable
internal fun SyncApiView(
  downloadSynBenchmarkSyncState: BenchmarkSyncState,
  bundleUploadSynBenchmarkSyncState: BenchmarkSyncState,
  perResourceChangeUploadBenchmarkSyncState: BenchmarkSyncState,
) {
  Column(
    Modifier.fillMaxSize().padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp),
  ) {
    SyncBenchmarkView("Download", downloadSynBenchmarkSyncState)
    SyncBenchmarkView("Upload (Transaction Bundle)", bundleUploadSynBenchmarkSyncState)
    SyncBenchmarkView(
      "Upload (Individual - Per Resource)",
      perResourceChangeUploadBenchmarkSyncState,
    )
  }
}

@Composable
internal fun SyncBenchmarkView(type: String, syncState: BenchmarkSyncState) {
  Column {
    FlowRow(
      itemVerticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(type, style = MaterialTheme.typography.titleMedium)
      SyncProgressIndicator(syncState.syncStatus)
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      "${if (syncState.isComplete) syncState.benchmarkDuration else "-"}",
      style = MaterialTheme.typography.displaySmall,
      fontFamily = FontFamily.Monospace,
    )

    if (syncState.isComplete) {
      Text(
        "Completed: ${syncState.completedResources} resources",
        fontFamily = FontFamily.Monospace,
      )
    }
  }
}

@Composable
internal fun SyncProgressIndicator(currentSyncJobStatus: CurrentSyncJobStatus) {
  val status =
    when (currentSyncJobStatus) {
      is CurrentSyncJobStatus.Running -> "Running \u2026"
      is CurrentSyncJobStatus.Failed -> "Failed"
      is CurrentSyncJobStatus.Succeeded -> "Success"
      is CurrentSyncJobStatus.Blocked -> "Blocked"
      else -> "Waiting \u2026"
    }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    if (
      currentSyncJobStatus is CurrentSyncJobStatus.Running ||
        currentSyncJobStatus == CurrentSyncJobStatus.Enqueued
    ) {
      CircularProgressIndicator(modifier = Modifier.then(Modifier.size(16.dp)))
    }
    Text(status)
  }
}

@Preview(showBackground = true)
@Composable
internal fun PreviewSyncApiView() {
  SyncApiView(
    BenchmarkSyncState(benchmarkDuration = 20.milliseconds, completedResources = 20_000),
    BenchmarkSyncState(
      benchmarkDuration = 18.milliseconds,
      completedResources = 100,
      syncStatus =
        CurrentSyncJobStatus.Running(SyncJobStatus.InProgress(SyncOperation.UPLOAD, 100, 100)),
    ),
    BenchmarkSyncState(
      benchmarkDuration = 18.seconds,
      completedResources = 100,
      syncStatus = CurrentSyncJobStatus.Succeeded(OffsetDateTime.now()),
    ),
  )
}

@Preview(showBackground = true, widthDp = 200)
@Composable
internal fun PreviewSyncApiViewSmallWidth() {
  SyncApiView(
    BenchmarkSyncState(benchmarkDuration = 20.milliseconds, completedResources = 20_000),
    BenchmarkSyncState(
      benchmarkDuration = 18.milliseconds,
      completedResources = 100,
      syncStatus =
        CurrentSyncJobStatus.Running(SyncJobStatus.InProgress(SyncOperation.UPLOAD, 100, 100)),
    ),
    BenchmarkSyncState(
      benchmarkDuration = 18.seconds,
      completedResources = 100,
      syncStatus = CurrentSyncJobStatus.Succeeded(OffsetDateTime.now()),
    ),
  )
}

@Preview(showBackground = true)
@Composable
internal fun PreviewSyncProgressIndicator() {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    SyncProgressIndicator(CurrentSyncJobStatus.Enqueued)
    SyncProgressIndicator(
      CurrentSyncJobStatus.Running(SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, 100, 50)),
    )
    SyncProgressIndicator(CurrentSyncJobStatus.Failed(OffsetDateTime.now()))
    SyncProgressIndicator(CurrentSyncJobStatus.Succeeded(OffsetDateTime.now()))
    SyncProgressIndicator(CurrentSyncJobStatus.Cancelled)
    SyncProgressIndicator(CurrentSyncJobStatus.Blocked)
  }
}
