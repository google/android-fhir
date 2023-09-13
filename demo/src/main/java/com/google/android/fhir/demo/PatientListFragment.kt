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
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.demo.PatientListViewModel.PatientListViewModelFactory
import com.google.android.fhir.demo.databinding.FragmentPatientListBinding
import com.google.android.fhir.sync.SyncJobStatus
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import timber.log.Timber

class PatientListFragment : Fragment() {
  private lateinit var fhirEngine: FhirEngine
  private lateinit var patientListViewModel: PatientListViewModel
  private lateinit var searchView: SearchView
  private lateinit var topBanner: LinearLayout
  private lateinit var syncStatus: TextView
  private lateinit var syncPercent: TextView
  private lateinit var syncProgress: ProgressBar
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
    val recyclerView: RecyclerView = binding.patientListContainer.patientList
    val adapter = PatientItemRecyclerViewAdapter(this::onPatientItemClicked)
    recyclerView.adapter = adapter
    recyclerView.addItemDecoration(
      DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
        setDrawable(ColorDrawable(Color.LTGRAY))
      }
    )

    patientListViewModel.liveSearchedPatients.observe(viewLifecycleOwner) {
      Timber.d("Submitting ${it.count()} patient records")
      adapter.submitList(it)
    }

    patientListViewModel.patientCount.observe(viewLifecycleOwner) {
      binding.patientListContainer.patientCount.text = "$it Patient(s)"
    }

    searchView = binding.search
    topBanner = binding.syncStatusContainer.linearLayoutSyncStatus
    syncStatus = binding.syncStatusContainer.tvSyncingStatus
    syncPercent = binding.syncStatusContainer.tvSyncingPercent
    syncProgress = binding.syncStatusContainer.progressSyncing
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

    binding.apply {
      addPatient.setOnClickListener { onAddPatientClick() }
      addPatient.setColorFilter(Color.WHITE)
    }
    setHasOptionsMenu(true)
    (activity as MainActivity).setDrawerEnabled(true)

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

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        // hide the soft keyboard when the navigation drawer is shown on the screen.
        searchView.clearFocus()
        (requireActivity() as MainActivity).openNavigationDrawer()
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

  private fun fadeInTopBanner(state: SyncJobStatus) {
    if (topBanner.visibility != View.VISIBLE) {
      syncStatus.text = resources.getString(R.string.syncing).uppercase()
      syncPercent.text = ""
      syncProgress.progress = 0
      syncProgress.visibility = View.VISIBLE
      topBanner.visibility = View.VISIBLE
      val animation = AnimationUtils.loadAnimation(topBanner.context, R.anim.fade_in)
      topBanner.startAnimation(animation)
    } else if (state is SyncJobStatus.InProgress) {
      val progress =
        state
          .let { it.completed.toDouble().div(it.total) }
          .let { if (it.isNaN()) 0.0 else it }
          .times(100)
          .roundToInt()
      "$progress% ${state.syncOperation.name.lowercase()}ed".also { syncPercent.text = it }
      syncProgress.progress = progress
    }
  }

  private fun fadeOutTopBanner(state: SyncJobStatus) {
    if (state is SyncJobStatus.Finished) syncPercent.text = ""
    syncProgress.visibility = View.GONE

    if (topBanner.visibility == View.VISIBLE) {
      "${resources.getString(R.string.sync).uppercase()} ${state::class.java.simpleName.uppercase()}".also {
        syncStatus.text = it
      }

      val animation = AnimationUtils.loadAnimation(topBanner.context, R.anim.fade_out)
      topBanner.startAnimation(animation)
      Handler(Looper.getMainLooper()).postDelayed({ topBanner.visibility = View.GONE }, 2000)
    }
  }
}
