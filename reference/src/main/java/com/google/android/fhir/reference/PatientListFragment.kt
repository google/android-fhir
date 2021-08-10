/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.reference

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.reference.PatientListViewModel.PatientListViewModelFactory
import com.google.android.fhir.reference.data.FhirPeriodicSyncWorker
import com.google.android.fhir.reference.databinding.FragmentPatientListBinding
import com.google.android.fhir.sync.Sync
import com.google.android.material.snackbar.Snackbar

class PatientListFragment : Fragment() {
  private lateinit var fhirEngine: FhirEngine
  private lateinit var patientListViewModel: PatientListViewModel
  private lateinit var searchView: SearchView
  private var _binding: FragmentPatientListBinding? = null
  private val binding
    get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentPatientListBinding.inflate(inflater, container, false)
    val view = binding.root
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = requireActivity().title
      setDisplayHomeAsUpEnabled(false)
    }
    fhirEngine = FhirApplication.fhirEngine(requireContext())
    patientListViewModel =
      ViewModelProvider(
          this,
          PatientListViewModelFactory(requireActivity().application, fhirEngine)
        )
        .get(PatientListViewModel::class.java)
    val recyclerView: RecyclerView = binding.patientListContainer.patientList
    val adapter = PatientItemRecyclerViewAdapter(this::onPatientItemClicked)
    recyclerView.adapter = adapter

    patientListViewModel.liveSearchedPatients.observe(
      viewLifecycleOwner,
      {
        Log.d("PatientListActivity", "Submitting ${it.count()} patient records")
        adapter.submitList(it)
      }
    )
    patientListViewModel.patientCount.observe(
      viewLifecycleOwner,
      { Log.d("PatientListActivity", "$it Patient") }
    )

    patientListViewModel.patientCount.observe(
      viewLifecycleOwner,
      { binding.patientListContainer.patientCount.text = "$it Patient(s)" }
    )
    searchView = binding.search
    searchView.setOnQueryTextListener(
      object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String): Boolean {
          patientListViewModel.searchPatientsByName(newText)
          return true
        }

        override fun onQueryTextSubmit(query: String): Boolean {
          patientListViewModel.searchPatientsByName(query)
          return true
        }
      }
    )
    requireActivity()
      .onBackPressedDispatcher
      .addCallback(
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

    binding.apply { addPatient.setOnClickListener { onAddPatientClick() } }
    setHasOptionsMenu(true)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.list_options_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.sync_resources -> {
        Sync.oneTimeSync<FhirPeriodicSyncWorker>(requireContext())
        Snackbar.make(
            binding.patientListContainer.patientList,
            R.string.message_syncing,
            Snackbar.LENGTH_LONG
          )
          .show()
        true
      }
      else -> false
    }
  }

  private fun onPatientItemClicked(patientItem: PatientListViewModel.PatientItem) {
    findNavController()
      .navigate(PatientListFragmentDirections.navigateToProductDetail(patientItem.resourceId))
  }

  private fun onAddPatientClick() {
    findNavController()
      .navigate(PatientListFragmentDirections.actionPatientListToAddPatientFragment())
  }
}
