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
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.marginLeft
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.reference.databinding.PatientDetailBinding
import com.google.android.fhir.reference.databinding.PatientDetailsHeaderBinding
import com.google.android.fhir.reference.databinding.PatientListItemViewBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

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
    fhirEngine = FhirApplication.fhirEngine(requireContext())
    patientDetailsViewModel =
      ViewModelProvider(
        this,
        PatientDetailsViewModelFactory(requireActivity().application, fhirEngine, args.patientId)
      )
        .get(PatientDetailsViewModel::class.java)
    patientDetailsViewModel.livePatientData.observe(viewLifecycleOwner) { setupPatientData(it) }
    patientDetailsViewModel.livePatientObservation.observe(viewLifecycleOwner) {
      val observations  = it.map { Model(it.code, it.value) }
      if (observations.isNotEmpty()) {
        val container : LinearLayoutCompat = binding.container
        container.addView(MaterialTextView(container.context).apply {
          text = "Observations"
          setPadding(30, 0, 0, 0)
        })
        val detailsCard = MaterialCardView(ContextThemeWrapper(container.context, R.style.CardView)).apply {
          addView(LinearLayout(this.context).apply {
            orientation = LinearLayout.VERTICAL
            observations.forEach {
              addView(ChildItemViewHolder(PatientListItemViewBinding.inflate(LayoutInflater.from(this.context), this, false)).apply { bindTo(it)}.itemView)
              addView(lineView(this))
            }
          })
        }
        container.addView(detailsCard)
        val param  = detailsCard.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(30)
      }
    }
  }

  private fun setupPatientData(patientItem: PatientListViewModel.PatientItem?) {
    patientItem?.let { patient ->
      val container : LinearLayoutCompat = binding.container
      val detailsCard = MaterialCardView(ContextThemeWrapper(container.context, R.style.CardView)).apply {
        addView(LinearLayout(this.context).apply {
          orientation = LinearLayout.VERTICAL
          addView(HeaderItemViewHolder(PatientDetailsHeaderBinding.inflate(LayoutInflater.from(this.context), this, false)).apply {
            binding.screener.setOnClickListener { onAddScreenerClick() }
            bindTo(Model("Name", patient.name, type = 1))
          }.itemView)
          addView(lineView(this))
          listOf(
            Model("Mobile Number", patient.phone),
            Model("ID Number", patient.resourceId),
            Model("Address", "${patient.city}, ${patient.country} "),
            Model("Date of Birth", patient.dob),
            Model("Gender", patient.gender.capitalize()),
          ).forEach {
            addView(ChildItemViewHolder(PatientListItemViewBinding.inflate(LayoutInflater.from(this.context), this, false)).apply { bindTo(it)}.itemView)
            addView(lineView(this))
          }
        })
      }
      container.addView(detailsCard)
      val param  = detailsCard.layoutParams as ViewGroup.MarginLayoutParams
      param.setMargins(30)
      (requireActivity() as AppCompatActivity).supportActionBar?.apply {
        title = "Patient Card"
        setDisplayHomeAsUpEnabled(true)
      }
    }
  }

  private fun lineView(container: ViewGroup) : View {
    return View(container.context).apply {
      setBackgroundColor(Color.LTGRAY)
      minimumWidth = ViewGroup.LayoutParams.MATCH_PARENT
      minimumHeight = 2
    }
  }

  private fun onAddScreenerClick() {
    findNavController()
      .navigate(PatientDetailsFragmentDirections.actionPatientDetailsToScreenEncounterFragment())
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
