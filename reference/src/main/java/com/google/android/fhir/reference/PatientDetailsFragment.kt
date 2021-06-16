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
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.reference.databinding.PatientDetailBinding

/**
 * A fragment representing a single Patient detail screen. This fragment is contained in a
 * [MainActivity].
 */
class PatientDetailsFragment : Fragment() {
  private lateinit var fhirEngine: FhirEngine
  private lateinit var patientDetailsViewModel: PatientDetailsViewModel
  private val args: PatientDetailsFragmentArgs by navArgs()
  private var _binding: PatientDetailBinding? = null
  private val binding
    get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = PatientDetailBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val recyclerView: RecyclerView = binding.observationList.observationList
    val adapter = ObservationItemRecyclerViewAdapter()
    recyclerView.adapter = adapter
    fhirEngine = FhirApplication.fhirEngine(requireContext())
    patientDetailsViewModel =
      ViewModelProvider(
          this,
          PatientDetailsViewModelFactory(requireActivity().application, fhirEngine, args.patientId)
        )
        .get(PatientDetailsViewModel::class.java)
    patientDetailsViewModel.livePatientData.observe(viewLifecycleOwner) {
      setupPatientData(view, it)
    }
    patientDetailsViewModel.livePatientObservation.observe(viewLifecycleOwner) {
      adapter.submitList(it)
    }
  }

  private fun setupPatientData(view: View, patient: PatientListViewModel.PatientItem?) {
    patient?.let {
      binding.patientDetail.text = HtmlCompat.fromHtml(it.html, HtmlCompat.FROM_HTML_MODE_LEGACY)
      binding.patientListItem.name.text = it.name
      binding.patientListItem.dob.text = it.dob
      binding.patientListItem.gender.text = it.phone

      (requireActivity() as AppCompatActivity).supportActionBar?.apply {
        title = it.name
        setDisplayHomeAsUpEnabled(true)
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  companion object {
    /** The fragment argument representing the patient item ID that this fragment represents. */
    const val ARG_ITEM_ID = "patient_item_id"
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
