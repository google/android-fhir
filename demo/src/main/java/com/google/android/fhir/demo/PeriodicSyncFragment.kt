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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.launch

class PeriodicSyncFragment : Fragment() {
  private val periodicSyncViewModel: PeriodicSyncViewModel by viewModels()

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
    refreshPeriodicSynUi()
    setUpSyncButtons(view)
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

  private fun setUpSyncButtons(view: View) {
    val syncNowButton = view.findViewById<Button>(R.id.sync_now_button)
    val cancelSyncButton = view.findViewById<Button>(R.id.cancel_sync_button)
    syncNowButton.apply {
      setOnClickListener {
        periodicSyncViewModel.collectPeriodicSyncJobStatus()
        toggleButtonVisibility(hiddenButton = syncNowButton, visibleButton = cancelSyncButton)
        visibility = View.GONE
      }
    }
    cancelSyncButton.apply {
      setOnClickListener {
        periodicSyncViewModel.cancelPeriodicSyncJob()
        toggleButtonVisibility(hiddenButton = cancelSyncButton, visibleButton = syncNowButton)
        visibility = View.GONE
      }
    }
  }

  private fun toggleButtonVisibility(hiddenButton: View, visibleButton: View) {
    hiddenButton.visibility = View.GONE
    visibleButton.visibility = View.VISIBLE
  }

  private fun refreshPeriodicSynUi() {
    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        periodicSyncViewModel.uiStateFlow.collect { uiState ->
          uiState.lastSyncStatus?.let {
            requireView().findViewById<TextView>(R.id.last_sync_status).text = it
          }

          uiState.lastSyncTime?.let {
            requireView().findViewById<TextView>(R.id.last_sync_time).text = it
          }

          uiState.currentSyncStatus?.let {
            requireView().findViewById<TextView>(R.id.current_sync_status).text = it
          }

          val syncIndicator = requireView().findViewById<ProgressBar>(R.id.sync_indicator)
          val progressLabel = requireView().findViewById<TextView>(R.id.progress_percentage_label)

          if (uiState.progress != null) {
            syncIndicator.progress = uiState.progress
            syncIndicator.visibility = View.VISIBLE

            progressLabel.text = "${uiState.progress}%"
            progressLabel.visibility = View.VISIBLE
          } else {
            syncIndicator.progress = 0
            progressLabel.visibility = View.GONE
          }
        }
      }
    }
  }
}
