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

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.reference.databinding.PatientDetailBinding
import com.google.android.fhir.reference.databinding.PatientDetailsHeaderBinding
import com.google.android.fhir.reference.databinding.PatientListItemViewBinding
import com.google.android.material.textview.MaterialTextView
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    fhirEngine = FhirApplication.fhirEngine(requireContext())
    patientDetailsViewModel =
      ViewModelProvider(
          this,
          PatientDetailsViewModelFactory(requireActivity().application, fhirEngine, args.patientId)
        )
        .get(PatientDetailsViewModel::class.java)
    patientDetailsViewModel.livePatientData.observe(viewLifecycleOwner) { setupPatientData(it) }
    patientDetailsViewModel.livePatientObservation.observe(viewLifecycleOwner) {
      setupObservationsData(it)
    }
    patientDetailsViewModel.livePatientCondition.observe(viewLifecycleOwner) {
      setupConditionData(it)
    }

    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = "Patient Card"
      setDisplayHomeAsUpEnabled(true)
    }

    observeRiskAssessment()
    patientDetailsViewModel.getPatientRiskAssessment()
  }

  private fun renderCard(container: LinearLayout, properties: List<PatientProperty>) {
    if (properties.isEmpty()) return
    container.apply { properties.forEach { addPropertyView(it) } }
  }

  private fun setupObservationsData(observationItems: List<PatientListViewModel.ObservationItem>) {
    val observations =
      observationItems.map { observation -> PatientProperty(observation.code, observation.value) }
    binding.patientObservations.header.text = resources.getText(R.string.header_observation)
    renderCard(binding.patientObservations.propertiesContainer, observations)
    binding.patientObservations.container.visibility =
      if (observations.isEmpty()) View.GONE else View.VISIBLE
  }

  private fun setupConditionData(conditionItems: List<PatientListViewModel.ConditionItem>) {
    val conditions =
      conditionItems.map { condition -> PatientProperty(condition.code, condition.value) }
    binding.patientConditions.header.text = resources.getText(R.string.header_conditions)
    renderCard(binding.patientConditions.propertiesContainer, conditions)
    binding.patientConditions.container.visibility =
      if (conditions.isEmpty()) View.GONE else View.VISIBLE
  }

  private fun setupPatientData(patientItem: PatientListViewModel.PatientItem?) {
    patientItem?.let { patient ->
      val patientDetailsCard = binding.patientDetailsCard
      patientDetailsCard.header.visibility = View.GONE
      patientDetailsCard.propertiesContainer.apply {
        addHeader(patient)
        listOf(
          PatientProperty("Mobile Number", patient.phone),
          PatientProperty("ID Number", patient.resourceId),
          PatientProperty("Address", "${patient.city}, ${patient.country} "),
          PatientProperty("Date of Birth", patient.dob),
          PatientProperty("Gender", patient.gender.capitalize(Locale.ROOT)),
        )
          .forEach { addPropertyView(it) }
      }
    }
  }

  private fun ViewGroup.addHeader(patient: PatientListViewModel.PatientItem) {
    addView(
      PatientDetailsHeaderBinding.inflate(LayoutInflater.from(this.context), this, false)
        .apply {
          screener.setOnClickListener { onAddScreenerClick() }
          title.text = patient.name
        }
        .root
    )
    addView(lineView(this))
  }

  private fun ViewGroup.addPropertyView(patientProperty: PatientProperty) {
    addView(
      PatientListItemViewBinding.inflate(LayoutInflater.from(this.context), this, false)
        .apply { bind(patientProperty) }
        .root
    )
    addView(lineView(this))
  }

  private fun PatientListItemViewBinding.bind(patientProperty: PatientProperty) {
    name.text = patientProperty.header
    age.text = patientProperty.value
    status.visibility = View.GONE
    id.visibility = View.GONE
  }

  private fun lineView(container: ViewGroup): View {
    return View(container.context).apply {
      setBackgroundColor(Color.LTGRAY)
      minimumWidth = ViewGroup.LayoutParams.MATCH_PARENT
      minimumHeight = 2
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

  private fun observeRiskAssessment() {
    patientDetailsViewModel.livePatientRiskAssessment.observe(viewLifecycleOwner) {
      lifecycleScope.launch {
        delay(100)
        view?.findViewById<LinearLayout>(R.id.patient_container)?.apply {
          setBackgroundColor(it.backgroundColor)
        }
        view?.findViewById<MaterialTextView>(R.id.status_value)?.apply {
          text = it.riskStatus
          setBackgroundColor(it.riskStatusColor)
        }
        view?.findViewById<MaterialTextView>(R.id.last_contact_value)?.apply {
          text = it.lastContacted
        }
      }
    }
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

  /** Model to store the properties displayed on the patient details page. */
  data class PatientProperty(
    val header: String,
    val value: String,
  )
}
