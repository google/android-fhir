/*
 * Copyright 2022-2023 Google LLC
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
package com.google.android.fhir.configurablecare.screening

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.configurablecare.screening.ListScreeningsViewModel
import com.google.android.fhir.configurablecare.R
import com.google.android.fhir.configurablecare.databinding.ItemTaskViewBinding

class TaskItemViewHolder(binding: ItemTaskViewBinding) : RecyclerView.ViewHolder(binding.root) {
  private val description: TextView = binding.taskDescription
  private val dueDate: TextView = binding.taskDueDate
  private val taskIcon: ImageView = binding.taskIcon
  private var requestType: String = "Task"

  fun bindTo(
    taskItem: ListScreeningsViewModel.TaskItem,
    onItemClicked: (ListScreeningsViewModel.TaskItem) -> Unit
  ) {
    this.requestType = taskItem.resourceType
    this.description.text = taskItem.description

    var statusStr = ""
    if (taskItem.dueDate == "Do Not Perform") {
      statusStr = "Do Not Perform" + " | " + taskItem.intent
      this.taskIcon.setImageResource(R.drawable.ic_task)
    } else if (taskItem.status == "completed") {
      statusStr = "Completed" + " | " + taskItem.intent
      this.taskIcon.setImageResource(R.drawable.ic_task_check)
    } else if (taskItem.status == "cancelled") {
      statusStr = "Cancelled" + " | " + taskItem.intent
      this.taskIcon.setImageResource(R.drawable.ic_task)
    } else {
      statusStr = "Due " + getDate(taskItem.dueDate) + " | " + taskItem.intent
      this.taskIcon.setImageResource(R.drawable.ic_task)
    }

    this.dueDate.text = statusStr
    if (taskItem.clickable) {
      this.itemView.setOnClickListener { onItemClicked(taskItem) }
    }
  }

  private fun getDate(date: String): String {
    return date.substring(4, 10) + " " + date.substring(date.length - 4)
  }

}
