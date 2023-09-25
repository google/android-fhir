/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.catalog.databinding.LandingPageItemBinding

class BehaviorsRecyclerViewAdapter(
  private val onItemClick: (BehaviorListViewModel.Behavior) -> Unit,
) : ListAdapter<BehaviorListViewModel.Behavior, BehaviorViewHolder>(BehaviorDiffUtil()) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BehaviorViewHolder {
    return BehaviorViewHolder(
      LandingPageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      onItemClick,
    )
  }

  override fun onBindViewHolder(holder: BehaviorViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}

class BehaviorViewHolder(
  val binding: LandingPageItemBinding,
  private val onItemClick: (BehaviorListViewModel.Behavior) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(behavior: BehaviorListViewModel.Behavior) {
    binding.componentLayoutIconImageview.setImageResource(behavior.iconId)
    binding.componentLayoutTextView.text =
      binding.componentLayoutTextView.context.getString(behavior.textId)
    binding.root.setOnClickListener { onItemClick(behavior) }
  }
}

class BehaviorDiffUtil : DiffUtil.ItemCallback<BehaviorListViewModel.Behavior>() {
  override fun areItemsTheSame(
    oldBehavior: BehaviorListViewModel.Behavior,
    newBehavior: BehaviorListViewModel.Behavior,
  ) = oldBehavior === newBehavior

  override fun areContentsTheSame(
    oldBehavior: BehaviorListViewModel.Behavior,
    newBehavior: BehaviorListViewModel.Behavior,
  ) = oldBehavior == newBehavior
}
