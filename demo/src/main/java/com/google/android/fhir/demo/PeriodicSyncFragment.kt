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
import com.google.android.fhir.sync.LastSyncJobStatus
import com.google.android.fhir.sync.PeriodicSyncJobStatus

class PeriodicSyncFragment : Fragment() {
  private val syncFragmentViewModel: SyncFragmentViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    return inflater.inflate(R.layout.periodic_sync, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpActionBar()
    setHasOptionsMenu(true)
    view.findViewById<Button>(R.id.sync_now_button).setOnClickListener {
      launchAndRepeatStarted(
        { syncFragmentViewModel.pollPeriodicSyncJobStatus.collect(::periodicSyncJobStatus) },
      )
    }
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
      title = requireContext().getString(R.string.periodic_sync)
      setDisplayHomeAsUpEnabled(true)
    }
  }

  private fun periodicSyncJobStatus(periodicSyncJobStatus: PeriodicSyncJobStatus) {
    // Handle last sync job status and update UI
    periodicSyncJobStatus.lastSyncJobStatus?.let { lastStatus ->
      val lastSyncStatusValue = when (lastStatus) {
        is LastSyncJobStatus.Succeeded -> getString(R.string.last_sync_status, LastSyncJobStatus.Succeeded::class.java.simpleName)
        is LastSyncJobStatus.Failed -> getString(R.string.last_sync_status, LastSyncJobStatus.Failed::class.java.simpleName)
        else -> null
      }

      lastSyncStatusValue?.let { statusText ->
        requireView().findViewById<TextView>(R.id.last_sync_status).text = statusText
        requireView().findViewById<TextView>(R.id.last_sync_time).text = getString(
          R.string.last_sync_timestamp,
          syncFragmentViewModel.formatSyncTimestamp(lastStatus.timestamp)
        )
      }
    }

    // Set current sync status
    val currentSyncStatusTextView = requireView().findViewById<TextView>(R.id.current_sync_status)
    currentSyncStatusTextView.text = getString(
      R.string.current_status,
      periodicSyncJobStatus.currentSyncJobStatus::class.java.simpleName
    )

    // Update progress indicator visibility based on current sync status
    val syncIndicator = requireView().findViewById<ProgressBar>(R.id.sync_indicator)
    syncIndicator.visibility = if (periodicSyncJobStatus.currentSyncJobStatus is CurrentSyncJobStatus.Running) {
      View.VISIBLE
    } else {
      View.GONE
    }

    // Control the visibility of the current sync status text view
    currentSyncStatusTextView.visibility = when (periodicSyncJobStatus.currentSyncJobStatus) {
      is CurrentSyncJobStatus.Failed, is CurrentSyncJobStatus.Succeeded -> View.GONE
      else -> View.VISIBLE
    }
  }

}
