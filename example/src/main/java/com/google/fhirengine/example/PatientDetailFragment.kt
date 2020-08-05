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

package com.google.fhirengine.example

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.fhirengine.FhirEngine

/**
 * A fragment representing a single Patient detail screen.
 * This fragment is contained in a [PatientDetailActivity].
 */
class PatientDetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.patient_detail, container, false)

        val recyclerView: RecyclerView = rootView.findViewById(R.id.observation_list)
        val adapter = ObservationItemRecyclerViewAdapter()
        recyclerView.adapter = adapter

        var patients: List<PatientListViewModel.PatientItem>? = null
        var observations: List<PatientListViewModel.ObservationItem>? = null
        val fhirEngine: FhirEngine = FhirApplication.fhirEngine(activity!!.applicationContext)
        var patient: PatientListViewModel.PatientItem? = null

        val viewModel: PatientListViewModel = ViewModelProvider(this, PatientListViewModelFactory(
            this.activity!!.application, fhirEngine
        ))
            .get(PatientListViewModel::class.java)
        viewModel.getSearchedPatients()?.observe(viewLifecycleOwner,
            Observer<List<PatientListViewModel.PatientItem>> {
                patients = it
                //adapter.submitList(it)
            }
        )
        viewModel.getObservations().observe(viewLifecycleOwner,
            Observer<List<PatientListViewModel.ObservationItem>> {
                observations = it
                adapter.submitList(it)
            })

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                patient = viewModel.getPatientItem(it.getString(ARG_ITEM_ID))
                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = patient
                    ?.name
            }
        }

        patient?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                rootView.findViewById<TextView>(R.id.patient_detail).setText(Html.fromHtml(it.html, Html.FROM_HTML_MODE_LEGACY))
                rootView.findViewById<TextView>(R.id.name).text = it.name
                rootView.findViewById<TextView>(R.id.dob).text = it.dob
                rootView.findViewById<TextView>(R.id.gender).text = it.phone
            }
            else {
                rootView.findViewById<TextView>(R.id.patient_detail).setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>"))
            }
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
