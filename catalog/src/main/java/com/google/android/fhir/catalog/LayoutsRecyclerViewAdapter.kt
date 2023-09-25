/*
 * Copyright 2021-2023 Google LLC
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

class LayoutsRecyclerViewAdapter(private val onItemClick: (LayoutListViewModel.Layout) -> Unit) :
  ListAdapter<LayoutListViewModel.Layout, LayoutViewHolder>(LayoutDiffUtil()) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutViewHolder {
    return LayoutViewHolder(
      LandingPageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      onItemClick,
    )
  }

  override fun onBindViewHolder(holder: LayoutViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}

class LayoutViewHolder(
  val binding: LandingPageItemBinding,
  private val onItemClick: (LayoutListViewModel.Layout) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(layout: LayoutListViewModel.Layout) {
    binding.componentLayoutIconImageview.setImageResource(layout.iconId)
    binding.componentLayoutTextView.text =
      binding.componentLayoutTextView.context.getString(layout.textId)
    binding.root.setOnClickListener { onItemClick(layout) }
  }
}

class LayoutDiffUtil : DiffUtil.ItemCallback<LayoutListViewModel.Layout>() {
  override fun areItemsTheSame(
    oldLayout: LayoutListViewModel.Layout,
    newLayout: LayoutListViewModel.Layout,
  ) = oldLayout === newLayout

  override fun areContentsTheSame(
    oldLayout: LayoutListViewModel.Layout,
    newLayout: LayoutListViewModel.Layout,
  ) = oldLayout == newLayout
}
