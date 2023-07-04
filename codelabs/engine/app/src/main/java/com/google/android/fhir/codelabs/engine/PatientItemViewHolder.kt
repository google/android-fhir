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

package com.google.android.fhir.codelabs.engine

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.codelabs.engine.databinding.PatientListItemViewBinding
import org.hl7.fhir.r4.model.Patient

class PatientItemViewHolder(binding: PatientListItemViewBinding) :
  RecyclerView.ViewHolder(binding.root) {

  private val nameTextView: TextView = binding.name
  private val genderTextView: TextView = binding.gender
  private val cityTextView = binding.city

  fun bind(patientItem: Patient) {
    nameTextView.text =
      patientItem.name.first().let { it.given.joinToString(separator = " ") + " " + it.family }
    genderTextView.text = patientItem.gender.display
    cityTextView.text = patientItem.address.singleOrNull()?.city
  }
}
