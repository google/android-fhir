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
import com.google.android.fhir.reference.databinding.ObservationListItemBinding

class ObservationItemViewHolder(private val binding: ObservationListItemBinding) :
  RecyclerView.ViewHolder(binding.root) {
  private val observationTextView: TextView = binding.observationDetail

  fun bindTo(observationItem: PatientListViewModel.ObservationItem) {
    this.observationTextView.text =
      itemView.resources.getString(
        R.string.observation_brief_text,
        observationItem.code,
        observationItem.value,
        observationItem.effective
      )
  }
}
