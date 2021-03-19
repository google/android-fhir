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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.fhir.datacapture.gallery.databinding.QuestionnaireListItemViewBinding

class QuestionnaireListAdapter(private val questionnaireList: List<QuestionnaireListItem>) :
  Adapter<QuestionnaireListAdapter.ViewHolder>() {

  class ViewHolder(binding: QuestionnaireListItemViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val name = binding.questionnaireName
    val description = binding.questionnaireDescription
    lateinit var questionnaireListItem: QuestionnaireListItem

    init {
      binding.root.setOnClickListener {
        val action =
          QuestionnaireListFragmentDirections
            .actionQuestionnaireListFragmentToQuestionnaireContainerFragment(
              questionnaireListItem.name,
              questionnaireListItem.questionnairePath,
              questionnaireListItem.questionnaireResponsePath
            )
        it.findNavController().navigate(action)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      QuestionnaireListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val questionnaireListItem = questionnaireList[position]
    holder.questionnaireListItem = questionnaireListItem
    holder.name.text = questionnaireListItem.name
    holder.description.text = questionnaireListItem.description
  }

  override fun getItemCount() = questionnaireList.size
}
