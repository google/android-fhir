/*
 * Copyright 2025 Google LLC
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

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.fhir.datacapture.views.compose.RepeatedGroupAddItem

/**
 * Compose-based ViewHolder for the "Add" button for repeated group items. This is the Compose
 * equivalent of [RepeatedGroupAddItemViewHolder].
 */
class RepeatedGroupAddItemComposeViewHolder(private val composeView: ComposeView) :
  RecyclerView.ViewHolder(composeView) {

  fun bind(questionnaireViewItem: QuestionnaireViewItem) {
    composeView.setContent { Mdc3Theme { RepeatedGroupAddItem(questionnaireViewItem) } }
  }

  companion object {
    fun create(parent: ViewGroup): RepeatedGroupAddItemComposeViewHolder {
      return RepeatedGroupAddItemComposeViewHolder(ComposeView(parent.context))
    }
  }
}
