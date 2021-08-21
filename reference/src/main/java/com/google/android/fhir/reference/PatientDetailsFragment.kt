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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
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
  ): View {
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
    patientDetailsViewModel.livePatientData.observe(viewLifecycleOwner) { setupPatientData(it) }
    patientDetailsViewModel.livePatientObservation.observe(viewLifecycleOwner) {
      adapter.submitList(it)
    }
    binding.apply { addScreener.setOnClickListener { onAddScreenerClick() } }
  }

  private fun setupPatientData(patientItem: PatientListViewModel.PatientItem?) {
    patientItem?.let { patient ->
      binding.patientDetail.apply {
        text = HtmlCompat.fromHtml(patient.html, HtmlCompat.FROM_HTML_MODE_LEGACY)
      }
      binding.patientListItem.apply {
        title.text = patient.name
        gender.text = patient.gender
        dob.text = patient.dob
        phoneNumber.text = patient.phone
        city.text = patient.city
        country.text = patient.country
        isActive.text = patient.isActive.toString()
      }
      (requireActivity() as AppCompatActivity).supportActionBar?.apply {
        title = patient.name
        setDisplayHomeAsUpEnabled(true)
      }
    }
  }

  private fun onAddScreenerClick() {
    findNavController()
      .navigate(
        PatientDetailsFragmentDirections.actionPatientDetailsToScreenEncounterFragment(
          args.patientId
        )
      )
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.details_options_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      R.id.menu_patient_edit -> {
        findNavController()
          .navigate(PatientDetailsFragmentDirections.navigateToEditPatient(args.patientId))
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
