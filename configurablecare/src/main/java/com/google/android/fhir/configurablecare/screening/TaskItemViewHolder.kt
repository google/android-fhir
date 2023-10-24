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

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.configurablecare.screening.ListScreeningsViewModel
import com.google.android.fhir.configurablecare.R
import com.google.android.fhir.configurablecare.databinding.ItemTaskViewBinding

class TaskItemViewHolder(binding: ItemTaskViewBinding) : RecyclerView.ViewHolder(binding.root) {
  private val description: TextView = binding.taskDescription
  private val taskStatus: TextView = binding.taskStatus
  private val taskIcon: ImageView = binding.taskIcon
  private val taskId: TextView = binding.taskId
  private var requestType: String = "Task"
  private var lastUpdated: TextView = binding.lastUpdated

  fun bindTo(
    taskItem: ListScreeningsViewModel.TaskItem,
    onItemClicked: (ListScreeningsViewModel.TaskItem) -> Unit
  ) {
    this.requestType = taskItem.resourceType
    this.description.text = taskItem.description
    this.taskId.text = "id: ${taskItem.resourceId.substring(9) + "..."}"

    var statusStr = ""

    var lastUpdated = ""
    if (taskItem.completedDate != "") {
      lastUpdated = "Last updated: " + taskItem.completedDate
      this.lastUpdated.text = lastUpdated
    }

    if (taskItem.dueDate == "Do Not Perform") {
      statusStr =  getTransition(taskItem) + "Do Not Perform"
      this.taskStatus.setTextColor(Color.rgb(204,0,0))
      this.taskIcon.setImageResource(R.drawable.gm_remove_circle_outline_24)
    } else if (taskItem.status == "completed") {
      statusStr =  getTransition(taskItem) + taskItem.status
      this.taskStatus.setTextColor(Color.rgb(20, 108, 46))
      this.taskIcon.setImageResource(R.drawable.ic_task_check)
    } else if (taskItem.status == "cancelled") {
      statusStr =  getTransition(taskItem) + taskItem.status
      this.taskStatus.setTextColor(Color.rgb(204,0,0))
      this.taskIcon.setImageResource(R.drawable.gm_remove_circle_outline_24)
    } else if (taskItem.status == "on-hold") {
      statusStr =  getTransition(taskItem) + taskItem.status
      this.taskStatus.setTextColor(Color.rgb(204,0,0))
      this.taskIcon.setImageResource(R.drawable.gm_remove_circle_outline_24)
    } else if (taskItem.status == "stopped") {
      this.taskStatus.setTextColor(Color.rgb(204,0,0))
      statusStr =  getTransition(taskItem) + taskItem.status
      this.taskIcon.setImageResource(R.drawable.gm_remove_circle_outline_24)
    } else if (taskItem.resourceType == "ServiceRequest") {
      statusStr = "Active"
      this.taskStatus.setTextColor(Color.rgb(255,187,51))
      this.taskIcon.setImageResource(R.drawable.ic_error_48px)
    } else {
      this.taskStatus.setTextColor(Color.rgb(11,87,208))
      statusStr = "Due " + getDate(taskItem.dueDate) + " | " + taskItem.intent
      this.taskIcon.setImageResource(R.drawable.ic_task)
    }

    this.taskStatus.text = statusStr
    if (taskItem.clickable) {
      this.itemView.setOnClickListener { onItemClicked(taskItem) }
    }
  }

  private fun getDate(date: String): String {
    return date.substring(4, 10) + " " + date.substring(date.length - 4)
  }

  private fun getTransition(taskItem: ListScreeningsViewModel.TaskItem): String {
    var transition = ""
    if (taskItem.intent == "order") {
      transition = "proposal -> plan -> order -> "
    } else if (taskItem.intent == "plan") {
      transition = "proposal -> plan -> "
    } else if (taskItem.intent == "proposal") {
      transition = "proposal -> "
    }
    return transition
  }
}
