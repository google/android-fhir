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

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.reference.databinding.PatientListItemViewBinding

class PatientItemViewHolder(private val binding: PatientListItemViewBinding) :
  RecyclerView.ViewHolder(binding.root) {
  private val idView: TextView = binding.idPatientNumber
  private val nameView: TextView = binding.name
  private val genderView: TextView = binding.gender
  private val dobView: TextView = binding.dob

  fun bindTo(
    patientItem: PatientListViewModel.PatientItem,
    onItemClicked: (PatientListViewModel.PatientItem) -> Unit
  ) {
    this.idView.text = patientItem.id
    this.nameView.text = patientItem.name
    this.genderView.text = patientItem.gender
    this.dobView.text = patientItem.dob

    this.itemView.setOnClickListener { onItemClicked(patientItem) }
  }
}
