/*
 * Copyright 2022-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.fhir.datacapture.QuestionnaireAdapterItem
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.MediaView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem

/**
 * Factory for [QuestionnaireItemViewHolder].
 *
 * @param resId the layout resource for the view
 */
abstract class QuestionnaireItemAndroidViewHolderFactory(@LayoutRes open val resId: Int) :
  QuestionnaireItemViewHolderFactory {
  override fun create(parent: ViewGroup): QuestionnaireItemViewHolder {
    return QuestionnaireItemViewHolder(
      LayoutInflater.from(parent.context).inflate(resId, parent, false),
      getQuestionnaireItemViewHolderDelegate(),
    )
  }

  /**
   * Returns a [QuestionnaireItemViewHolderDelegate] that handles the initialization of views and
   * binding of items in [RecyclerView].
   */
  abstract fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemAndroidViewHolderDelegate
}

interface QuestionnaireItemComposeViewHolderFactory : QuestionnaireItemViewHolderFactory {
  override fun create(parent: ViewGroup): QuestionnaireItemViewHolder =
    QuestionnaireItemViewHolder(
      ComposeView(parent.context),
      getQuestionnaireItemViewHolderDelegate(),
    )

  fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemComposeViewHolderDelegate
}

sealed interface QuestionnaireItemViewHolderFactory {
  fun create(parent: ViewGroup): QuestionnaireItemViewHolder
}

/**
 * The [RecyclerView.ViewHolder] for [QuestionnaireViewItem].
 *
 * This is used by [QuestionnaireItemAdapter] to initialize views and bind items in [RecyclerView].
 */
class QuestionnaireItemViewHolder(
  itemView: View,
  private val delegate: QuestionnaireItemViewHolderDelegate,
) : RecyclerView.ViewHolder(itemView) {

  init {
    if (delegate is QuestionnaireItemAndroidViewHolderDelegate) {
      delegate.init(itemView)
    }
  }

  fun bind(questionnaireViewItem: QuestionnaireViewItem) {
    when (delegate) {
      is QuestionnaireItemAndroidViewHolderDelegate -> {
        delegate.questionnaireViewItem = questionnaireViewItem
        delegate.bind(questionnaireViewItem)
        itemView
          .findViewById<MediaView>(R.id.item_media)
          .bind(questionnaireViewItem.questionnaireItem)
        delegate.setReadOnly(questionnaireViewItem.questionnaireItem.readOnly)
      }
      is QuestionnaireItemComposeViewHolderDelegate -> {
        require(itemView is ComposeView)
        delegate.bind(itemView as ComposeView, questionnaireViewItem)
      }
    }
  }
}

/** The [RecyclerView.ViewHolder] for [QuestionnaireAdapterItem.RepeatedGroupHeader]. */
internal class RepeatedGroupHeaderItemViewHolder(
  itemView: View,
) : RecyclerView.ViewHolder(itemView) {
  private val header: TextView = itemView.findViewById(R.id.repeated_group_instance_header_title)
  private val delete: View =
    itemView.findViewById(R.id.repeated_group_instance_header_delete_button)

  fun bind(repeatedGroupHeader: QuestionnaireAdapterItem.RepeatedGroupHeader) {
    header.text =
      header.context.getString(
        R.string.repeated_group_title,
        "${repeatedGroupHeader.index + 1}",
        repeatedGroupHeader.title,
      )
    delete.setOnClickListener { repeatedGroupHeader.onDeleteClicked() }
  }
}

/**
 * Delegate for [QuestionnaireItemViewHolder].
 *
 * This interface provides an abstraction of the operations that need to be implemented for a type
 * of view in the questionnaire.
 *
 * There is a 1:1 relationship between this and [QuestionnaireItemViewHolder]. In other words, there
 * is a unique [QuestionnaireItemViewHolderDelegate] for each [QuestionnaireItemViewHolder]. This is
 * critical for the correctness of the recycler view.
 */
interface QuestionnaireItemAndroidViewHolderDelegate : QuestionnaireItemViewHolderDelegate {

  var questionnaireViewItem: QuestionnaireViewItem

  /**
   * Initializes the view in [QuestionnaireItemViewHolder]. Any listeners to record user input
   * should be set in this function.
   */
  fun init(itemView: View)

  /**
   * Binds a [QuestionnaireViewItem] to the view. This should update the UI to display the question,
   * the answer, and any validation result.
   */
  fun bind(questionnaireViewItem: QuestionnaireViewItem)

  /** Sets view read only if [isReadOnly] is true. */
  fun setReadOnly(isReadOnly: Boolean)
}

interface QuestionnaireItemComposeViewHolderDelegate : QuestionnaireItemViewHolderDelegate {
  fun bind(composeView: ComposeView, questionnaireViewItem: QuestionnaireViewItem) {
    composeView.setContent { Mdc3Theme { Content(questionnaireViewItem) } }
  }

  @Composable fun Content(questionnaireViewItem: QuestionnaireViewItem)
}

sealed interface QuestionnaireItemViewHolderDelegate
