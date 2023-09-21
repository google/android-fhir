/*
 * Copyright 2023 Google LLC
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.QuestionnaireNavigationViewState
import com.google.android.fhir.datacapture.QuestionnairePageNavigationState
import com.google.android.fhir.datacapture.R

class NavigationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  fun bind(questionnairePageNavigationState: QuestionnairePageNavigationState) {
    fun View.updateState(navigationViewState: QuestionnaireNavigationViewState) {
      when (navigationViewState) {
        QuestionnaireNavigationViewState.Disabled -> {
          visibility = View.VISIBLE
          isEnabled = false
        }
        is QuestionnaireNavigationViewState.Enabled -> {
          visibility = View.VISIBLE
          isEnabled = true
          setOnClickListener { navigationViewState.onClickAction() }
        }
        QuestionnaireNavigationViewState.Hidden -> {
          visibility = View.GONE
        }
      }
    }

    itemView.findViewById<View>(R.id.navigation_item_pagination_previous_button).apply {
      updateState(questionnairePageNavigationState.previousPageNavigationActionState)
    }

    itemView.findViewById<View>(R.id.navigation_item_pagination_next_button).apply {
      updateState(questionnairePageNavigationState.nextPageNavigationActionState)
    }

    itemView.findViewById<View>(R.id.navigation_item_review_mode_button).apply {
      updateState(questionnairePageNavigationState.reviewNavigationActionState)
    }
    itemView.findViewById<View>(R.id.navigation_item_submit_questionnaire).apply {
      updateState(questionnairePageNavigationState.submitNavigationActionState)
    }
  }
}
