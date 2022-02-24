/*
 * Copyright 2021 Google LLC
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

class ComponentsRecyclerViewAdapter(
  private val onItemClick: (ComponentListViewModel.Component) -> Unit
) : ListAdapter<ComponentListViewModel.Component, ComponentListViewHolder>(ComponentDiffUtil()) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentListViewHolder {
    return ComponentListViewHolder(
      LandingPageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      onItemClick
    )
  }

  override fun onBindViewHolder(holder: ComponentListViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}

class ComponentListViewHolder(
  private val binding: LandingPageItemBinding,
  private val onItemClick: (ComponentListViewModel.Component) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(component: ComponentListViewModel.Component) {
    binding.componentLayoutIconImageview.setImageResource(component.iconId)
    binding.componentLayoutTextView.text =
      binding.componentLayoutTextView.context.getString(component.textId)
    binding.root.setOnClickListener { onItemClick(component) }
  }
}

class ComponentDiffUtil : DiffUtil.ItemCallback<ComponentListViewModel.Component>() {
  override fun areItemsTheSame(
    oldComponent: ComponentListViewModel.Component,
    newComponent: ComponentListViewModel.Component
  ) = oldComponent === newComponent

  override fun areContentsTheSame(
    oldComponent: ComponentListViewModel.Component,
    newComponent: ComponentListViewModel.Component
  ) = oldComponent == newComponent
}
