/*
 * Copyright 2020 Google LLC
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
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.fetchBitmap
import com.google.android.fhir.datacapture.itemImage
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import com.google.android.fhir.datacapture.validation.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal object QuestionnaireItemDisplayViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_display_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var textView: TextView
      private lateinit var itemImageView: ImageView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix)
        textView = itemView.findViewById(R.id.text_view)
        itemImageView = itemView.findViewById(R.id.itemImage)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefix
        } else {
          prefixTextView.visibility = View.GONE
        }
        textView.text = questionnaireItemViewItem.questionnaireItem.localizedText
        textView.visibility =
          if (textView.text.isEmpty()) {
            View.GONE
          } else {
            View.VISIBLE
          }

        itemImageView.setImageBitmap(null)

        questionnaireItemViewItem.questionnaireItem.itemImage?.let {
          GlobalScope.launch {
            it.fetchBitmap()?.run {
              GlobalScope.launch(Dispatchers.Main) {
                itemImageView.visibility = View.VISIBLE
                itemImageView.setImageBitmap(this@run)
              }
            }
          }
        }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        // display type questions have no user input to be validated
      }
    }
}
