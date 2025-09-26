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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse

class RepeatedGroupAddItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  private var context: AppCompatActivity = itemView.context.tryUnwrapContext()!!

  fun bind(questionnaireViewItem: QuestionnaireViewItem) {
    val addItemButton: Button = itemView.findViewById(R.id.add_item_to_repeated_group)

    addItemButton.text =
      itemView.context.getString(
        R.string.add_repeated_group_item,
        questionnaireViewItem.questionText ?: "",
      )
    addItemButton.visibility =
      if (questionnaireViewItem.questionnaireItem.repeats) View.VISIBLE else View.GONE
    addItemButton.setOnClickListener {
      context.lifecycleScope.launch {
        questionnaireViewItem.addAnswer(
          // Nested items will be added in answerChangedCallback in the QuestionnaireViewModel
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent(),
        )
      }
    }

    addItemButton.isEnabled = !questionnaireViewItem.questionnaireItem.readOnly
  }

  companion object {
    val layoutRes = R.layout.add_repeated_item

    fun create(parent: ViewGroup): RepeatedGroupAddItemViewHolder {
      return RepeatedGroupAddItemViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false),
      )
    }
  }
}
