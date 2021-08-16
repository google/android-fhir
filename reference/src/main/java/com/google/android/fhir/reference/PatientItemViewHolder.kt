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

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.reference.databinding.PatientListItemViewBinding
import java.time.LocalDate
import java.time.Period
import org.hl7.fhir.r4.model.codesystems.RiskProbability

class PatientItemViewHolder(private val binding: PatientListItemViewBinding) :
  RecyclerView.ViewHolder(binding.root) {
  private val statusView: ImageView = binding.status
  private val nameView: TextView = binding.name
  private val ageView: TextView = binding.age
  private val idView: TextView = binding.id

  fun bindTo(
    patientItem: PatientListViewModel.PatientItem,
    onItemClicked: (PatientListViewModel.PatientItem) -> Unit
  ) {
    this.nameView.text = patientItem.name
    this.ageView.text = "${getAge(patientItem)} years old"
    // The new ui just shows shortened id with just last 3 characters.
    this.idView.text = "Id: #---${getTruncatedId(patientItem)}"

    this.itemView.setOnClickListener { onItemClicked(patientItem) }
    statusView.imageTintList =
      ColorStateList.valueOf(
        ContextCompat.getColor(
          statusView.context,
          when (patientItem.risk) {
            RiskProbability.HIGH.toCode() -> R.color.high_risk
            RiskProbability.MODERATE.toCode() -> R.color.moderate_risk
            RiskProbability.LOW.toCode() -> R.color.low_risk
            else -> R.color.unknown_risk
          }
        )
      )
  }

  private fun getAge(patientItem: PatientListViewModel.PatientItem): Int {
    return Period.between(LocalDate.parse(patientItem.dob), LocalDate.now()).years.let {
      return@let if (it > 0) it else 1
    }
  }

  private fun getTruncatedId(patientItem: PatientListViewModel.PatientItem): String {
    return patientItem.resourceId.substring(patientItem.resourceId.length - 3)
  }
}
