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

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.reference.databinding.PatientListItemViewBinding
import java.time.LocalDate
import java.time.Period
import org.hl7.fhir.utilities.DateTimeUtil

class PatientItemViewHolder(private val binding: PatientListItemViewBinding) :
  RecyclerView.ViewHolder(binding.root) {
  private val statusView: ImageView = binding.status
  private val nameView: TextView = binding.name
  private val ageView: TextView = binding.age
  private val idView: TextView = binding.id

  fun bindTo(
    patientItem: PatientListViewModel.PatientItem,
    onItemClicked: (PatientListViewModel.PatientItem) -> Unit
  ) {
//    this.idView.text = patientItem.id
    this.nameView.text = patientItem.name
    this.ageView.text = "${Period.between(
      LocalDate.parse(patientItem.dob),
      LocalDate.now()
    ).years.let {  return@let if (it > 0) it else 1 }} years old"
    this.idView.text = "Id: #---${patientItem.resourceId.substring(patientItem.resourceId.length - 3)}"

    this.itemView.setOnClickListener { onItemClicked(patientItem) }
  }
}
