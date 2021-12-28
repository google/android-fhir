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

package com.google.android.fhir.datacapture.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.gallery.databinding.LandingPageItemBinding

class LayoutsRecyclerViewAdapter() :
  ListAdapter<ComponentsViewModel.SdcLayouts, LayoutViewHolder>(LayoutsDiffUtil()) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutViewHolder {
    return LayoutViewHolder(
      LandingPageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun onBindViewHolder(holder: LayoutViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}

class LayoutViewHolder(val binding: LandingPageItemBinding) :
  RecyclerView.ViewHolder(binding.root) {
  fun bind(component: ComponentsViewModel.SdcLayouts) {
    binding.componentLayoutIconImageview.setImageResource(component.iconId)
    binding.componentLayoutTextView.text =
      binding.componentLayoutTextView.context.getString(component.textId)
  }
}

class LayoutsDiffUtil : DiffUtil.ItemCallback<ComponentsViewModel.SdcLayouts>() {
  override fun areItemsTheSame(
    oldComponent: ComponentsViewModel.SdcLayouts,
    newComponent: ComponentsViewModel.SdcLayouts
  ) = oldComponent == newComponent

  override fun areContentsTheSame(
    oldComponent: ComponentsViewModel.SdcLayouts,
    newComponent: ComponentsViewModel.SdcLayouts
  ) = areItemsTheSame(oldComponent, newComponent)
}
