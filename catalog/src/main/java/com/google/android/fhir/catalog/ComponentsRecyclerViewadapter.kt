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
) : ListAdapter<ComponentListViewModel.ViewItem, RecyclerView.ViewHolder>(ComponentDiffUtil()) {

  enum class ViewType(val spanCount: Int) {
    HEADER(2),
    ITEM(1)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      ViewType.HEADER.ordinal ->
        ComponentHeaderViewHolder(
          ComponentHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
      ViewType.ITEM.ordinal ->
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
    return when (getItem(position)) {
      is ComponentListViewModel.ViewItem.HeaderItem -> ViewType.HEADER.ordinal
      is ComponentListViewModel.ViewItem.ComponentItem -> ViewType.ITEM.ordinal
    }
  }
}

interface ComponentViewHolder {
  fun bind(component: ComponentListViewModel.ViewItem)
}

class ComponentListViewHolder(
  private val binding: LandingPageItemBinding,
  private val onItemClick: (ComponentListViewModel.Component) -> Unit
) : RecyclerView.ViewHolder(binding.root), ComponentViewHolder {
  override fun bind(component: ComponentListViewModel.ViewItem) {
    val componentItem = component as ComponentListViewModel.ViewItem.ComponentItem
    binding.componentLayoutIconImageview.setImageResource(componentItem.component.iconId)
    binding.componentLayoutTextView.text =
      binding.componentLayoutTextView.context.getString(componentItem.component.textId)
    binding.root.setOnClickListener { onItemClick(component.component) }
  }
}

class ComponentHeaderViewHolder(private val binding: ComponentHeaderLayoutBinding) :
  RecyclerView.ViewHolder(binding.root), ComponentViewHolder {
  override fun bind(component: ComponentListViewModel.ViewItem) {
    val headerItem = component as ComponentListViewModel.ViewItem.HeaderItem
    binding.tvComponentHeader.text =
      binding.tvComponentHeader.context.getString(headerItem.header.textId)
  }
}

class ComponentDiffUtil : DiffUtil.ItemCallback<ComponentListViewModel.ViewItem>() {
  override fun areItemsTheSame(
    oldComponent: ComponentListViewModel.ViewItem,
    newComponent: ComponentListViewModel.ViewItem
  ) = oldComponent === newComponent

  override fun areContentsTheSame(
    oldComponent: ComponentListViewModel.ViewItem,
    newComponent: ComponentListViewModel.ViewItem
  ) = oldComponent == newComponent
}
