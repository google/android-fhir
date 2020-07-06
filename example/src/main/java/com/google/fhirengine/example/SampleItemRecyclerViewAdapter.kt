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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.fhirengine.example.data.SamplePatients

/**
 * UI Controller helper class to monitor Patient viewmodel and display list of patients, with
 * possibly patient details, on a two pane device.
 */
class SampleItemRecyclerViewAdapter(
  private val itemOnClickListener: View.OnClickListener
) : ListAdapter<SamplePatients.PatientItem, PatientItemViewHolder>(PatientItemDiffCallback()) {

    class PatientItemDiffCallback : DiffUtil.ItemCallback<SamplePatients.PatientItem>() {
        override fun areItemsTheSame(oldItem: SamplePatients.PatientItem,
            newItem: SamplePatients.PatientItem): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: SamplePatients.PatientItem,
            newItem: SamplePatients.PatientItem): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientItemViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.patient_list_item, parent, false)
        return PatientItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientItemViewHolder, position: Int) {
        val item = currentList[position]
        holder.bindTo(item, itemOnClickListener)
    }
}

class PatientItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val idView: TextView = itemView.findViewById(R.id.id_patient_number)
    private val nameView: TextView = itemView.findViewById(R.id.name)
    private val genderView: TextView = itemView.findViewById(R.id.gender)
    private val dobView: TextView = itemView.findViewById(R.id.dob)

    fun bindTo(patientItem: SamplePatients.PatientItem, onClickListener: View.OnClickListener) {
        this.idView.text = patientItem.id
        this.nameView.text = patientItem.name
        this.genderView.text = patientItem.gender
        this.dobView.text = patientItem.dob

        with(this.itemView) {
            tag = patientItem
            setOnClickListener(onClickListener)
        }
    }
}