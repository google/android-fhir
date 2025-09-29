/*
 * Copyright 2022-2025 Google LLC
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

package com.google.android.fhir.datacapture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.views.NavigationViewHolder
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.android.fhir.datacapture.views.factories.ReviewViewHolderFactory

/** List Adapter used to bind answers to [QuestionnaireItemViewHolder] in review mode. */
internal class QuestionnaireReviewAdapter :
  ListAdapter<ReviewAdapterItem, RecyclerView.ViewHolder>(
    DiffCallbacks.REVIEW_ITEMS,
  ) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      VIEW_TYPE_QUESTION -> ReviewViewHolderFactory.create(parent)
      VIEW_TYPE_NAVIGATION ->
        NavigationViewHolder(
          LayoutInflater.from(parent.context)
            .inflate(R.layout.pagination_navigation_view, parent, false),
        )
      else -> throw IllegalStateException("Invalid view type: $viewType")
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (val item = getItem(position)) {
      is QuestionnaireAdapterItem.Question -> {
        holder as QuestionnaireItemViewHolder
        holder.bind(item.item)
      }
      is QuestionnaireAdapterItem.Navigation -> {
        holder as NavigationViewHolder
        holder.bind(item.questionnaireNavigationUIState)
      }
    }
  }

  override fun getItemViewType(position: Int): Int =
    when (getItem(position)) {
      is QuestionnaireAdapterItem.Question -> VIEW_TYPE_QUESTION
      is QuestionnaireAdapterItem.Navigation -> VIEW_TYPE_NAVIGATION
      else -> super.getItemViewType(position)
    }

  companion object {
    const val VIEW_TYPE_QUESTION = 10
    const val VIEW_TYPE_NAVIGATION = 110
  }
}
