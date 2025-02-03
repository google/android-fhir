/*
 * Copyright 2024-2025 Google LLC
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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.fhir.demo.extensions.launchAndRepeatStarted
import com.google.android.fhir.sync.CurrentSyncJobStatus
import timber.log.Timber

class SyncFragment : Fragment() {
  private val syncFragmentViewModel: SyncFragmentViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    return inflater.inflate(R.layout.sync, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpActionBar()
    setHasOptionsMenu(true)
    view.findViewById<Button>(R.id.sync_now_button).setOnClickListener {
      syncFragmentViewModel.triggerOneTimeSync()
    }
    view.findViewById<Button>(R.id.cancel_sync_button).setOnClickListener {
      syncFragmentViewModel.cancelOneTimeSyncWork()
    }
    observeLastSyncTime()
    launchAndRepeatStarted(
      { syncFragmentViewModel.pollState.collect(::currentSyncJobStatus) },
    )
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      else -> false
    }
  }

  private fun setUpActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = requireContext().getString(R.string.sync)
      setDisplayHomeAsUpEnabled(true)
    }
  }

  private fun currentSyncJobStatus(currentSyncJobStatus: CurrentSyncJobStatus) {
    Timber.d("currentSyncJobStatus: $currentSyncJobStatus")
    // Update status text
    val statusTextView = requireView().findViewById<TextView>(R.id.current_status)
    statusTextView.text =
      getString(R.string.current_status, currentSyncJobStatus::class.java.simpleName)

    // Get views once to avoid repeated lookups
    val syncIndicator = requireView().findViewById<ProgressBar>(R.id.sync_indicator)
    val syncNowButton = requireView().findViewById<Button>(R.id.sync_now_button)
    val cancelSyncButton = requireView().findViewById<Button>(R.id.cancel_sync_button)

    // Update view states based on sync status
    when (currentSyncJobStatus) {
      is CurrentSyncJobStatus.Running -> {
        syncIndicator.visibility = View.VISIBLE
        syncNowButton.visibility = View.GONE
        cancelSyncButton.visibility = View.VISIBLE
      }
      is CurrentSyncJobStatus.Succeeded -> {
        syncIndicator.visibility = View.GONE
        syncFragmentViewModel.updateLastSyncTimestamp(currentSyncJobStatus.timestamp)
        syncNowButton.visibility = View.VISIBLE
        cancelSyncButton.visibility = View.GONE
      }
      is CurrentSyncJobStatus.Failed,
      is CurrentSyncJobStatus.Cancelled, -> {
        syncIndicator.visibility = View.GONE
        syncNowButton.visibility = View.VISIBLE
        cancelSyncButton.visibility = View.GONE
      }
      is CurrentSyncJobStatus.Enqueued,
      is CurrentSyncJobStatus.Blocked, -> {
        syncIndicator.visibility = View.GONE
        syncNowButton.visibility = View.GONE
        cancelSyncButton.visibility = View.VISIBLE
      }
    }
  }

  private fun observeLastSyncTime() {
    syncFragmentViewModel.lastSyncTimestampLiveData.observe(viewLifecycleOwner) {
      requireView().findViewById<TextView>(R.id.lastSyncTime).text =
        getString(R.string.last_sync_timestamp, it)
    }
  }
}
