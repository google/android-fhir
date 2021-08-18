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

class PatientDetailsRecyclerViewAdapter(val onScreenerClick: () -> Unit) :
  ListAdapter<PatientDetailDataModel, PatientDetailItemVH>(PatientDetailDiffUtil()) {
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
    return when {
      item.isHeader -> ViewTypes.HEADER
      item.isPatient -> ViewTypes.PATIENT
      item.isPatientProperty -> ViewTypes.PATIENT_PROPERTY
      item.isObservation -> ViewTypes.OBSERVATION
      item.isCondition -> ViewTypes.CONDITION
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
  abstract fun bind(dataModel: PatientDetailDataModel)
}

class PatientOverviewItemVH(
  private val binding: PatientDetailsHeaderBinding,
  val onScreenerClick: () -> Unit
) : PatientDetailItemVH(binding.root) {
  override fun bind(dataModel: PatientDetailDataModel) {
    binding.screener.setOnClickListener { onScreenerClick() }
    binding.title.text = dataModel.patient?.name
  }
}

class PatientPropertyItemVH(private val binding: PatientListItemViewBinding) :
  PatientDetailItemVH(binding.root) {
  override fun bind(dataModel: PatientDetailDataModel) {
    binding.name.text = dataModel.patientProperty?.header
    binding.age.text = dataModel.patientProperty?.value
    binding.status.visibility = View.GONE
    binding.id.visibility = View.GONE
  }
}

class PatientDetailsHeaderItemVH(private val binding: PatientDetailsCardViewBinding) :
  PatientDetailItemVH(binding.root) {
  override fun bind(dataModel: PatientDetailDataModel) {
    binding.header.text = dataModel.header
  }
}

class PatientDetailsObservationItemVH(private val binding: PatientListItemViewBinding) :
  PatientDetailItemVH(binding.root) {
  override fun bind(dataModel: PatientDetailDataModel) {
    binding.name.text = dataModel.observation?.code
    binding.age.text = dataModel.observation?.value
    binding.status.visibility = View.GONE
    binding.id.visibility = View.GONE
  }
}

class PatientDetailsConditionItemVH(private val binding: PatientListItemViewBinding) :
  PatientDetailItemVH(binding.root) {
  override fun bind(dataModel: PatientDetailDataModel) {
    binding.name.text = dataModel.condition?.code
    binding.age.text = dataModel.condition?.value
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

class PatientDetailDiffUtil : DiffUtil.ItemCallback<PatientDetailDataModel>() {
  override fun areItemsTheSame(o: PatientDetailDataModel, n: PatientDetailDataModel): Boolean {
    return (o == n ||
      o.isHeader && n.isHeader && n.header == o.header ||
      o.isPatient && n.isPatient && o.patient?.id == n.patient?.id) ||
      (o.isObservation && n.isObservation && o.observation?.id == n.observation?.id) ||
      (o.isCondition && n.isCondition && o.condition?.id == n.condition?.id)
  }

  override fun areContentsTheSame(o: PatientDetailDataModel, n: PatientDetailDataModel) =
    areItemsTheSame(o, n)
}
