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

package com.google.android.fhir.datacapture.gallery

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class QuestionnaireListAdapter : Adapter<QuestionnaireListAdapter.ViewHolder>() {

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
  {
    val name: TextView = view.findViewById(R.id.questionnaire_name)
    val description: TextView = view.findViewById(R.id.questionnaire_description)
    lateinit var questionnaireListItem: QuestionnaireListItem
    init {
      view.setOnClickListener {
        val context = view.context
        context.startActivity(
                Intent(context, QuestionnaireActivity::class.java).apply {
                  putExtra(QuestionnaireActivity.QUESTIONNAIRE_TITLE_KEY, questionnaireListItem.name)
                  putExtra(QuestionnaireActivity.QUESTIONNAIRE_FILE_PATH_KEY, questionnaireListItem.path)
                }
        )
      }
    }
  }

  private val differCallback = object : DiffUtil.ItemCallback<QuestionnaireListItem>(){
    override fun areItemsTheSame(
            oldItem: QuestionnaireListItem,
            newItem: QuestionnaireListItem
    ): Boolean {
      return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(
            oldItem: QuestionnaireListItem,
            newItem: QuestionnaireListItem
    ): Boolean {
      return oldItem == newItem
    }
  }

  val differ = AsyncListDiffer(this, differCallback)

  override fun getItemCount() = differ.currentList.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.questionnaire_list_item_view, parent, false)
    )
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = differ.currentList[position]

    holder.apply {
      name.text = questionnaireListItem.name
      description.text = questionnaireListItem.description
      questionnaireListItem = item
    }
  }
}


