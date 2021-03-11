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
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.FhirEngine
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * A fragment representing a single Patient detail screen. This fragment is contained in a
 * [PatientDetailActivity].
 */
class PatientDetailFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val rootView = inflater.inflate(R.layout.patient_detail, container, false)

    val recyclerView: RecyclerView = rootView.findViewById(R.id.observation_list)
    val adapter = ObservationItemRecyclerViewAdapter()
    recyclerView.adapter = adapter

    var patient: PatientListViewModel.PatientItem? = null
    setupPatientData(rootView, patient)

    return rootView
  }

  private fun setupPatientData(view: View, patient: PatientListViewModel.PatientItem?) {
    if (patient != null) {
      view.findViewById<TextView>(R.id.patient_detail).text =
        HtmlCompat.fromHtml(patient.html, HtmlCompat.FROM_HTML_MODE_LEGACY)
      view.findViewById<TextView>(R.id.name).text = patient.name
      view.findViewById<TextView>(R.id.dob).text = patient.dob
      view.findViewById<TextView>(R.id.gender).text = patient.phone
    }
  }

  companion object {
    /** The fragment argument representing the patient item ID that this fragment represents. */
    const val ARG_ITEM_ID = "patient_item_id"
  }
}
