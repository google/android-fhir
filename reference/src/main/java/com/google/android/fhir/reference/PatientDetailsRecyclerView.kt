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

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.reference.databinding.PatientDetailsHeaderBinding
import com.google.android.fhir.reference.databinding.PatientListItemViewBinding

data class Model(
  val header: String,
  val value: String,
  val resourceId: Int = -1,
  val type: Int = 0
)

abstract class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  abstract fun bindTo(model: Model)
}

class ChildItemViewHolder(binding: PatientListItemViewBinding) : ItemViewHolder(binding.root) {
  private val statusView: ImageView = binding.status
  private val nameView: TextView = binding.name
  private val ageView: TextView = binding.age
  private val idView: TextView = binding.id

  override fun bindTo(model: Model) {
    this.nameView.text = model.header
    this.ageView.text = model.value
    statusView.visibility = View.GONE
    idView.visibility = View.GONE
  }
}

class HeaderItemViewHolder(val binding: PatientDetailsHeaderBinding) :
  ItemViewHolder(binding.root) {
  private val nameView: TextView = binding.title

  override fun bindTo(model: Model) {
    this.nameView.text = model.value
  }
}
