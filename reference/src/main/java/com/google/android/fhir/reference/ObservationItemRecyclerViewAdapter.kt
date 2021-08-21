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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.fhir.reference.databinding.ObservationListItemBinding

/** UI Controller helper class to display list of observations. */
class ObservationItemRecyclerViewAdapter :
  ListAdapter<PatientListViewModel.ObservationItem, ObservationItemViewHolder>(
    ObservationItemDiffCallback()
  ) {

  class ObservationItemDiffCallback :
    DiffUtil.ItemCallback<PatientListViewModel.ObservationItem>() {
    override fun areItemsTheSame(
      oldItem: PatientListViewModel.ObservationItem,
      newItem: PatientListViewModel.ObservationItem
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
      oldItem: PatientListViewModel.ObservationItem,
      newItem: PatientListViewModel.ObservationItem
    ): Boolean = oldItem.id == newItem.id
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationItemViewHolder {
    return ObservationItemViewHolder(
      ObservationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun onBindViewHolder(holder: ObservationItemViewHolder, position: Int) {
    val item = currentList[position]
    holder.bindTo(item)
  }
}
