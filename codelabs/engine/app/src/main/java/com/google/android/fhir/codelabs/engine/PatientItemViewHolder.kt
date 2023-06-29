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

class PatientItemViewHolder(binding: PatientListItemViewBinding) :
  RecyclerView.ViewHolder(binding.root) {
  private val nameView: TextView = binding.name
  private val idView: TextView = binding.id

  fun bindTo(
    patientItem: MainActivityViewModel.PatientItem,
  ) {
    this.nameView.text = patientItem.name
    this.idView.text = "Id: #---${getTruncatedId(patientItem)}"
  }

  /** The new ui just shows shortened id with just last 3 characters. */
  private fun getTruncatedId(patientItem: MainActivityViewModel.PatientItem): String {
    return patientItem.resourceId.takeLast(3)
  }
}
