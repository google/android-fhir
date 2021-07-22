package com.google.android.fhir.reference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.reference.databinding.PatientDetailsHeaderBinding
import com.google.android.fhir.reference.databinding.PatientListItemViewBinding

data class Model(val header: String, val value: String, val resourceId: Int = -1, val type: Int = 0)

abstract class ItemViewHolder(view : View) :
  RecyclerView.ViewHolder(view) {
  abstract fun bindTo(
    model: Model
  )
  }
class ChildItemViewHolder(binding: PatientListItemViewBinding) :
  ItemViewHolder(binding.root) {
  private val statusView: ImageView = binding.status
  private val nameView: TextView = binding.name
  private val ageView: TextView = binding.age
  private val idView: TextView = binding.id

  override fun bindTo(
    model: Model
  ) {
    this.nameView.text = model.header
    this.ageView.text = model.value
    statusView.visibility = View.GONE
    idView.visibility = View.GONE
  }
}

class HeaderItemViewHolder(val binding: PatientDetailsHeaderBinding) :
  ItemViewHolder(binding.root) {
  private val nameView: TextView = binding.title

  override fun bindTo(
    model: Model
  ) {
    this.nameView.text = model.value
  }
}
class PatientDetailsRecyclerView :
  ListAdapter<Model, ItemViewHolder>(PatientItemDiffCallback()) {

  class PatientItemDiffCallback : DiffUtil.ItemCallback<Model>() {
    override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean = oldItem == newItem
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    return if (viewType == 1) HeaderItemViewHolder(PatientDetailsHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
     else ChildItemViewHolder(PatientListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.bindTo(getItem(position))
  }

  override fun getItemViewType(position: Int): Int {
    return getItem(position).type
  }
}
