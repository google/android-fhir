/*
 * Copyright 2022-2023 Google LLC
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
import androidx.recyclerview.widget.ListAdapter
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.android.fhir.datacapture.views.factories.ReviewViewHolderFactory

/** List Adapter used to bind answers to [QuestionnaireItemViewHolder] in review mode. */
internal class QuestionnaireReviewAdapter :
  ListAdapter<QuestionnaireAdapterItem.Question, QuestionnaireItemViewHolder>(
    DiffCallbacks.QUESTIONS,
  ) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionnaireItemViewHolder {
    return ReviewViewHolderFactory.create(parent)
  }

  override fun onBindViewHolder(holder: QuestionnaireItemViewHolder, position: Int) {
    holder.bind(getItem(position).item)
  }
}
