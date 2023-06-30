/*
 * Copyright 2022 Google LLC
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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.fhir.codelabs.engine.databinding.FragmentPatientListBinding
import com.google.android.fhir.sync.SyncJobStatus
import kotlinx.coroutines.launch

class PatientListFragment : Fragment() {
  private lateinit var searchView: SearchView
  private var _binding: FragmentPatientListBinding? = null
  private val binding
    get() = _binding!!

  private val viewModel: MainActivityViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentPatientListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
    }
    val adapter = PatientItemRecyclerViewAdapter()
    binding.patientList.apply {
      this.adapter = adapter
      addItemDecoration(
        DividerItemDecoration(
          requireContext(),
          DividerItemDecoration.VERTICAL
        ).apply {
          setDrawable(ColorDrawable(Color.LTGRAY))
        })
    }
    initSearchView()
    initMenu()

    viewModel.liveSearchedPatients.observe(viewLifecycleOwner) {
      adapter.submitList(it)
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        // Show the user a message when the sync is finished and then refresh the list of patients
        // on the UI by sending a search patient request
        viewModel.pollState.collect {
          when (it) {
            is SyncJobStatus.Finished -> {
              Toast.makeText(
                requireContext(),
                "Sync Finished",
                Toast.LENGTH_SHORT
              ).show()
              viewModel.searchPatientsByName("")
            }

            else -> {}
          }

        }
      }
    }
  }

  private fun initMenu() {
    (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
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
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
        // hide soft keyboard
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
