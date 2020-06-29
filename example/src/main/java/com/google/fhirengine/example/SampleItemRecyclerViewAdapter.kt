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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.fhirengine.example.data.SamplePatients

/**
 * UI Controller helper class to monitor Patient viewmodel and display list of patients, with
 * possibly patient details, on a two pane device.
 */
class SampleItemRecyclerViewAdapter(
  private val parentActivity: SamplePatientListActivity,
  viewModel: PatientListViewModel,
  private val twoPane: Boolean
) :
        RecyclerView.Adapter<SampleItemRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    private lateinit var patientValues: List<SamplePatients.PatientItem>

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as SamplePatients.PatientItem
            if (twoPane) {
                val fragment = SamplePatientDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(SamplePatientDetailFragment.ARG_ITEM_ID, item.id)
                    }
                }
                parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.samplepatient_detail_container, fragment)
                        .commit()
            } else {
                val intent = Intent(v.context, SamplePatientDetailActivity::class.java).apply {
                    putExtra(SamplePatientDetailFragment.ARG_ITEM_ID, item.id)
                }
                v.context.startActivity(intent)
            }
        }

        // Trigger value update in the view holder when underlying viewmodel changes.
        viewModel.getPatients().observe(
                parentActivity, Observer<List<SamplePatients.PatientItem>> { patients ->
            patientValues = patients
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.patient_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = patientValues[position]
        holder.idView.text = item.id
        holder.nameView.text = item.name
        holder.genderView.text = item.gender
        holder.dobView.text = item.dob

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = patientValues.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.id_text)
        val nameView: TextView = view.findViewById(R.id.name)
        val genderView: TextView = view.findViewById(R.id.gender)
        val dobView: TextView = view.findViewById(R.id.dob)
    }
}
