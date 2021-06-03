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

package com.google.android.fhir.datacapture.gallery

import android.view.View
import android.widget.NumberPicker
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem

object CustomNumberPickerFactory :
  QuestionnaireItemViewHolderFactory(R.layout.custom_number_picker_layout) {
  override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var numberPicker: NumberPicker

      override fun init(itemView: View) {
        numberPicker = itemView.findViewById(R.id.number_picker)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        numberPicker.minValue = 1
        numberPicker.maxValue = 100
      }
    }
}
