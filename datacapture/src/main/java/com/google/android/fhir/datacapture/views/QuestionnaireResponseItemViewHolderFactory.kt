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

package com.google.android.fhir.datacapture.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Factory for [QuestionnaireResponseItemViewHolder].
 *
 * @param resId the layout resource for the view
 */
abstract class QuestionnaireResponseItemViewHolderFactory(@LayoutRes val resId: Int) {
  internal open fun create(parent: ViewGroup): QuestionnaireResponseItemViewHolder {
    return QuestionnaireResponseItemViewHolder(
      LayoutInflater.from(parent.context).inflate(resId, parent, false),
      getQuestionnaireResponseItemViewHolderDelegate()
    )
  }

  /**
   * Returns a [QuestionnaireResponseItemViewHolderDelegate] that handles the initialization of
   * views and binding of items in [RecyclerView].
   */
  abstract fun getQuestionnaireResponseItemViewHolderDelegate():
    QuestionnaireResponseItemViewHolderDelegate
}

/**
 * The [RecyclerView.ViewHolder] for [QuestionnaireResponseItemViewItem].
 *
 * This is used by [QuestionnaireResponseItemAdapter] to initialize views and bind items in
 * [RecyclerView].
 */
open class QuestionnaireResponseItemViewHolder(
  itemView: View,
  private val delegate: QuestionnaireResponseItemViewHolderDelegate
) : RecyclerView.ViewHolder(itemView) {
  init {
    delegate.init(itemView)
  }

  open fun bind(questionnaireResponseItemViewItem: QuestionnaireResponseItemViewItem) {
    delegate.questionnaireResponseItemViewItem = questionnaireResponseItemViewItem
    delegate.bind(questionnaireResponseItemViewItem)
  }
}

/**
 * Delegate for [QuestionnaireResponseItemViewHolder].
 *
 * This interface provides an abstraction of the operations that need to be implemented for a type
 * of view in the questionnaire.
 *
 * There is a 1:1 relationship between this and [QuestionnaireResponseItemViewHolder]. In other
 * words, there is a unique [QuestionnaireResponseItemViewHolderDelegate] for each
 * [QuestionnaireResponseItemViewHolder]. This is critical for the correctness of the recycler view.
 */
interface QuestionnaireResponseItemViewHolderDelegate {

  var questionnaireResponseItemViewItem: QuestionnaireResponseItemViewItem

  /**
   * Initializes the view in [QuestionnaireResponseItemViewHolder]. Any listeners to record user
   * input should be set in this function.
   */
  fun init(itemView: View)

  /** Binds a [QuestionnaireResponseItemViewItem] to the view. */
  fun bind(questionnaireResponseItemViewItem: QuestionnaireResponseItemViewItem)
}
