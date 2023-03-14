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

package com.google.android.fhir.demo.screening

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.demo.R
import com.google.android.fhir.demo.care.TaskManager
import com.google.android.fhir.demo.databinding.ItemScreeningViewBinding
import org.hl7.fhir.r4.model.Task

class ScreeningLandingAdapter(private val screeningClickHandler: ScreeningClickHandler) :
  RecyclerView.Adapter<ScreeningLandingAdapter.ScreeningLandingViewHolder>() {

  private val taskList = mutableListOf<Task>()

  fun setItemsList(taskList: List<Task>) {
    this.taskList.clear()
    this.taskList.addAll(taskList)
    notifyDataSetChanged()
  }

  inner class ScreeningLandingViewHolder(
    private val itemScreeningViewBinding: ItemScreeningViewBinding
  ) : RecyclerView.ViewHolder(itemScreeningViewBinding.root) {
    fun bind(task: Task) {
      itemScreeningViewBinding.title = TaskManager.getTaskName(task)
      //            itemScreeningViewBinding.icon =
      itemScreeningViewBinding.status = task.status.display
      //            itemScreeningViewBinding.statusColor =
      itemScreeningViewBinding.taskId = task.id
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreeningLandingViewHolder {
    return ScreeningLandingViewHolder(
        DataBindingUtil.inflate(
          LayoutInflater.from(parent.context),
          R.layout.item_screening_view,
          parent,
          false
        )
      )
      .listen { view, position, type -> screeningClickHandler.onClick(taskList[position]) }
  }

  override fun onBindViewHolder(holder: ScreeningLandingViewHolder, position: Int) {
    holder.bind(taskList.elementAt(position))
  }

  override fun getItemCount() = taskList.size
}

interface ScreeningClickHandler {
  fun onClick(task: Task)
}

fun <T : RecyclerView.ViewHolder> T.listen(
  event: (view: View, position: Int, type: Int) -> Unit
): T {
  itemView.setOnClickListener { event.invoke(itemView, adapterPosition, itemViewType) }
  return this
}
