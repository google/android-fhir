/*
 * Copyright 2023-2024 Google LLC
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

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.QuestionnaireNavigationUIState
import com.google.android.fhir.datacapture.QuestionnaireNavigationViewUIState
import com.google.android.fhir.datacapture.R

class NavigationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  fun bind(questionnaireNavigationUIState: QuestionnaireNavigationUIState) {
    itemView.findViewById<View>(R.id.cancel_questionnaire).apply {
      updateState(questionnaireNavigationUIState.navCancel)
    }

    itemView.findViewById<View>(R.id.pagination_previous_button).apply {
      updateState(questionnaireNavigationUIState.navPrevious)
    }

    itemView.findViewById<View>(R.id.pagination_next_button).apply {
      updateState(questionnaireNavigationUIState.navNext)
    }

    itemView.findViewById<View>(R.id.review_mode_button).apply {
      updateState(questionnaireNavigationUIState.navReview)
    }
    itemView.findViewById<View>(R.id.submit_questionnaire).apply {
      updateState(questionnaireNavigationUIState.navSubmit)
    }
  }

  private fun View.updateState(navigationViewState: QuestionnaireNavigationViewUIState) {
    when (navigationViewState) {
      is QuestionnaireNavigationViewUIState.Enabled -> {
        visibility = View.VISIBLE
        isEnabled = true
        if (navigationViewState.labelText.isNullOrBlank().not() && this is Button) {
          text = navigationViewState.labelText
        }
        setOnClickListener { navigationViewState.onClickAction() }
      }
      QuestionnaireNavigationViewUIState.Hidden -> {
        visibility = View.GONE
      }
    }
  }
}
