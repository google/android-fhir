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

package com.google.android.fhir.demo

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.demo.PatientListViewModel.PatientListViewModelFactory
import com.google.android.fhir.demo.care.CareWorkflowExecutionStatus
import com.google.android.fhir.demo.care.CareWorkflowExecutionViewModel
import com.google.android.fhir.demo.databinding.FragmentPatientListBinding
import com.google.android.fhir.sync.SyncJobStatus
import kotlinx.coroutines.launch
import timber.log.Timber

class PatientListFragment : Fragment() {
  private lateinit var fhirEngine: FhirEngine
  private lateinit var patientListViewModel: PatientListViewModel
  private val careWorkflowExecutionViewModel by activityViewModels<CareWorkflowExecutionViewModel>()

  private lateinit var recyclerView: RecyclerView
  private lateinit var adapter: PatientItemRecyclerViewAdapter
  private lateinit var searchView: SearchView
  private lateinit var topBanner: LinearLayout
  private lateinit var syncStatus: TextView
  private lateinit var syncPercent: TextView
  private lateinit var syncProgress: ProgressBar
  private lateinit var careWorkflowExecutionStatusLayout: LinearLayout
  private lateinit var careWorkflowExecutionStatus: TextView
  private lateinit var careWorkflowExecutionImage: ImageView
  private var _binding: FragmentPatientListBinding? = null
  private val binding
    get() = _binding!!
  private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentPatientListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = resources.getString(R.string.title_patient_list)
      setDisplayHomeAsUpEnabled(true)
    }
    fhirEngine = FhirApplication.fhirEngine(requireContext())
    patientListViewModel =
      ViewModelProvider(
          this,
          PatientListViewModelFactory(requireActivity().application, fhirEngine)
        )
        .get(PatientListViewModel::class.java)

    setupRecyclerView()
    setupSearchView()
    observeTasksUpdate()
    setupSyncProgress()
    observePatients()

    binding.apply {
      addPatient.setOnClickListener { onAddPatientClick() }
      addPatient.setColorFilter(Color.WHITE)
    }
    setHasOptionsMenu(false)
    (activity as MainActivity).setDrawerEnabled(false)
  }

  private fun setupRecyclerView() {
    recyclerView = binding.patientListContainer.patientList
    adapter = PatientItemRecyclerViewAdapter(this::onPatientItemClicked)
    recyclerView.adapter = adapter
    recyclerView.addItemDecoration(
      DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
        setDrawable(ColorDrawable(Color.LTGRAY))
      }
    )
  }

  private fun onPatientItemClicked(patientItem: PatientListViewModel.PatientItem) {
    findNavController()
      .navigate(PatientListFragmentDirections.navigateToProductDetail(patientItem.resourceId))
  }

  private fun setupSearchView() {
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

  private fun setupSyncProgress() {
    topBanner = binding.syncStatusContainer.linearLayoutProgressStatus
    syncStatus = binding.syncStatusContainer.progressStatus
    syncPercent = binding.syncStatusContainer.progressPercent
    syncProgress = binding.syncStatusContainer.progressBar
    lifecycleScope.launch {
      mainActivityViewModel.pollState.collect {
        Timber.d("onViewCreated: pollState Got status $it")
        when (it) {
          is SyncJobStatus.Started -> {
            Timber.i("Sync: ${it::class.java.simpleName}")
            fadeInTopBanner(it)
          }
          is SyncJobStatus.InProgress -> {
            Timber.i("Sync: ${it::class.java.simpleName} with data $it")
            fadeInTopBanner(it)
          }
          is SyncJobStatus.Finished -> {
            Timber.i("Sync: ${it::class.java.simpleName} at ${it.timestamp}")
            patientListViewModel.searchPatientsByName(searchView.query.toString().trim())
            mainActivityViewModel.updateLastSyncTimestamp()
            fadeOutTopBanner(it)
          }
          is SyncJobStatus.Failed -> {
            Timber.i("Sync: ${it::class.java.simpleName} at ${it.timestamp}")
            patientListViewModel.searchPatientsByName(searchView.query.toString().trim())
            mainActivityViewModel.updateLastSyncTimestamp()
            fadeOutTopBanner(it)
          }
          else -> {
            Timber.i("Sync: Unknown state.")
            patientListViewModel.searchPatientsByName(searchView.query.toString().trim())
            mainActivityViewModel.updateLastSyncTimestamp()
            fadeOutTopBanner(it)
          }
        }
      }
    }
  }

  private fun fadeInTopBanner(state: SyncJobStatus) {
    // workaround
    careWorkflowExecutionStatusLayout.setBackgroundColor(
      resources.getColor(R.color.workflow_running)
    )
    careWorkflowExecutionStatus.text = resources.getString(R.string.syncing)
    careWorkflowExecutionImage.setImageResource(R.drawable.ic_baseline_sync_24)
  }

  private fun fadeOutTopBanner(state: SyncJobStatus) {
    // workaround

    careWorkflowExecutionStatusLayout.setBackgroundColor(
      resources.getColor(R.color.workflow_finished)
    )
    careWorkflowExecutionStatus.text =
      "${resources.getString(R.string.sync)} ${state::class.java.simpleName}"
    careWorkflowExecutionImage.setImageResource(R.drawable.ic_check)
  }

  private fun observePatients() {
    patientListViewModel.liveSearchedPatients.observe(viewLifecycleOwner) {
      Timber.d("Submitting ${it.count()} patient records")
      adapter.submitList(it)
    }

    patientListViewModel.patientCount.observe(viewLifecycleOwner) {
      binding.patientListContainer.patientCount.text = "$it Patients"
    }
  }

  private fun observeTasksUpdate() {
    careWorkflowExecutionStatusLayout =
      binding.workflowExecutionStatusContainer.careWorkflowExecutionStatusLayout
    careWorkflowExecutionStatus =
      binding.workflowExecutionStatusContainer.careWorkflowExecutionStatus
    careWorkflowExecutionImage =
      binding.workflowExecutionStatusContainer.careWorkflowExecutionStatusImage
    lifecycleScope.launch {
      careWorkflowExecutionViewModel.patientFlowForCareWorkflowExecution.collect {
        val status = it.careWorkflowExecutionStatus
        if (status is CareWorkflowExecutionStatus.Finished) {
          if (status.total == status.completed) {
            patientListViewModel.searchPatientsByName(searchView.query.toString().trim())
          }
        }
        updateWorkflowExecutionBar(status)
      }
    }
  }

  private fun updateWorkflowExecutionBar(status: CareWorkflowExecutionStatus) {
    when (status) {
      is CareWorkflowExecutionStatus.Started -> {
        careWorkflowExecutionStatusLayout.setBackgroundColor(
          resources.getColor(R.color.workflow_running)
        )
        careWorkflowExecutionStatus.text = resources.getString(R.string.updating_tasks)
        careWorkflowExecutionImage.setImageResource(R.drawable.ic_baseline_sync_24)
      }
      is CareWorkflowExecutionStatus.InProgress -> {}
      is CareWorkflowExecutionStatus.Finished -> {
        if (status.completed == status.total) {
          careWorkflowExecutionStatusLayout.setBackgroundColor(
            resources.getColor(R.color.workflow_finished)
          )
          careWorkflowExecutionStatus.text = resources.getString(R.string.tasks_updated)
          careWorkflowExecutionImage.setImageResource(R.drawable.ic_check)
        }
      }
      is CareWorkflowExecutionStatus.Failed -> {}
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        // hide the soft keyboard when the navigation drawer is shown on the screen.
        searchView.clearFocus()
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      else -> false
    }
  }

  private fun onAddPatientClick() {
    findNavController()
      .navigate(PatientListFragmentDirections.actionPatientListToAddPatientFragment())
  }
}
