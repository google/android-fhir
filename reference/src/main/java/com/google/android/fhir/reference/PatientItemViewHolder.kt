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

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  private val idView: TextView = itemView.findViewById(R.id.id_patient_number)
  private val nameView: TextView = itemView.findViewById(R.id.name)
  private val genderView: TextView = itemView.findViewById(R.id.gender)
  private val dobView: TextView = itemView.findViewById(R.id.dob)

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
