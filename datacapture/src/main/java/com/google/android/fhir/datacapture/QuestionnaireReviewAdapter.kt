/*
 * Copyright 2022-2024 Google LLC
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
  ListAdapter<QuestionnaireAdapterItem, RecyclerView.ViewHolder>(
    DiffCallbacks.ITEMS,
  ) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val typedViewType = QuestionnaireEditAdapter.ViewType.parse(viewType)
    return when (typedViewType.type) {
      QuestionnaireEditAdapter.ViewType.Type.QUESTION -> ReviewViewHolderFactory.create(parent)
      QuestionnaireEditAdapter.ViewType.Type.NAVIGATION ->
        NavigationViewHolder(
          LayoutInflater.from(parent.context)
            .inflate(R.layout.pagination_navigation_view, parent, false),
        )
      QuestionnaireEditAdapter.ViewType.Type.REPEATED_GROUP_HEADER -> TODO()
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
      is QuestionnaireAdapterItem.RepeatedGroupHeader -> TODO()
    }
  }

  override fun getItemViewType(position: Int): Int {
    // Because we have multiple Item subtypes, we will pack two ints into the item view type.

    // The first 8 bits will be represented by this type, which is unique for each Item subclass.
    val type: QuestionnaireEditAdapter.ViewType.Type
    // The last 24 bits will be represented by this subtype, which will further divide each Item
    // subclass into more view types.
    val subtype: Int
    when (getItem(position)) {
      is QuestionnaireAdapterItem.Question -> {
        type = QuestionnaireEditAdapter.ViewType.Type.QUESTION
        subtype = 0xFFFFFF
      }
      is QuestionnaireAdapterItem.Navigation -> {
        type = QuestionnaireEditAdapter.ViewType.Type.NAVIGATION
        subtype = 0xFFFFFF
      }
      is QuestionnaireAdapterItem.RepeatedGroupHeader -> TODO()
    }
    return QuestionnaireEditAdapter.ViewType.from(type = type, subtype = subtype).viewType
  }
}
