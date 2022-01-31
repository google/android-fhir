/*
 * Copyright 2021 Google LLC
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

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.fhir.datacapture.views.QuestionnaireResponseItemSimpleQuestionAnswerDisplayViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireResponseItemViewHolder
import com.google.android.fhir.datacapture.views.QuestionnaireResponseItemViewItem

internal class QuestionnaireResponseItemAdapter :
  ListAdapter<QuestionnaireResponseItemViewItem, QuestionnaireResponseItemViewHolder>(
    ResponseDiffCallback
  ) {

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): QuestionnaireResponseItemViewHolder {
    val viewHolderFactory = QuestionnaireResponseItemSimpleQuestionAnswerDisplayViewHolderFactory
    return viewHolderFactory.create(parent)
  }

  override fun onBindViewHolder(holder: QuestionnaireResponseItemViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}

internal object ResponseDiffCallback : DiffUtil.ItemCallback<QuestionnaireResponseItemViewItem>() {
  override fun areItemsTheSame(
    oldItem: QuestionnaireResponseItemViewItem,
    newItem: QuestionnaireResponseItemViewItem
  ) = oldItem.questionnaireItem.linkId == newItem.questionnaireItem.linkId

  override fun areContentsTheSame(
    oldItem: QuestionnaireResponseItemViewItem,
    newItem: QuestionnaireResponseItemViewItem
  ) =
    oldItem.questionnaireItem.equalsDeep(newItem.questionnaireItem) &&
      oldItem.questionnaireResponseItem.equalsDeep(newItem.questionnaireResponseItem)
}
