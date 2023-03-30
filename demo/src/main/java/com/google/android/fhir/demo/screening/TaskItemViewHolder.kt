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

import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.demo.databinding.ItemTaskViewBinding
import org.hl7.fhir.r4.model.Task

class TaskItemViewHolder(binding: ItemTaskViewBinding) : RecyclerView.ViewHolder(binding.root) {
  private val title: TextView = binding.taskTitle
  private val status: TextView = binding.taskStatus
  private val taskId: TextView = binding.taskId

  fun bindTo(
    taskItem: ListScreeningsViewModel.TaskItem,
    onItemClicked: (ListScreeningsViewModel.TaskItem) -> Unit
  ) {
    this.title.text = taskItem.name
    this.status.text = taskItem.status
    this.status.setTextColor(getStatusColor(taskItem.status))
    this.taskId.text = "Id: #---${getTruncatedId(taskItem)}"
    this.itemView.setOnClickListener { onItemClicked(taskItem) }
  }

  /** The new ui just shows shortened id with just last 3 characters. */
  private fun getTruncatedId(taskItem: ListScreeningsViewModel.TaskItem): String {
    return taskItem.resourceId.takeLast(3)
  }

  private fun getStatusColor(status: String): Int {
    if (status == Task.TaskStatus.COMPLETED.toCode()) return Color.RED
    return Color.GREEN
  }
}
