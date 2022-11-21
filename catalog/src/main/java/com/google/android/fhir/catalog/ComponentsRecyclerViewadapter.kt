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

package com.google.android.fhir.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.catalog.databinding.ComponentHeaderLayoutBinding
import com.google.android.fhir.catalog.databinding.LandingPageItemBinding

class ComponentsRecyclerViewAdapter(
  private val onItemClick: (ComponentListViewModel.Component) -> Unit
) : ListAdapter<ComponentListViewModel.Component, RecyclerView.ViewHolder>(ComponentDiffUtil()) {

  enum class ViewType {
    HEADER_TYPE,
    ITEM_TYPE
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      ViewType.HEADER_TYPE.ordinal ->
        ComponentHeaderViewHolder(
          ComponentHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
      ViewType.ITEM_TYPE.ordinal ->
        ComponentListViewHolder(
          LandingPageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
          onItemClick
        )
      else -> throw IllegalArgumentException("$viewType must be ViewType.")
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is ComponentViewHolder) {
      holder.bind(getItem(position))
    }
  }

  override fun getItemViewType(position: Int): Int {
    return when (getItem(position).viewType) {
      ViewType.HEADER_TYPE -> ViewType.HEADER_TYPE.ordinal
      ViewType.ITEM_TYPE -> ViewType.ITEM_TYPE.ordinal
    }
  }
}

interface ComponentViewHolder {
  fun bind(component: ComponentListViewModel.Component)
}

class ComponentListViewHolder(
  private val binding: LandingPageItemBinding,
  private val onItemClick: (ComponentListViewModel.Component) -> Unit
) : RecyclerView.ViewHolder(binding.root), ComponentViewHolder {
  override fun bind(component: ComponentListViewModel.Component) {
    binding.componentLayoutIconImageview.setImageResource(component.iconId)
    binding.componentLayoutTextView.text =
      binding.componentLayoutTextView.context.getString(component.textId)
    binding.root.setOnClickListener { onItemClick(component) }
  }
}

class ComponentHeaderViewHolder(private val binding: ComponentHeaderLayoutBinding) :
  RecyclerView.ViewHolder(binding.root), ComponentViewHolder {
  override fun bind(component: ComponentListViewModel.Component) {
    binding.tvComponentHeader.text = binding.tvComponentHeader.context.getString(component.textId)
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
