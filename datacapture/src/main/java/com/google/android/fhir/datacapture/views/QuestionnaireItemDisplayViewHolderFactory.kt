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

import android.view.View
import android.widget.ImageView
import com.google.android.fhir.datacapture.fetchBitmap
import com.google.android.fhir.datacapture.itemImage
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal object QuestionnaireItemDisplayViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_display_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var itemImageView: ImageView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        itemImageView = itemView.findViewById(R.id.itemImage)

      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        header.bind(questionnaireItemViewItem.questionnaireItem)

        itemImageView.setImageBitmap(null)

        questionnaireItemViewItem.questionnaireItem.itemImage?.let {
          GlobalScope.launch {
            it.fetchBitmap(itemImageView.context)?.run {
              GlobalScope.launch(Dispatchers.Main) {
                itemImageView.visibility = View.VISIBLE
                itemImageView.setImageBitmap(this@run)

                // throw RuntimeException("At last! This has run")
              }
            }
          }
        }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        // Display type questions have no user input to be validated
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        // Display type questions have no user input
      }
    }
}
