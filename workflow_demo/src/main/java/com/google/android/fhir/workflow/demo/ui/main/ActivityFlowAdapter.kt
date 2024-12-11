/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.workflow.demo.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.workflow.demo.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class ActivityFlowAdapter :
  ListAdapter<DataModel, ActivityFlowAdapter.ViewHolder>(DataModelDiffUtil) {

  class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private val name: MaterialTextView = view.findViewById(R.id.phase_name)
    private val details: MaterialTextView = view.findViewById(R.id.phase_details)
    private val selection: MaterialButton = view.findViewById(R.id.start_phase)

    @SuppressLint("SetTextI18n")
    fun bind(model: DataModel) {
      name.text = "Phase: ${model.phase.name}"
      details.text = model.details
      selection.isEnabled = model.isActive
      selection.setOnClickListener { model.onclick(model.phase) }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    ViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.card_item_phase, parent, false),
    )

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}

data class DataModel(
  val phase: FlowPhase,
  val details: String,
  val isActive: Boolean,
  val onclick: (phase: FlowPhase) -> Unit,
)

object DataModelDiffUtil : DiffUtil.ItemCallback<DataModel>() {
  override fun areItemsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
    return oldItem.phase == newItem.phase
  }

  override fun areContentsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
    return oldItem == newItem
  }
}
