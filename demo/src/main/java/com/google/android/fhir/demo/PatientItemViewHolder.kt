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

package com.google.android.fhir.demo

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.demo.databinding.PatientListItemViewBinding
import com.google.android.material.textview.MaterialTextView
import java.time.LocalDate
import java.time.Period

class PatientItemViewHolder(binding: PatientListItemViewBinding) :
  RecyclerView.ViewHolder(binding.root) {
  private val nameView: TextView = binding.name
  private val ageView: TextView = binding.fieldName
  private val tasksCount: MaterialTextView = binding.tasksCount

  fun bindTo(
    patientItem: PatientListViewModel.PatientItem,
    onItemClicked: (PatientListViewModel.PatientItem) -> Unit
  ) {
    this.nameView.text = patientItem.name
    this.ageView.text = getFormattedAge(patientItem, ageView.context.resources)
    this.tasksCount.text = "${patientItem.pendingTasksCount} Tasks"
    this.tasksCount.setTextColor(Color.BLACK)
    this.tasksCount.background =
      PatientDetailsRecyclerViewAdapter.allCornersRounded().apply {
        fillColor = ColorStateList.valueOf(Color.parseColor("#D2E3FC"))
      }
    this.itemView.setOnClickListener { onItemClicked(patientItem) }
  }

  private fun getFormattedAge(
    patientItem: PatientListViewModel.PatientItem,
    resources: Resources
  ): String {
    if (patientItem.dob == null) return ""
    return Period.between(patientItem.dob, LocalDate.now()).let {
      when {
        it.years > 0 -> resources.getQuantityString(R.plurals.ageYear, it.years, it.years)
        it.months > 0 -> resources.getQuantityString(R.plurals.ageMonth, it.months, it.months)
        else -> resources.getQuantityString(R.plurals.ageDay, it.days, it.days)
      }
    }
  }
}
