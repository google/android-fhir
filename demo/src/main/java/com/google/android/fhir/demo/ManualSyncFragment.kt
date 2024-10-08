/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.fhir.demo.extensions.launchAndRepeatStarted
import com.google.android.fhir.sync.CurrentSyncJobStatus

class ManualSyncFragment : Fragment() {
  private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    return inflater.inflate(R.layout.manual_sync, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<Button>(R.id.sync_now_button).setOnClickListener {
      mainActivityViewModel.triggerOneTimeSync()
    }
    observeLastSyncTime()
    launchAndRepeatStarted(
      { mainActivityViewModel.pollState.collect(::currentSyncJobStatus) },
    )
  }

  private fun currentSyncJobStatus(currentSyncJobStatus: CurrentSyncJobStatus) {
    requireView().findViewById<TextView>(R.id.current_status).text =
      getString(R.string.current_status, currentSyncJobStatus::class.java.simpleName)

    // Update progress indicator visibility and handle status-specific actions
    val syncIndicator = requireView().findViewById<ProgressBar>(R.id.sync_indicator)
    when (currentSyncJobStatus) {
      is CurrentSyncJobStatus.Running -> {
        syncIndicator.visibility = View.VISIBLE
      }
      is CurrentSyncJobStatus.Succeeded -> {
        syncIndicator.visibility = View.GONE
        mainActivityViewModel.updateLastSyncTimestamp(currentSyncJobStatus.timestamp)
      }
      is CurrentSyncJobStatus.Failed,
      is CurrentSyncJobStatus.Enqueued,
      is CurrentSyncJobStatus.Cancelled,
      is CurrentSyncJobStatus.Blocked, -> {
        syncIndicator.visibility = View.GONE
      }
    }
  }

  private fun observeLastSyncTime() {
    mainActivityViewModel.lastSyncTimestampLiveData.observe(viewLifecycleOwner) {
      requireView().findViewById<TextView>(R.id.lastSyncTime).text = it
    }
  }
}
