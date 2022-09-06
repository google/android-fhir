/*
 * Copyright 2022 Google LLC
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

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.fetchBitmap
import com.google.android.fhir.datacapture.isImage
import com.google.android.fhir.datacapture.itemMedia
import com.google.android.fhir.datacapture.utilities.tryUnwrapContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Questionnaire

class QuestionnaireItemMediaView(context: Context, attrs: AttributeSet?) :
  LinearLayout(context, attrs) {

  init {
    LayoutInflater.from(context).inflate(R.layout.questionnaire_item_media, this, true)
  }

  private var itemImage: ImageView = findViewById(R.id.item_image)

  fun bind(questionnaireItem: Questionnaire.QuestionnaireItemComponent) {
    itemImage.setImageBitmap(null)

    questionnaireItem.itemMedia?.let {
      val activity = context.tryUnwrapContext()!!

      activity.lifecycleScope.launch(Dispatchers.IO) {
        if (it.isImage) {
          it.fetchBitmap(itemImage.context)?.run {
            launch(Dispatchers.Main) {
              itemImage.visibility = View.VISIBLE
              itemImage.setImageBitmap(this@run)
            }
          }
        }
      }
    }
  }
}
