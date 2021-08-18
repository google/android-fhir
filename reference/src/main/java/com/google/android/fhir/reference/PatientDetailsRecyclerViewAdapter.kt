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
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.reference.databinding.PatientDetailsCardViewBinding
import com.google.android.fhir.reference.databinding.PatientDetailsHeaderBinding
import com.google.android.fhir.reference.databinding.PatientListItemViewBinding
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel

class PatientDetailsRecyclerViewAdapter(private val onScreenerClick: () -> Unit) :
  ListAdapter<PatientDetailData, PatientDetailItemVH>(PatientDetailDiffUtil()) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientDetailItemVH {
    return when (ViewTypes.from(viewType)) {
      ViewTypes.HEADER ->
        PatientDetailsHeaderItemVH(
          PatientDetailsCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
      ViewTypes.PATIENT ->
        PatientOverviewItemVH(
          PatientDetailsHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
          onScreenerClick
        )
      ViewTypes.PATIENT_PROPERTY ->
        PatientPropertyItemVH(
          PatientListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
      ViewTypes.OBSERVATION ->
        PatientDetailsObservationItemVH(
          PatientListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
      ViewTypes.CONDITION ->
        PatientDetailsConditionItemVH(
          PatientListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
  }

  override fun onBindViewHolder(holder: PatientDetailItemVH, position: Int) {
    val model = getItem(position)
    holder.bind(model)
    if (holder is PatientDetailsHeaderItemVH) return

    holder.itemView.background =
      if (model.firstInGroup && model.lastInGroup) {
        allCornersRounded()
      } else if (model.firstInGroup) {
        topCornersRounded()
      } else if (model.lastInGroup) {
        bottomCornersRounded()
      } else {
        noCornersRounded()
      }
  }

  override fun getItemViewType(position: Int): Int {
    val item = getItem(position)
    return when (item) {
      is PatientDetailHeader -> ViewTypes.HEADER
      is PatientDetailOverview -> ViewTypes.PATIENT
      is PatientDetailProperty -> ViewTypes.PATIENT_PROPERTY
      is PatientDetailObservation -> ViewTypes.OBSERVATION
      is PatientDetailCondition -> ViewTypes.CONDITION
      else -> {
        throw IllegalArgumentException("Undefined Item type")
      }
    }.ordinal
  }

  companion object {
    private const val STROKE_WIDTH = 2f
    private const val CORNER_RADIUS = 10f
    @ColorInt private const val FILL_COLOR = Color.TRANSPARENT
    @ColorInt private const val STROKE_COLOR = Color.GRAY

    fun allCornersRounded(): MaterialShapeDrawable {
      return MaterialShapeDrawable(
          ShapeAppearanceModel.builder()
            .setAllCornerSizes(CORNER_RADIUS)
            .setAllCorners(RoundedCornerTreatment())
            .build()
        )
        .applyStrokeColor()
    }

    fun topCornersRounded(): MaterialShapeDrawable {
      return MaterialShapeDrawable(
          ShapeAppearanceModel.builder()
            .setTopLeftCornerSize(CORNER_RADIUS)
            .setTopRightCornerSize(CORNER_RADIUS)
            .setTopLeftCorner(RoundedCornerTreatment())
            .setTopRightCorner(RoundedCornerTreatment())
            .build()
        )
        .applyStrokeColor()
    }

    fun bottomCornersRounded(): MaterialShapeDrawable {
      return MaterialShapeDrawable(
          ShapeAppearanceModel.builder()
            .setBottomLeftCornerSize(CORNER_RADIUS)
            .setBottomRightCornerSize(CORNER_RADIUS)
            .setBottomLeftCorner(RoundedCornerTreatment())
            .setBottomRightCorner(RoundedCornerTreatment())
            .build()
        )
        .applyStrokeColor()
    }

    fun noCornersRounded(): MaterialShapeDrawable {
      return MaterialShapeDrawable(ShapeAppearanceModel.builder().build()).applyStrokeColor()
    }

    private fun MaterialShapeDrawable.applyStrokeColor(): MaterialShapeDrawable {
      strokeWidth = STROKE_WIDTH
      fillColor = ColorStateList.valueOf(FILL_COLOR)
      strokeColor = ColorStateList.valueOf(STROKE_COLOR)
      return this
    }
  }
}

abstract class PatientDetailItemVH(v: View) : RecyclerView.ViewHolder(v) {
  abstract fun bind(data: PatientDetailData)
}

class PatientOverviewItemVH(
  private val binding: PatientDetailsHeaderBinding,
  val onScreenerClick: () -> Unit
) : PatientDetailItemVH(binding.root) {
  override fun bind(data: PatientDetailData) {
    binding.screener.setOnClickListener { onScreenerClick() }
    (data as PatientDetailOverview).let { binding.title.text = it.patient.name }
  }
}

class PatientPropertyItemVH(private val binding: PatientListItemViewBinding) :
  PatientDetailItemVH(binding.root) {
  override fun bind(data: PatientDetailData) {
    (data as PatientDetailProperty).let {
      binding.name.text = it.patientProperty.header
      binding.age.text = it.patientProperty.value
    }
    binding.status.visibility = View.GONE
    binding.id.visibility = View.GONE
  }
}

class PatientDetailsHeaderItemVH(private val binding: PatientDetailsCardViewBinding) :
  PatientDetailItemVH(binding.root) {
  override fun bind(data: PatientDetailData) {
    (data as PatientDetailHeader).let { binding.header.text = it.header }
  }
}

class PatientDetailsObservationItemVH(private val binding: PatientListItemViewBinding) :
  PatientDetailItemVH(binding.root) {
  override fun bind(data: PatientDetailData) {
    (data as PatientDetailObservation).let {
      binding.name.text = it.observation.code
      binding.age.text = it.observation.value
    }
    binding.status.visibility = View.GONE
    binding.id.visibility = View.GONE
  }
}

class PatientDetailsConditionItemVH(private val binding: PatientListItemViewBinding) :
  PatientDetailItemVH(binding.root) {
  override fun bind(data: PatientDetailData) {
    (data as PatientDetailCondition).let {
      binding.name.text = it.condition.code
      binding.age.text = it.condition.value
    }
    binding.status.visibility = View.GONE
    binding.id.visibility = View.GONE
  }
}

enum class ViewTypes {
  HEADER,
  PATIENT,
  PATIENT_PROPERTY,
  OBSERVATION,
  CONDITION;

  companion object {
    fun from(ordinal: Int): ViewTypes {
      return values()[ordinal]
    }
  }
}

class PatientDetailDiffUtil : DiffUtil.ItemCallback<PatientDetailData>() {
  override fun areItemsTheSame(o: PatientDetailData, n: PatientDetailData): Boolean {
    return (o == n ||
      o is PatientDetailHeader && n is PatientDetailHeader && n.header == o.header ||
      o is PatientDetailOverview && n is PatientDetailOverview && o.patient.id == n.patient.id) ||
      (o is PatientDetailObservation &&
        n is PatientDetailObservation &&
        o.observation.id == n.observation.id) ||
      (o is PatientDetailCondition &&
        n is PatientDetailCondition &&
        o.condition.id == n.condition.id) ||
      (o is PatientDetailProperty &&
        n is PatientDetailProperty &&
        o.patientProperty.header == n.patientProperty.header)
  }

  override fun areContentsTheSame(o: PatientDetailData, n: PatientDetailData) =
    areItemsTheSame(o, n)
}
