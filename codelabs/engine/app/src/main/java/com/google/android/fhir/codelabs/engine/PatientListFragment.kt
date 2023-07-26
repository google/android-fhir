/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.codelabs.engine

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.fhir.codelabs.engine.databinding.FragmentPatientListViewBinding
import com.google.android.fhir.sync.SyncJobStatus
import kotlinx.coroutines.launch

class PatientListFragment : Fragment() {
  private lateinit var searchView: SearchView
  private var _binding: FragmentPatientListViewBinding? = null
  private val binding
    get() = _binding!!

  private val viewModel: PatientListViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentPatientListViewBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initSearchView()
    initMenu()

    PatientItemRecyclerViewAdapter().apply {
      binding.patientList.adapter = this
      viewModel.liveSearchedPatients.observe(viewLifecycleOwner) { submitList(it) }
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        // Show the user a message when the sync is finished and then refresh the list of patients
        // on the UI by sending a search patient request
        viewModel.pollState.collect { handleSyncJobStatus(it) }
      }
    }
  }

  private fun handleSyncJobStatus(syncJobStatus: SyncJobStatus) {}

  private fun initMenu() {
    (requireActivity() as MenuHost).addMenuProvider(
      object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
          menuInflater.inflate(R.menu.menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
          return when (menuItem.itemId) {
            R.id.sync -> {
              viewModel.triggerOneTimeSync()
              true
            }
            R.id.update -> {
              viewModel.triggerUpdate()
              true
            }
            else -> false
          }
        }
      },
      viewLifecycleOwner,
      Lifecycle.State.RESUMED
    )
  }

  private fun initSearchView() {
    searchView = binding.search
    searchView.setOnQueryTextListener(
      object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String): Boolean {
          viewModel.searchPatientsByName(newText)
          return true
        }

        override fun onQueryTextSubmit(query: String): Boolean {
          viewModel.searchPatientsByName(query)
          return true
        }
      }
    )
    searchView.setOnQueryTextFocusChangeListener { view, focused ->
      if (!focused) {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
          .hideSoftInputFromWindow(view.windowToken, 0)
      }
    }
    requireActivity()
      .onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
          override fun handleOnBackPressed() {
            if (searchView.query.isNotEmpty()) {
              searchView.setQuery("", true)
            } else {
              isEnabled = false
              activity?.onBackPressed()
            }
          }
        }
      )
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
